package com.moneymanager.ledger.domain.dto.response.edit;

import com.moneymanager.ledger.domain.dto.response.item.CategoryItem;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto.response<br>
 * 파일이름       : CategoryOptions<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 5. 2<br>
 * 설명              : 가계부 수정에 필요한 카테고리 정보를 담은 데이터 클래스
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
 * 		 	  <td>26. 5. 2</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class CategoryOptions {
	private final List<String> selected;
	private final List<CategoryItem> middleOptions;
	private final List<CategoryItem> lowOptions;

	private CategoryOptions(List<String> selected, List<CategoryItem> middleOptions, List<CategoryItem> lowOptions) {
		this.selected = selected;
		this.middleOptions = middleOptions;
		this.lowOptions = lowOptions;
	}

	public static CategoryOptions of(List<String> selected, List<CategoryItem> middleOptions, List<CategoryItem> lowOptions) {
		return new CategoryOptions(selected, middleOptions, lowOptions);
	}

}