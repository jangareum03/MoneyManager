package com.moneymanager.unit.domain.ledger.policy;

import com.moneymanager.BusinessExceptionAssert;
import com.moneymanager.domain.global.Policy;
import com.moneymanager.domain.global.vo.DateRange;
import com.moneymanager.domain.ledger.enums.HistoryType;
import com.moneymanager.domain.ledger.policy.LedgerHistoryPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static com.moneymanager.exception.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.domain.ledger.policy<br>
 * 파일이름       : LedgerHistoryPolicyTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 2<br>
 * 설명              : LedgerHistoryPolicy 클래스 기능을 검증하는 테스트 클래스
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
public class LedgerHistoryPolicyTest {

	private final LedgerHistoryPolicy ledgerHistoryPolicy = new LedgerHistoryPolicy();

	private final static DateTimeFormatter DATE_TIME_FORMATTER =
			DateTimeFormatter.ofPattern("yyyyMMdd");

	//==================[ validate ]==================
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("validDateRangeCases")
	@DisplayName("정상범위 날짜면 검증에 통과한다.")
	void validate_Success(String description, DateRange dateRange) {
		//when & then
		assertThatCode(() -> ledgerHistoryPolicy.validate(dateRange))
				.doesNotThrowAnyException();
	}

	static Stream<Arguments> validDateRangeCases() {
		LocalDate now = LocalDate.now();

		return Stream.of(
				Arguments.of(
						"시작일, 종료일 모두 5년 전 날짜",
						new DateRange(
								now.minusYears(5).format(DATE_TIME_FORMATTER),
								now.minusYears(5).format(DATE_TIME_FORMATTER)
						)
				),
				Arguments.of(
						"시작일 5년 전 날짜, 종료일은 현재날짜",
						new DateRange(
								now.minusYears(5).format(DATE_TIME_FORMATTER),
								now.format(DATE_TIME_FORMATTER)
						)
				),
				Arguments.of(
						"시작일, 종료일 정책 허용하는 날짜",
						new DateRange(
								now.minusYears(3).minusMonths(2).format(DATE_TIME_FORMATTER),
								now.minusYears(3).format(DATE_TIME_FORMATTER)
						)
				),
				Arguments.of(
						"시작일, 종료일 모두 현재날짜",
						new DateRange(
								now.format(DATE_TIME_FORMATTER),
								now.format(DATE_TIME_FORMATTER)
						)
				)
		);
	}

	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("invalidDateRangeCases")
	@DisplayName("비정상 범위 날짜면 예외가 발생한다")
	void  validate_Failure_NotRange(String description, DateRange dateRange) {
		//when
		Throwable throwable = catchThrowable(() -> ledgerHistoryPolicy.validate(dateRange));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_HISTORY_POLICY_VIOLATION)
				.hasUserMessage("최근 5년")
				.hasLogMessage("조건불만족", "DateRange");
	}

	static Stream<Arguments> invalidDateRangeCases() {
		LocalDate now = LocalDate.now();

		LocalDate min = now.minusYears(Policy.LEDGER_MAX_YEAR);

		return Stream.of(
				Arguments.of(
						"시작일이 5년전보다 이전날짜",
						new DateRange(
								min.minusDays(1).format(DATE_TIME_FORMATTER),
								now.format(DATE_TIME_FORMATTER)
						)
				),
				Arguments.of(
						"종료일이 현재보다 이후날짜",
						new DateRange(
								min.format(DATE_TIME_FORMATTER),
								now.plusDays(1).format(DATE_TIME_FORMATTER)
						)
				),
				Arguments.of(
						"시작일은 5년전 이전날짜, 종료일은 미래날짜",
						new DateRange(
								min.minusDays(1).format(DATE_TIME_FORMATTER),
								now.plusDays(1).format(DATE_TIME_FORMATTER)
						)
				)
		);
	}


	//==================[ resolveRangeType ]==================
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("validRangeTypeCases")
	@DisplayName("날짜 범위에 따라 HistoryType이 올바르게 반환한다.")
	void resolveRangeType_Success_returnWeek(String description, DateRange dateRange, HistoryType historyType) {
		//when
		HistoryType result = ledgerHistoryPolicy.resolveRangeType(dateRange);

		//then
		assertThat(result).isSameAs(historyType);
	}

	static Stream<Arguments> validRangeTypeCases() {
		return Stream.of(
				Arguments.of("0일 → WEEK", dateRange(0), HistoryType.WEEK),
				Arguments.of("7일 → WEEK", dateRange(7), HistoryType.WEEK),
				Arguments.of("8일 → MONTH", dateRange(8), HistoryType.MONTH),
				Arguments.of("31일 → MONTH", dateRange(31), HistoryType.MONTH),
				Arguments.of("32일 → YEAR", dateRange(32), HistoryType.YEAR),
				Arguments.of("365일 → YEAR", dateRange(365), HistoryType.YEAR),
				Arguments.of("366일 → MONTH", dateRange(366), HistoryType.MONTH)
		);
	}

	private static DateRange dateRange(int addDay) {
		LocalDate start = LocalDate.now().plusDays(0);
		LocalDate end = start.plusDays(addDay);

		return new DateRange(start.format(DATE_TIME_FORMATTER), end.format(DATE_TIME_FORMATTER));
	}


	//==================[ getTitleByHistoryType ]==================
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("validTitleCases")
	@DisplayName("내역 범위에 따라 제목 형식이 다르게 반환된다.")
	void getTitleByHistoryType_Success(String description, LocalDate date, HistoryType historyType, String expected) {
		//when
		String result = ledgerHistoryPolicy.getTitleByHistoryType(date, historyType);

		//then
		assertThat(result).isEqualTo(expected);
	}

	static Stream<Arguments> validTitleCases() {
		return Stream.of(
				Arguments.of(
					"YEAR 타입 - 2026년 1월 1일",
						LocalDate.of(2026, 1, 1),
						HistoryType.YEAR,
						"2026년"
				),
				Arguments.of(
						"YEAR 타입 - 2026년 12월 31일",
						LocalDate.of(2026, 12, 31),
						HistoryType.YEAR,
						"2026년"
				),
				Arguments.of(
						"MONTH 타입 - 2026년 1월 1일",
						LocalDate.of(2026, 1, 1),
						HistoryType.MONTH,
						"2026년 01월"
				),
				Arguments.of(
						"MONTH 타입 - 2025년 12월 1일",
						LocalDate.of(2025, 12, 1),
						HistoryType.MONTH,
						"2025년 12월"
				),
				Arguments.of(
						"WEEK 타입 - 2026년 1월 1일",
						LocalDate.of(2026, 1, 1),
						HistoryType.WEEK,
						"2026년 01월 1주"
				),
				Arguments.of(
						"WEEK 타입 - 2026년 1월 4일",
						LocalDate.of(2026, 1, 4),
						HistoryType.WEEK,
						"2026년 01월 1주"
				),
				Arguments.of(
						"WEEK 타입 - 2026년 1월 5일",
						LocalDate.of(2026, 1, 5),
						HistoryType.WEEK,
						"2026년 01월 2주"
				)
		);
	}


	//==================[ calculateWeekOfMonth ]==================
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("validWeekOfMonthCases")
	@DisplayName("해당 월의 일 기준으로 주차를 반환한다.")
	void calculateWeekOfMonth_Success(String description, LocalDate date, int expected) {
		//when
		int result = ledgerHistoryPolicy.calculateWeekOfMonth(date);

		//then
		assertThat(result).isEqualTo(expected);
	}

	static Stream<Arguments> validWeekOfMonthCases() {
		return Stream.of(
				Arguments.of("2026년 1월 1일 → 1주", LocalDate.of(2026, 1,1), 1),
				Arguments.of("2026년 1월 4일 → 1주", LocalDate.of(2026, 1,1), 1),
				Arguments.of("2026년 1월 5일 → 2주", LocalDate.of(2026, 1,1), 1),
				Arguments.of("2026년 2월 19일 → 3주", LocalDate.of(2026, 1,1), 1),
				Arguments.of("2026년 3월 29일 → 4주", LocalDate.of(2026, 1,1), 1),
				Arguments.of("2026년 4월 30일 → 5주", LocalDate.of(2026, 1,1), 1)
		);
	}
}
