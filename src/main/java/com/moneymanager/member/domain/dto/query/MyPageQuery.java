package com.moneymanager.member.domain.dto.query;

import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.domain.enums.MemberType;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;

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

    private final MemberType type;
    private final String name;
    private final String nickname;
    private final MemberGender gender;
    private final String email;
    private final LocalDate lastLogin;
    private final LocalDate joinDate;
    private final String attendanceDays;

    private MyPageQuery(MemberType type, String name, String nickname, MemberGender gender, String email, LocalDate lastLogin, LocalDate joinDate, String attendanceDays) {
        this.type = type;
        this.name = name;
        this.nickname = nickname;
        this.gender = gender;
        this.email = email;
        this.lastLogin = lastLogin;
        this.joinDate = joinDate;
        this.attendanceDays = attendanceDays;
    }

    public static MyPageQuery of(String type, String name, String nickname, String gender, String email, Timestamp login_at, Timestamp created_at, Long consecutive_days) {
        return new MyPageQuery(
                MemberType.fromValue(type),
                name,
                nickname,
                MemberGender.fromValue(gender),
                email,
               login_at == null
                ? null
                : login_at.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                created_at.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                String.valueOf(consecutive_days)
        );
    }

}