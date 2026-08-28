package com.moneymanager.ledger.service.policy;

import com.moneymanager.global.config.MutableClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.policy<br>
 * 파일이름       : LedgerDatePolicyTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 12<br>
 * 설명              : LedgerDatePolicy 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 8. 12</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class LedgerDatePolicyTest {

	private LedgerDatePolicy target;
	private final MutableClock clock = new MutableClock();;

	@BeforeEach
	void setUp() {
		target = new LedgerDatePolicy(clock);
	}

	@Nested
	@DisplayName("작성 가능한 날짜를 조회할 때")
	class GetWriteDate {

		@BeforeEach
		void setUp() {
			clock.set(LocalDate.of(2026, 3, 10));
		}

		@Test
		@DisplayName("최소날짜는 5년 전을 반환한다.")
		void returnsFiveYearsAgo_whenMinimumDateIsRequested() {
			//when
			LocalDate result = target.minimum();
			
			//then
			assertThat(result).isBeforeOrEqualTo(LocalDate.now(clock).minusYears(5));
		}
		
		@Test
		@DisplayName("최대날짜는 현재 날짜를 반환한다.")
		void returnsToday_whenMaximumDateIsRequested() {
			//when
			LocalDate result = target.maximum();
			
			//then:
			assertThat(result).isBeforeOrEqualTo(LocalDate.now(clock));
		}

	}


	@Nested
	@DisplayName("날짜 범위 검증할 때")
	class ValidDate {

		@BeforeEach
		void setUp() {
			clock.set(LocalDate.of(2026, 3, 10));
		}

		@ParameterizedTest
		@MethodSource("validDates")
		@DisplayName("최소날짜와 최대날짜 내에 있으면 true를 반환한다.")
		void returnsTrue_whenDateIsBetweenMinAndMax(LocalDate date) {
			//when
			boolean result = target.isValidDate(date);
			
			//then
			assertThat(result).isTrue();
		}

		static Stream<Arguments> validDates() {
			return Stream.of(
					Arguments.of(
							named("최소날짜 (경계값)", LocalDate.of(2021, 3, 10))
					),
					Arguments.of(
							named("최소날짜 다음날 (2021년 3월 11일)", LocalDate.of(2021, 3, 11))
					),
					Arguments.of(
							named("기간 내 날짜 (2023년 8월 20일)", LocalDate.of(2023, 8, 20))
					),
					Arguments.of(
							named("최대날짜 전날 (2026년 3월 09일)", LocalDate.of(2026, 3, 9))
					),
					Arguments.of(
							named("최대날짜 (경계값)", LocalDate.of(2026, 3, 10))
					)
			);
		}
		
		@Test
		@DisplayName("최소날짜보다 과거면 false를 반환한다.")
		void returnsFalse_whenDateIsBeforeMinDate() {
			//when
			boolean result = target.isValidDate(target.minimum().minusDays(1));
			
			//then
			assertThat(result).isFalse();
		}
		
		@Test
		@DisplayName("최대날짜보다 미래면 false를 반환한다.")
		void returnsFalse_whenDateIsAfterMaxDate() {
			//when
			boolean result = target.isValidDate(target.maximum().plusDays(1));
			
			//then
			assertThat(result).isFalse();
		}

	}


	@Nested
	@DisplayName("얀도를 검증할 때")
	class ValidYear {

		private final LocalDate today = LocalDate.now(clock);

		@Test
		@DisplayName("허용하는 최소 날짜라면 true를 반환한다.")
		void returnsTrue_whenDateIsMinAllowed() {
			//given
			int value = today.minusYears(LedgerDatePolicy.getMIN_YEAR()).getYear();
			
			//when
			boolean result = target.isValidYear(Year.of(value));
			
			//then
			assertThat(result).isTrue();
		}
		
		@Test
		@DisplayName("허용하는 날짜 범위 내라면 true를 반환한다.")
		void returnsTrue_whenDateIsInRange() {
			//given
			int value = today.minusYears(3).getYear();

			//when
			boolean result = target.isValidYear(Year.of(value));

			//then
			assertThat(result).isTrue();
		}
		
		@Test
		@DisplayName("허용하는 최대 날짜라면 true를 반환한다.")
		void returnsTrue_whenDateIsMaxAllowed() {
			//given
			int value = today.getYear();

			//when
			boolean result = target.isValidYear(Year.of(value));

			//then
			assertThat(result).isTrue();
		}
		
		@Test
		@DisplayName("허용하는 최소날짜보다 과거 연도라면 false를 반환한다..")
		void returnsFalse_whenDateIsBeforeMinAllowed() {
			//given
			int value = today.minusYears(LedgerDatePolicy.getMIN_YEAR() + 1).getYear();
			
			//when
			boolean result = target.isValidYear(Year.of(value));
			
			//then
			assertThat(result).isFalse();
		}
		
		@Test
		@DisplayName("허용하는 최대 날짜보다 미래 연도라면 false를 반환한다.")
		void returnsFalse_whenDateIsAfterMaxAllowed() {
			//given
			int value = today.plusYears(1).getYear();

			//when
			boolean result = target.isValidYear(Year.of(value));

			//then
			assertThat(result).isFalse();
		}

	}


	@Nested
	@DisplayName("얀도와 월을 검증할 때")
	class ValidYearMont {

		private LocalDate today;

		@BeforeEach
		void setUp() {
			today  = LocalDate.now(clock);
		}
		
		@Test
		@DisplayName("허용하는 최소 날짜라면 true를 반환한다.")
		void returnsTrue_whenDateIsMinAllowed() {
			//given
			YearMonth yearMonth
					= YearMonth.of(
							today.minusYears(LedgerDatePolicy.getMIN_YEAR()).getYear(),
							today.getMonth()
					);
			
			//when
			boolean result = target.isValidYearMonth(yearMonth);
			
			//then
			assertThat(result).isTrue();
		}
		
		@ParameterizedTest(name = "[{index}] {0}개월 전인 경우")
		@ValueSource(ints = {1, 36, 59})
		@DisplayName("허용하는 날짜 범위 내라면 true를 반환한다.")
		void returnsTrue_whenDateIsInRange(int subMonth) {
			//given
			YearMonth yearMonth = YearMonth.from(today.minusMonths(subMonth));

			//when
			boolean result = target.isValidYearMonth(yearMonth);

			//then
			assertThat(result).isTrue();
		}
		
		@Test
		@DisplayName("허용하는 최대 날짜라면 true를 반환한다.")
		void returnsTrue_whenDateIsMaxAllowed() {
			//given
			YearMonth yearMonth = YearMonth.from(today);

			//when
			boolean result = target.isValidYearMonth(yearMonth);

			//then
			assertThat(result).isTrue();
		}
		
		@Test
		@DisplayName("허용하는 최소 날짜의 이전달이라면 false를 반환한다.")
		void returnsFalse_whenDateIsBeforeMinAllowed() {
			//given
			YearMonth yearMonth = YearMonth.from(today.minusYears(LedgerDatePolicy.getMIN_YEAR()).minusMonths(1));

			//when
			boolean result = target.isValidYearMonth(yearMonth);

			//then
			assertThat(result).isFalse();
		}
		
		@Test
		@DisplayName("허용하는 최대 날짜의 다음달이라면 false를 반환한다.")
		void returnsFalse_whenDateIsAfterMaxAllowed() {
			//given
			YearMonth yearMonth = YearMonth.from(today.plusMonths(1));

			//when
			boolean result = target.isValidYearMonth(yearMonth);

			//then
			assertThat(result).isFalse();
		}
		
	}

}