package com.moneymanager.ledger.domain.dto.vo;

import com.moneymanager.global.exception.code.CommonErrorCode;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.support.ApplicationExceptionAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.vo<br>
 * 파일이름       : MoneyTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 19<br>
 * 설명              : Money 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 8. 19</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@DisplayName("Money 객체를 생성할 때")
class MoneyTest {

    @Nested
    @DisplayName("성공")
    class Success {
        
        @ParameterizedTest
        @ValueSource(longs = {1L, 1000L, 30000L, 500000L, 100000000L})
        @DisplayName("금액이 1이상이면 생성한다.")
        void returnsMoney_whenAmountIsGreaterThanOrEqualToOne(Long amount) {
        	//when
            Money result = Money.of(amount, PaymentType.BANK.name());
        	
        	//then
        	assertThat(result.getAmount()).isEqualTo(amount);
        }
        
        @ParameterizedTest
        @EnumSource(PaymentType.class)
        @DisplayName("유효한 금액유형이면 생성한다.")
        void returnsMoney_whenAmountTypeIsValid(PaymentType paymentType) {
        	//when
            Money result = Money.of(10000L, paymentType.name());
        	
        	//then
        	assertThat(result.getPaymentType()).isSameAs(paymentType);
        }
        
    }

    @Nested
    @DisplayName("실패")
    class Failure {

        @ParameterizedTest
        @NullSource
        @DisplayName("금액유형이 null이거나 비어있으면 예외를 발생시킨다.")
        void throwsValidationException_whenAmountIsNull(Long amount) {
            //when
            Throwable throwable = catchThrowable(() -> Money.of(amount, PaymentType.BANK.name()));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
                    .hasWork("Money 생성")
                    .hasCauseMessage("필수값 누락")
                    .hasValue("amount", amount);
        }
        
        @ParameterizedTest
        @ValueSource(longs = {-100000L, -1L, 0L})
        @DisplayName("금액이 0이하면 예외를 발생시킨다.")
        void throwsException_whenAmountIsLessThanOrEqualToZero(Long amount) {
        	//when
        	Throwable throwable = catchThrowable(() -> Money.of(amount, PaymentType.NONE.name()));

        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
                    .hasWork("Money 생성")
                    .hasCauseMessage("필수값 누락")
                    .hasValue("amount", amount);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("금액유형이 null이면 예외를 발생시킨다.")
        void throwsValidationException_whenPaymentTypeIsBlank(String paymentType) {
            //when
            Throwable throwable = catchThrowable(() -> Money.of(10000L, paymentType));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(CommonErrorCode.REQUIRED_VALUE)
                    .hasWork("Money 생성")
                    .hasCauseMessage("필수값 누락")
                    .hasValue("paymentType", paymentType);
        }

    }

}