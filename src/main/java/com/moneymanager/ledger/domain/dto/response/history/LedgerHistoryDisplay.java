package com.moneymanager.ledger.domain.dto.response.history;

import com.moneymanager.ledger.domain.dto.response.item.HistoryItem;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response.history<br>
 * 파일이름       : LedgerHistoryDisplay<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 28<br>
 * 설명              : 가계부 내역을 화면 표시용 데이터를 담은 객체
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
 * 		 	  <td>26. 8. 28</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LedgerHistoryDisplay {

    private final String date;
    private final List<List<HistoryItem>> rows;

    private LedgerHistoryDisplay(String date, List<List<HistoryItem>> rows) {
        this.date = date;
        this.rows = rows;
    }

    public static LedgerHistoryDisplay of(String date, List<List<HistoryItem>> rows) {
        return new LedgerHistoryDisplay(date, rows);
    }

}