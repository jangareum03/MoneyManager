package com.moneymanager.global.util.date;


import com.moneymanager.support.ApplicationExceptionAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.util.date<br>
 * 파일이름       : DateRangeUtilsTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 10.<br>
 * 설명              : DateRangeUtils 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 8. 10.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class DateRangeUtilsTest {

	@Nested
	@DisplayName("연도 리스트를 조회할 때")
	class getYearsRangeTest {

		@Nested
		@DisplayName("성공")
		class Success {

			@Test
			@DisplayName("시작과 종료 모두 양수라면 범위의 연도 리스트를 반환한다.")
			void returnsYearList_whenStartAndEndYearsArePositive() {
				//given: 시작연도가 종료연도보다 작다.
				int startYear = 2000;
				int endYear = 2005;

				//when
				List<Integer> result = DateRangeUtils.getYearsInRange(startYear, endYear);

				//then: 시작연도와 종료연도가 포함된 6개의 리스트가 반환된다.
				assertThat(result)
						.hasSize(6)
						.containsExactly(2000, 2001, 2002, 2003, 2004, 2005);
			}

			@Test
			@DisplayName("시작과 종료가 동일하다면 하나의 연도 리스트를 반환한다.")
			void returnsSingleYearList_whenStartAndEndYearsAreEqual() {
				//given: 시작연도와 종료연도가 동일한 값이다.
				int startYear = 2000;
				int endYear = 2000;

				//when
				List<Integer> result = DateRangeUtils.getYearsInRange(startYear, endYear);

				//then: 2000 한 개를 가지고 있는 리스트가 반환된다.
				assertThat(result)
						.singleElement()
						.isEqualTo(startYear);
			}
			
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@ParameterizedTest
			@MethodSource("com.moneymanager.global.util.date.DateRangeUtilsTest#invalidMinValue")
			@DisplayName("시작 연도가 0 이하라면 예외를 발생시킨다.")
			void throwsInternalException_whenStartYearIsZeroOrNegative(int start) {
				//given
				int end = 2000;
				
				//when & then: 연도 검증 중 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
						catchThrowable(() -> DateRangeUtils.getYearsInRange(start, end))
				)
						
						.hasErrorCode(OUT_OF_RANGE)
						.hasWork("연도 리스트 조회")
						.hasCauseMessage("시작 연도 0이하")
						.hasField("start")
						.hasValue(start)
						.hasOption("min", 1);
			}

			@ParameterizedTest
			@MethodSource("com.moneymanager.global.util.date.DateRangeUtilsTest#invalidMinValue")
			@DisplayName("종료 연도가 0 이하라면 예외를 발생시킨다")
			void throwsInternalException_whenEndYearIsZeroOrNegative(int end) {
				//given
				int start = 2000;

				//when & then: 연도 검증 중 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateRangeUtils.getYearsInRange(start, end))
						)
						
						.hasErrorCode(OUT_OF_RANGE)
						.hasWork("연도 리스트 조회")
						.hasCauseMessage("종료 연도 0이하")
						.hasField("end")
						.hasValue(end)
						.hasOption("min", 1);
			}

			@Test
			@DisplayName("시작 연도가 종료 연도보다 크다면 예외를 발생시킨다.")
			void throwsInternalException_whenStartYearIsGreaterThanEndYear() {
				//given: 시작연도는 2000으로, 종료연도는 1999으로 주어진다.
				int start = 2000;
				int end = 1999;

				//when & then: 연도 검증 중 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateRangeUtils.getYearsInRange(start, end))
						)
						
						.hasErrorCode(OUT_OF_RANGE)
						.hasWork("연도 리스트 조회")
						.hasCauseMessage("시작연도 > 종료연도")
						.hasValue("start", "end", start, end);
			}

		}

	}


	@Nested
	@DisplayName("월 리스트를 조회할 때")
	class getMonthsRangeTest {

		@Nested
		@DisplayName("성공")
		class Success {

			@Test
			@DisplayName("시작과 종료 모두 양수라면 범위의 월 리스트를 반환한다.")
			void returnsMonthList_whenStartAndEndMonthsArePositive() {
				//given: 시작 월이 종료 월보다 작다.
				int start = 3;
				int end = 5;

				//when
				List<Integer> result = DateRangeUtils.getMonthsInRange(start, end);

				//then: 시작월과 종료월이 포함된 3개의 리스트가 반환된다.
				assertThat(result)
						.hasSize(3)
						.containsExactly(3, 4, 5);
			}

			@Test
			@DisplayName("시작과 종료가 동일하다면 하나의 월 리스트를 반환한다.")
			void returnsSingleMonthList_whenStartAndEndDaysAreEqual() {
				//given: 시작월이 종료월이랑 동일한 값이다.
				int start = 1;
				int end = 1;

				//when
				List<Integer> result = DateRangeUtils.getMonthsInRange(start, end);

				//then: 1 한 개를 가지고 있는 리스트가 반환된다.
				assertThat(result)
						.singleElement()
						.isEqualTo(start);
			}

		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@ParameterizedTest
			@MethodSource({
					"com.moneymanager.global.util.date.DateRangeUtilsTest#invalidMinValue",
					"invalidMaxValue",
			})
			@DisplayName("시작월이 범위 외라면 예외를 발생시킨다.")
			void throwsInternalException_whenStartMonthOutOfRange(int start) {
				//given
				int end = 10;

				//when & then: 월 검증 중 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
						catchThrowable(() -> DateRangeUtils.getMonthsInRange(start, end))
				)
						
						.hasErrorCode(OUT_OF_RANGE)
						.hasCauseMessage("시작월 범위 초과")
						.hasField("start")
						.hasValue(start)
						.hasOption("min", 1)
						.hasOption("max", 12);
			}

			@ParameterizedTest
			@MethodSource({
					"com.moneymanager.global.util.date.DateRangeUtilsTest#invalidMinValue",
					"invalidMaxValue",
			})
			@DisplayName("종료월이 범위 외라면 예외를 발생시킨다")
			void throwsInternalException_whenEndMonthOutOfRange(int end) {
				//given
				int start = 5;

				//when & then: 월 검증 중 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateRangeUtils.getMonthsInRange(start, end))
						)
						
						.hasErrorCode(OUT_OF_RANGE)
						.hasCauseMessage("종료월 범위 초과")
						.hasField("end")
						.hasValue(end)
						.hasOption("min", 1)
						.hasOption("max", 12);
			}

			@Test
			@DisplayName("시작 월이 종료 월보다 크다면 예외를 발생시킨다.")
			void throwsInternalException_whenStartMonthIsGreaterThanEndYear() {
				//given: 시작월은 10으로, 종료월은 9로 주어진다.
				int start = 10;
				int end = 9;

				//when & then: 월 검증 중 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateRangeUtils.getMonthsInRange(start, end))
						)
						
						.hasErrorCode(OUT_OF_RANGE)
						.hasCauseMessage("시작월 > 종료월")
						.hasValue("start", "end", start, end);
			}

			static Stream<Arguments> invalidMaxValue() {
				return Stream.of(
						Arguments.of(
								named("13월인 경우", 13)
						)
				);
			}

		}

	}


	@Nested
	@DisplayName("일 리스트를 조회할 때")
	class getDaysRangeTest {

		@Nested
		@DisplayName("성공")
		class Success {

			@Test
			@DisplayName("시작과 종료 모두 양수라면 범위의 일 리스트를 반환한다.")
			void returnsDaysList_whenStartAndEndDaysArePositive() {
				//given: 시작일이 종료일보다 작다.
				int start = 13;
				int end = 20;

				//when
				List<Integer> result = DateRangeUtils.getDaysInRange(start, end);

				//then: 시작일과 종료일이 포함된 8개의 리스트가 반환된다.
				assertThat(result)
						.hasSize(8)
						.containsExactly(13, 14, 15, 16, 17, 18, 19 ,20);
			}

			@Test
			@DisplayName("시작과 종료가 동일하다면 하나의 일 리스트를 반환한다.")
			void returnsSingleDayList_whenStartAndEndDaysAreEqual() {
				//given: 시작월이 종료월이랑 동일한 값이다.
				int start = 1;
				int end = 1;

				//when
				List<Integer> result = DateRangeUtils.getDaysInRange(start, end);

				//then: 1 한 개를 가지고 있는 리스트가 반환된다.
				assertThat(result)
						.singleElement()
						.isEqualTo(start);
			}

		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@ParameterizedTest
			@MethodSource({
					"com.moneymanager.global.util.date.DateRangeUtilsTest#invalidMinValue",
					"invalidMaxValue"
			})
			@DisplayName("시작일이 범위 외라면 예외를 발생시킨다")
			void throwsInternalException_whenStartDayOutOfRange(int start) {
				//given
				int end = 10;

				//when & then: 일 검증 중 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateRangeUtils.getDaysInRange(start, end))
						)
						
						.hasErrorCode(OUT_OF_RANGE)
						.hasCauseMessage("시작일 범위 초과")
						.hasField("start")
						.hasValue(start)
						.hasOption("min", 1)
						.hasOption("max", 31);
			}

			@ParameterizedTest
			@MethodSource({
					"com.moneymanager.global.util.date.DateRangeUtilsTest#invalidMinValue",
					"invalidMaxValue"
			})
			@DisplayName("종료일이 범위 외라면 예외를 발생시킨다")
			void throwsInternalException_whenEndDayOutOfRange(int end) {
				//given
				int start = 5;

				//when & then: 일 검증 중 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateRangeUtils.getDaysInRange(start, end))
						)
						
						.hasErrorCode(OUT_OF_RANGE)
						.hasCauseMessage("종료일 범위 초과")
						.hasField("end")
						.hasValue(end)
						.hasOption("min", 1)
						.hasOption("max", 31);
			}

			@Test
			@DisplayName("시작일이 종료일보다 크다면 예외를 발생시킨다.")
			void throwsInternalException_whenStartDayIsGreaterThanEndYear() {
				//given: 시작일은 10으로, 종료일은 9로 주어진다.
				int start = 10;
				int end = 9;

				//when & then: 일 검증 중 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateRangeUtils.getDaysInRange(start, end))
						)
						
						.hasErrorCode(OUT_OF_RANGE)
						.hasCauseMessage("시작일 > 종료일")
						.hasValue("start", "end", start, end);
			}


			static Stream<Arguments> invalidMaxValue() {
				return Stream.of(
						Arguments.of(
								named("32일인 경우", 32)
						)
				);
			}

		}

	}


	static Stream<Arguments> invalidMinValue() {
		return Stream.of(
				Arguments.of(
						named("음수인 경우", -500)
				),
				Arguments.of(
						named("0인 경우(경계값)", 0)
				)
		);
	}

}