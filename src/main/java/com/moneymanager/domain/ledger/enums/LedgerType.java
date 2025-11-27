package com.moneymanager.domain.ledger.enums;


import com.moneymanager.exception.ErrorCode;
import lombok.Getter;

import static com.moneymanager.exception.ErrorUtil.createClientException;


/**
 * <p>
 *  * 패키지이름    : com.moneymanager.domain.ledger.enums<br>
 *  * 파일이름       : LedgerType<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 7. 15<br>
 *  * 설명              : 가계부 유형을 정의한 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>22. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Getter
public enum LedgerType {

	INCOME("income", "01"), OUTLAY("outlay", "02");

	private final String type;
	private final String dbValue;

	LedgerType(String type, String code) {
		this.type = type;
		this.dbValue = code;
	}


	/**
	 * 가계부 카테고리 코드의 앞 2글자와 일치하는 유형을 반환합니다. <br>
	 * 일치하는 유형이 없으면 {@link com.moneymanager.exception.custom.ClientException} 이 발생합니다.
	 *
	 * @param code		유형을 확인할 가계부 카테고리 코드
	 * @return	가계부 유형 정보를 담은 {@link LedgerType} 객체
	 */
	public static LedgerType from(String code) {
		for( LedgerType type : LedgerType.values() ) {
			if( code.startsWith(type.getDbValue()) ) {
				return type;
			}
		}

		throw createClientException(ErrorCode.LEDGER_TYPE_INVALID, "가계부 유형을 확인해주세요.", code);
	}

}
