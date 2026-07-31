package com.moneymanager.ledger.domain.policy;

import com.moneymanager.ledger.service.policy.Policy;
import com.moneymanager.global.domain.DateRange;
import com.moneymanager.ledger.domain.enums.HistoryType;
import com.moneymanager.ledger.service.policy.LedgerHistoryPolicy;
import com.moneymanager.support.ApplicationExceptionAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.CommonErrorCode.OUT_OF_RANGE;
import static com.moneymanager.global.exception.code.CommonErrorCode.REQUIRED_VALUE;
import static com.moneymanager.global.exception.log.DeveloperLogInfo.valueOf;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Named.named;

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

	private LedgerHistoryPolicy target;

	private static final Clock FIXED_CLOCK = Clock.fixed(
			Instant.parse("2026-01-15T00:00:00Z"),
			ZoneId.systemDefault()
	);

	private static final LocalDate TODAY = LocalDate.now(FIXED_CLOCK);


	@BeforeEach
	void setUp() {
		target = new LedgerHistoryPolicy(FIXED_CLOCK);
	}

	@Nested
	@DisplayName("날짜범위 계산")
	class CalculateDateRangeTest {
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {
		
			@Test
			@DisplayName("YEAR이면 날짜가 연초와 연말로 DateRange 객체를 생성한다.")
			void createsDateRangeWithYearBounds_whenHistoryTypeIsYear() {
				//given: YEAR 타입으로 요청한다.
				HistoryType type = HistoryType.YEAR;
				LocalDate date = LocalDate.of(2026, 4,23);
				
				//when: DateRange 객체를 생성한다.
				DateRange result = target.calculateDateRange(type, date);
				
				//then: 시작일과 종료일을 확인한다.
				assertThat(result.getFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
				assertThat(result.getTo()).isEqualTo(LocalDate.of(2026, 12, 31));
			}

			@ParameterizedTest
			@MethodSource("provideValidMonthDates")
			@DisplayName("MONTH이면 날짜가 월의 첫날과 마지막날로 DateRange 객체를 생성한다.")
			void createsDateRangeWithMonthBounds_whenHistoryTypeIsMonth(LocalDate date, LocalDate expectedFrom, LocalDate expectedTo) {
				//given: MONTH 타입으로 요청한다.
				HistoryType type = HistoryType.MONTH;

				//when: DateRange 객체를 생성한다.
				DateRange result = target.calculateDateRange(type, date);

				//then: 시작일과 종료일을 확인한다.
				assertThat(result.getFrom()).isEqualTo(expectedFrom);
				assertThat(result.getTo()).isEqualTo(expectedTo);
			}

			static Stream<Arguments> provideValidMonthDates() {
				return Stream.of(
						Arguments.of(
								named("2월 (평년)", LocalDate.of(2026, 2, 15)),
								LocalDate.of(2026, 2, 1),
								LocalDate.of(2026, 2, 28)
						),
						Arguments.of(
								named("2월 (윤년)", LocalDate.of(2024, 2, 15)),
								LocalDate.of(2024, 2, 1),
								LocalDate.of(2024, 2, 29)
						),
						Arguments.of(
								named("30일인 달", LocalDate.of(2026, 6, 15)),
								LocalDate.of(2026, 6, 1),
								LocalDate.of(2026, 6, 30)
						),
						Arguments.of(
								named("31일인 달", LocalDate.of(2026, 3, 15)),
								LocalDate.of(2026, 3, 1),
								LocalDate.of(2026, 3, 31)
						)
				);
			}

			@ParameterizedTest
			@MethodSource("provideValidWeekDates")
			@DisplayName("WEEK면 날짜가 주의 월요일과 일요일로 DateRange 객체를 생성한다.")
			void createsDateRangeWithWeekBounds_whenHistoryTypeIsWeek(LocalDate date, LocalDate expectedFrom, LocalDate expectedTo) {
				//given: WEEK 타입으로 요청한다.
				HistoryType type = HistoryType.WEEK;

				//when: DateRange 객체를 생성한다.
				DateRange result = target.calculateDateRange(type, date);

				//then: 시작일과 종료일을 확인한다.
				assertThat(result.getFrom()).isEqualTo(expectedFrom);
				assertThat(result.getTo()).isEqualTo(expectedTo);
			}

			static Stream<Arguments> provideValidWeekDates() {
				return Stream.of(
						Arguments.of(
								named("3월 1주차", LocalDate.of(2026, 3, 1)),
								LocalDate.of(2026, 3, 1),
								LocalDate.of(2026, 3,1)
						),
						Arguments.of(
								named("3월 2주차", LocalDate.of(2026, 3, 2)),
								LocalDate.of(2026, 3, 2),
								LocalDate.of(2026, 3,8)
						),
						Arguments.of(
								named("3월 3주차", LocalDate.of(2026, 3, 15)),
								LocalDate.of(2026, 3, 9),
								LocalDate.of(2026, 3,15)
						),
						Arguments.of(
								named("3월 4주차", LocalDate.of(2026, 3, 19)),
								LocalDate.of(2026, 3, 16),
								LocalDate.of(2026, 3,22)
						),
						Arguments.of(
								named("3월 5주차", LocalDate.of(2026, 3, 28)),
								LocalDate.of(2026, 3, 23),
								LocalDate.of(2026, 3,29)
						),
						Arguments.of(
								named("3월 6주차", LocalDate.of(2026, 3, 31)),
								LocalDate.of(2026, 3, 30),
								LocalDate.of(2026, 3,31)
						)
				);
			}
			
		}


		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("HistoryType이 null이면 예외가 발생한다.")
			void throwsException_whenHistoryTypeIsNull() {
				//when & then: HistoryType을 null로 하면 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(catchException(() -> target.calculateDateRange(null, LocalDate.now())))
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("날짜 계산")
						.hasCauseMessage("필수값 누락")
						.hasTarget(HistoryType.class)
						.hasValue(null)
						.hasUserMessage("내역 유형", "필수");
			}

			@Test
			@DisplayName("날짜가 null이면 예외가 발생한다.")
			void throwsException_whenLocalDateIsNull() {
				//when & then: LocalDate를 null로 하면 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(catchException(() -> target.calculateDateRange(HistoryType.MONTH, null)))
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("날짜 계산")
						.hasCauseMessage("필수값 누락")
						.hasTarget(LocalDate.class)
						.hasValue(null)
						.hasUserMessage("날짜", "필수");
				
			}

		}

	}


	@Nested
	@DisplayName("날짜 기간 검증")
	class ValidateTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@ParameterizedTest
			@MethodSource("provideValidDateRanges")
			@DisplayName("날짜가 범위내면 검증에 통과한다.")
			void validatesDate_whenDateInRange(DateRange dateRange) {
				//when & then: DateRange 검증을 통과한다.
				assertThatCode(() -> target.validate(dateRange))
						.doesNotThrowAnyException();

			}

			static Stream<Arguments> provideValidDateRanges() {
				return Stream.of(
					Arguments.of(
							named(
					"시작일, 종료일 모두 범위 내 날짜",
									new DateRange(
											TODAY.minusYears(3).plusMonths(7).plusDays(5),
											TODAY.minusMonths(5)
									)
					)),
					Arguments.of(
							named(
									"시작일은 5년전, 종료일은 범위 내 날짜",
									new DateRange(
											TODAY.minusYears(5),
											TODAY.minusMonths(1).plusDays(2)
									)
							)
					),
					Arguments.of(
							named(
									"시작일은 범위 내, 종료일은 현재날짜",
									new DateRange(
											TODAY.minusYears(2).plusMonths(2).minusDays(10),
											TODAY
									)
							)
					),
					Arguments.of(
							named(
									"시작일, 종료일 모두 5년전 날짜",
									new DateRange(
											TODAY.minusYears(5),
											TODAY.minusYears(5)
									)
							)
					),
					Arguments.of(
							named(
									"시작일, 종료일 모두 현재 날짜",
									new DateRange(
											TODAY,
											TODAY
									)
							)
					)
				);

			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest
			@MethodSource("provideInvalidDateRange")
			@DisplayName("날짜가 범위에 벗어나면 예외가 발생한다.")
			void throwsException_whenDateIsOutOfRange(DateRange dateRange) {
				//when: 내역 날짜 검증을 진행한다.
				Throwable throwable = catchThrowable(() -> target.validate(dateRange));

				//then: 범위에 벗어나면 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(OUT_OF_RANGE)
						.hasWork("기간 검증")
						.hasCauseMessage("범위 오류")
						.hasTarget(DateRange.class)
						.hasValue(valueOf("from", dateRange.getFrom(), "to", dateRange.getTo()))
						.hasOption("min", String.valueOf(TODAY.minusYears(Policy.LEDGER_MAX_YEAR)))
						.hasOption("max", String.valueOf(TODAY))
						.hasUserMessage("내역", "최근 5년 이내");
			}

			static Stream<Arguments> provideInvalidDateRange() {
				return Stream.of(
						Arguments.of(
								named(
										"시작일, 종료일 모두 현재일 기준 5년 이전인 경우",
										new DateRange(
												TODAY.minusYears(5).minusMonths(1),
												TODAY.minusYears(5).minusDays(5)
										)
								)
						),
						Arguments.of(
								named(
										"시작일은 현재일 기준 5년을 초과한 미래 날짜고, 종료일은 현재일 기준 5년 이내인 경우",
										new DateRange(
												TODAY.plusYears(5).plusMonths(1),
												TODAY
										)
								)
						),
						Arguments.of(
								named(
										"시작일은 현재일 기준 5년 이내이고, 종료일은 현재일 기준 5년을 초과한 미래 날짜인 경우",
										new DateRange(
												TODAY,
												TODAY.plusMonths(3)
										)
								)
						),
						Arguments.of(
								named(
										"시작일이 다음날인 경우",
										new DateRange(
												TODAY.plusDays(1),
												TODAY
										)
								)
						),
						Arguments.of(
								named(
										"종료일이 다음날인 경우",
										new DateRange(
												TODAY,
												TODAY.plusDays(1)
										)
								)
						),
						Arguments.of(
								named(
										"시작일, 종료일 모두 미래 날짜인 경우",
										new DateRange(
												TODAY.plusMonths(1),
												TODAY.plusMonths(2)
										)
								)
						)
				);
			}

		}

	}


	@Nested
	@DisplayName("제목 생성")
	class CreateTitleTest {

		@Test
		@DisplayName("YEAR이면 오늘날짜 기준으로 연 형식으로 제목을 반환한다.")
		void returnsYearFormattedTitle_whenHistoryTypeIsYearAndDateIsToday() {
			//given: HistoryType을 YEAR로 설정한다.
			HistoryType historyType = HistoryType.YEAR;

			//when: 타입에 맞는 제목을 반환한다.
			String result = target.getTitleByHistoryType(historyType);

			//then: 제목에 연도만 포함된다.
			assertThat(result).isEqualTo("2026년");
		}

		@Test
		@DisplayName("MONTH이면 오늘날짜 기준으로 월 형식으로 제목을 반환한다.")
		void returnsMonthFormattedTitle_whenHistoryTypeIsMonthAndDateIsToday() {
			//given: HistoryType을 MONTH로 설정한다.
			HistoryType historyType = HistoryType.MONTH;

			//when: 타입에 맞는 제목을 반환한다.
			String result = target.getTitleByHistoryType(historyType);

			//then: 제목에 연도+월만 포함된다.
			assertThat(result).isEqualTo("2026년 01월");
		}

		@Test
		@DisplayName("WEEK이면 오늘날짜 기준으로 주 형식으로 제목을 반환한다.")
		void returnsWeekFormattedTitle_whenHistoryTypeIsWeekAndDateIsToday() {
			//given: HistoryType을 WEEK로 설정한다.
			HistoryType historyType = HistoryType.WEEK;

			//when: 타입에 맞는 제목을 반환한다.
			String result = target.getTitleByHistoryType(historyType);

			//then: 제목에 연도+월+주가 포함된다.
			assertThat(result).isEqualTo("2026년 01월 3주");
		}

		@Test
		@DisplayName("YEAR이면 설정한 날짜 기준으로 연 형식으로 제목을 반환한다.")
		void returnsYearFormattedTitle_whenHistoryTypeIsYearAndCustomDateIsGiven() {
			//given: HistoryType을 YEAR, 날짜를 설정한다.
			HistoryType historyType = HistoryType.YEAR;
			LocalDate localDate = LocalDate.of(2025, 12, 12);
			
			//when: 타입에 맞는 제목을 반환한다.
			String result = target.getTitleByHistoryType(localDate, historyType);
			
			//then: 제목에 연도만 포함된다.
			assertThat(result).isEqualTo("2025년");
		}

		@ParameterizedTest
		@MethodSource("provideValidLocalDates")
		@DisplayName("MONTH이면 설정한 날짜 기준으로 월 형식으로 제목을 반환한다.")
		void returnsMonthFormattedTitle_whenHistoryTypeIsMonthAndCustomDateIsGiven(LocalDate localDate, String expected) {
			//given: HistoryType을 MONTH로 설정한다.
			HistoryType historyType = HistoryType.MONTH;

			//when: 타입에 맞는 제목을 반환한다.
			String result = target.getTitleByHistoryType(localDate, historyType);

			//then: 제목에 연도+월만 포함한다.
			assertThat(result).isEqualTo(expected);
		}

		static Stream<Arguments> provideValidLocalDates() {
			return Stream.of(
					Arguments.of(
							named("한자리 월인 경우", LocalDate.of(2025, 5, 1)),
							"2025년 05월"
					),
					Arguments.of(
							named("두자리 월인 경우", LocalDate.of(2025, 10,1)),
							"2025년 10월"
					)
			);
		}

		@ParameterizedTest
		@MethodSource("provideValidWeeks")
		@DisplayName("WEEK면 설정한 닐짜 기준으로 주 형식으로 제목을 반환한다.")
		void returnsWeekFormattedTitle_whenHistoryTypeIsWeekAndCustomDateIsGiven(LocalDate localDate, String expected) {
			//given: HistoryType을 WEEK로 설정한다.
			HistoryType historyType = HistoryType.WEEK;

			//when: 타입에 맞는 제목을 반환한다.
			String result = target.getTitleByHistoryType(localDate, historyType);

			//then: 제목에 연도+월+주 포함한다.
			assertThat(result).isEqualTo(expected);
		}

		static Stream<Arguments> provideValidWeeks() {
			return Stream.of(
					Arguments.of(
							named("첫째주인 경우", LocalDate.of(2026, 1, 1)),
							"2026년 01월 1주"
					),
					Arguments.of(
							named("둘째주인 경우", LocalDate.of(2026, 3, 5)),
							"2026년 03월 2주"
					),
					Arguments.of(
							named("셋째주인 경우", LocalDate.of(2025, 6, 15)),
							"2025년 06월 3주"
					),
					Arguments.of(
							named("넷째주인 경우", LocalDate.of(2025, 10, 23)),
							"2025년 10월 4주"
					),
					Arguments.of(
							named("다섯째인 경우", LocalDate.of(2026, 1, 29)),
							"2026년 01월 5주"
					)
			);

		}

	}


	@Nested
	@DisplayName("주차 계산")
	class CalculateWeekTest {

		@ParameterizedTest
		@MethodSource("provideValidWeekOfMonths")
		@DisplayName("해당 월의 일 기준으로 주차를 반환한다.")
		void returnsWeek_whenDayOfMonthIsGiven(LocalDate date, int expected) {
			//when
			int result = target.calculateWeekOfMonth(date);

			//then
			assertThat(result).isEqualTo(expected);
		}

		static Stream<Arguments> provideValidWeekOfMonths() {
			return Stream.of(
					Arguments.of(
							named("1주차인 경우", LocalDate.of(2026, 1,1)), 1
					),
					Arguments.of(
							named("2주차인 경우", LocalDate.of(2026, 1,5)), 2
					),
					Arguments.of(
							named("3주차인 경우", LocalDate.of(2026, 2,12)), 3
					),
					Arguments.of(
							named("4주차인 경우", LocalDate.of(2026, 3,22)), 4
					),
					Arguments.of(
							named("5주차인 경우", LocalDate.of(2026, 4,30)), 5
					)
			);

		}

	}

}
