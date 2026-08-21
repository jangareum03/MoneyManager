package com.moneymanager.ledger.service.policy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.policy<br>
 * 파일이름       : LedgerDatePolicy<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 10<br>
 * 설명              : 가계부 거래날짜 정책을 정의하는 클래스
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
 * 		 	  <td>26. 8. 10</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
@RequiredArgsConstructor
public final class LedgerDatePolicy {

	@Getter
	private static final int MIN_YEAR = 5;
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	private final Clock clock;

	LocalDate minimum() {
		return LocalDate.now(clock).minusYears(MIN_YEAR);
	}

	LocalDate maximum() {
		return LocalDate.now(clock);
	}

	boolean isValidDate(LocalDate date) {
		LocalDate min = minimum();
		LocalDate max = maximum();

		return !date.isBefore(min) && !date.isAfter(max);
	}

	boolean isValidYear(Year year) {
		Year minYear = Year.of(minimum().getYear());
		Year maxYear = Year.of(maximum().getYear());

		return !year.isBefore(minYear) && !year.isAfter(maxYear);
	}

	boolean isValidYearMonth(YearMonth yearMonth) {
		YearMonth minYearMonth = YearMonth.of(minimum().getYear(), minimum().getMonthValue());
		YearMonth maxYearMonth = YearMonth.of(maximum().getYear(), maximum().getMonthValue());

		return !yearMonth.isBefore(minYearMonth) && !yearMonth.isAfter(maxYearMonth);
	}

}