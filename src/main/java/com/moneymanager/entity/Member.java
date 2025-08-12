package com.moneymanager.entity;

import com.moneymanager.enums.type.MemberStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.entity<br>
 *  * 파일이름       : Member<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 11. 2<br>
 *  * 설명              : TB_MEMBER 테이블과 매칭되는 엔티티 클래스
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
 *		 	  <td>22. 11. 2</td>
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
@ToString
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    /* 회원번호(PK) */
    private String id;
    /* 회원유형 */
    private String type;
    /* 회원상태 */
    private MemberStatus status;
    /* 회원권한 */
    private String role;
    /* 아이디 */
    private String userName;
    /* 비밀번호 */
    private String password;
    /* 이름 */
    private String name;
    /* 생년월일 */
    private String birthDate;
    /* 닉네임 */
    private String nickName;
    /* 이메일 */
    private String email;
    /* 가입일 */
    private LocalDateTime createdAt;
    /* 탈퇴일 */
    private LocalDateTime deletedAt;
    /* 부가정보 */
    private MemberInfo info;
}
