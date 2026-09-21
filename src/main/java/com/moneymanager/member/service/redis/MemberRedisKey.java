package com.moneymanager.member.service.redis;

import org.springframework.stereotype.Component;

/**
 * <p>
 * 패키지이름    : com.moneymanager.redis<br>
 * 파일이름       : MemberRedisKey<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 13<br>
 * 설명              : Redis Key를 생성하는 클래스
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
 * 		 	  <td>26. 9. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class MemberRedisKey {

    public String emailVerificationCode(String email) {
        return "email-verification:code:" +email;
    }

    public String emailVerificationToken(String email) {
        return "email-verification:token:" +email;
    }

    public String passwordResetToken(String token) {
        return "password-reset:" +token;
    }

    public String sidebarNickname(String memberId) {
        return "sidebar:nickname:" + memberId;
    }

    public String sidebarProfile(String memberId) {
        return "sidebar:profile:" + memberId;
    }

}