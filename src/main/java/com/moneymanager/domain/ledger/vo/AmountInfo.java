package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.ledger.enums.AmountType;
import lombok.Getter;
import lombok.Value;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : AmountInfo<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 29<br>
 * 설명              : 결제정보를 나타내는 클래스
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
 * 		 	  <td>25. 11. 29</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
@Value
public class AmountInfo {
	Long amount;
	AmountType type;

	public AmountInfo(long amount, AmountType type) {
		this.amount = amount;
		this.type = type;
	}
}
