package com.moneymanager.global.security;


import com.moneymanager.member.domain.dto.MemberAuth;
import com.moneymanager.member.domain.enums.MemberStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
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
public class CustomUserDetails implements UserDetails {

	private final String memberId;
	private final String username;
	private final String password;
	private final String role;
	private final MemberStatus status;
	private final int failCount;
	private final LocalDateTime cancellationDate;

	public CustomUserDetails(MemberAuth memberAuth) {
		this.memberId = memberAuth.getMemberId();
		this.username = memberAuth.getUsername();
		this.password = memberAuth.getPassword();
		this.role = memberAuth.getRole();
		this.status = memberAuth.getStatus();
		this.failCount = memberAuth.getLoginFailCount();
		this.cancellationDate = memberAuth.getDeletedDate();
	}

	/**
	 * 로그인 처리 중인 사용자의 아이디를 반환합니다.
	 *
	 * @return 아이디
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/**
	 * 로그인 처리중인 사용자의 암호화된 비밀번호를 반환합니다.
	 *
	 * @return 암호화된 비밀번호
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * 로그인 처리 중인 사용자의 권한 정보를 반환합니다.
	 *
	 * @return 권한정보(ex. ROLE_USER)
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(role));
	}

	/**
	 * 계정이 활성화 상태인지 확인합니다.
	 *
	 * @return 계정 활성화 상태면 {@code ture}, 비활성화면 {@code false} 반환
	 */
	@Override
	public boolean isEnabled() {
		return status == MemberStatus.ACTIVE && failCount < 5;
	}

	/**
	 * 계정이 잠겨있는 상태인지 확인합니다.
	 *
	 * @return 잠겨있지 않으면 {@code ture}, 잠겨있으면 {@code false}
	 */
	@Override
	public boolean isAccountNonLocked() {
		return !(status == MemberStatus.LOCKED) && failCount < 5;
	}

	/**
	 * 계정이 만료되었는지 확인합니다. 만료된 계정은 인증이 불가능합니다.
	 *
	 * @return	만료가 되지 않으면 {@code ture}, 만료되면 {@code false}
	 */
	@Override
	public boolean isAccountNonExpired() {
		LocalDateTime dayAgo = LocalDateTime.now().minusMonths(1);

		return !( (status == MemberStatus.DELETED || status == MemberStatus.REPAIR)
				&& cancellationDate != null
				&& (cancellationDate.isBefore(dayAgo)) );
	}

	/**
	 * 사용자의 자격 증명(비밀번호)이 만료되었는지 확인합니다.
	 *
	 * @return 만료가 되지 않으면 {@code ture}, 만료되면 {@code false}
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * 로그인 완료된 사용자의 회원번호를 반환합니다.
	 * @return	회원번호(PK)
	 */
	public String getId() {
		return memberId;
	}

}