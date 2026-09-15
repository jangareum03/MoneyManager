package com.moneymanager.member.domain.dto.response;

import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.domain.enums.MemberType;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.dto.response<br>
 * 파일이름       : MyPageResponse<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 15<br>
 * 설명              : 마이 페이지를 위한 응답 클래스
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
 * 		 	  <td>26. 9. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class MyPageResponse {

    private final MemberType type;
    private final String name;
    private final String nickname;
    private final MemberGender gender;
    private final String email;
    private final String profile;
    private final String lastLogin;
    private final String joinDate;
    private final String attendanceDays;

    private MyPageResponse(MemberType type, String name, String nickname, MemberGender gender, String email, String profile, String lastLogin, String joinDate, String attendanceDays) {
        this.type = type;
        this.name = name;
        this.nickname = nickname;
        this.gender = gender;
        this.email = email;
        this.profile = profile;
        this.lastLogin = lastLogin;
        this.joinDate = joinDate;
        this.attendanceDays = attendanceDays;
    }

    public static MyPageResponse of(MemberType type, String name, String nickname, MemberGender gender, String email, String profile, String login_at, String created_at, String consecutive_days) {
        return new MyPageResponse(
                type,
                name,
                nickname,
                gender,
                email,
                profile,
                login_at,
                created_at,
                consecutive_days
        );
    }

}