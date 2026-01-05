package com.moneymanager.service.ledger;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.domain.global.enums.SystemMessage;
import com.moneymanager.domain.ledger.dto.response.LedgerTypeResponse;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep1Response;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.service.validation.LedgerValidator;
import com.moneymanager.utils.DateFormatUtils;
import com.moneymanager.utils.DateUtils;
import com.moneymanager.utils.LoggerUtil;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.moneymanager.exception.ErrorUtil.createServerException;

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
 * 		 	  <td>26. 1. 4.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
public class LedgerReadService {

	private final Clock clock;

	public LedgerReadService(Clock clock) {
		this.clock = clock;
	}

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
	 *     날짜 계산 과정에서 유효하지 않은 값이 발생할 경우 시스템 로그를 기록하고, {@link com.moneymanager.exception.custom.ServerException} 예외가 발생합니다.
	 * </p>
	 *
	 * @return	가계부 작성 1단계에 필요한 초기 데이터를 담은 {@link LedgerWriteStep1Response} 객체
	 */
	public LedgerWriteStep1Response getInitialData() {
		LocalDate today = LocalDate.now(clock);

		try{
			int pastYear = LedgerValidator.getPAST_YEAR();

			//연도, 월, 일 리스트 구하기
			List<Integer> years = DateUtils.getListByYearRange( today.minusYears(pastYear).getYear(), today.getYear() );
			List<Integer> months = DateUtils.getListByMonthRange(1, today.getMonthValue());
			List<Integer> days = DateUtils.getListByDayRange( 1, today.getDayOfMonth() );

			//날짜를 문자열로 변환
			String title = DateFormatUtils.formatKorean(today);

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
		}catch ( IllegalArgumentException e ) {
			//로그 작성
			LoggerUtil.logSystemError( ErrorDTO.builder().errorCode(ErrorCode.SYSTEM_LOGIC_INTERNAL).serviceName(this.getClass().getSimpleName()).message(e.getMessage()).build() );

			throw createServerException(SystemMessage.SYSTEM.getMessage());
		}
	}

}
