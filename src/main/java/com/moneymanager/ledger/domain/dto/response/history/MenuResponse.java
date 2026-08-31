package com.moneymanager.ledger.domain.dto.response.history;

import com.moneymanager.ledger.domain.dto.response.item.MenuItem;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response.history<br>
 * 파일이름       : MenuResponse<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 29<br>
 * 설명              : 가계부 내역 메뉴 응답을 위한 클래스
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
 * 		 	  <td>26. 8. 29.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class MenuResponse {

    private final List<MenuItem> menus;

    private MenuResponse(List<MenuItem> menus) {
        this.menus = menus;
    }

    public static MenuResponse from(List<MenuItem> menus) {
        return new MenuResponse(menus);
    }

}