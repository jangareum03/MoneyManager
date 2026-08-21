package com.moneymanager.ledger.service.validation;

import com.moneymanager.global.exception.code.CommonErrorCode;
import com.moneymanager.global.exception.exception.InternalException;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.CommonErrorCode.INVALID_FORMAT;
import static com.moneymanager.global.exception.code.CommonErrorCode.REQUIRED_NOT_EXIST;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Named.named;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.validation<br>
 * 파일이름       : LedgerRegisterValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 18.<br>
 * 설명              : LedgerRegisterValidatorTest 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 8. 18.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@DisplayName("가계부 등록 요청 검증할 때")
@ExtendWith(MockitoExtension.class)
class LedgerRegisterValidatorTest {

    private LedgerRegisterValidator target;

    @Mock
    private LedgerImageValidator imageValidator;

    @BeforeEach
    void setUp() {
        this.target = new LedgerRegisterValidator(imageValidator);
    }

    @Nested
    class ValidRequestValidation {

        @Test
        @DisplayName("필수정보가 유효하면 검증에 성공한다.")
        void validatesSuccessfully_whenOnlyRequiredFieldsAreGiven() {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder().build();

            //when
            assertDoesNotThrow(
                    () -> target.validate(request)
            );
        }

        @Test
        @DisplayName("선택정보가 유효하면 검증에 성공한다.")
        void validatesSuccessfully_whenOptionalFieldsAreGiven() {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.withPlace().build();

            //when
            assertDoesNotThrow(
                    () -> target.validate(request)
            );
        }

        @Test
        @DisplayName("이미지가 유효하면 검증에 성공한다.")
        void validatesSuccessfully_whenRequestContainsImage() {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.withImages(2).build();

            //when
            assertDoesNotThrow(
                    () -> target.validate(request)
            );
        }

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("장소명과 기본주소가 null이거나 비어있으면 검증에 성공한다.")
        void validatesPlace_whenNameAndAddressAreNullOrEmpty(String value) {
        	//given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .placeName(value)
                    .roadAddress(value)
                    .build();
        	
        	//when
            assertDoesNotThrow(
                    () -> target.validate(request)
            );
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("요청 객체가 null이면 예외를 발생시킨다.")
        void throwsInternalException_whenRequestIsNull(LedgerWriteRequest request) {
            //when & then
            ApplicationExceptionAssert.assertThatApplicationException(
                            catchThrowable(() -> target.validate(request))
                    ).isInstanceOf(InternalException.class)
                    .hasErrorCode(REQUIRED_NOT_EXIST)
                    .hasWork("가계부 등록 요청 검증")
                    .hasCauseMessage("요청객체 없음");
        }

    }


    @Nested
    class DateValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("거래날짜가 null이거나 비어있으면 예외가 발생한다.")
        void throwsValidationException_whenTransactionDateIsBlank(String date) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .date(date)
                    .build();

            //when & then
            ApplicationExceptionAssert.assertThatApplicationException(
                            catchThrowable(() -> target.validate(request))
                    ).isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
                    .hasWork("가계부 등록 요청 검증")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("date", date);
        }

        @ParameterizedTest
        @MethodSource("com.moneymanager.support.data.DateTestData#invalidDates")
        @DisplayName("거래날짜가 포맷이 잘못되면 예외가 발생한다.")
        void throwsValidationException_whenTransactionDateHasInvalidFormat(String date) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .date(date)
                    .build();

