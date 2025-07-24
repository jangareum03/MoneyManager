package com.areum.moneymanager.dto.request.main;

import lombok.*;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.request.main<br>
 *  * 파일이름       : QnARequestDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 문의사항 데이터 전달하기 위한 클래스
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
public class QnARequestDTO {


	@Getter
	@AllArgsConstructor
	public static class Create {
		private String title;
		private Boolean isOpen;
		private String content;
	}



	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CheckWriter {
		private Long id;
		private String memberId;
	}


	@Getter
	@Builder
	@ToString
	public static class Update {
		private Long id;
		private String title;
		private Boolean isOpen;
		private String content;
	}

}
