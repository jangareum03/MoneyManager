package com.moneymanager.ledger.domain.dto.response.history;

import com.moneymanager.ledger.domain.dto.response.item.HistoryItem;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response.history<br>
 * 파일이름       : HistoryGroup<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 28<br>
 * 설명              : 조회된 가계부 내역을 날짜별로 그룹화하여 담은 객체
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
 * 		 	  <td>26. 8. 28.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class HistoryGroup {

    private final LocalDate date;
    private final List<HistoryItem> items;

    private HistoryGroup(LocalDate date, List<HistoryItem> items) {
        this.date = date;
        this.items = items;
    }

    public static HistoryGroup of(LocalDate date, List<HistoryItem> items) {
        return new HistoryGroup(date, items);
    }

}