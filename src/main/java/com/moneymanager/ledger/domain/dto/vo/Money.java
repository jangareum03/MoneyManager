package com.moneymanager.ledger.domain.dto.vo;

import com.moneymanager.global.exception.code.CommonErrorCode;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.ledger.domain.enums.PaymentType;
import lombok.Value;

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
		this.amount = amount;
		this.paymentType = paymentType;
	}

	public static Money of(Long amount, String type) {
		if(StringUtil.isNullOrBlank(type)) {
			throw ValidationException.of(
					CommonErrorCode.REQUIRED_VALUE,
					LogContent.of(
							"Money 생성",
							Money.class,
							"paymentType", type
					)
			);
		}

		return of(amount, PaymentType.from(type));
	}

	public static Money of(Long amount, PaymentType paymentType) {
		if(amount == null || amount <= 0) {
			throw ValidationException.of(
					CommonErrorCode.REQUIRED_VALUE,
					LogContent.of(
							"Money 생성",
							Money.class,
							"amount", amount
					)
			);
		}

		if(paymentType == null) {
			throw ValidationException.of(
					CommonErrorCode.REQUIRED_VALUE,
					LogContent.of(
							"Money 생성",
							Money.class,
							"paymentType", null
					)
			);
		}

		return new Money(amount, paymentType);
	}

}