package com.moneymanager.domain.ledger.dto;

import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto<br>
 * 파일이름       : LedgerCategoryDto<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 20<br>
 * 설명              : LEDGER 테이블과 LEDGER_CATEGORY 테이블의 join 결과를 담기위한 클래스
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
 * 		 	  <td>25. 12. 20.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@AllArgsConstructor
@Getter
public class LedgerCategoryDto {
	private Ledger ledger;
	private Category category;
}
