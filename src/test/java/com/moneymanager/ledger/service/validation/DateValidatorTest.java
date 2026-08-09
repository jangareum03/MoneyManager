package com.moneymanager.ledger.service.validation;

import com.moneymanager.global.domain.vo.DateRange;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.global.exception.code.CommonErrorCode;
import com.moneymanager.global.log.DeveloperLogInfo;
import com.moneymanager.global.validation.DateValidator;
import com.moneymanager.support.ApplicationExceptionAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service<br>
 * 파일이름       : DateValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 21<br>
 * 설명              : DateValidator 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 7. 21</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class DateValidatorTest {

	@Nested
	@DisplayName("가계부 날짜 검증")
	class LedgerDateTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {
		
			@Test
			@DisplayName("가계부 날짜가 yyyyMMdd 형식이면 예외가 발생하지 않는다.")
			void validatesLedgerDate_whenDateFormatIsValid() {
				//given: 날짜 형식이 yyyyMMdd으로 주어진다.
				String date = "20260101";
				
				//when: 가계부 날짜를 검증한다.
				assertThatCode(() -> DateValidator.validateLedgerDate(date))
						.doesNotThrowAnyException();
			}
			
		}
		
		@Nested
		@DisplayName("실패 케이스")
		class Failure {
			
			@Test
			@DisplayName("가계부 날짜가 null이면 예외가 발생한다.")
			void throwsException_whenLedgerDateIsNull() {
				//when: 날짜 형식 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
						catchThrowable(() -> DateValidator.validateLedgerDate(null))
				)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("날짜 검증")
						.hasCauseMessage("필수값 누락")
						.hasTarget(Ledger.class)
						.hasField("date")
						.hasValue(null)
						.hasUserMessage("날짜", "선택");
			}
			
			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("가계부 날짜가 빈 문자열이면 예외가 발생한다.")
			void throwsException_whenLedgerDateIsEmpty(String date) {
				//when: 날짜 형식 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateValidator.validateLedgerDate(date))
						)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("날짜 검증")
						.hasCauseMessage("필수값 누락")
						.hasTarget(Ledger.class)
						.hasField("date")
						.hasValue(null)
						.hasUserMessage("날짜", "선택");
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.ledger.service.validation.DateValidatorTest#invalidDates")
			@DisplayName("가계부 날짜 형식이 yyyyMMdd가 아니면 예외가 발생한다.")
			void throwsException_whenLedgerDateFormatIsInvalid(String date) {
				//when: 날짜 형식 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateValidator.validateLedgerDate(date))
						)
						.hasErrorCode(CommonErrorCode.INVALID_FORMAT)
						.hasWork("날짜 검증")
						.hasCauseMessage("형식 오류")
						.hasTarget(Ledger.class)
						.hasField("date")
						.hasValue(date)
						.hasOption("format", "yyyyMMdd")
						.hasUserMessage("날짜", "yyyyMMdd 형식");
			}

		}

	}


	@Nested
	@DisplayName("기간 검증")
	class PeriodTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("validPeriodDates")
			@DisplayName("시작일이 종료일보다 과거면 예외가 발생하지 않는다.")
			void validatesDateRange_whenStartDateIsBeforeEndDate(String caseName, String start, String end) {
				//when: 시작일과 종료일 검증한다.
				assertThatCode(() -> DateValidator.validatePeriod(start, end))
						.doesNotThrowAnyException();
			}

			static Stream<Arguments> validPeriodDates() {
				return Stream.of(
						Arguments.of(
								"시작일과 종료일 차이가 하루인 경우",
								"20251231",
								"20260101"
						),
						Arguments.of(
								"시작일과 종료일 차이가 한달인 경우",
								"20260101",
								"20260201"
						),
						Arguments.of(
								"시작일과 종료일 차이가 일년인 경우",
								"20260101",
								"20270101"
						)
				);
			}
			
			@Test
			@DisplayName("시작일과 종료일이 동일하면 예외가 발생하지 않는다.")
			void validatesDateRange_whenStartDateIsEqualToEndDate() {
				//given: 시작일과 종료일이 동일하게 주어진다.
				String start = "20260101";
				String end = start;

				//when: 시작일과 종료일 검증한다.
				assertThatCode(() -> DateValidator.validatePeriod(start, end))
						.doesNotThrowAnyException();
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest
			@NullSource
			@DisplayName("시작일이 null이면 예외가 발생한다.")
			void throwsException_whenStartDateIsNull(String start) {
				//given: 정상적인 종료일이 주어진다.
				String end = "20260101";
				
				//when & then: 시작일과 종료일을 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
						catchThrowable(() -> DateValidator.validatePeriod(start, end))
				)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("기간 검증")
						.hasCauseMessage("필수값 누락")
						.hasField("startDate")
						.hasValue(start)
						.hasUserMessage("시작일", "입력");
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("시작일이 빈 문자열이면 예외가 발생한다.")
			void throwsException_whenStartDateIsEmpty(String start) {
				//given: 정상적인 종료일이 주어진다.
				String end = "20260101";

				//when & then: 시작일과 종료일을 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateValidator.validatePeriod(start, end))
						)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("기간 검증")
						.hasCauseMessage("필수값 누락")
						.hasField("startDate")
						.hasValue(start)
						.hasUserMessage("시작일", "입력");
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.ledger.service.validation.DateValidatorTest#invalidDates")
			@DisplayName("유효하지 않은 시작일 형식이면 예외가 발생한다.")
			void throwsException_whenStartDateFormatIsInvalid(String start) {
				//given: 정상적인 종료일이 주어진다.
				String end = "20260101";

				//when & then: 시작일과 종료일을 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateValidator.validatePeriod(start, end))
						)
						.hasErrorCode(CommonErrorCode.INVALID_FORMAT)
						.hasWork("기간 검증")
						.hasCauseMessage("형식 오류")
						.hasField("startDate")
						.hasValue(start)
						.hasUserMessage("시작일", "yyyyMMdd 형식");
			}
			
			@ParameterizedTest
			@NullSource
			@DisplayName("종료일이 null이면 예외가 발생한다.")
			void throwsException_whenEndDateIsNull(String end) {
				//given: 정상적인 시작일이 주어진다.
				String start = "20260101";

				//when & then: 시작일과 종료일을 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateValidator.validatePeriod(start, end))
						)
						.hasErrorCode(CommonErrorCode.INVALID_FORMAT)
						.hasWork("기간 검증")
						.hasCauseMessage("필수값 누락")
						.hasField("endDate")
						.hasValue(end)
						.hasUserMessage("날짜", "선택");
			}

			@ParameterizedTest
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("종료일이 빈 문자열이면 예외가 발생한다.")
			void throwsException_whenEndDateIsEmpty(String end) {
				//given: 정상적인 시작일이 주어진다.
				String start = "20260101";

				//when & then: 시작일과 종료일을 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateValidator.validatePeriod(start, end))
						)
						.hasErrorCode(CommonErrorCode.INVALID_FORMAT)
						.hasWork("기간 검증")
						.hasCauseMessage("필수값 누락")
						.hasField("endDate")
						.hasValue(end)
						.hasUserMessage("날짜", "선택");
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.ledger.service.validation.DateValidatorTest#invalidDates")
			@DisplayName("유효하지 않은 종료일 형식이면 예외가 발생한다.")
			void throwsException_whenEndDateFormatIsInvalid(String end) {
				//given: 정상적인 시작일이 주어진다.
				String start = "20260101";

				//when & then: 시작일과 종료일을 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateValidator.validatePeriod(start, end))
						)
						.hasErrorCode(CommonErrorCode.INVALID_FORMAT)
						.hasWork("기간 검증")
						.hasCauseMessage("형식 오류")
						.hasField("endDate")
						.hasValue(end)
						.hasUserMessage("종료일", "yyyyMMdd 형식");
			}
			
			@Test
			@DisplayName("시작일이 종료일보다 미래면 예외가 발생한다.")
			void throwsException_whenStartDateIsAfterEndDate() {
				//given: 시작일이 종료일보다 미래날짜로 주어진다.
				String start = "20260102";
				String end = "20260101";

				//when & then: 시작일과 종료일을 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> DateValidator.validatePeriod(start, end))
						)
						.hasErrorCode(CommonErrorCode.OUT_OF_RANGE)
						.hasWork("기간 검증")
						.hasCauseMessage("시작일 > 종료일")
						.hasTarget(DateRange.class)
						.hasValue(
								DeveloperLogInfo.valueOf("from", start, "to", end)
						)
						.hasUserMessage("종료일", "yyyyMMdd 형식");
			}

		}

	}


	static Stream<Arguments> invalidDates() {
		return Stream.of(
				Arguments.of(named("yyyy-MM-dd 형식인 경우", "2026-01-01")),
				Arguments.of(named("yyyy.MM.dd 형식인 경우", "2026.01.01")),
				Arguments.of(named("yyyy년 MM월 dd일 형식인 경우", "2026년 01월 01일"))
		);
	}

}