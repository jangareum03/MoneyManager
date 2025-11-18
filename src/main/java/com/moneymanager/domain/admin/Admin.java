package com.moneymanager.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.domain<br>
 *  * 파일이름       : Admin<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 23. 2. 11<br>
 *  * 설명              : TB_QA_ANSWER 테이블과 매칭되는 엔티티 클래스
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
public class Admin {
    /* 관리자번호(PK) */
    private String id;
    /* 관리자 역할 */
    private String role;
    /* 아이디 */
    private String adminName;
    /* 비밀번호 */
    private String password;
    /* 이름 */
    private String name;
    /* 닉네임 */
    private String nickName;
    /* 전화번호 */
    private String phone;
    /* 이메일 */
    private String email;
    /* 가입일 */
    private LocalDateTime createdAt;
    /* 답변등록수 */
    private Long answerCount;
}
