package com.moneymanager.domain.ledger.dto.query;

import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.CategoryType;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto.query<br>
 * 파일이름       : LedgerHistoryQuery<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 4<br>
 * 설명              : 데이터베이스에서 가계부 내역 조회 결과를 담기 위한 데이터 클래스
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
 * 		 	  <td>26. 4. 4</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LedgerHistoryQuery {
	private final String code;												//가계부 코드
	private final String date;												//가계부 거래 날짜
	private final Long amount;											//가계부 금액
	private final String memo;												//가계부 메모
	private final CategoryType categoryType;					//카테고리 타입
	private final String categoryName;								//카테고리 이름

	public LedgerHistoryQuery(Ledger ledger, Category category) {
		this.code = ledger.getCode();
		this.date = ledger.getDate();
		this.amount = ledger.getAmount();
		this.memo = ledger.getMemo();

		this.categoryType = CategoryType.fromCategoryCode(category.getCode());
		this.categoryName = category.getName();
	}
}
