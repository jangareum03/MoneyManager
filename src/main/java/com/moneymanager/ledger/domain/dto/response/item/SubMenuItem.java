package com.moneymanager.ledger.domain.dto.response.item;

import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response.item<br>
 * 파일이름       : SubMenuItem<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 29<br>
 * 설명              : 가계부 하위 메뉴 정보를 담은 클래스
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
public class SubMenuItem {

    private final Map<String, List<CategoryItem>> subItems;

    private SubMenuItem(Map<String, List<CategoryItem>> subItems) {
        this.subItems = subItems;
    }

    public static SubMenuItem of() {
        return new SubMenuItem(Collections.emptyMap());
    }

    public static SubMenuItem of(Map<String, List<CategoryItem>> subItems) {
        return new SubMenuItem(subItems);
    }

}