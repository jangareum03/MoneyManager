package com.areum.moneymanager.dto.inquiry.response;

import com.areum.moneymanager.dto.common.response.PageResDTO;
import com.areum.moneymanager.dto.common.response.SearchResDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.inquiry.response<br>
 * * 파일이름       : ListReqDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 26.<br>
 * * 설명              : 문의사항 목록 응답을 위한 데이터 클래스
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
public class ListResDTO {
	//문의사항 목록
	private List<Row> list;
	//문의사항 페이지
	private PageResDTO page;
	//문의사항 검색 결과
	private SearchResDTO search;


	/**
	 * 문의사항 목록요소 1개의 정보를 담은 DTO
	 */
	@Builder
	@Getter
	public static class Row {
		//문의사항 식별 번호
		private Long id;
		//문의사항 제목
		private String title;
		//문의사항 작성날짜
		private String date;
		//문의사항 작성자
		private String writer;
		//비밀글 여부(true: 모든회원 접근 O, false: 작성자만 접근 O)
		private boolean isOpen;
		//문의사항 답변상태
		private Answer answer;
	}


	/**
	 * 문의사항 답변상태를 보기 위한 DTO
	 */
	public static class Answer {
		//답변 코드
		private char code;
		//답변 문구
		private String text;
	}

}
