package com.moneymanager.security;


import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.enums.MemberStatus;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.member.auth<br>
 * 파일이름       : CustomUserDetails<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 3.<br>
 * 설명              :	데이터베이스에서 사용자 정보를 가져오는 클래스
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
 * 		 	  <td>25. 8. 3.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ToString
public class CustomUserDetails implements UserDetails {

	private final Member member;

	public CustomUserDetails(Member member) {
		this.member = member;
	}

	/**
	 * 로그인 처리 중인 사용자의 권한 정보를 반환합니다.
	 *
	 * @return 권한정보(ex. ROLE_USER)
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(member.getRole()));
	}


	/**
	 * 로그인 처리중인 사용자의 암호화된 비밀번호를 반환합니다.
	 *
	 * @return 암호화된 비밀번호
	 */
	@Override
	public String getPassword() {
		return member.getPassword();
	}


	/**
	 * 로그인 처리 중인 사용자의 아이디를 반환합니다.
	 *
	 * @return 아이디
	 */
	@Override
	public String getUsername() {
		return member.getUserName();
	}


	/**
	 * 로그인 완료된 사용자의 회원번호를 반환합니다.
	 * @return	회원번호(PK)
	 */
	public String getId() {
		return member.getId();
	}

	/**
	 * 로그인 처리 중인 사용자의 닉네임을 반환합니다.
	 *
	 * @return	닉네임
	 */
	public String getNickname() {
		return member.getNickName();
	}


	/**
	 * 사용자의 프로필 이미지를 반환합니다.
	 *
	 * @return	프로필 이미지
	 */
	public String getProfile() {
		return member.getDetail().getProfile();
	}


	/**
	 * 계정이 만료됐는지 확인합니다.
	 *
	 * @return 만료되지 않으면 true, 만료되면 false
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}


	/**
	 * 계정이 잠겨있는지 확인합니다.
	 *
	 * @return 잠겨있지 않으면 true, 잠겨 있으면 false
	 */
	@Override
	public boolean isAccountNonLocked() {
		return !(member.getStatus() == MemberStatus.LOCKED);
	}


	/**
	 * 비밀번호가 만료됐는지 확인합니다.
	 *
	 * @return 만료되지 않으면 true, 만료되면 false
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}


	/**
	 * 계정이 사용 가능한 상태인지 확인합니다.
	 *
	 * @return 사용 가능하면 true, 불가능하면 false
	 */
	@Override
	public boolean isEnabled() {
		return member.getStatus() == MemberStatus.ACTIVE;
	}


	/**
	 * 계정의 상태를 반환합니다.
	 *
	 * @return	회원상태 정보를 담은 Enum
	 */
	public MemberStatus getStatus() {
		return member.getStatus();
	}


	/**
	 * 계정의 로그인 실패 횟수를 반환합니다.
	 *
	 * @return	로그인 실패 횟수
	 */
	public int getFailureCount() {
		return member.getDetail().getFailureCount();
	}

}
