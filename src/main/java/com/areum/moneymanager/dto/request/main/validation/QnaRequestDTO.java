package com.areum.moneymanager.dto.request.main.validation;


import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Q&A의 정보를 검증하기 위해 서버로 전송하는 데이터 객체
 */

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.request.main.validation<br>
 *  * 파일이름       : QnaRequestDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 문의사항 검증 시 데이터 전달하기 위한 클래스
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
public class QnaRequestDTO {


	/**
	 * 질문 제목값을 검증할 때 필요한 객체입니다. <br>
	 * <b color='white'>title</b>은 제목입니다.
	 */
	@Getter
	@NoArgsConstructor
	public static class Title {
		private String title;
	}


	/**
	 * 질문 내용값을 검증할 때 필요한 객체입니다. <br>
	 * <b color='white'>content</b>은 내용입니다.
	 */
	@Getter
	@NoArgsConstructor
	public static class Content {
		private String content;
	}

}
