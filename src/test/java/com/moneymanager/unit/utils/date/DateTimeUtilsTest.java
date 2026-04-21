package com.moneymanager.unit.utils.date;

import com.moneymanager.utils.date.DateTimeUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.utils.date<br>
 * 파일이름       : DateTimeUtilsTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 24<br>
 * 설명              : DateTimeUtils 클래스 로직을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 3. 24</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class DateTimeUtilsTest {


	@Nested
	@DisplayName("날짜 포맷 변환")
	class DateFormatTest {

		@Test
		@DisplayName("변환할 날짜 문자열 형식이 yyyyMMdd면 LocalDate 반환한다.")
		void parseDateFromYyyyMMdd() {
			//given
			String date = "20260101";

			//when
			LocalDate result = DateTimeUtils.parseDateFromYyyyMMdd(date);

			//then
			assertThat(result)
					.isNotNull()
					.isEqualTo(LocalDate.of(2026, 1, 1));
		}

		@ParameterizedTest(name = "[{index}] date={0}")
		@NullAndEmptySource
		@DisplayName("날짜 문자열이 null 또는 공백이면 예외가 발생한다.")
		void throwsException_whenDateIsMissing(String date) {
			//when & then
			assertThatExceptionOfType(IllegalArgumentException.class)
					.isThrownBy(() -> DateTimeUtils.parseDateFromYyyyMMdd(date))
					.withMessageContainingAll("필수값누락", "date");
		}

		@ParameterizedTest(name = "[{index}] {0}")
		@MethodSource("provideInvalidDateFormats")
		@DisplayName("날짜 문자열이 yyyyMMdd 형식이 아니면 예외가 발생한다.")
		void throwsException_whenDateIsNotYyyyMMddFormat(String description, String date) {
			//when & then
			assertThatExceptionOfType(IllegalArgumentException.class)
					.isThrownBy(() -> DateTimeUtils.parseDateFromYyyyMMdd(date))
					.withMessageContainingAll("형식오류", "yyyyMMdd");
		}

		static Stream<Arguments> provideInvalidDateFormats() {
			return Stream.of(
					Arguments.of("yyyy-MM-dd", "2025-12-31"),
					Arguments.of("yyyy.MM.dd", "2026.01.01"),
					Arguments.of("yyyy/MM/dd", "2026/10/12"),
					Arguments.of("MM/dd/yyyy", "01/01/2026"),
					Arguments.of("dd/MM/yyyy", "01/01/2026"),
					Arguments.of("yyyy年MM月dd日", "2026年01月01日"),
					Arguments.of("yyyy년 M월 d일", "2026년 1월 1일")
			);
		}

	}


	@Nested
	@DisplayName("LocalDate 변환")
	class LocalDateParseTest {

		@Test
		@DisplayName("날짜 문자열이 yyyyMMdd 형식이면 LocalDate를 반환한다..")
		void returnsLocalDate_whenDateIsYyyyMMddFormat() {
			//given
			String date = "20260101";

			//when
			LocalDate result = DateTimeUtils.parseDateOrElse(date, LocalDate.now());

			//then
			assertThat(result).isEqualTo(LocalDate.of(2026, 1, 1));
		}

		@ParameterizedTest(name = "[{index}] {0}")
		@MethodSource({
				"provideNullOrBlankDates",
				"provideInvalidDateFormats"
		})
		@DisplayName("날짜 문자열 파싱 중 문제가 발생하면 기본 문자열이 반환한다.")
		void returnsDefaultLocalDate_whenDateParsingFails(String description, String date) {
			//given
			LocalDate today = LocalDate.now();

			//when
			LocalDate result = DateTimeUtils.parseDateOrElse(date, today);

			//then
			assertThat(result).isEqualTo(today);
		}

		static Stream<Arguments> provideNullOrBlankDates() {
			return Stream.of(
					Arguments.of("null", null),
					Arguments.of("빈 문자열", " ")
			);
		}

		static Stream<Arguments> provideInvalidDateFormats() {
			return Stream.of(
					Arguments.of("yyyy-MM-dd", "2025-12-31"),
					Arguments.of("yyyy.MM.dd", "2026.01.01"),
					Arguments.of("yyyy/MM/dd", "2026/10/12"),
					Arguments.of("MM/dd/yyyy", "01/01/2026"),
					Arguments.of("dd/MM/yyyy", "01/01/2026"),
					Arguments.of("yyyy年MM月dd日", "2026年01月01日"),
					Arguments.of("yyyy년 M월 d일", "2026년 1월 1일")
			);
		}

		@Test
		@DisplayName("기본 날짜가 null이면 예외가 발생한다. ")
		void parseDateOrElse_failure_defaultDateIsNull() {
			//when & then
			assertThatExceptionOfType(IllegalArgumentException.class)
					.isThrownBy(() -> DateTimeUtils.parseDateOrElse("20260101", null))
					.withMessageContainingAll("객체없음", "LocalDate", "null");
		}

	}


	@Nested
	@DisplayName("기간범위 확인")
	class DateRangeTest {

		@ParameterizedTest(name = "[{index}] {0}")
		@MethodSource("provideValidateRangeDates")
		@DisplayName("날짜가 시작일과 종료일 사이라면 true를 반환한다. ")
		void returnsTrue_whenDateWithinPeriod(String description, LocalDate date, LocalDate start, LocalDate end) {
			//when
			boolean result = DateTimeUtils.isDateInRange(date, start, end);

			//then
			assertThat(result).isTrue();
		}

		static Stream<Arguments> provideValidateRangeDates() {
			LocalDate date = LocalDate.now();
			LocalDate start = date.minusMonths(1);
			LocalDate end = date.plusMonths(1);

			return Stream.of(
					Arguments.of("기간 내에 포함된 날짜", date, start, end),
					Arguments.of("시작일 동일", date, date, end),
					Arguments.of("종료일 동일", date, start, date)
			);
		}

		@ParameterizedTest(name = "[{index}] date={0}, start={1}, end={2}")
		@MethodSource("provideInvalidDateRangeIsNull")
		@DisplayName("날짜가 null이면 false을 반환한다.")
		void returnsFalse_whenDateIsNull(String description, LocalDate date, LocalDate start, LocalDate end) {
			//when
			boolean result = DateTimeUtils.isDateInRange(date, start, end);

			//then
			assertThat(result).isFalse();
		}

		static Stream<Arguments> provideInvalidDateRangeIsNull() {
			LocalDate date = LocalDate.now();

			return Stream.of(
					Arguments.of(null, null, null),
					Arguments.of(null, date, date),
					Arguments.of(date, null, date),
					Arguments.of(date, date, null)
			);
		}

		@ParameterizedTest(name = "[{index}] {0}")
		@MethodSource("invalidateRangeBoundary")
		@DisplayName("날짜 범위에 벗어나면 false을 반환한다.")
		void returnsFalse_whenDateOutsidePeriod(String description, LocalDate date, LocalDate start, LocalDate end) {
			//when
			boolean result = DateTimeUtils.isDateInRange(date, start, end);

			//then
			assertThat(result).isFalse();
		}

		static Stream<Arguments> invalidateRangeBoundary() {
			LocalDate date = LocalDate.now();

			return Stream.of(
					Arguments.of(
							"시작 > 종료",
							date,
							date.plusMonths(1),
							date.minusDays(5)
					),
					Arguments.of(
							"시작 = 종료",
							date,
							date.minusDays(5),
							date.minusDays(5)
					)
			);
		}

	}


	@Nested
	@DisplayName("문자열 포맷")
	class FormatDateTest {

		@ParameterizedTest(name = "[{index}] {0}")
		@MethodSource("provideValidPatternDates")
		@DisplayName("지정한 패턴으로 문자열이 변환된다.")
		void returnsFormattedDate(String description, LocalDate date, String pattern, String expected) {
			//when
			String result = DateTimeUtils.formatDate(date, pattern);

			//then
			assertThat(result).isEqualTo(expected);
		}

		static Stream<Arguments> provideValidPatternDates() {
			LocalDate date = LocalDate.of(2026, 1, 1);

			return Stream.of(
					Arguments.of("연도 표시", date, "yyyy", "2026"),
					Arguments.of("연+월 표시", date, "yyyy-MM", "2026-01"),
					Arguments.of("연+월+일 표시 (1월 1일)", date, "yyyy년 MM월 dd일", "2026년 01월 01일"),
					Arguments.of("연+월+일 표시 (10월 11일)", date.withMonth(10).plusDays(10), "yyyy년 MM월 dd일", "2026년 10월 11일"),
					Arguments.of("연+월+일+요일 표시", date, "yyyy년 MM월 dd일 E요일", "2026년 01월 01일 목요일")
			);
		}

	}

}
