package com.moneymanager.domain.ledger.policy;

import com.moneymanager.domain.global.Policy;
import com.moneymanager.domain.global.vo.DateRange;
import com.moneymanager.domain.ledger.enums.HistoryType;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ErrorCode;
import com.moneymanager.utils.date.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.moneymanager.utils.date.DateTimeUtils.isDateInRange;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.policy<br>
 * 파일이름       : LedgerHistoryPolicy<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 2<br>
 * 설명              : 가계부 거래내역 정책을 나타내는 클래스
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
 * 		 	  <td>26. 4. 2</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class LedgerHistoryPolicy {

	public DateRange createDateRange(HistoryType historyType) {

	}


	/**
	 *	<p>
	 *	   	조회기간(from, to)은 현재 날짜 기준 최대 {@code Policy.LEDGER_MAX_YEAR}년 이내여야 합니다.
	 *	   	두 날짜 모두 허용 범위를 벗어날 경우 예외가 발생합니다.
	 *	</p>
	 *
	 * @param dateRange	검증할 조회 기간을 담은 객체
	 * @throws BusinessException	조회 기간이 허용 범위를 초과한 경우 발생
	 */
	public void validate(DateRange dateRange) {
		LocalDate now = LocalDate.now();
		LocalDate fiveYearsAgo = now.minusYears(Policy.LEDGER_MAX_YEAR);

		if( !(isDateInRange(dateRange.getFrom(), fiveYearsAgo, now) && isDateInRange(dateRange.getTo(), fiveYearsAgo, now)) ) {
			throw BusinessException.of(
					ErrorCode.LEDGER_HISTORY_POLICY_VIOLATION,
					"가계부 내역은 최근 5년 이내만 가능합니다.",
					"가계부 거래내역 검증 실패   |   reason=조건불만족   |   object=DateRange   |   condition=기간 초과   |   value={from: " + dateRange.getFrom().toString() + ", to: " + dateRange.getTo().toString() + "}"
			);
		}
	}


	/**
	 * 현재 날짜 기준으로 기간 유형에 맞는 제목 문자열을 반환합니다.
	 * <p>
	 *     내부적으로 {@link LedgerHistoryPolicy#getTitleByHistoryType(LocalDate, HistoryType)} 메서드를 호출합니다.
	 * </p>
	 *
	 * @param historyType	조회 기간 유형
	 * @return	기간 유형에 맞는 제목 문자열
	 */
	public String getTitleByHistoryType(HistoryType historyType) {
		return getTitleByHistoryType(LocalDate.now(), historyType);
	}


	/**
	 *	조회 기준 날짜와 기간 유형에 맞는 제목 문자열을 반환합니다.
	 *<p>
	 *     기간 유형이 {@link HistoryType#WEEK}인 경우에는 해당 날짜의 연월 정보와 월 기준 주차를 조합하여 제목을 생성합니다.
	 *     그 외 기간 유형은 각 유형에 정의된 날짜 포맷으로 제목을 생성합니다.
	 *</p>
	 *
	 * @param localDate		기준 날짜
	 * @param historyType	조회 기간 유형
	 * @return 기간 유형에 맞는 제목 문자열
	 */
	public String getTitleByHistoryType(LocalDate localDate, HistoryType historyType) {
		if(historyType == HistoryType.WEEK) {
			String prefix = DateTimeUtils.formatDate(localDate, HistoryType.MONTH.getFormat());
			int week = calculateWeekOfMonth(localDate);

			return String.format("%s %d주", prefix, week);
		}

		return DateTimeUtils.formatDate(localDate, historyType.getFormat());
	}


	/**
	 * 주어진 날짜가 해당 월의 몇 번째 주차인지 계산합니다.
	 * <p>
	 *     월의 1일과 {@link Policy#LEDGER_START_WEEK} 기준으로 첫 주의 시작 위치를 보정한 뒤 주차를 계산합니다.
	 * </p>
	 *
	 * @param localDate	주차를 구할 날짜
	 * @return	해당 월 기준 주차
	 */
	public int calculateWeekOfMonth(LocalDate localDate) {
		LocalDate firstDayOfMonth = localDate.withDayOfMonth(1);	//해당 월 1일

		int diffDay = firstDayOfMonth.getDayOfWeek().getValue() - Policy.LEDGER_START_WEEK.getValue();
		if(diffDay < 0) {
			diffDay += 7;
		}

		return ((localDate.getDayOfMonth() + diffDay - 1) / 7) + 1;
	}
}
