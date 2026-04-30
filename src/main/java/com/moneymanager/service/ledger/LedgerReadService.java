package com.moneymanager.service.ledger;

import com.moneymanager.domain.global.Policy;
import com.moneymanager.domain.global.enums.DatePatterns;
import com.moneymanager.domain.global.vo.DateRange;
import com.moneymanager.domain.ledger.dto.query.LedgerHistoryQuery;
import com.moneymanager.domain.ledger.dto.response.*;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.domain.ledger.enums.HistoryMenuType;
import com.moneymanager.domain.ledger.enums.HistoryType;
import com.moneymanager.domain.ledger.policy.LedgerHistoryPolicy;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ServiceAction;
import com.moneymanager.mapper.LedgerMapper;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.utils.date.DateTimeUtils;
import com.moneymanager.utils.date.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.moneymanager.exception.error.ErrorCode.*;

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
@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerReadService {

	private final SecurityUtil securityUtil;

	private final CategoryReadService categoryReadService;
	private final LedgerImageReadService imageReadService;

	private final LedgerRepository ledgerRepository;

	private final LedgerHistoryPolicy ledgerHistoryPolicy;
	private final LedgerMapper ledgerMapper;
	private final Clock clock;



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
		LocalDate today = LocalDate.now(clock);
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
	public LedgerWriteStep2Response getWriteStep2Data(CategoryType type, LocalDate date) {
		//카테고리 목록 조회
		List<CategoryResponse> categories = categoryReadService.getCategoriesByTypeAndLevel(type, CategoryLevel.MIDDLE);

		//제목 포맷 변환
		String title = DateTimeUtils.formatDate(date, DatePatterns.KOREAN_DATE_WITH_DAY.getPattern());

		//회원별로 이미지 슬롯 조회
		List<Boolean> imageSlot = fetchBooleanList();

		return (type == CategoryType.INCOME) ? LedgerWriteStep2Response.ofDataByIncome(title, categories, imageSlot) : LedgerWriteStep2Response.ofDataByOutlay(title, categories, imageSlot);
	}


	public HistoryDashboardResponse getHistoryDashboard(HistoryType historyType) {
		// 1. 인증된 사용자 조회
		String memberId = securityUtil.getMemberId();

		// 2. 기간 생성 후 검증
		LocalDate today = LocalDate.now(clock);
		DateRange dateRange = ledgerHistoryPolicy.calculateDateRange(historyType, today);

		ledgerHistoryPolicy.validate(dateRange);

		// 3. 내역 조회
		List<LedgerHistoryQuery> histories = ledgerRepository.findHistoriesByMemberAndDateBetween(memberId, dateRange.getFrom(), dateRange.getTo());

		// 4. 내역 그룹화 (날짜 기준)
		Map<LocalDate, List<HistoryItem>> listGroups = groupByDate(histories);

		// 5. 통계 생성
		LedgerStatistics statistics = calculateStatistics(histories);

		// 6. 제목 생성
		String title = ledgerHistoryPolicy.getTitleByHistoryType(historyType);

		// 7. 검색 메뉴 생성
		List<MenuItem> menu = createMenu();

		// 8. 응답 생성
		return HistoryDashboardResponse.of(title, menu, statistics, listGroups);
	}


	//날짜 기준으로 내림차순 정렬
	private Map<LocalDate, List<HistoryItem>> groupByDate(List<LedgerHistoryQuery> histories) {
		return histories.stream()
				.filter(history -> history.getDate() != null)
				.collect(Collectors.groupingBy(
						LedgerHistoryQuery::getDate,
						() -> new TreeMap<>(Comparator.reverseOrder()),
						Collectors.mapping(
								HistoryItem::from,
								Collectors.toList()
						)
				));
	}

	//내역에서 카테고리별 금액 합계 구하기
	private LedgerStatistics calculateStatistics(List<LedgerHistoryQuery> histories) {
		Map<CategoryType, Long> sumByCategory = histories.stream()
				.filter(h -> h.getCategoryCode() != null)
				.collect(Collectors.groupingBy(
						h -> CategoryType.fromCategoryCode(h.getCategoryCode()),
						Collectors.summingLong(LedgerHistoryQuery::getAmount)
				));

		Long income = sumByCategory.getOrDefault(CategoryType.INCOME, 0L);
		Long outlay = sumByCategory.getOrDefault(CategoryType.OUTLAY, 0L);

		return LedgerStatistics.of(income, outlay);
	}

	//내역 메뉴 생성
	private List<MenuItem> createMenu() {
		return List.of(
			new MenuItem("전체", HistoryMenuType.ALL.name()),
			new MenuItem("수입/지출", HistoryMenuType.CATEGORY.name()),
			new MenuItem("카테고리", HistoryMenuType.SUB_CATEGORY.name()),
			new MenuItem("메모", HistoryMenuType.MEMO.name()),
			new MenuItem("기간", HistoryMenuType.DATE.name())
		);
	}


	public LedgerDetailResponse getLedgerDetail(String code) {
		ServiceAction action = ServiceAction.LEDGER_DETAIL;

		String memberId = null;

		try{
			// 1. 인증된 사용자 조회
			memberId = securityUtil.getMemberId();

			//2. 가계부/카테고리 조회
			Ledger ledger = getLedger(memberId, code);
			Category category = categoryReadService.getCategory(code);

			//3. 가계부 이미지 조회
			List<LedgerImage> images = imageReadService.getLedgerImages(ledger.getId());
			//4. 정책에 맞춰 이미지 개수 보장
			images = adjustImageCountToPolicy(images);

			LedgerDetailResponse response = ledgerMapper.toDetailDto(ledger, category, images);

			log.info("{} 성공   |   memberId={}   |   result=success   |   ledger={}", action.getTitle(), memberId, code);
			return response;
		}catch (BusinessException e) {
			log.error(
					"[{}] {} 실패   |   memberId={}   |   result=failure   |   errorCode={}",
					e.getTraceId(), action.getTitle(), memberId, e.getErrorCode()
			);

			throw e.withService(action);
		}
	}

	private List<LedgerImage> adjustImageCountToPolicy(List<LedgerImage> imageList) {
		int max = Policy.LEDGER_MAX_IMAGE;

		Map<Integer, LedgerImage> imageMap = imageList.stream()
				.collect(Collectors.toMap(
						LedgerImage::getSortOrder,
						i -> i
				));

		List<LedgerImage> result = new ArrayList<>();
		for(int i=1; i<=max; i++) {
			result.add(imageMap.get(i));
		}

		return result;
	}

	private Ledger getLedger(String memberId, String code) {
		try{
			return ledgerRepository.findByCode(memberId, code);
		} catch (EmptyResultDataAccessException e) {
			throw BusinessException.of(
					LEDGER_TARGET_NOT_FOUND,
					"요청하신 가계부를 찾을 수 없습니다. 입력하신 주소가 정확한지 확인해주세요.",
					"가계부 조회 실패   |   reason=객체없음   |   object=Ledger   |   value={code: " + code + "}"
			);
		}
	}

}
