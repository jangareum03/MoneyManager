package com.moneymanager.member.redis;

import org.springframework.stereotype.Component;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.redis<br>
 * 파일이름       : PasswordResetRedisKey<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 13<br>
 * 설명              : Redis용 비밀번호 변경 인증키 생성하는 클래스
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
public class PasswordResetRedisKey {

    public String token(String token) {
        return "password-reset:" + token;
    }

}