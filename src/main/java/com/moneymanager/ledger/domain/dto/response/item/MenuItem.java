package com.moneymanager.ledger.domain.dto.response.item;

import com.moneymanager.ledger.domain.enums.HistoryMenu;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response.item<br>
 * 파일이름       : MenuItem<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 29<br>
 * 설명              : 가계부 메뉴 정보를 담은 클래스
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
 * 		 	  <td>26. 8. 29</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class MenuItem {

    private final HistoryMenu type;
    private final SubMenuItem subMenus;
    private final boolean selected;

    public MenuItem(HistoryMenu type, SubMenuItem subMenus, HistoryMenu selected) {
        this.type = type;
        this.subMenus = subMenus;
        this.selected = (type == selected);
    }

    public static MenuItem from(HistoryMenu menu, SubMenuItem subMenus, HistoryMenu selected) {
        return new MenuItem(menu, subMenus, selected);
    }

}