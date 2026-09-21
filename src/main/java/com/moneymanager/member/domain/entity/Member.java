package com.moneymanager.member.domain.entity;

import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.domain.enums.MemberStatus;
import com.moneymanager.member.domain.enums.MemberType;
import lombok.Getter;

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
@Getter
public class Member {

	private String id;											//회원번호(내부용)
	private String memberNumber;								//회원번호(외부용)
	private final String username;								//아이디
	private String password;								//비밀번호
	private String name;										//이름
	private final String birthdate;								//생년월일

	private final MemberType type;										//회원유형
	private MemberStatus status;								//회원상태
	private final String role;												//회원권한
	private String nickname;										//닉네임
	private String email;												//이메일
	private final LocalDateTime createdAt;				//가입일
	private final LocalDateTime deletedAt;						//탈퇴일

	private MemberInfo info;										//상세정보

	private Member(String id, String memberNumber, String username, String password, String name, String birthdate, String nickname, String email, String role, MemberType type, MemberStatus status, LocalDateTime createdAt, LocalDateTime deletedAt, MemberInfo info) {
		this.id = id;
		this.memberNumber = memberNumber;
		this.username = username;
		this.password = password;
		this.name = name;
		this.birthdate = birthdate;
		this.nickname = nickname;
		this.email = email;
		this.role = role;
		this.type = type;
		this.status = status;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;

		this.info = info;
	}

	public static Member createForJoin(String id, String number, String username, String password, String name, String birthdate, String nickname, String email, MemberGender gender) {
		MemberInfo memberInfo = MemberInfo.create(id, gender);

		return new Member(id, number, username, password, name, birthdate, nickname, email, "ROLE_USER", MemberType.COMMON, MemberStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), memberInfo);
	}

	//DB용
	public static Member restore(String id, String memberNumber, String username, String password, String name, String birthdate, String nickname, String email, String role, MemberType type, MemberStatus status, LocalDateTime createdAt, LocalDateTime deletedAt, MemberInfo info) {
		return new Member(id, memberNumber, username, password, name, birthdate, nickname, email, role, type, status, createdAt, deletedAt, info);
	}

	public void changeMemberNumber(String memberNumber) {
		this.memberNumber = memberNumber;
	}

	public void changeId(String id) {
		this.id = id;
	}

	public void changeName(String newName) {
		if(status != MemberStatus.ACTIVE) {
			throw new IllegalStateException(status.name() + "상태에서 수정 불가");
		}

		if(name.equals(newName)) {
			throw new IllegalArgumentException("동일값 수정 불가");
		}

		this.name = newName;
	}

	public void changePassword() {
		if(status != MemberStatus.ACTIVE) {
			throw new IllegalStateException(status.name() + "상태에서 수정 불가");
		}
	}

	public void changeGender(String newGender) {
		if(status !=MemberStatus.ACTIVE) {
			throw new IllegalStateException(status.name() + "상태에서 수정 불가");
		}

		MemberGender gender =  MemberGender.fromValue(newGender);

		if(getInfo().getGender() == gender) {
			throw new IllegalArgumentException("동일값 수정 불가");
		}

		info.changeGender(gender);
	}

	public void changeEmail(String newEmail) {
		if(status != MemberStatus.ACTIVE) {
			throw new IllegalStateException(status.name() + "상태에서 수정 불가");
		}

		if(email.equals(newEmail)) {
			throw new IllegalArgumentException("동일값 수정 불가");
		}

		this.email = newEmail;
	}

	public void changeProfile(String newProfile) {
		if(status != MemberStatus.ACTIVE) {
			throw new IllegalStateException(status.name() + "상태에서 수정 불가");
		}

		info.changeProfile(newProfile);
	}

}