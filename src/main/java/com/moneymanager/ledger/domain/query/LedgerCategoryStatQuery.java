package com.moneymanager.ledger.domain.query;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.query<br>
 * 파일이름       : LedgerCategoryStatQuery<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 31<br>
 * 설명              : 데이터베이스에서 월간 가계부 카테고리별 통계 금액 조회 결과를 담기 위한 클래스
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
 * 		 	  <td>26. 8. 31</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LedgerCategoryStatQuery {

    private final String category;
    private final Long amount;

    private LedgerCategoryStatQuery(String category, Long amount) {
        this.category = category;
        this.amount = amount;
    }

    public static LedgerCategoryStatQuery of(String category, Long amount) {
        return new LedgerCategoryStatQuery(category, amount);
    }

}