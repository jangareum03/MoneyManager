package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.CategoryType;
import lombok.Builder;
import lombok.Value;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : LedgerSummary<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 29<br>
 * 설명              : 가계부 요약 정보를 나타내는 클래스
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
 * 		 	  <td>25. 11. 29.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
@Builder
public class LedgerSummary {
	String id;										//가계부 번호
	CategoryType type;					//가계부 유형
	String category;							//카테고리 이름
	String memo;								//가계부 메모
	Long amount;								//가계부 금액

	//Ledger 엔티티를 변환하는 정적 메서드
	public static LedgerSummary from(Ledger ledger) {
		return LedgerSummary.builder()
				.id(ledger.getCode())
				.type(CategoryType.fromCode(ledger.getCategory()))
				.memo(ledger.getMemo())
				.amount(ledger.getAmount())
				.build();
	}
}