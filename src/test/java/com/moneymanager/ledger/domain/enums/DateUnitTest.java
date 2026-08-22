package com.moneymanager.ledger.domain.enums;

import com.moneymanager.support.ApplicationExceptionAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.enums<br>
 * 파일이름       : DateUnitTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 14<br>
 * 설명              : DateUnit 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 8. 14</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class DateUnitTest {

	@Nested
	@DisplayName("DateUnit 생성할 때")
	class FromTest {

		@Nested
		@DisplayName("성공")
		class Success {

			@ParameterizedTest
			@MethodSource("validValues")
			@DisplayName("값이 존재하면 DateUnit으로 생성한다.")
			void createsDateUnit_whenValueExists(String value, DateUnit expected) {
				//when
				DateUnit result = DateUnit.from(value);
				
				//then
				assertThat(result).isSameAs(expected);
			}

			static Stream<Arguments> validValues() {
				return Stream.of(
						Arguments.of(
								named("소문자인 경우", "year"),
								DateUnit.YEAR
						),
						Arguments.of(
								named("대문자인 경우", "MONTH"),
								DateUnit.MONTH
						),
						Arguments.of(
								named("대소문자 혼합인 경우", "yEAr"),
								DateUnit.YEAR
						)
				);
			}
			
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("값이 null이거나 빈 문자열이면 예외를 발생시킨다.")
			void throwsNoSuchElementException_whenValueIsNullOrEmpty(String value) {
				assertThatThrownBy(() -> DateUnit.from(value))
						;
			}
			
			@Test
			@DisplayName("값이 존재하지 않으면 예외를 발생시킨다.")
			void throwsNoSuchElementException_whenValueDoesNotExist() {
				assertThatThrownBy(() -> DateUnit.from("nonexistent"))
						;
			}

			
		}

	}


	@Nested
	@DisplayName("단위별로 날짜 검증할 때")
	class ValidDateTest {

		@Nested
		@DisplayName("성공")
		class Success {

			@ParameterizedTest
			@ValueSource(strings = {"2026", "1111", "0123", "0000"})
			@DisplayName("YEAR이면 4자리 숫자로 이루어진 문자열만 반환한다.")
			void returnsFourDigits_whenUnitIsYear(String date) {
				//given
				DateUnit dateUnit = DateUnit.YEAR;
				
				//when
				assertDoesNotThrow(() -> dateUnit.validateDate(date));
			}

			@ParameterizedTest
			@ValueSource(strings = {"202600", "202655", "000005", "123123", "000000"})
			@DisplayName("MONTH면 6자리 숫자로 이루어진 문자열만 반횐한다.")
			void returnsSixDigits_whenUnitIsMonth(String date) {
				//given
				DateUnit dateUnit = DateUnit.MONTH;
				
				//when
				assertDoesNotThrow(() -> dateUnit.validateDate(date));
			}

		}

		@Nested
		@DisplayName("실패")
		class Failure {

			private final List<DateUnit> dateUnits = Arrays.stream(DateUnit.values()).toList();
			
			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("모든 단위에서 null이거나 빈 문자열이면 예외를 발생시킨다.")
			void throwsValidationException_whenInputIsNullOrEmpty(String date) {
				//when & then
				dateUnits.forEach(dateUnit -> {
					ApplicationExceptionAssert.assertThatApplicationException(
							catchThrowable(() -> dateUnit.validateDate(date))
					)
							.hasErrorCode(REQUIRED_VALUE)
							.hasWork("날짜 검증")
							.hasCauseMessage("필수값 누락");
				});
			}
			
			@ParameterizedTest
			@ValueSource(strings = {"1", "22", "333", "55555"})
			@DisplayName("YEAR에서 4자리가 아니면 예외를 발생시킨다.")
			void throwsValidationException_whenYearIsNotFourDigits(String date) {
				//given
				DateUnit dateUnit = DateUnit.YEAR;

				//when & then
				ApplicationExceptionAssert.assertThatApplicationException(
						catchThrowable(() -> dateUnit.validateDate(date))
				)
						.hasErrorCode(OUT_OF_RANGE)
						.hasWork("날짜 검증")
						.hasField("date")
						.hasValue(date)
						.hasOption("size", 4);
			}

			@ParameterizedTest
			@ValueSource(strings = {"1", "22", "333", "4444", "55555", "7777777"})
			@DisplayName("MONTH에서 6자리가 아니면 예외를 발생시킨다.")
			void throwsValidationException_whenMonthIsNotSixDigits(String date) {
				//given
				DateUnit dateUnit = DateUnit.MONTH;

				//when & then
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> dateUnit.validateDate(date))
						)
						.hasErrorCode(OUT_OF_RANGE)
						.hasWork("날짜 검증")
						.hasField("date")
						.hasValue(date.length())
						.hasOption("size", 6);
			}
			
			@Test
			@DisplayName("모든 단위에서 숫자 제외한 다른 문자가 포함되면 예외를 발생시킨다.")
			void throwsValidationException_whenInputContainsNonNumericChar() {
				//when & then
				dateUnits.forEach(dateUnit -> {
					if(dateUnit ==  DateUnit.YEAR) {
						String date = "안녕12";

						ApplicationExceptionAssert.assertThatApplicationException(
										catchThrowable(() -> dateUnit.validateDate(date))
								)
								.hasErrorCode(INVALID_VALUE)
								.hasWork("날짜 검증")
								.hasField("date")
								.hasValue(date);
					}

					if(dateUnit ==  DateUnit.MONTH) {
						String date = "안녕12!!";

						ApplicationExceptionAssert.assertThatApplicationException(
										catchThrowable(() -> dateUnit.validateDate(date))
								)
								.hasErrorCode(INVALID_VALUE)
								.hasWork("날짜 검증")
								.hasField("date")
								.hasValue(date);
					}
				});

			}

		}

	}

}