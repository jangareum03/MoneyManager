package com.moneymanager.ledger.service.validation;

import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.global.exception.code.CommonErrorCode;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.fixture.request.LedgerUpdateRequestFixture;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service<br>
 * 파일이름       : LedgerValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 21<br>
 * 설명              : LedgerValidator 클래스 로직을 검증하는 단위 테스트 클래스
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
public class LedgerValidatorTest {

	private LedgerValidator target;

	@BeforeEach
	void setUp() {
		target = new LedgerValidator();
	}

	@Nested
	@DisplayName("가계부 등록 검증")
	class RegisterTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("정상적인 가계부 등록 요청이면 예외가 발생하지 않는다.")
			void validatesLedgerWriteData_whenRequestIsValid() {
				//given: 검증할 가계부 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.withImages(3)
						.fixed(LedgerTestData.FIX_Y)
						.fixCycle(LedgerTestData.FIX_CYCLE)
						.memo("메모")
						.placeName(LedgerTestData.PLACE_NAME)
						.roadAddress(LedgerTestData.ROAD_ADDRESS)
						.detailAddress(LedgerTestData.DETAIL_ADDRESS)
						.build();
				
				//when & then: 등록 요청 데이터를 검증하면 예외가 발생하지 않는다.
				assertDoesNotThrow(() -> target.register(request));
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {
			
			@Test
			@DisplayName("가계부 등록 요청이 null이면 예외가 발생한다.")
			void throwsException_whenRequestIsNull() {
				//when & then: 등록 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
						catchThrowable(() -> target.register(null))
				)
						.isInstanceOf(ValidationException.class)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("요청 객체 없음")
						.hasTarget(LedgerWriteRequest.class)
						.hasValue(null)
						.hasUserMessage("가계부", "등록");
			}
			
			@Test
			@DisplayName("카테고리가 null이면 예외가 발생한다.")
			void throwsException_whenCategoryIsNull() {
				//given: 카테고리가 null인 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
								.categoryCode(null)
										.build();

				//when & then: 등록 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.register(request))
						)
						.isInstanceOf(ValidationException.class)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("카테고리 없음")
						.hasField("category")
						.hasValue(request.getCategoryCode())
						.hasUserMessage("카테고리", "선택");
			}
			
			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("카테고리가 빈 문자열이면 예외가 발생한다.")
			void throwsException_whenCategoryIsEmpty(String category) {
				//given: 카테고리가 빈 문자열인 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.categoryCode(category)
						.build();

				//when & then: 등록 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.register(request))
						)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("카테고리 없음")
						.hasField("category")
						.hasValue(category)
						.hasUserMessage("카테고리", "선택");
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.ledger.service.validation.LedgerValidatorTest#invalidCategoryCodes")
			@DisplayName("카테고리가 6자리 숫자 형식이 아니면 예외가 발생한다.")
			void throwsException_whenCategoryFormatIsInvalid(String category) {
				//given: 6자리 숫자가 아닌 카테고리가 포함된 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.categoryCode(category)
						.build();

				//when & then: 등록 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.register(request))
						)
						.isInstanceOf(ValidationException.class)
						.hasErrorCode(CommonErrorCode.INVALID_FORMAT)
						.hasWork("가계부 검증")
						.hasCauseMessage("카테고리 형식 불일치")
						.hasField("category")
						.hasValue(category)
						.hasUserMessage("허용", "않은 카테고리");
			}
			
			@Test
			@DisplayName("금액이 null이면 예외가 발생한다.")
			void throwsException_whenAmountIsNull() {
				//given: 금액이 null인 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.amount(null)
						.build();

				//when & then: 등록 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.register(request))
						)
						.isInstanceOf(ValidationException.class)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("금액 없음")
						.hasField("amount")
						.hasValue(null)
						.hasUserMessage("금액", "입력");
			}
			
			@Test
			@DisplayName("금액 유형이 null이면 예외가 발생한다.")
			void throwsException_whenAmountTypeIsNull() {
				//given: 금액 유형이 null인 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.paymentType(null)
						.build();

				//when & then: 등록 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.register(request))
						)
						.isInstanceOf(ValidationException.class)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("금액 유형 없음")
						.hasField("paymentType")
						.hasValue(request.getPaymentType())
						.hasUserMessage("금액 유형", "선택");
			}
			
			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("금액 유형이 빈 문자열이면 예외가 발생한다.")
			void throwsException_whenAmountTypeIsEmpty(String type) {
				//given: 금액 유형이 빈 문자열인 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.paymentType(type)
						.build();

				//when & then: 등록 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.register(request))
						)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("금액 유형 없음")
						.hasField("paymentType")
						.hasValue(request.getPaymentType())
						.hasUserMessage("금액 유형", "선택");
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.ledger.service.validation.LedgerValidatorTest#invalidFixCycles")
			@DisplayName("고정주기가 영어가 아니면 예외가 발생한다.")
			void throwsException_whenFixCycleIsInvalid(String cycle) {
				//given: 영어가 아닌 고정주기가 포함된 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.fixCycle(cycle)
						.build();

				//when & then: 등록 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.register(request))
						)
						.isInstanceOf(ValidationException.class)
						.hasErrorCode(CommonErrorCode.INVALID_FORMAT)
						.hasWork("가계부 검증")
						.hasCauseMessage("고정주기 형식 불일치")
						.hasField("fixCycle")
						.hasValue(cycle)
						.hasUserMessage("고정주기", "선택");
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.ledger.service.validation.LedgerValidatorTest#invalidMemoLengths")
			@DisplayName("메모가 150자 초과하면 예외가 발생한다.")
			void throwsException_whenMemoExceedsLimit(String memo) {
				//given: 메모 길이가 150자 초과된 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.memo(memo)
						.build();

				//when & then: 등록 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.register(request))
						)
						.isInstanceOf(ValidationException.class)
						.hasErrorCode(CommonErrorCode.OUT_OF_RANGE)
						.hasWork("가계부 검증")
						.hasCauseMessage("메모 길이 초과")
						.hasField("memo")
						.hasValue(memo)
						.hasOption("min", "0")
						.hasOption("max", "150")
						.hasUserMessage("메모", "최대 150");
			}

		}

	}


	@Nested
	@DisplayName("가계부 수정 검증")
	class UpdateTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("정상적인 가계부 수정 요청이면 예외가 발생하지 않는다.")
			void validatesLedgerUpdateData_whenRequestIsValid() {
				//given: 검증할 가계부 수정 요청이 준비되어 있다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.withImages(3)
						.fixed(LedgerTestData.FIX_Y)
						.fixCycle(LedgerTestData.FIX_CYCLE)
						.memo("메모")
						.placeName(LedgerTestData.PLACE_NAME)
						.roadAddress(LedgerTestData.ROAD_ADDRESS)
						.detailAddress(LedgerTestData.DETAIL_ADDRESS)
						.build();

				//when & then: 등록 요청 데이터를 검증하면 예외가 발생하지 않는다.
				assertThatCode(() -> target.update(request))
						.doesNotThrowAnyException();
			}
			
		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("가계부 수정 요청이 null이면 예외가 발생한다.")
			void throwsException_whenRequestIsNull() {
				//when & then: 수정 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.update(null))
						)
						.isInstanceOf(ValidationException.class)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("요청 객체 없음")
						.hasTarget(LedgerUpdateRequest.class)
						.hasValue(null)
						.hasUserMessage("가계부", "수정");
			}

			@Test
			@DisplayName("카테고리가 null이면 예외가 발생한다.")
			void throwsException_whenCategoryIsNull() {
				//given: 카테고리가 null인 수정 요청이 준비되어 있다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
						.categoryCode(null)
						.build();

				//when & then: 등록 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.update(request))
						)
						.isInstanceOf(ValidationException.class)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("카테고리 없음")
						.hasField("category")
						.hasValue(request.getCategoryCode())
						.hasUserMessage("카테고리", "선택");
			}

			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("카테고리가 빈 문자열이면 예외가 발생한다.")
			void throwsException_whenCategoryIsEmpty(String category) {
				//given: 카테고리가 빈 문자열인 수정 요청이 준비되어 있다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
						.categoryCode(category)
						.build();

				//when & then: 수정 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.update(request))
						)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("카테고리 없음")
						.hasField("category")
						.hasValue(category)
						.hasUserMessage("카테고리", "선택");
			}

			@ParameterizedTest
			@MethodSource("com.moneymanager.ledger.service.validation.LedgerValidatorTest#invalidCategoryCodes")
			@DisplayName("카테고리가 6자리 숫자 형식이 아니면 예외가 발생한다.")
			void throwsException_whenCategoryFormatIsInvalid(String category) {
				//given: 6자리 숫자가 아닌 카테고리가 포함된 수정 요청이 준비되어 있다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
						.categoryCode(category)
						.build();

				//when & then: 수정 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.update(request))
						)
						.isInstanceOf(ValidationException.class)
						.hasErrorCode(CommonErrorCode.INVALID_FORMAT)
						.hasWork("가계부 검증")
						.hasCauseMessage("카테고리 형식 불일치")
						.hasField("category")
						.hasValue(category)
						.hasUserMessage("허용", "않은 카테고리");
			}

			@Test
			@DisplayName("금액이 null이면 예외가 발생한다.")
			void throwsException_whenAmountIsNull() {
				//given: 금액이 null인 수정 요청이 준비되어 있다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
						.amount(null)
						.build();

				//when & then: 수정 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.update(request))
						)
						.isInstanceOf(ValidationException.class)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("금액 없음")
						.hasField("amount")
						.hasValue(null)
						.hasUserMessage("금액", "입력");
			}

			@Test
			@DisplayName("금액 유형이 null이면 예외가 발생한다.")
			void throwsException_whenAmountTypeIsNull() {
				//given: 금액 유형이 null인 수정 요청이 준비되어 있다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
						.paymentType(null)
						.build();

				//when & then: 수정 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.update(request))
						)
						.isInstanceOf(ValidationException.class)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("금액 유형 없음")
						.hasField("paymentType")
						.hasValue(request.getPaymentType())
						.hasUserMessage("금액 유형", "선택");
			}

			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("금액 유형이 빈 문자열이면 예외가 발생한다.")
			void throwsException_whenAmountTypeIsEmpty(String type) {
				//given: 금액 유형이 빈 문자열인 수정 요청이 준비되어 있다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
						.paymentType(type)
						.build();

				//when & then: 수정 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.update(request))
						)
						.hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("금액 유형 없음")
						.hasField("paymentType")
						.hasValue(request.getPaymentType())
						.hasUserMessage("금액 유형", "선택");
			}

			@ParameterizedTest
			@MethodSource("com.moneymanager.ledger.service.validation.LedgerValidatorTest#invalidFixCycles")
			@DisplayName("고정주기가 영어가 아니면 예외가 발생한다.")
			void throwsException_whenFixCycleIsInvalid(String cycle) {
				//given: 영어가 아닌 고정주기가 포함된 수정 요청이 준비되어 있다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
						.fixCycle(cycle)
						.build();

				//when & then: 수정 요청 데이터를 검증하면 ValidationException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.update(request))
						)
						.isInstanceOf(ValidationException.class)
						.hasErrorCode(CommonErrorCode.INVALID_FORMAT)
						.hasWork("가계부 검증")
						.hasCauseMessage("고정주기 형식 불일치")
						.hasField("fixCycle")
						.hasValue(cycle)
						.hasUserMessage("고정주기", "선택");
			}

		}

	}


	static Stream<Arguments> invalidCategoryCodes() {
		return Stream.of(
				Arguments.of(named("숫자가 아닌 경우", "ab한글!!")),
				Arguments.of(named("숫자와 문자 조합인 경우", "123한글A")),
				Arguments.of(named("6글자가 아닌 경우", "1234567"))
		);
	}

	static Stream<Arguments> invalidFixCycles() {
		return Stream.of(
				Arguments.of(named("한글인 경우", "한들")),
				Arguments.of(named("숫자인 경우", "123")),
				Arguments.of(named("영어와 한글 조합인 경우", "w1"))
		);
	}

	static Stream<Arguments> invalidMemoLengths() {
		return Stream.of(
				Arguments.of(named("메모가 151자인 경우", "가".repeat(151))),
				Arguments.of(named("메모가 151자 초과한 경우", "가".repeat(152)))
		);
	}

}