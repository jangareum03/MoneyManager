package com.moneymanager.domain.ledger.enums;

import com.moneymanager.exception.ErrorCode;
import lombok.Getter;

import static com.moneymanager.exception.ErrorUtil.createClientException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.enums<br>
 * 파일이름       : PaymentType<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 12.<br>
 * 설명              : 가계부 결제 유형을 정의한 클래스
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
 * 		 	  <td>25. 11. 12.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public enum PaymentType {
	NONE("없음", "NONE"),
	CASH("현금", "CASH"),
	CARD("카드", "CARD"),
	BANK("계좌이체", "BANK");

	private final String displayName;
	private final String dbValue;

	PaymentType(String displayName, String dbValue) {
		this.displayName = displayName;
		this.dbValue = dbValue;
	}

	public static PaymentType fromDbValue(String dbValue) {
		for( PaymentType type : values() ) {
			if(type.dbValue.equalsIgnoreCase(dbValue)) return type;
		}

		throw createClientException(ErrorCode.LEDGER_PAYMENT_FORMAT, "지원하지 않은 결제유형입니다.", dbValue);
	}
}