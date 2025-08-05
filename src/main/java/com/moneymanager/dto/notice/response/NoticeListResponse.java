package com.moneymanager.dto.notice.response;

import com.moneymanager.dto.common.response.PageResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.notice.response<br>
 * * 파일이름       : NoticeListResponse<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 26.<br>
 * * 설명              : 공지사항 목록 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 26.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class NoticeListResponse {
	//공지사항 목록
	private List<Row> list;
	//공지사항 페이지
	private PageResponse page;

	/**
	 * 공지사항 목록요소 1개의 정보를 담은 DTO
	 */
	@Builder
	@Getter
	public static class Row {
		//공지사항 식별 번호
		private String id;
		//공지사항 제목
		private String title;
		//공지사항 작성 날짜
		private String date;
		//공지사항 유형
		private Type type;
		//공지사항 조회수
		private Long view;
	}

	/**
	 * 공지사항 유형 정보를 담은 DTO
	 */
	@Builder
	@Getter
	public static class Type {
		//공지사항 유형 코드
		private char code;
		//공지사항 유형 문구
		private String text;
	}

}
