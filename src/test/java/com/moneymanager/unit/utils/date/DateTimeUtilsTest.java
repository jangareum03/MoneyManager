package com.moneymanager.unit.utils.date;

import com.moneymanager.utils.date.DateTimeUtils;
import org.junit.jupiter.api.DisplayName;
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

	//==================[ TEST ]==================
	@Test
	@DisplayName("문자열 형식이 yyyyMMdd면 LocalDate 반환한다.")
	void parseDateFromYyyyMMdd_Success() {
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
	@DisplayName("날짜 문자열이 null이거나 빈 문자열이면 예외가 발생한다.")
	void parseDateFromYyyyMMdd_Failure_NullAndEmpty(String date) {
		//when & then
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> DateTimeUtils.parseDateFromYyyyMMdd(date))
				.withMessageContainingAll("필수값누락", "date");
	}

	@ParameterizedTest(name = "[{index}] date={0}")
	@ValueSource(strings = {"2025-12-31", "2026.01.01", "2026/10/12"})
	@DisplayName("지원하지 않은 날짜 형식이면 예외가 발생한다.")
	void parseDateFromYyyyMMdd_Failure_Format(String date) {
		//when & then
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> DateTimeUtils.parseDateFromYyyyMMdd(date))
				.withMessageContainingAll("형식오류", "yyyyMMdd", date);
	}


	@ParameterizedTest(name = "[{index}] date={0}")
	@NullAndEmptySource
	@DisplayName("날짜가 Null 또는 공백이면 기본 날짜를 반환한다.")
	void parseDateOrDefault_Success_NullAndEmpty(String date) {
		//given
		LocalDate defaultDate = LocalDate.now();

		//when
		LocalDate result = DateTimeUtils.parseDateOrElse(date, defaultDate);

		//then
		assertThat(result)
				.isNotNull()
				.isEqualTo(defaultDate);
	}

	@ParameterizedTest(name = "[{index}] format={0}")
	@MethodSource("invalidateParse")
	@DisplayName("지원하지 않은 날짜 형식이면 기본날짜를 반환한다.")
	void parseDateOrDefault_Success_DateIsUnpaid(String date) {
		//given
		LocalDate defaultDate = LocalDate.now();

		//when
		LocalDate result = DateTimeUtils.parseDateOrElse(date, defaultDate);

		//then
		assertThat(result)
				.isNotNull()
				.isEqualTo(defaultDate);
	}

	@ParameterizedTest
	@NullSource
	@DisplayName("기본 날짜가 Null이면 예외가 발생한다.")
	void parseDateOrDefault_Failure_defaultDateNotExist(LocalDate localDate) {
		//given
		String date = "20260101";

		//when & then
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> DateTimeUtils.parseDateOrElse(date, localDate))
				.withMessageContainingAll("객체없음", "LocalDate", "null");
	}

	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("validateRange")
	@DisplayName("날짜가 시작일과 종료일 사이라면 true를 반환한다. ")
	void isDateInRange_Success(String title, LocalDate date, LocalDate start, LocalDate end) {
		//when
		boolean result = DateTimeUtils.isDateInRange(date, start, end);

		//then
		assertThat(result).isTrue();
	}

	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("invalidateRangeNull")
	@DisplayName("날짜가 null이면 false을 반환한다.")
	void isDateInRange_Failure_DateIsNull(String title, LocalDate date, LocalDate start, LocalDate end) {
		//when
		boolean result = DateTimeUtils.isDateInRange(date, start, end);

		//then
		assertThat(result).isFalse();
	}

	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("invalidateRangeBoundary")
	@DisplayName("날짜 범위가 이상하면 false을 반환한다.")
	void isDateInRange_Failure_BoundaryValue(String title, LocalDate date, LocalDate start, LocalDate end) {
		//when
		boolean result = DateTimeUtils.isDateInRange(date, start, end);

		//then
		assertThat(result).isFalse();
	}



	//==================[ Method Sources ]==================
	static Stream<Arguments> validateParse() {
		return Stream.of(
				Arguments.of("yyyy-MM-dd", "2026-01-01", 2026, 1, 1),
				Arguments.of("yyyy/MM/dd", "2026/01/01", 2026, 1, 1),
				Arguments.of("yyyyMMdd", "20260101", 2026, 1, 1),
				Arguments.of("yyyy년 MM월 dd일", "2026년 01월 01일", 2026, 1, 1),
				Arguments.of("yyyy년 MM월 dd일 E요일", "2026년 01월 01일 목요일", 2026, 1, 1)
		);
	}

	static Stream<Arguments> invalidateParse() {
		return Stream.of(
				Arguments.of("MM/dd/yyyy", "01/01/2026"),
				Arguments.of("dd/MM/yyyy", "01/01/2026"),
				Arguments.of("yyyy.MM.dd", "2026.01.01"),
				Arguments.of("yyyy年MM月dd日", "2026年01月01日"),
				Arguments.of("yyyy년 M월 d일", "2026년 1월 1일")
		);
	}

	static Stream<Arguments> validateRange() {
		LocalDate date = LocalDate.now();
		LocalDate start = date.minusMonths(1);
		LocalDate end = date.plusMonths(1);

		return Stream.of(
			Arguments.of("기간 내에 포함된 날짜", date, start, end),
			Arguments.of("시작일 동일", date, date, end),
			Arguments.of("종료일 동일", date, start, date)
		);
	}

	static Stream<Arguments> invalidateRangeNull() {
		LocalDate date = LocalDate.now();

		return Stream.of(
				Arguments.of("모든 날짜 null", null, null, null),
				Arguments.of("확인할 날짜 null", null, date, date),
				Arguments.of("시작 날짜 null", date, null, date),
				Arguments.of("종료 날짜 null", date, date, null)
		);
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
