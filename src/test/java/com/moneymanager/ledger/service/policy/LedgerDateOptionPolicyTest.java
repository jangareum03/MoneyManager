package com.moneymanager.ledger.service.policy;

import com.moneymanager.global.config.MutableClock;
import com.moneymanager.ledger.domain.enums.DateUnit;
import com.moneymanager.support.ApplicationExceptionAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.ErrorCode.POLICY_VIOLATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.policy<br>
 * 파일이름       : LedgerDateOptionPolicyTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 14.<br>
 * 설명              :
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
 * 		 	  <td>26. 8. 14.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class LedgerDateOptionPolicyTest {

	private LedgerDateOptionPolicy target;

	private MutableClock clock;
	private LedgerDatePolicy ledgerDatePolicy;


	@BeforeEach
	void setUp() {
		clock = new MutableClock();

		ledgerDatePolicy = new LedgerDatePolicy(clock);
		target = new LedgerDateOptionPolicy(ledgerDatePolicy, clock);
	}

	@Nested
	@DisplayName("DateUnit별로 날짜 옵션을 조회할 때")
	class GetOptionsTest {

		@BeforeEach
		void setUp() {
			clock.set(LocalDate.of(2020, 5, 10));
		}

		@Nested
		@DisplayName("성공")
		class Success {

			@Test
			@DisplayName("YEAR이고 올해면 1월부터 현재월까지 포함한 월 리스트가 반환된다.")
			void returnsMonthsUpToCurrentMonth_whenPeriodTypeIsYearAndIsCurrentYear() {
				//given
				LocalDate today = LocalDate.now(clock);

				DateUnit YEAR =  DateUnit.YEAR;
				String date = String.valueOf(today.getYear());
				
				//when
				List<Integer> result = target.getOptions(YEAR, date);
				
				//then
				assertThat(result)
						.hasSize(5)
						.containsExactly(1, 2, 3, 4, 5);
			}
			
			@Test
			@DisplayName("YEAR이고 올해가 아니면 1월부터 12월까지 포함한 월 리스트가 반환된다.")
			void returnsAllMonths_whenPeriodTypeIsYearAndIsNotCurrentYear() {
				//given: 올해는 2020이다.
				DateUnit YEAR =  DateUnit.YEAR;
				String date = "2019";

				//when
				List<Integer> result = target.getOptions(YEAR, date);
				
				//then
				assertThat(result)
						.hasSize(12)
						.containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
			}
			
			@Test
			@DisplayName("MONTH이고 올해 현재 월이면 1일부터 현재일까지 포함한 일 리스트가 반환된다.")
			void returnsDaysUpToCurrentDay_whenPeriodTypeIsMonthAndIsCurrentMonth() {
				//given
				LocalDate today = LocalDate.now(clock);

				DateUnit MONTH =  DateUnit.MONTH;
				String date = today.getYear() + String.format("%02d", today.getMonthValue());

				//when
				List<Integer> result = target.getOptions(MONTH, date);

				//then
				assertThat(result)
						.hasSize(10)
						.containsExactly(1, 2, 3, 4, 5, 6 ,7 ,8 ,9, 10);
			}
			
			@Test
			@DisplayName("MONTH이고 오늘 날짜가 아니면 1일부터 날짜 최대일까지 포함한 월 리스트가 반환된다.")
			void returnsAllDaysInMonth_whenPeriodTypeIsMonthAndIsNotCurrentMonth() {
				//given
				DateUnit MONTH =  DateUnit.MONTH;
				String date = "201901";

				//when
				List<Integer> result = target.getOptions(MONTH, date);

				//then
				assertThat(result)
						.hasSize(31)
						.doesNotHaveDuplicates()
						.containsExactly(
								1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
								11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
								21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
								31
						);
			}

		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@BeforeEach
			void setUp() {
				clock.set(LocalDate.of(2025, 5, 10));
			}

			@ParameterizedTest
			@MethodSource("invalidYears")
			@DisplayName("YEAR인데 작성할 수 있는 연도가 아니면 예외가 발생한다.")
			void throwsBusinessException_whenPeriodTypeIsYearAndYearIsInvalid(String date) {
				//given
				DateUnit YEAR =  DateUnit.YEAR;
				
				//when & then
				ApplicationExceptionAssert.assertThatApplicationException(
						catchThrowable(() -> target.getOptions(YEAR, date))
				)
						.hasErrorCode(POLICY_VIOLATION)
						.hasWork("월 목록 조회")
						.hasCauseMessage("허용되지 않은 연도")
						.hasField("date")
						.hasValue(date);
			}

			static Stream<Arguments> invalidYears() {
				return Stream.of(
						Arguments.of(
								named("허용하는 최소 연도에서 과거인 경우 (경계값: 2019)", "20190510")
						),
						Arguments.of(
								named("허용하는 최대 연도에서 미래인 경우 (경계값: 2026)", "20260510")
						)
				);
			}

			@ParameterizedTest
			@MethodSource("invalidMonths")
			@DisplayName("MONTH인데 작성할 수 있는 연도와 월이 아니면 예외가 발생한다.")
			void throwsException_whenPeriodTypeIsMonthAndYearMonthIsInvalid(String date) {
				//given
				DateUnit MONTH =  DateUnit.MONTH;

				//when & then
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.getOptions(MONTH, date))
						)
						.hasErrorCode(POLICY_VIOLATION)
						.hasWork("일 목록 조회")
						.hasCauseMessage("허용되지 않은 월")
						.hasValue("year", "month", date.substring(0, 4), date.substring(5));
			}

			static Stream<Arguments> invalidMonths() {
				return Stream.of(
						Arguments.of(
								named("허용하는 최소 날짜에서 과거인 경우 (경계값: 201904)", "201904")
						),
						Arguments.of(
								named("허용하는 최대 연도에서 미래인 경우 (경계값: 202606)", "202606")
						)
				);
			}

		}

	}

}