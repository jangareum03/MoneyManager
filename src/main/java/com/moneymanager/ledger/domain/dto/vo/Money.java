package com.moneymanager.ledger.domain.dto.vo;

import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.log.DeveloperLogInfo;
import lombok.Value;

import static com.moneymanager.global.exception.code.CommonErrorCode.INVALID_VALUE;

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
public class Money {

	Long amount;
	PaymentType paymentType;

	private Money(Long amount, PaymentType paymentType) {
		validateAmount(amount);

		this.amount = amount;
		this.paymentType = paymentType;
	}

	public static Money of(Long amount, PaymentType paymentType) {
		return new Money(amount, paymentType);
	}

	private void validateAmount(Long amount) {
		if(amount < 1) {
			throw ValidationException.of(
					INVALID_VALUE,
					DeveloperLogInfo.builder()
							.work("가계부 금액 검증")
							.cause("범위 오류")
							.field("amount")
							.value(String.valueOf(amount))
							.build()
							.addOption("min", 1)
			);
		}
	}

}