package com.moneymanager.ledger.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.domain.ledger.enums<br>
 * 파일이름       : PaymentTypeTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 1<br>
 * 설명              : PaymentType 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 6. 1</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class PaymentTypeTest {

	@Nested
	@DisplayName("PaymentType 변환할 때")
	class FromTest {

		@Nested
		@DisplayName("성공")
		class Success {

			@ParameterizedTest
			@MethodSource("validPaymentTypes")
			@DisplayName("대문자 값이면 변환한다.")
			void createsPaymentType_whenUpperCaseIsGiven(String value, PaymentType expected) {
				//when: 대문자로 PaymentType으로 변환한다.
				PaymentType result = PaymentType.from(value.toUpperCase());
				
				//then: PaymentType으로 변환된다.
				assertThat(result).isSameAs(expected);
			}

			@ParameterizedTest
			@MethodSource("validPaymentTypes")
			@DisplayName("소문자 값이면 변환한다.")
			void createsPaymentType_whenLowerCaseIsValid(String value, PaymentType expected) {
				//when: 소문자로 PaymentType으로 변환한다.
				PaymentType result = PaymentType.from(value.toLowerCase());

				//then: PaymentType으로 변환된다.
				assertThat(result).isSameAs(expected);
			}

			@ParameterizedTest
			@MethodSource("validPaymentTypes")
			@DisplayName("혼합 대소문자 값이면 변환한다.")
			void createsPaymentType_whenMixedCaseIsValid(String value, PaymentType expected) {
				//when: 혼합 대소문자로 PaymentType으로 변환한다.
				PaymentType result = PaymentType.from(value);

				//then: PaymentType으로 변환된다.
				assertThat(result).isSameAs(expected);
			}
			
			static Stream<Arguments> validPaymentTypes() {
			    return Stream.of(
			        Arguments.of(
							named("none인 경우", "None"),
							PaymentType.NONE
					),
					Arguments.of(
							named("cash인 경우", "cASh"),
							PaymentType.CASH
					),
					Arguments.of(
							named("card인 경우", "carD"),
							PaymentType.CARD
					),
					Arguments.of(
							named("bank인 경우", "BanK"),
							PaymentType.BANK
					)
			    );
			}

		}


		@Nested
		@DisplayName("실패")
		class Failure {

			@ParameterizedTest
			@ValueSource(strings = {"TYPE", "b", "cash1"})
			@DisplayName("유효하지 않은 금액 유형이면 예외를 발생시킨다.")
			void throwsNoSuchElementException_whenCategoryCodeIsInvalid(String value) {
				//when
				assertThatThrownBy(() -> PaymentType.from(value))
						;
			}

		}

	}

}
