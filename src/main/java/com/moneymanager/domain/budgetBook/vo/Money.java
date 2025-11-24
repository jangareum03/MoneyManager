package com.moneymanager.domain.budgetBook.vo;

import com.moneymanager.exception.ErrorCode;
import lombok.Getter;
import lombok.Value;

import static com.moneymanager.exception.ErrorUtil.createClientException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : Money<br>
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
 * 		 	  <td>25. 8. 31</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 11. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>
 * 		 	      [클래스 이름] MoneyVO → Money<br>
 * 		 	      [메서드 이름] validateAmount → validateMoney<br>
 * 		 	      [메서드 삭제] parseAmount, isDouble → 서비스 계층에서 사용
 * 		 	  </td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
@Getter
public class Money {

	int value;

	public Money(int value) {
		if( value <= 0 ) {	//금액이 0보다 작은 경우
			throw createClientException(ErrorCode.BUDGET_PRICE_INVALID, "금액은 0보다 크게 입력해야 합니다.", value);
		}

		this.value = value;
	}

}
