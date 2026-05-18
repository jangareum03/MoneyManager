package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.exception.BusinessException;
import lombok.Getter;
import lombok.Value;

import static com.moneymanager.exception.error.ErrorCode.LEDGER_INPUT_INVALID;
import static com.moneymanager.exception.error.ErrorCode.LEDGER_INPUT_RANGE;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : Money<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 5. 15<br>
 * 설명              : 가계부 금액을 나타내는 클래스
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
 * 		 	  <td>26. 5. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
@Getter
public class Money {

	Long amount;
	PaymentType paymentType;

	public Money(Long amount, String paymentType) {
		validateAmount(amount);
		validatePaymentType(paymentType);

		this.amount = amount;
		this.paymentType = PaymentType.of(paymentType);
	}

	private void validateAmount(Long amount) {
		if(amount < 1) {
			throw BusinessException.of(
					LEDGER_INPUT_RANGE,
					"가계부 검증 실패   |   reason=범위오류   |   field=amount   |   min=1   |   value="+amount
			).withUserMessage("금액은 1 이상 입력해주세요.");
		}
	}

	private void validatePaymentType(String type) {
		try{
			PaymentType.of(type);
		}catch (IllegalArgumentException e) {
			throw BusinessException.of(LEDGER_INPUT_INVALID,"가게부 검증 실패   |   " + e.getMessage())
					.withUserMessage("사용할 수 없는 금액유형 입니다.")
					.withCause(e);
		}
	}

}