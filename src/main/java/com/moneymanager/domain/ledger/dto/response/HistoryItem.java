package com.moneymanager.domain.ledger.dto.response;

import com.moneymanager.domain.ledger.dto.query.LedgerHistoryQuery;
import com.moneymanager.domain.ledger.enums.CategoryType;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto.response<br>
 * 파일이름       : HistoryItem<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 5<br>
 * 설명              : 가계부 내역 조회에 필요한 내역 1건 정보를 담은 객체
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
 * 		 	  <td>26. 4. 5</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class HistoryItem {
	private final String code;												//가계부 코드
	private final Long amount;											//가계부 금액
	private final String memo;												//가계부 메모
	private final CategoryType categoryType;					//카테고리 타입
	private final String categoryName;								//카테고리 이름

	private HistoryItem(String code, Long amount, String memo, CategoryType categoryType, String categoryName) {
		this.code = code;
		this.amount = amount;
		this.memo = memo;
		this.categoryType = categoryType;
		this.categoryName = categoryName;
	}

	public static HistoryItem from(LedgerHistoryQuery query) {
		return new HistoryItem(
				query.getCode(),
				query.getAmount(),
				query.getMemo(),
				CategoryType.fromCode(query.getCategoryCode()),
				query.getCategoryName()
		);
	}
}
