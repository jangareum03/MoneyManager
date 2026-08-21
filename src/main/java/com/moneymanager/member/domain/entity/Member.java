package com.moneymanager.member.domain.entity;

import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.domain.enums.MemberStatus;
import com.moneymanager.member.domain.enums.MemberType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.entity<br>
 * 파일이름       : Member<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 11.<br>
 * 설명              : MEMBER 테이블과 매칭되는 클래스
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
 * 		 	  <td>26. 8. 11.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class Member {

	private final String id;											//회원번호(내부용)
	private final String username;								//아이디
	private final String password;								//비밀번호
	private final String name;										//이름
	private final String birthdate;								//생년월일
	private final LocalDateTime createdAt;				//가입일

	private MemberType type;										//회원유형
	private MemberStatus status;								//회원상태
	private String role;												//회원권한
	private String nickname;										//닉네임
	private String email;												//이메일
	private LocalDateTime deletedAt;						//탈퇴일

	private MemberInfo info;										//상세정보

	public static Member testMember(PasswordEncoder passwordEncoder) {
		MemberInfo memberInfo = MemberInfo.builder()
				.id("UCt01001")
				.gender(MemberGender.MALE)
				.build();


		return Member.builder()
				.id("UCt01001")
				.username("test123")
				.password(passwordEncoder.encode("pw1234!!"))
				.name("홍길동")
				.birthdate("19950321")
				.type(MemberType.COMMON)
				.status(MemberStatus.ACTIVE)
				.role("ROLE_USER")
				.nickname("홍길동전")
				.email("test@test.com")
				.info(memberInfo)
				.build();
	}

}