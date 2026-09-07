package com.moneymanager.member.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.Locale;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.dto.request<br>
 * 파일이름       : MemberSignUpRequest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 4<br>
 * 설명              : 회원가입을 위한 요청 클래스
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
 * 		 	  <td>26. 9. 4</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class MemberSignUpRequest {

    private final String username;
    private final String password;
    private final String name;
    private final String birthdate;
    private final String nickname;
    private final String email;
    private final String token;
    private final String gender;

    private MemberSignUpRequest(String username, String password, String name, String birthdate, String nickname, String email, String token, String gender) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.birthdate = birthdate;
        this.nickname = nickname;
        this.email = email;
        this.token = token;
        this.gender = gender;
    }

    public static MemberSignUpRequest of(String username, String password, String name, String birthdate, String nickname, String email, String token, String gender) {
        return  new MemberSignUpRequest(
                username,
                password,
                name,
                birthdate,
                nickname,
                email,
                token,
                gender.toLowerCase(Locale.ROOT)
        );
    }

}