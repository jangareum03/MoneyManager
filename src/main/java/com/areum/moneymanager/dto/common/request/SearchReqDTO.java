package com.areum.moneymanager.dto.common.request;

import lombok.*;

import java.util.List;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.common<br>
 * * 파일이름       : SearchReqDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              : 검색 요청을 위한 데이터 클래스
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
public class SearchReqDTO {

	/**
	 * 가계부 검색을 위한 DTO<br>
	 * <span color='#BE2E22'>검색유형, 검색 기간</span>은 필수값입니다.
	 */
	@Builder
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Budget {
		//가계부 검색 기간
		private DateReqDTO.WeekRange date;
		//검색 유형
		private String mode;
		//검색 키워드
		private List<String> keywords;
	}

	/**
	 * 고객센터 검색을 위한 DTO<br>
	 * <span color='#BE2E22'>검색유형</span>은 필수값입니다.
	 */
	@Builder
	@Getter
	public static class Support {
		//검색 유형
		private String mode;
		//검색 키워드
		private List<String> keyword;

	}

	/**
	 * 문의사항 검색하기 위한 DTO
	 */
	@Builder
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Inquiry {
		private PageReqDTO page = new PageReqDTO();
		private SearchReqDTO search = new SearchReqDTO();
	}

}
