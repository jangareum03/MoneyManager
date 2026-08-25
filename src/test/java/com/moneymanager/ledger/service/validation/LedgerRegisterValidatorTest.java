package com.moneymanager.ledger.service.validation;

import com.moneymanager.global.exception.exception.ApplicationException;
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
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.moneymanager.global.exception.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.validation<br>
 * 파일이름       : LedgerRegisterValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 18.<br>
 * 설명              : LedgerRegisterValidator 클래스 로직을 검증하는 단위 테스트 클래스
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
    private LedgerFieldValidator fieldValidator;

    @BeforeEach
    void setUp() {
        this.target = new LedgerRegisterValidator(fieldValidator);
    }

    @Nested
    class Valid {

        @Nested
        @DisplayName("성공")
        class Success {

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

            @Test
            @DisplayName("고정 여부가 REPEAT이면 고정 주기 검증 기능을 수행한다.")
            void validatesFixedCycle_whenIsFixedIsRepeat() {
                //given
                LedgerWriteRequest request = LedgerWriteRequestFixture
                        .builder()
                        .fixed(LedgerTestData.FIXED_REPEAT.getValue())
                        .fixCycle(LedgerTestData.MONTHLY_CYCLE.getValue())
                        .build();

                //when
                assertDoesNotThrow(
                        () -> target.validate(request)
                );

                //then
                verify(fieldValidator).validateFixCycle(any(), anyString());
            }

            @Test
            @DisplayName("고정 여부가 VARIABLE이면 고정 주기 검증 기능을 수행하지 않는다.")
            void doesNotValidateFixedCycle_whenIsFixedIsVariable() {
                //given
                LedgerWriteRequest request = LedgerWriteRequestFixture
                        .builder()
                        .fixed(LedgerTestData.DEFAULT_FIX.getValue())
                        .build();

                //when
                assertDoesNotThrow(
                        () -> target.validate(request)
                );

                //then
                verify(fieldValidator, never()).validateFixCycle(any(), any());
            }

            @ParameterizedTest
            @NullAndEmptySource
            @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
            @DisplayName("장소명과 기본주소가 null이거나 비어있으면 검증을 수행하지 않는다.")
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

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @ParameterizedTest
            @NullSource
            @DisplayName("요청 객체가 null이면 예외를 발생시킨다.")
            void throwsInternalException_whenRequestIsNull(LedgerWriteRequest request) {
                //when & then
                ApplicationExceptionAssert.assertThatApplicationException(
                                catchThrowable(() -> target.validate(request))
                        )
                        .hasErrorCode(REQUIRED_NOT_EXIST)
                        .hasWork("가계부 등록 요청 검증");
            }

            @Test
            @DisplayName("날짜 검증에 실패하면 그 후 동작은 수행하지 않는다.")
            void doesNotProceed_whenDateValidationFails() {
                //given
                LedgerWriteRequest request = LedgerWriteRequestFixture
                        .builder()
                        .date(null)
                        .build();

                doThrow(ApplicationException.class)
                        .when(fieldValidator)
                        .validateDate(eq(request.getDate()), any());

                //when
                assertThatThrownBy(() -> target.validate(request))
                        .isInstanceOf(ApplicationException.class);

                //then
                verify(fieldValidator, never()).validateCategory(eq(request.getCategoryCode()), anyString());
            }

            @Test
            @DisplayName("결제 유형 검증에 실패하면 선택필드 검증 기능은 수행하지 않는다.")
            void doesNotValidateOptionalFields_whenPaymentTypeValidationFails() {
                //given
                LedgerWriteRequest request = LedgerWriteRequestFixture
                        .builder()
                        .paymentType(null)
                        .build();

                doThrow(ApplicationException.class)
                        .when(fieldValidator)
                        .validatePaymentType(eq(request.getPaymentType()), any());

                //when
                assertThatThrownBy(() -> target.validate(request))
                        .isInstanceOf(ApplicationException.class);

                //then
                verify(fieldValidator, never()).validateImages(any());
            }

        }

    }

}