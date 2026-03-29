package com.moneymanager.service.ledger;

import com.moneymanager.domain.global.Policy;
import com.moneymanager.domain.global.enums.DatePatterns;
import com.moneymanager.domain.ledger.dto.response.CategoryResponse;
import com.moneymanager.domain.ledger.dto.response.LedgerTypeResponse;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep1Response;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep2Response;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.domain.ledger.vo.DateRange;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.member.MemberReadService;
import com.moneymanager.utils.date.DateTimeUtils;
import com.moneymanager.utils.date.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.ledger<br>
 * 파일이름       : LedgerReadService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 4<br>
 * 설명              : 가계부 정보를 조회하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 * 		<thead>
 * 		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 * 		 	  	<td>날짜</td>
 * 		 	  	<td>작성자</td>
 * 		 	  	<td>변경내용</td>
 * 		 	</tr>
 * 		</thead>
 * 		<tbody>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 1. 4</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 1. 9</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 이름] getInitialData → getWriteStep1Data</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class LedgerReadService {

	private final SecurityUtil securityUtil;

	private final CategoryReadService categoryReadService;
	private final MemberReadService memberReadService;



	/**
	 *	가계부 작성 1단계 화면에서 사용되는 초기 데이터를 조회합니다.
	 *<p>
	 *     오늘 날짜를 기준으로 다음 정보를 생성하여 반환합니다.
	 *     <ul>
	 *         <li>선택 가능한 연도 목록 (과거 설정된 연도부터 현재 연도까지)</li>
	 *         <li>선택 가능한 월 목록 (1월부터 현재 월까지)</li>
	 *         <li>선택 가능한 일 목록 (1월부터 월의 마지막일까지)</li>
	 *         <li>화면 제목에 사용될 날짜 문자열</li>
	 *     </ul>
	 *</p>
	 * <p>
	 *     날짜 계산 과정에서 유효하지 않은 값이 발생할 경우 시스템 로그를 기록하고, 예외가 발생합니다.
	 * </p>
	 *
	 * @return	가계부 작성 1단계에 필요한 초기 데이터를 담은 {@link LedgerWriteStep1Response} 객체
	 */
	public LedgerWriteStep1Response getWriteStep1Data() {
		LocalDate today = LocalDate.now();
		int pastYear = today.minusYears(Policy.LEDGER_MAX_YEAR).getYear();

		//연도, 월, 일 리스트 구하기
		List<Integer> years = DateUtils.getYearsInRange( pastYear, today.getYear() );
		List<Integer> months = DateUtils.getMonthsInRange(1, today.getMonthValue());
		List<Integer> days = DateUtils.getDaysInRange( 1, today.getDayOfMonth() );

		//날짜를 문자열로 변환
		String title = DateTimeUtils.formatDate(today, DatePatterns.KOREAN_DATE_WITH_DAY.getPattern());

		return LedgerWriteStep1Response.builder()
				.types(LedgerTypeResponse.fromEnum())
				.years(years)
				.months(months)
				.days(days)
				.currentYear(today.getYear())
				.currentMonth(today.getMonthValue())
				.currentDay(today.getDayOfMonth())
				.displayDate(title)
				.build();
	}


	/**
	 * 가계부 작성 2단계 화면에 사용되는 초기 데이터를 조회합니다.
	 * <p>
	 *     다음 정보를 생성하고 반환됩니다.
	 *     <ul>
	 *         <li>가계부 유형에 따라 중간 단계({@link CategoryLevel#MIDDLE}) 카테고리 목록</li>
	 *         <li>화면 제목에 사용될 날짜 문자열</li>
	 *         <li>회원별 이미지 슬롯 사용 가능 여부 리스트</li>
	 *     </ul>
	 * </p>
	 *
	 * @param type	가계부 유형(수입/지출)
	 * @param date	가계부 거래 날짜를 담은 {@link LocalDate}
	 * @return	가계부 작성 2단계에 필요한 초기 데이터를 담은 {@link LedgerWriteStep2Response} 객체
	 */
	public LedgerWriteStep2Response getWriteStep2Data(LedgerType type, LocalDate date) {
		//카테고리 목록 조회
		List<CategoryResponse> categories = categoryReadService.getCategoriesByTypeAndLevel(type, CategoryLevel.MIDDLE);

		//제목 포맷 변환
		String title = DateTimeUtils.formatDate(date, DatePatterns.KOREAN_DATE_WITH_DAY.getPattern());

		//회원별로 이미지 슬롯 조회
		List<Boolean> imageSlot = fetchBooleanList();

		return (type == LedgerType.INCOME) ? LedgerWriteStep2Response.ofDataByIncome(title, categories, imageSlot) : LedgerWriteStep2Response.ofDataByOutlay(title, categories, imageSlot);
	}


	public void getHistorySummaries(String from, String to) {
		//날짜 검증
		DateRange dateRange = new DateRange(from, to);
		//검증한 날짜로 가계부 조회
		//조회 내역의 금액을 총합/수입/지출로 구분하여 조회
		//기간별로 제목 얻기
		//조회 내역 기반으로 그래프 조회
	}


	/**
	 * 회원이 사용할 수 있는 가계부 이미지 슬롯 상태를 반환합니다.
	 * <p>
	 *     회원이 등록할 수 있는 최대 이미지 개수({@code maxSlot})를 기준으로,
	 *     실제 사용 가능한 슬롯과 사용 불가능한 슬롯을 {@link Boolean} 값으로 표현합니다.
	 *     사용 가능한 슬롯은 {@code true}로, 사용 불가능한 슬롯은 {@code false}로 표현합니다.
	 * </p>
	 *
	 * @return	이미지 슬롯 사용 가능 여부를 나타내는 Boolean 리스트
	 */
	public List<Boolean> fetchBooleanList(){
		List<Boolean> slotList = new ArrayList<>();

		String memberId = securityUtil.getMemberId();

		int availableSlot = memberReadService.getImageLimit(memberId);
		int maxSlot = Policy.LEDGER_MAX_IMAGE;
		for( int i =0; i < maxSlot; i++ ) {
			slotList.add( i < availableSlot );
		}

		return slotList;
	}
}
