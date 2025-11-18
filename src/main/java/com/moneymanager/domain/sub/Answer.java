package com.moneymanager.domain.inquiry;

import com.moneymanager.domain.admin.Admin;
import lombok.Builder;
import lombok.Getter;

import java.sql.Date;
import java.time.LocalDate;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.domain.inquiry<br>
 *  * 파일이름       : Answer<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 23. 2. 11<br>
 *  * 설명              : TB_QA_ANSWER 테이블과 매칭되는 클래스
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
 *		 	  <td>23. 2. 11</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>최초 생성(버전 1.0)</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>22. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Builder
@Getter
public class Answer {
    private String id;								//답변번호(식별자)
	private String content;						//답변 내용
	private LocalDate createdDate;		//등록일
	private LocalDate updatedDate;		//수정일
    private Question question;				//질문 정보
    private Admin admin;							//관리자 정보
}