            //when & then
            ApplicationExceptionAssert.assertThatApplicationException(
                            catchThrowable(() -> target.validate(request))
                    ).isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.INVALID_FORMAT)
                    .hasWork("가계부 등록 요청 검증")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("date", date)
                    .hasOption("format", "yyyyMMdd (예: 20260101)");
        }

    }


    @Nested
    class CategoryValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("카테고리가 null이거나 비어있으면 예외가 발생한다.")
        void throwsValidationException_whenCategoryIsBlank(String category) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .categoryCode(category)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validate(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
                    .hasWork("가계부 등록 요청 검증")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("category", category);
        }

        @ParameterizedTest
        @MethodSource("invalidCategories")
        @DisplayName("카테고리 포맷이 잘못되면 예외가 발생한다.")
        void throwsValidationException_whenCategoryCodeHasInvalidFormat(String category) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .categoryCode(category)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validate(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.INVALID_FORMAT)
                    .hasWork("가계부 등록 요청 검증")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("category", category)
                    .hasOption("format", "6자리 숫자 (예: 123456)");
        }

        static Stream<Arguments> invalidCategories() {
            return Stream.of(
                    Arguments.of(
                            named("한글이 포함된 경우 (예: 1234한글)", "1234한글")
                    ),
                    Arguments.of(
                            named("영어가 포함된 경우 (예: en3456)", "en3456")
                    ),
                    Arguments.of(
                            named("특수문자가 포함된 경우 (예: 12345*)", "12345")
                    ),
                    Arguments.of(
                            named("한자가 포함된 경우 (예: 12氷水56)", "12氷水56")
                    ),
                    Arguments.of(
                            named("숫자만 5자리인 경우 (예: 12345)", "12345")
                    ),
                    Arguments.of(
                            named("숫자만 7자리인 경우 (예: 1234567)", "1234567")
                    )
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"000000", "030101", "040101"})
        @DisplayName("카테고리가 허용하지 않은 값이면 예외를 발생시킨다.")
        void throwsValidationException_whenCategoryCodeIsInvalid(String category) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .categoryCode(category)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validate(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.INVALID_VALUE)
                    .hasWork("가계부 등록 요청 검증")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("category", category);
        }

    }


    @Nested
    class AmountValidation {

        @ParameterizedTest
        @NullSource
        @DisplayName("금액이 null이면 예외가 발생한다.")
        void throwsValidationException_whenAmountIsNull(Long amount) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .amount(amount)
                    .build();

            //when & then
            ApplicationExceptionAssert.assertThatApplicationException(
                            catchThrowable(() -> target.validate(request))
                    ).isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
                    .hasWork("가계부 등록 요청 검증")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("amount", amount);
        }

        @ParameterizedTest
        @ValueSource(longs = {-10000, -1, 0})
        @DisplayName("금액이 0이하면 예외를 발생시킨다.")
        void throwsException_whenAmountIsZeroOrNegative(Long amount) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .amount(amount)
                    .build();

            //when & then
            ApplicationExceptionAssert.assertThatApplicationException(
                            catchThrowable(() -> target.validate(request))
                    ).isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
                    .hasWork("가계부 등록 요청 검증")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("amount", amount);
        }

    }


    @Nested
    class PaymentTypeValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("금액 유형이 null이거나 비어있으면 예외가 발생한다.")
        void throwsValidationException_whenPaymentTypeIsBlank(String paymentType) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .paymentType(paymentType)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validate(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
                    .hasWork("가계부 등록 요청 검증")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("paymentType", paymentType);
        }

    }


    @Nested
    class FixValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("고정 주기가 null이거나 비어있으면 예외가 발생한다.")
        void throwsValidationException_whenFixedIsBlank(String fix) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .fixed(fix)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validate(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
                    .hasWork("가계부 등록 요청 검증")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("fix", fix);
        }

        @ParameterizedTest
        @ValueSource(strings = {"yes", "no", "p"})
        @DisplayName("금액 유형이 허용하지 않은 값이면 예외를 발생시킨다.")
        void throwsValidationException_whenFixedIsInvalid(String fix) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .fixed(fix)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validate(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.INVALID_VALUE)
                    .hasWork("가계부 등록 요청 검증")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("fix", fix)
                    .hasOption("allowed", "y, n");
        }

    }


    @Nested
    class FixCycleValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("고정 주기가 REPEAT이고 고정 유형이 null이거나 비어있으면 예외가 발생한다.")
        void throwsValidationException_whenFixCycleIsBlank(String fixCycle) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .fixed("y")
                    .fixCycle(fixCycle)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validate(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
                    .hasWork("가계부 등록 요청 검증")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("fixCycle", fixCycle);
        }

        @ParameterizedTest
        @ValueSource(strings = {"month", "q", "w2"})
        @DisplayName("고정 주기가 허용하지 않은 값이면 예외를 발생시킨다.")
        void throwsValidationException_whenFixCycleIsInvalid(String fixCycle) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .fixed("y")
                    .fixCycle(fixCycle)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validate(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.INVALID_VALUE)
                    .hasWork("가계부 등록 요청 검증")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("fixCycle", fixCycle)
                    .hasOption("allowed", "y, m, w");
        }

    }


    @Nested
    class ImagesValidation {

        @Test
        @DisplayName("이미지 리스트가 null이면 이미지 검증은 동작하지 않는다.")
        void doesNothing_whenImageListIsNull() {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .images(null)
                    .build();

            //when
            assertDoesNotThrow(() -> target.validate(request));
        }

        @Test
        @DisplayName("이미지 리스트가 비어있으면 이미지 검증은 동작하지 않는다.")
        void doesNothing_whenImageListIsEmpty() {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .images(Collections.emptyList())
                    .build();

            //when
            assertDoesNotThrow(() -> target.validate(request));
        }

        @Test
        @DisplayName("이미지 리스트 개수만큼 이미지 검증을 수행한다.")
        void validatesImageForEach_whenImageListIsGiven() {
            //given:
            LedgerWriteRequest request = LedgerWriteRequestFixture.withImages(2).build();

            //when
            target.validate(request);

            //then
            verify(imageValidator, times(2)).validate(any(MultipartFile.class));
        }

    }


    @Nested
    class PlaceValidation {

        @ParameterizedTest
        @MethodSource("invalidPlaceNames")
        @DisplayName("유효하지 않은 장소명이면 예외를 발생시킨다.")
        void throwsValidationException_whenPlaceNameContainsInvalidCharacters(String placeName) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .placeName(placeName)
                    .roadAddress(LedgerTestData.ROAD_ADDRESS)
                    .detailAddress(LedgerTestData.DETAIL_ADDRESS)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validate(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_FORMAT)
                    .hasWork("가계부 등록 요청 검증")
                    .hasCauseMessage("형식 불일치")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("placeName", placeName)
                    .hasOption("format", "한글, 영문, 숫자, 공백, 괄호, 하이픈, 점");
        }

        private static Stream<Arguments> invalidPlaceNames() {
            return Stream.of(
                    Arguments.of(named("한자가 포함된 장소", "김家네")),
                    Arguments.of(named("특수문자가 포함된 장소", "🍒야호★")),
                    Arguments.of(named("허용되지 않은 문자가 포함된 장소", "아파트, ***호"))
            );
        }

        @ParameterizedTest
        @MethodSource("invalidRoadAddress")
        @DisplayName("유효하지 않은 도로명 주소면 예외를 발생시킨다.")
        void throwsValidationException_whenRoadAddressContainsInvalidCharacters(String roadAddress) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .placeName(LedgerTestData.PLACE_NAME)
                    .roadAddress(roadAddress)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validate(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_FORMAT)
                    .hasWork("가계부 등록 요청 검증")
                    .hasCauseMessage("형식 불일치")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("roadAddress", roadAddress)
                    .hasOption("format", "한글, 영문, 숫자, 공백, 하이픈");
        }

        private static Stream<Arguments> invalidRoadAddress() {
            return Stream.of(
                    Arguments.of(named("한자가 포함된 주소", "김家네")),
                    Arguments.of(named("특수문자가 포함된 주소", "🍒야호★")),
                    Arguments.of(named("허용되지 않은 문자가 포함된 주소", "아파트, ***호"))
            );
        }

        @ParameterizedTest
        @MethodSource("invalidDetailAddressFormats")
        @DisplayName("유효하지 않은 상세주소면 예외를 발생시킨다.")
        void throwsValidationException_whenDetailAddressContainsInvalidCharacters(String detailAddress) {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
                    .placeName(LedgerTestData.PLACE_NAME)
                    .roadAddress(LedgerTestData.ROAD_ADDRESS)
                    .detailAddress(detailAddress)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validate(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_FORMAT)
                    .hasWork("가계부 등록 요청 검증")
                    .hasCauseMessage("형식 불일치")
                    .hasTarget(LedgerWriteRequest.class)
                    .hasValue("detailAddress", detailAddress)
                    .hasOption("format", "한글, 영문, 숫자, 공백, 하이픈, 괄호, 쉼표, 슬래시, 점, #");
        }

        private static Stream<Arguments> invalidDetailAddressFormats() {
            return Stream.of(
                    Arguments.of(named("한자가 포함된 주소", "김家네")),
                    Arguments.of(named("특수문자가 포함된 주소", "🍒야호★")),
                    Arguments.of(named("허용되지 않은 문자가 포함된 주소", "아파트, ***호"))
            );
        }

    }

}