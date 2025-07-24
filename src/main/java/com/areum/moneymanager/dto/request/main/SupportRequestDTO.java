package com.areum.moneymanager.dto.request.main;

import lombok.*;

import java.util.List;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.request.main<br>
 *  * 파일이름       : SupportRequestDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 고객센터 검색 및 페이지 데이터 전달하기 위한 클래스
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
public class SupportRequestDTO {


	/**
	 * 설명<br>
	 * <b color='white'>page</b>는 현재 페이지 번호,
	 * <b color='white'>size</b>는 한 페이지 당 공지사항 수입니다.
	 */
	@Builder
	@Data
	@AllArgsConstructor
	@ToString
	public static class Page {
		private Integer num;
		private Integer size;

		public Page() {
			this.num = null;
			this.size = null;
		}
	}


	@Builder
	@Getter
	@AllArgsConstructor
	public static class Search {
		private String mode;
		private List<String> keyword;

		public Search() {
			this.mode = "all";
			this.keyword = null;
		}
	}


	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SearchAPI {
		private Page pagination = new Page();
		private Search search = new Search();
	}

}
