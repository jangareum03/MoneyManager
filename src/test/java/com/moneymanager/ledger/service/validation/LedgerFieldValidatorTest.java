package com.moneymanager.ledger.service.validation;

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

import java.util.List;
import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Named.named;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.validation<br>
 * 파일이름       : LedgerFieldValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 22<br>
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
 * 		 	  <td>26. 8. 22</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class LedgerFieldValidatorTest {

    private final String work = "작업 중..zz";

    private LedgerFieldValidator target;

    @Mock
    private LedgerImageValidator imageValidator;

    @BeforeEach
    void setUp() {
        target = new LedgerFieldValidator(imageValidator);
    }


    @Nested
    @DisplayName("날짜 검증할 때")
    class DateValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
        void throwsValidationException_whenDateIsNullOrBlank(String date) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateDate(date, work));

            // then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasWork(work)
                    .hasField("date")
                    .hasValue(date);
        }

        @ParameterizedTest
        @MethodSource("com.moneymanager.support.data.DateTestData#invalidDates")
        @DisplayName("포맷이 잘못되면 예외를 발생시킨다.")
        void throwsValidationException_whenDateHasInvalidFormat(String date) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateDate(date, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_FORMAT)
                    .hasWork(work)
                    .hasField("date")
                    .hasValue(date)
                    .hasOption("format", "yyyyMMdd (예: 20260101)");
        }
    }


    @Nested
    @DisplayName("카테고리 코드 검증할 때")
    class CategoryValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
        void throwsValidationException_whenCategoryIsNullOrBlank(String category) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateCategory(category, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasField("category")
                    .hasValue(category);
        }

        @ParameterizedTest
        @MethodSource("invalidCategories")
        @DisplayName("잘못된 포맷이면 예외를 발생시킨다.")
        void throwsValidationException_whenCategoryCodeHasInvalidFormat(String category) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateCategory(category, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_FORMAT)
                    .hasField("category")
                    .hasValue(category)
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
        @DisplayName("유효하지 않으면 예외를 발생시킨다.")
        void throwsValidationException_whenCategoryCodeIsInvalid(String category) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateCategory(category, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_VALUE)
                    .hasField("category")
                    .hasValue(category);
        }

    }


    @Nested
    @DisplayName("금액을 검증할 때")
    class AmountValidation {

        @ParameterizedTest
        @NullSource
        @DisplayName("null이면 예외를 발생시킨다.")
        void throwsValidationException_whenAmountIsNull(Long amount) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateAmount(amount, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasField("amount")
                    .hasValue(amount);
        }

        @ParameterizedTest
        @ValueSource(longs = {-10000, -1, 0})
        @DisplayName("0이하면 예외를 발생시킨다.")
        void throwsException_whenAmountIsZeroOrNegative(Long amount) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateAmount(amount, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasField("amount")
                    .hasValue(amount);
        }

    }


    @Nested
    @DisplayName("금액 유형을 검증할 때")
    class PaymentTypeValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
        void throwsValidationException_whenPaymentTypeIsBlank(String paymentType) {
            //when
            Throwable throwable = catchThrowable(() -> target.validatePaymentType(paymentType, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasField("paymentType")
                    .hasValue(paymentType);
        }

    }


    @Nested
    @DisplayName("고정 여부를 검증할 때")
    class FixValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
        void throwsValidationException_whenFixedIsBlank(String fix) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateFix(fix, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasField("fix")
                    .hasValue(fix);
        }

        @ParameterizedTest
        @ValueSource(strings = {"yes", "no", "p"})
        @DisplayName("유효하지 않으면 예외를 발생시킨다.")
        void throwsValidationException_whenFixedIsInvalid(String fix) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateFix(fix, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_VALUE)
                    .hasField("fix")
                    .hasValue(fix)
                    .hasOption("allowed", "y, n");
        }

    }


    @Nested
    @DisplayName("고정 주기를 검증할 때")
    class FixCycleValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("REPEAT이고 고정 주기가 null이거나 비어있으면 예외를 발생시킨다.")
        void throwsValidationException_whenFixCycleIsBlank(String fixCycle) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateFixCycle(fixCycle, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasField("fixCycle")
                    .hasValue(fixCycle);
        }

        @ParameterizedTest
        @ValueSource(strings = {"month", "q", "w2"})
        @DisplayName("유효하지 않으면 예외를 발생시킨다.")
        void throwsValidationException_whenFixCycleIsInvalid(String fixCycle) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateFixCycle(fixCycle, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_VALUE)
                    .hasField("fixCycle")
                    .hasValue(fixCycle)
                    .hasOption("allowed", "y, m, w");
        }

    }


    @Nested
    @DisplayName("이미지 검증할 때")
    class ImagesValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("리스트가 null이거나 비어있으면 이미지 검증은 동작하지 않는다.")
        void doesNothing_whenImageListIsNull(List<MultipartFile> imageList) {
            assertDoesNotThrow(() -> target.validateImages(imageList));
        }

        @Test
        @DisplayName("리스트 개수만큼 이미지 검증을 수행한다.")
        void validatesImageForEach_whenImageListIsGiven() {
            //given:
            LedgerWriteRequest request = LedgerWriteRequestFixture.withImages(2).build();

            //when
            target.validateImages(request.getImages());

            //then
            verify(imageValidator, times(2)).validate(any(MultipartFile.class));
        }

    }


    @Nested
    @DisplayName("장소 정보를 검증할 때")
    class PlaceValidation {

        private final String name = LedgerTestData.PLACE_NAME;
        private final String road = LedgerTestData.ROAD_ADDRESS;
        private final String detail = LedgerTestData.DETAIL_ADDRESS;

        @ParameterizedTest
        @MethodSource("invalidPlaceNames")
        @DisplayName("유효하지 않은 장소명이면 예외를 발생시킨다.")
        void throwsValidationException_whenPlaceNameContainsInvalidCharacters(String placeName) {
            //when
            Throwable throwable = catchThrowable(() -> target.validatePlace(placeName, road, detail, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_FORMAT)
                    .hasCauseMessage("형식 불일치")
                    .hasField("placeName")
                    .hasValue(placeName)
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
            //when
            Throwable throwable = catchThrowable(() -> target.validatePlace(name, roadAddress, detail, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_FORMAT)
                    .hasCauseMessage("형식 불일치")
                    .hasField("roadAddress")
                    .hasValue(roadAddress)
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
            //when
            Throwable throwable = catchThrowable(() -> target.validatePlace(name, detail, detailAddress, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_FORMAT)
                    .hasCauseMessage("형식 불일치")
                    .hasField("detailAddress")
                    .hasValue(detailAddress)
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