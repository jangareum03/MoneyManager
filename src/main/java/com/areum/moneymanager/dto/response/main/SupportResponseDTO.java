package com.areum.moneymanager.dto.response.main;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.response.main<br>
 *  * 파일이름       : SupportResponseDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 사용자에게 고객센터 데이터를 전달하기 위한 클래스
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
public class SupportResponseDTO {


	/**
	 * 공지사항 내역의 요약정보를 리스트 형태로 볼 때 필요한 객체입니다.<br>
	 * <b color='white'>notices</b>는 공지사항 리스트, <b color='white'>details</b> 요약정보입니다.
	 */
	@Builder
	@Getter
	public static class NoticeList {
		private List<NoticeRow> notices;
		private Page page;
	}



	/**
	 * 설명<br>
	 * <b color='white'>title</b>는 제목,
	 * <b color='white'>date</b>는 공지사항 등록 날짜,
	 * <b color='white'>view</b>는 조회수입니다.
	 */
	@Builder
	@Getter
	public static class NoticeRow {
		private String id;
		private String title;
		private NoticeType type;
		private String date;
		private Long view;
	}


	@Builder
	@Getter
	public static class NoticeType {
		private char code;
		private String text;
	}


	@Builder
	@Getter
	public static class Notice {
		private String title;
		private String date;
		private String content;
	}



	@Builder
	@Getter
	public static class InquiryList {
		private List<InquiryRow> inquiries;
		private Search search;
		private Page page;
		private String resultText;
	}



	/**
	 * 설명<br>
	 * <b color='white'>id</b>는 QnA 번호,
	 * <b color='white'>title</b>는 제목,
	 * <b color='white'>date</b>는 작성날짜,
	 * <b color='white'>writer</b>는 작성자,
	 * <b color='white'>isLock</b>는 잠금여부 입니다.
	 */
	@Builder
	@Getter
	public static class InquiryRow {
		private Long id;
		private String title;
		private String date;
		private String writer;
		private Answer answer;
		private boolean isOpen;
	}



	@Builder
	@Getter
	public static class Answer {
		private char code;
		private String text;
	}


	/** 설명<br>
	 * <b color='white'>mode</b>는 검색유형,
	 * <b color='white'>keyword</b>는 검색 키워드 입니다.
	 */
	@Getter
	@Builder
	public static class Search {
		private String mode;
		private String keyword;
	}



	/**
	 * 설명<br>
	 * <b color='white'>page</b>는 현재 페이지 번호,
	 * <b color='white'>start</b>는 시작 페이지 번호,
	 * <b color='white'>end</b>는 끝 페이지 번호,
	 * <b color='white'>isPrev</b>는 이전 페이지 표시,
	 * <b color='white'>isNext</b>는 다음 페이지 표시,
	 * <b color='white'>size</b>는 한 페이지당 공지사항 수입니다.
	 */
	@Builder
	@Getter
	public static class Page {
		private Integer page;
		private Integer start;
		private Integer end;
		private boolean isPrev, isNext;
		private Integer size;
	}
}
