package com.areum.moneymanager.dto.response.main;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.response.main<br>
 *  * 파일이름       : QnAResponseDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 사용자에게 문의사항 데이터를 전달하기 위한 클래스
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
public class QnAResponseDTO {


	@Builder
	@Getter
	public  static class Update {
		private String title;
		private Boolean isOpen;
		private String content;
	}

	/**
	 * 설명<br>
	 * <b color='white'>title</b>는 제목,
	 * <b color='white'>writer</b>는 작성자,
	 * <b color='white'>date</b>는 작성날짜,
	 * <b color='white'>content</b>는 내용,
	 * <b color='white'>answer</b>는 답변 입니다.
	 */
	@Builder
	@Getter
	public static class Detail {
		private Question question;
		private Answer answer;
	}


	/**
	 * 설명<br>
	 * <b color='white'>date</b>는 작성날짜,
	 * <b color='white'>writer</b>는 작성자,
	 * <b color='white'>title</b>는 제목,
	 * <b color='white'>content</b>는 내용 입니다.
	 */
	@Builder
	@Getter
	public static class Question {
		private String date;
		private String writer;
		private String title;
		private String content;
	}


	/**
	 * 설명<br>
	 * <b color='white'>date</b>는 작성날짜,
	 * <b color='white'>writer</b>는 작성자,
	 * <b color='white'>content</b>는 내용 입니다.
	 */
	@Builder
	@Getter
	public static class Answer {
		private String date;
		private String writer;
		private String content;
	}


}