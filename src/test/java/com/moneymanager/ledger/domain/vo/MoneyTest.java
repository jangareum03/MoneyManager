package com.moneymanager.ledger.domain.vo;

import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.moneymanager.exception.code.CommonErrorCode.INVALID_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.domain.ledger.vo<br>
 * 파일이름       : MoneyTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 2<br>
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
 * 		 	  <td>26. 6. 2</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class MoneyTest {

	@Nested
	@DisplayName("객체 생성")
	class CreateTest {

		@ParameterizedTest
		@EnumSource(PaymentType.class)
		@DisplayName("금액과 유형이 정상이면 Money객체를 생성한다.")
		void createsMoney_whenRequestIsValid(PaymentType type) {
			//given: 정상적인 금액이 준비되어 있다.
			Long amount = 25000L;

			//when: Money 객체를 생성한다.
			Money result = Money.of(amount, type);
			
			//then: 요청한 값대로 설정된다.
			assertThat(result.getAmount()).isEqualTo(amount);
			assertThat(result.getPaymentType()).isSameAs(type);
		}


		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest(name = "[{index}] 금액이 {0}")
			@ValueSource(longs = {0L, -1000L, -50000L})
			@DisplayName("금액이 0이하면 ValidationException이 발생한다.")
			void throwsException_whenAmountIsZeroOrNegative(Long amount) {
				//given: 임의의 금지유형을 설정한다.
				PaymentType type = PaymentType.CASH;
				
				//when: Money 객체를 생성한다.
				Throwable throwable = catchThrowable(() -> Money.of(amount, type));
				
				//then: 0이하에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(INVALID_VALUE)
						.hasWork("가계부 금액 검증")
						.hasCauseMessage("범위 오류")
						.hasField("amount")
						.hasValue(String.valueOf(amount))
						.hasOption("min", String.valueOf(1));
			}

		}

	}

}
