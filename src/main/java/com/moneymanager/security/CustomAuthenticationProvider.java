package com.moneymanager.security;

import com.moneymanager.domain.global.dto.ErrorDTO;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
						if( userDetails.getFailureCount() == 5 ) {	//로그인 실패를 5번한 경우
							LocalDate today = LocalDate.now();

							throw new LoginException(ErrorDTO.<String>builder()
									.logMessage( String.format("로그인 시도 횟수를 초과하셨습니다. %s 00시부터 다시 이용하실 수 있습니다.", today.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))) )
									.build());
						}

						throw new LoginException(ErrorDTO.<String>builder()
								.logMessage("아이디 또는 비밀번호를 확인해주세요.")
								.build());
					}

					return new UsernamePasswordAuthenticationToken(userDetails, userPassword, userDetails.getAuthorities());
				case LOCKED:
					throw new LoginException(ErrorDTO.builder()
							.logMessage( String.format("로그인 횟수 초과로 로그인이 불가능합니다. %s 00시부터 다시 로그인이 가능합니다.", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))) )
							.build());
				case REPAIR:
					throw new LoginException(ErrorDTO.builder()
							.logMessage("해당 계정은 탈퇴된 상태로 로그인이 불가능합니다. 가입하실 때 입력하신 이메일로 임시 비밀번호를 보내드렸으니, 다시 한 번 로그인 부탁드립니다.")
							.build());
				case DELETE:
					throw new LoginException( ErrorDTO.builder()
							.logMessage("회원가입 하지 않는 아이디입니다. 회원가입을 진행해 주세요.")
							.build() );
			}

			throw new LoginException( ErrorDTO.builder()
					.logMessage("알 수 없는 회원 계정 상태입니다. 잠시 후 다시 시도해주세요.")
					.build() );
		}catch ( EmptyResultDataAccessException e ) {
			throw new LoginException(ErrorDTO.builder()
					.logMessage("아이디 또는 비밀번호를 확인해주세요.")
					.build());
		}
	}


	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}


}

