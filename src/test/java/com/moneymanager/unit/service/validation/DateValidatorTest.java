package com.moneymanager.unit.service.validation;

import com.moneymanager.BusinessExceptionAssert;
import com.moneymanager.service.validation.DateValidator;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static com.moneymanager.exception.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.validation<br>
 * 파일이름       : DateValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 31<br>
 * 설명              : DateValidator 클래스 로직을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 3. 31</td>
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

		@ParameterizedTest(name = "[{index}] date={0}")
		@ValueSource(strings = {"20200925", "20230601", "20251012", "20251231", "20260101"})
		@DisplayName("정상 날짜 형식이면 검증 통과한다.")
		void validatesDate_whenDateFormatSuccessfully(String date) {
			//when & then
			assertThatCode(() -> DateValidator.validateLedgerDate(date))
					.doesNotThrowAnyException();
		}


		@ParameterizedTest(name = "[{index}] date={0}")
		@NullAndEmptySource
		@DisplayName("날짜가 없거나 비어있으면 예외가 발생한다.")
		void throwsException_whenLedgerDateIsMissing(String date) {
			//when
			Throwable throwable = catchThrowable(() -> DateValidator.validateLedgerDate(date));

			//then
			BusinessExceptionAssert.assertThatBusinessException(throwable)
					.hasErrorCode(LEDGER_INPUT_NULL)
					.hasUserMessage("날짜", "선택")
					.hasLogMessage("필수값누락");
		}


		@ParameterizedTest(name = "[{index}] {0}")
		@MethodSource("provideInvalidDates")
		@DisplayName("가계부 날짜 형식이 올바르지 않으면 예외가 발생한다.")
		void throwsException_whenDateFormatIsInvalid(String date) {
			//when
			Throwable throwable = catchThrowable(() -> DateValidator.validateLedgerDate(date));

			//then
			BusinessExceptionAssert.assertThatBusinessException(throwable)
					.hasErrorCode(LEDGER_INPUT_FORMAT)
					.hasUserMessage("yyyyMMdd", "입력")
					.hasLogMessage("형식오류", date);
		}

		static Stream<Arguments> provideInvalidDates() {
			return Stream.of(
					Arguments.of(
							"숫자 외에 다른 문자가 포함된 값",
							"십월 이십이일", "2026年 2月 2日", "2026 year"
					),
					Arguments.of(
							"날짜 형식이지만 다른 문자가 포함된 값",
							"2025년 12월 12일 화요일", "2026.1.1", "2026-01-01 (화)"
					),
					Arguments.of(
							"길이가 8이 아닌 숫자로만 이뤄진 값",
							"00000000", "1234567890", "200261212", "202601001"
					)
			);
		}

	}


	@Nested
	@DisplayName("기간 검증")
	class PeriodTest {

		@ParameterizedTest(name = "[{index}] {0}, start={0}, end={1}")
		@CsvSource({
				"시직일 < 종료일, 20260101, 20260131",
				"시작일 <= 종료일, 20260101, 20260101"
		})
		@DisplayName("가계부 거래내역 기간이 정상이면 검증에 통과한다.")
		void validatesPeriod_whenDateWithinPeriod(String description, String start, String end) {
			//when & then
			assertThatCode(() -> DateValidator.validatePeriod(start, end))
					.doesNotThrowAnyException();
		}


		@ParameterizedTest(name = "[{index}] start={0}, end={1}|")
		@CsvSource({
				"20260101, 20251231",
				"20260107, 20260101"
		})
		@DisplayName("가계부 거래내역 시작일이 종료일보다 미래면 예외가 발생한다.")
		void throwsException_whenStartDateIsAfterEndDate(String start, String end) {
			//when
			Throwable throwable = catchThrowable(() -> DateValidator.validatePeriod(start, end));

			//then
			BusinessExceptionAssert.assertThatBusinessException(throwable)
					.hasErrorCode(LEDGER_HISTORY_INPUT_CONFLICT)
					.hasUserMessage("시작일", "이전 날짜")
					.hasLogMessage("논리충돌", "시작일<=종료일");
		}

	}


	@Nested
	@DisplayName("거래내역 날짜 검증")
	class HistoryDateTest {

		@ParameterizedTest(name = "[{index}] date={0}")
		@MethodSource("provideValidDates")
		@DisplayName("가계부 내역 날짜 형식이 정상이면 검증 통과한다.")
		void validateHistoryDate_whenDateFormatSuccessfully(String date) {
			//when & then
			assertThatCode(() -> DateValidator.validateHistoryDate(date, "필드명"))
					.doesNotThrowAnyException();
		}


		@ParameterizedTest(name = "[{index}] date={0}")
		@NullAndEmptySource
		@DisplayName("가계부 내역 날짜가 없거나 비어있으면 예외가 발생한다.")
		void throwsException_whenTransactionDateIsMissing(String date) {
			//given
			String fieldName = "필드명";

			//when
			Throwable throwable = catchThrowable(() -> DateValidator.validateHistoryDate(date, fieldName));

			//then
			BusinessExceptionAssert.assertThatBusinessException(throwable)
					.hasErrorCode(LEDGER_HISTORY_INPUT_NULL)
					.hasUserMessage(fieldName, "입력")
					.hasLogMessage("거래내역", "필수값누락");
		}


		@ParameterizedTest(name = "[{index}] {0}")
		@ValueSource(strings = {"20200925", "20230601", "20251012", "20251231", "20260101"})
		@DisplayName("가계부 거래내역 날짜 형식이 올바르지 않으면 예외가 발생한다.")
		void throwsException_whenTransactionDateFormatIsInvalid(String date) {
			//given
			String fieldName = "필드명";

			//when
			Throwable throwable = catchThrowable(() -> DateValidator.validateHistoryDate(date, fieldName));

			//then
			BusinessExceptionAssert.assertThatBusinessException(throwable)
					.hasErrorCode(LEDGER_HISTORY_INPUT_FORMAT)
					.hasUserMessage(fieldName, "yyyyMMdd", "입력")
					.hasLogMessage("거래내역", "형식오류", date);
		}

	}

}