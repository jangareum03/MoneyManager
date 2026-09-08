package com.moneymanager.global.security;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.member.service.validation.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.member.auth<br>
 * 파일이름       : CustomAuthenticationProvider<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 4.<br>
 * 설명              : 사용자가 입력한 정보로 인증을 처리하는 클래스
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
 * 		 	  <td>25. 8. 4.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;
	private final AuthValidator authValidator;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		//1. 로그인 페이지에서 입력한 아이디와 비밀번호 조회
		String username = authentication.getName();
		String userPassword = authentication.getCredentials().toString();

		//2. 아이디와 비밀번호 검증
		try{
			authValidator.login(username, userPassword);
		}catch (ApplicationException e){
			throw new AuthenticationServiceException(e.getUserMessage());
		}

		//3. 사용자 정보 조회
		CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

		throwsAuthenticationException(userDetails);

		//4. 비밀번호 일치여부 검증
		if(!passwordEncoder.matches(userPassword, userDetails.getPassword())) {
			throw new BadCredentialsException("member.login.failed");
		}

		//5. 로그인 인증 토큰 발급
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}


	private void throwsAuthenticationException(CustomUserDetails userDetails) throws AuthenticationException {
		if(!userDetails.isAccountNonExpired()) {
			throw new DisabledException("member.login.not_found");
		}

		if(!userDetails.isAccountNonLocked()) {
			throw new LockedException("member.login.locked");
		}

		if(!userDetails.isEnabled()) {
			throw new DisabledException("member.login.restricted");
		}
	}

}