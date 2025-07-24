package com.areum.moneymanager.dto.response.main;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.response.main<br>
 *  * 파일이름       : CategoryResponseDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 사용자에게 가계부 카테고리 데이터를 전달하기 위한 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
public class CategoryResponseDTO {

	/**
	 * 가계부 카테고리 정보를 저장한객체입니다.<p>
	 * <b color='white'>name</b>는 카테고리 이름, <b color='white'>code</b>는 카테고리 코드입니다.
	 */
	@Builder
	@Getter
	// 카테고리 정보를 가져올 때
	public static class Read {
		private String name;
		private String code;

	}

	@Builder
	@Getter
	// 카테고리 이름 가져올 때
	public static class Name {
		private String name;
	}
}

