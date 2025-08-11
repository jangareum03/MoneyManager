package com.moneymanager.service.member.auth;

import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.LoginException;
import com.moneymanager.service.validation.MemberValidator;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final PasswordEncoder passwordEncoder;
	private final UserDetailsService userDetailService;

	public CustomAuthenticationProvider( CustomUserDetailService userDetailService, PasswordEncoder passwordEncoder ) {
		this.userDetailService = userDetailService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String userPassword = authentication.getCredentials().toString();

		//기본 검증 시작
		MemberValidator.validateLogin( username, userPassword );

		try{
			CustomUserDetails userDetails = (CustomUserDetails) userDetailService.loadUserByUsername(username);

			switch (userDetails.getStatus()) {
				case ACTIVE:
					//비밀번호 불일치인 경우
					if( !passwordEncoder.matches(userPassword, userDetails.getPassword()) ) {
						if( userDetails.getFailureCount() == 5 ) {
							throw new LoginException(ErrorDTO.builder().errorCode(ErrorCode.LOGIN_ACCOUNT_UNAUTHORIZED_LOCKED).requestData(username).build());
						}
						throw new LoginException(ErrorDTO.builder().errorCode(ErrorCode.LOGIN_ACCOUNT_MISMATCH).requestData(username).build());
					}

					return new UsernamePasswordAuthenticationToken(userDetails, userPassword, userDetails.getAuthorities());
				case LOCKED:
					throw new LoginException(ErrorDTO.builder().errorCode(ErrorCode.LOGIN_ACCOUNT_UNAUTHORIZED_LOCKED).requestData(username).build());
				case REPAIR:
					throw new LoginException(ErrorDTO.builder().errorCode(ErrorCode.LOGIN_ACCOUNT_UNAUTHORIZED_RESTORE).requestData(username).build());
				case DELETE:
					throw new LoginException( ErrorDTO.builder().errorCode(ErrorCode.LOGIN_ACCOUNT_UNAUTHORIZED_RESIGN).requestData(username).build() );
			}

			throw new LoginException( ErrorDTO.builder().errorCode(ErrorCode.LOGIN_SECURITY_AUTHORIZATION_ACCESS_DENIED).requestData(username).build() );
		}catch ( EmptyResultDataAccessException e ) {
			throw new LoginException(ErrorDTO.builder().errorCode(ErrorCode.LOGIN_ID_NONE).requestData(username).build());
		}
	}


	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}


}

