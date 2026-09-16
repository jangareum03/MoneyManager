package com.moneymanager.member.domain.query;

import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.domain.enums.MemberType;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.dto.query<br>
 * 파일이름       : MyPageQuery<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 15<br>
 * 설명              : 데이터베이스에서 마이페이지에서 필요한 회원 정보 조회 결과를 담기 위한 클래스
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
public class MyPageQuery {

    private final String id;
    private final MemberType type;
    private final String password;
    private final String name;
    private final String nickname;
    private final MemberGender gender;
    private final String email;
    private final LocalDateTime lastLogin;
    private final LocalDateTime joinDate;
    private final String attendanceDays;

    private MyPageQuery(String id, MemberType type, String password, String name, String nickname, MemberGender gender, String email, LocalDateTime lastLogin, LocalDateTime joinDate, String attendanceDays) {
        this.id = id;
        this.type = type;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.gender = gender;
        this.email = email;
        this.lastLogin = lastLogin;
        this.joinDate = joinDate;
        this.attendanceDays = attendanceDays;
    }

    public static MyPageQuery of(String id, String type, String password, String name, String nickname, String gender, String email, LocalDateTime login_at, LocalDateTime created_at, Long consecutive_days) {
        return new MyPageQuery(
                id,
                MemberType.fromValue(type),
                password,
                name,
                nickname,
                MemberGender.fromValue(gender),
                email,
                login_at,
                created_at,
                String.valueOf(consecutive_days)
        );
    }

}