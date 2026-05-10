package com.moneymanager.domain.ledger.dto.response;

import com.moneymanager.domain.ledger.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 패키지이름    : com.areum.moneymanager.domain.ledger.dto<br>
 * 파일이름       : CategoryItem<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 7. 25.<br>
 * 설명              : 가계부 카테고리 조회 정보를 클라이언트에게 전달하기 위한 클래스
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
 * 		 	  <td>25. 7. 25.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class CategoryItem {
	private String name;			//카테고리 이름
	private String code;			//카테고리 코드

	//Category 엔티티 리스트를 한 번에 변환하는 정적 메서드
	public static List<CategoryItem> from(List<Category> categories) {
		return categories.stream()
				.map(
						CategoryItem::from
				).collect(Collectors.toList());
	}

	//Category 엔티티를 변환하는 정적 메서드
	public static CategoryItem from(Category category) {
		return CategoryItem.builder()
				.code(category.getCode())
				.name(category.getName())
				.build();
	}
}
