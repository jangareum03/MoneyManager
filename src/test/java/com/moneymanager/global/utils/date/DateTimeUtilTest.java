package com.moneymanager.global.utils.date;

import com.moneymanager.global.domain.enums.DatePatterns;
import com.moneymanager.global.util.date.DateTimeUtil;
import com.moneymanager.support.ApplicationExceptionAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.utils.date<br>
 * 파일이름       : DateTimeUtilTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 8<br>
 * 설명              : DateTimeUtil 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 7. 8</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class DateTimeUtilTest {

	@Nested
	@DisplayName("문자열을 날짜 형식으로 변환할 때")
	class ParseDate {

		@Nested
		@DisplayName("성공")
		class Success {

			@ParameterizedTest
			@MethodSource("com.moneymanager.support.data.DateTestData#validDates")
			@DisplayName("지정된 날짜로 LocalDate를 반환한다.")
			void returnsLocalDate_whenDateFormatIsValid(String date) {
				//when
				LocalDate result = DateTimeUtil.parseDateOrToday(date);
				
				//then
				assertThat(result).isNotEqualTo(LocalDate.now());
			}
			
			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("null 또는 빈 값이면 오늘날짜의 LocalDate를 반환한다.")
			void returnsToday_whenDateIsNullOrEmpty(String date) {
				//when
				LocalDate result = DateTimeUtil.parseDateOrToday(date);

				//then
				assertThat(result).isToday();
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.support.data.DateTestData#unsupportedFormats")
			@DisplayName("지원하지 않는 날짜 형식이면 오늘날짜의 LocalDate를 반환한다.")
			void returnsToday_whenDateFormatIsInvalid(String date) {
				//when
				LocalDate result = DateTimeUtil.parseDateOrToday(date);

				//then
				assertThat(result).isToday();
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.support.data.DateTestData#invalidDates")
			@DisplayName("유효하지 않은 날짜면 오늘날짜의 LocalDate를 반환한다.")
			void returnsToday_whenDateIsInvalid(String date) {
				//when
				LocalDate result = DateTimeUtil.parseDateOrToday(date);

				//then
				assertThat(result).isToday();
			}
			
		}

	}


	@Nested
	@DisplayName("날짜 범위  검증")
	class DateRangeValidation {

		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@ParameterizedTest
			@MethodSource("validDateRanges")
			@DisplayName("날짜가 시작일과 종료일 사이면 true를 반환한다.")
			void returnsTrue_whenDateIsInRange(LocalDate date, LocalDate start, LocalDate end) {
				//when: 날짜가 지정된 범위에 포함되는지 확인한다.
				boolean result = DateTimeUtil.isDateInRange(date, start, end);
				
				//then: true가 반환된다.
				assertThat(result).isTrue();
			}

			static Stream<Arguments> validDateRanges() {
				return Stream.of(
						Arguments.of(
								named("날짜가 시작일과 같은 경우", LocalDate.of(2026, 1, 1)),
								LocalDate.of(2026, 1, 1),
								LocalDate.of(2026, 1, 31)
						),
						Arguments.of(
								named("날짜가 시작일과 종료일 사이인 경우", LocalDate.of(2026, 1, 15)),
								LocalDate.of(2026, 1, 1),
								LocalDate.of(2026, 1, 31)
						),
						Arguments.of(
								named("날짜가 종료일과 같은 경우", LocalDate.of(2026, 1, 31)),
								LocalDate.of(2026, 1, 1),
								LocalDate.of(2026, 1, 31)
						),
						Arguments.of(
								named("시작일과 종료이 같고 날짜도 같은 경우", LocalDate.of(2026, 1, 1)),
								LocalDate.of(2026, 1, 1),
								LocalDate.of(2026, 1, 1)
						)
				);
			}
			
		}
		
		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest
			@MethodSource("invalidDateRanges")
			@DisplayName("날짜가 시작일과 종료일에 벗어나면 false을 반환한다.")
			void returnsFalse_whenDateIsOutRange(LocalDate date, LocalDate start, LocalDate end) {
				//when: 날짜가 지정된 범위에 포함되는지 확인한다.
				boolean result = DateTimeUtil.isDateInRange(date, start, end);

				//then: false이 반환된다.
				assertThat(result).isFalse();
			}

			static Stream<Arguments> invalidDateRanges() {
				return Stream.of(
						Arguments.of(
								named("날짜가 시작일보다 이전인 경우", LocalDate.of(2025, 12, 31)),
								LocalDate.of(2026, 1, 1),
								LocalDate.of(2026, 1, 31)
						),
						Arguments.of(
								named("날짜가 종료일보다 이후인 경우", LocalDate.of(2026, 2, 1)),
								LocalDate.of(2026, 1, 1),
								LocalDate.of(2026, 1, 31)
						),
						Arguments.of(
								named("시작일이 종료일보다 이후인 경우", LocalDate.of(2026, 1, 1)),
								LocalDate.of(2026, 1, 31),
								LocalDate.of(2026, 1, 11)
						),
						Arguments.of(
								named("날짜가 null인 경우", null),
								LocalDate.of(2026, 1, 1),
								LocalDate.of(2026, 1, 31)
						),
						Arguments.of(
								named("시작일이 null인 경우", LocalDate.of(2026, 1, 1)),
								null,
								LocalDate.of(2026, 1, 1)
						),
						Arguments.of(
								named("종료일이 null인 경우", LocalDate.of(2026, 1, 1)),
								LocalDate.of(2026, 1, 1),
								null
						)
				);
			}
			
		}

	}


	@Nested
	@DisplayName("과거 날짜 여부 확인")
	class PastDateVerification {
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {
		
			@Test
			@DisplayName("과거날짜가 days보다 많이 지났으면 true를 반환한다.")
			void returnsTrue_whenDateIsOutOfRange() {
				//given: 현재보다 10일전인 날짜, days는 5가 주어진다.
				LocalDateTime date = LocalDateTime.now().minusDays(10);
				int days = 5;
				
				//when: 
				boolean result = DateTimeUtil.isPastDays(date, days);
				
				//then: true가 반환된다.
				assertThat(result).isTrue();
			}
			
			@Test
			@DisplayName("날짜가 정확히 days일 전이면 false을 반환한다.")
			void returnsFalse_whenDateIsEqualToDaysAgo() {
				//given: 현재랑 차이일수와 days가 동일하게 주어진다.
				int days = 2;
				LocalDateTime date = LocalDateTime.now().minusDays(days);
				
				//when: 
				boolean result = DateTimeUtil.isPastDays(date, days);
				
				//then: false이 반환된다.
				assertThat(result).isFalse();
			}
			
			@Test
			@DisplayName("days보다 적게 지난 날짜면 false을 반환한다.")
			void returnsFalse_whenDateIsInRange() {
				//given: days보다 적게 지난 날짜가 주어진다.
				int days = 3;
				LocalDateTime date = LocalDateTime.now().minusDays(2);
				
				//when:
				boolean result = DateTimeUtil.isPastDays(date, days);
				
				//then: false이 반환된다.
				assertThat(result).isFalse();
			}
			
			@ParameterizedTest
			@ValueSource(ints = {0, 1})
			@DisplayName("오늘 날짜면 false을 반환한다.")
			void returnsFalse_whenDateIsToday(int days) {
				//given: 오늘 날짜를 준비한다.
				LocalDateTime date = LocalDateTime.now();
				
				//when:
				boolean result = DateTimeUtil.isPastDays(date, days);
				
				//then: false이 반환된다.
				assertThat(result).isFalse();
			}

			@ParameterizedTest
			@ValueSource(ints = {0, 1})
			@DisplayName("미래 날짜면 false을 반환한다.")
			void returnsFalse_whenDateIsFuture(int days) {
				//given: 미래 날짜를 준비한다.
				LocalDateTime date = LocalDateTime.now().plusDays(1);
				
				//when:
				boolean result = DateTimeUtil.isPastDays(date, days);
				
				//then: false이 반환된다.
				assertThat(result).isFalse();
			}
			
		}
		
		
		@Nested
		@DisplayName("실패 케이스")
		class Failure {
		
			@Test
			@DisplayName("days가 음수면 예외가 발생한다.")
			void throwsException_whenDaysIsNegative() {
				//given: days는 음수로, 날짜는 오늘날짜가 주어진다.
				int days = -1;
				LocalDateTime date = LocalDateTime.now();
				
				//when:
				Throwable throwable = catchThrowable(() -> DateTimeUtil.isPastDays(date, days));
				
				//then: 음수에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable);
			}
			
		}

	}


	@Nested
	@DisplayName("날짜 객체 문자열 포맷")
	class DateFormatting {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@ParameterizedTest
			@MethodSource("validDatePatterns")
			@DisplayName("지원하는 날짜 형식이면 문자열로 변환된다.")
			void returnsString_whenDateFormatIsValid(String pattern, String expected) {
				//given: 날짜 객체가 준비되어 있다.
				LocalDate date = LocalDate.of(2026, 1, 10);
				
				//when: 날짜 패턴으로 변환된 문자열이 반환된다.
				String result = DateTimeUtil.formatDate(date, pattern);
				
				//then: 지정된 패턴으로 문자열이 반환된다.
				assertThat(result).isEqualTo(expected);
			}

			static Stream<Arguments> validDatePatterns() {
				return Stream.of(
						Arguments.of(
								named("yyyy-MM-dd", "yyyy-MM-dd"),
								"2026-01-10"
						),
						Arguments.of(
								named("yyyy/MM/dd", "yyyy/MM/dd"),
								"2026/01/10"
						),
						Arguments.of(
								named(DatePatterns.DATE.getPattern(), DatePatterns.DATE.getPattern()),
								"20260110"
						),
						Arguments.of(
								named(DatePatterns.DATE_DOT_WITH_DAY.getPattern(), DatePatterns.DATE_DOT_WITH_DAY.getPattern()),
								"2026. 01. 10 (토)"
						),
						Arguments.of(
								named(DatePatterns.KOREAN_YEAR.getPattern(), DatePatterns.KOREAN_YEAR.getPattern()),
								"2026년"
						),
						Arguments.of(
								named(DatePatterns.KOREAN_YEAR_MONTH.getPattern(), DatePatterns.KOREAN_YEAR_MONTH.getPattern()),
								"2026년 01월"
						),
						Arguments.of(
								named(DatePatterns.KOREAN_YEAR_MONTH_WEEK.getPattern(), DatePatterns.KOREAN_YEAR_MONTH_WEEK.getPattern()),
								"2026년 01월 2주"
						),
						Arguments.of(
								named(DatePatterns.KOREAN_DATE.getPattern(), DatePatterns.KOREAN_DATE.getPattern()),
								"2026년 01월 10일"
						),
						Arguments.of(
								named(DatePatterns.KOREAN_DATE_WITH_DAY.getPattern(), DatePatterns.KOREAN_DATE_WITH_DAY.getPattern()),
								"2026년 01월 10일 토요일"
						)
				);
			}

			@ParameterizedTest
			@EmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			void returnEmpty_whenPatternIsEmpty(String pattern) {
				//given: 오늘 날짜가 준비되어 있다.
				LocalDate today = LocalDate.now();

				//when: 오늘 날짜가 패턴형식으로 변환한다.
				String result = DateTimeUtil.formatDate(today, pattern);

				//then: 패턴 그대로 반환된다.
				assertThat(result).isEqualTo(pattern);
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest
			@NullSource
			@DisplayName("날짜가 null이면 예외가 발생한다.")
			void throwsException_whenDateIsNull(LocalDate date) {
				//when & then: null인 날짜로 문자열을 변환 중 예외가 발생한다.
				assertThatExceptionOfType(NullPointerException.class)
						.isThrownBy(() -> DateTimeUtil.formatDate(date, "yyyy-MM-dd"));
			}
			
			@ParameterizedTest
			@NullSource
			@DisplayName("패턴이 비어있으면 예외가 발생한다.")
			void throwsException_whenPatternIsInvalid(String pattern) {
				//given: 오늘 날짜가 준비되어 있다.
				LocalDate today = LocalDate.now();

				//when & then: null 패턴으로 변환 중 예외가 발생한다.
				assertThatExceptionOfType(NullPointerException.class)
						.isThrownBy(() -> DateTimeUtil.formatDate(today, pattern));
			}
			
			@ParameterizedTest
			@ValueSource(strings = {"abc", "yyyy-MM-dd '오늘"})
			@DisplayName("지원하지 않은 날짜 형식이면 예외가 발생한다.")
			void throwsException_whenDateFormatIsInvalid(String pattern) {
				//given: 오늘 날짜가 준비되어 있다.
				LocalDate today = LocalDate.now();

				//when & then: 잘못된 패턴으로 변환 중 예외가 발생한다.
				assertThatExceptionOfType(IllegalArgumentException.class)
						.isThrownBy(() -> DateTimeUtil.formatDate(today, pattern));
			}
			
		}

	}

}