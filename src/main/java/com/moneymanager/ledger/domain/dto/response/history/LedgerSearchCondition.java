package com.moneymanager.ledger.domain.dto.response.history;

import com.moneymanager.ledger.domain.enums.HistoryMenu;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response.history<br>
 * 파일이름       : LedgerSearchCondition<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 30<br>
 * 설명              : 가계부 내역 검색을 위한 데이터를 담은 객체
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
 * 		 	  <td>26. 8. 30</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LedgerSearchCondition {

    private final HistoryMenu menu;
    private final String keyword;
    private final List<String> keywords;

    private LedgerSearchCondition(HistoryMenu menu, String keyword, List<String> keywords) {
        this.menu = menu;
        this.keyword = keyword;
        this.keywords = keywords;
    }

    public static LedgerSearchCondition of(HistoryMenu menu) {
        return new LedgerSearchCondition(menu, null, null);
    }

    public static LedgerSearchCondition ofKeyword(HistoryMenu menu, String keyword) {
        return new LedgerSearchCondition(menu, keyword, null);
    }

    public static LedgerSearchCondition ofKeywords(HistoryMenu menu, List<String> keywords) {
        return new LedgerSearchCondition(menu, null, keywords);
    }

}