package com.moneymanager.member.domain.dto;

import com.moneymanager.member.domain.enums.MemberStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.dto<br>
 * 파일이름       : MemberAuth<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 11<br>
 * 설명              : 회원 인증에 필요한 정보를 담은 클래스
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
 * 		 	  <td>26. 8. 11</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class MemberAuth {

	private final String id;
	private final String username;
	private final String password;
	private final String nickname;
	private final String role;
	private final MemberStatus status;
	private final String profile;
	private final int loginFailCount;
	private final LocalDateTime deletedDate;

	private MemberAuth(String id, String username, String password, String nickname, String role, MemberStatus status, String profile, int loginFailCount, LocalDateTime deletedDate) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
		this.status = status;
		this.profile = profile;
		this.loginFailCount = loginFailCount;
		this.deletedDate = deletedDate;
	}

	public static MemberAuth forLogin(String id, String username, String password, String nickname, String role, MemberStatus status, String profile, int loginFailCount, LocalDateTime deletedDate) {
		return new MemberAuth(id, username, password, nickname, role, status, profile, loginFailCount, deletedDate);
	}

	public static MemberAuth forAuthentication(String id, String role, MemberStatus status) {
		return new MemberAuth(id, null, null, null, role, status, null,0, null);
	}

}