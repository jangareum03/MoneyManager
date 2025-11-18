package com.moneymanager.domain.ledger.vo;

import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.ErrorUtil;
import lombok.Value;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : MoneyVO<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 31.<br>
 * 설명              : 금액 값을 나타내는 클래스
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
 * 		 	  <td>25. 8. 31.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
public class MoneyVO {

	int amount;

	public MoneyVO(String amount) {
		this.amount = parseAmount(amount);

		validateAmount();
	}


	/**
	 * 정수 금액({@code amount})을 검증합니다.
	 * <p>
	 *     다음과 같은 경우 {@link ClientException}예외가 발생합니다.
	 *     <ul>
	 *         <li>금액이 0이거나 0보다 작은 경우 → {@link ErrorUtil#createClientException(ErrorCode, String, Object)} 메서도 호출</li>
	 *     </ul>
	 * </p>
	 * @throws ClientException	금액이 0 이거나 음수인 경우 발생
	 */
	private void validateAmount() {
		if( amount <= 0 ) {
			throw ErrorUtil.createClientException(ErrorCode.COMMON_AMOUNT_INVALID, "금액은 0보다 큰 숫자를 입력해주세요.", amount);
		}
	}


	/**
	 * 문자열 금액({@code amount})을 정수로 변환합니다.
	 * <p>
	 *     다음과 같은 경우 {@link ClientException} 예외가 발생합니다.
	 *     <ul>
	 *         <li>금액이 {@code null} 또는 "" 경우 → {@link ErrorUtil#createClientException(ErrorCode, String)} 메서도 호출</li>
	 *         <li>금액이 숫자로 변환이 안되는 경우 → {@link ErrorUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 *     </ul>
	 * </p>
	 * @param amount	문자열로 입력된 금액
	 * @return	정수로 변환된 금액
	 * @throws ClientException	금액이 {@code null}이거나 숫자로 변환할 수 없는 경우 발생
	 */
	private int parseAmount(String amount) {
		if( amount == null || amount.isBlank() ) throw ErrorUtil.createClientException(ErrorCode.COMMON_AMOUNT_MISSING, "금액을 입력해주세요.");

		try{
			return Integer.parseInt(amount);
		}catch ( NumberFormatException e ) {
			if( isDouble(amount) ) {
				throw ErrorUtil.createClientException(ErrorCode.COMMON_AMOUNT_FORMAT, "금액은 1원 단위로 입력해주세요.", amount);
			}

			throw ErrorUtil.createClientException(ErrorCode.COMMON_AMOUNT_FORMAT, "금액은 숫자만 입력 가능합니다.", amount);
		}
	}


	/**
	 * 금액({@code amount})이 실수인지 확인합니다.
	 *
	 * @param amount	문자열로 입력된 금액
	 * @return	실수이면 true, 아니면 false
	 */
	private boolean isDouble(String amount) {
		try{
			Double.parseDouble(amount);

			return true;
		}catch ( NumberFormatException e ) {
			return false;
		}
	}

}
