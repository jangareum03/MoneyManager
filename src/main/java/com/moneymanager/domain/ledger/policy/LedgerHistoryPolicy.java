package com.moneymanager.domain.ledger.policy;

import com.moneymanager.domain.global.Policy;
import com.moneymanager.domain.global.vo.DateRange;
import com.moneymanager.domain.ledger.enums.HistoryType;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ErrorCode;
import com.moneymanager.utils.date.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static com.moneymanager.domain.global.Policy.LEDGER_END_WEEK;
import static com.moneymanager.domain.global.Policy.LEDGER_START_WEEK;
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
@RequiredArgsConstructor
public class LedgerHistoryPolicy {

	private final Clock clock;

	public DateRange calculateDateRange(HistoryType historyType, LocalDate date) {
		if(historyType == null) {
			throw new IllegalArgumentException("날짜기간 계산 실패   |   reason=필수값누락   |   enum=HistoryType   |   value=null");
		}

		if(date == null) {
			throw new IllegalArgumentException("날짜기간 계산 실패   |   reason=필수값누락   |   object=LocalDate   |   value=null");
		}

		return switch (historyType) {
			case YEAR -> calculateYearRange(date);
			case MONTH -> calculateMonthRange(date);
			case WEEK -> calculateWeekRange(date);
		};
	}


	private DateRange calculateYearRange(LocalDate date) {
		LocalDate from = date.with(TemporalAdjusters.firstDayOfYear());
		LocalDate to = date.with(TemporalAdjusters.lastDayOfYear());

		return new DateRange(from, to);
	}


	private DateRange calculateMonthRange(LocalDate date) {
		LocalDate from = date.withDayOfMonth(1);
		LocalDate to = date.with(TemporalAdjusters.lastDayOfMonth());

		return new DateRange(from, to);
	}


	private DateRange calculateWeekRange(LocalDate date) {
		LocalDate from = date.with(TemporalAdjusters.previousOrSame(LEDGER_START_WEEK));
		LocalDate to = date.with(TemporalAdjusters.nextOrSame(LEDGER_END_WEEK));

		//시작일이 전달이면 1일로 변경
		LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
		if(from.isBefore(firstDay)) {
			from = firstDay;
		}

		//마지막일이 다음달이면 마지막일로 변경
		LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
		if(to.isAfter(lastDay)) {
			to = lastDay;
		}

		return new DateRange(from, to);
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
		LocalDate now = LocalDate.now(clock);
		LocalDate fiveYearsAgo = now.minusYears(Policy.LEDGER_MAX_YEAR);

		LocalDate from = dateRange.getFrom();
		LocalDate to = dateRange.getTo();

		if(to.isAfter(now)) {
			to = now;
		}

		if( !(isDateInRange(from, fiveYearsAgo, now) && isDateInRange(to, fiveYearsAgo, now)) ) {
			throw BusinessException.of(
					ErrorCode.LEDGER_HISTORY_POLICY_VIOLATION,
					"가계부 거래내역 검증 실패   |   reason=조건불만족   |   object=DateRange   |   condition=기간 초과   |   value={from: " + dateRange.getFrom().toString() + ", to: " + dateRange.getTo() + "}"
			).withUserMessage("가계부 내역은 최근 5년 이내만 가능합니다.");
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
		return getTitleByHistoryType(LocalDate.now(clock), historyType);
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

		int diffDay = firstDayOfMonth.getDayOfWeek().getValue() - LEDGER_START_WEEK.getValue();
		if(diffDay < 0) {
			diffDay += 7;
		}

		return ((localDate.getDayOfMonth() + diffDay - 1) / 7) + 1;
	}
}
