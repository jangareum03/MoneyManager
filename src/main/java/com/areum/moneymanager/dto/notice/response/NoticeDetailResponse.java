package com.areum.moneymanager.dto.notice.response;

import lombok.Builder;
import lombok.Getter;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.notice.response<br>
 * * 파일이름       : NoticeDetailResponse<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 30.<br>
 * * 설명              : 공지사항 상세정보 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 30.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class NoticeDetailResponse {
	//공지사항 제목
	private String title;
	//공지사항 날짜
	private String date;
	//공지사항 내용
	private String content;
}
