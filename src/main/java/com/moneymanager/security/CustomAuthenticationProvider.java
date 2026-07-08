package com.moneymanager.security;

import com.moneymanager.domain.member.Member;
import com.moneymanager.exception.exception.BusinessException;
import com.moneymanager.exception.log.DeveloperLogInfo;
import com.moneymanager.service.validation.MemberValidator;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.moneymanager.exception.code.MemberErrorCode.*;

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

		String work = "회원 인증";

		//기본 검증 시작
		MemberValidator.validateLogin( username, userPassword );

		try{
			CustomUserDetails userDetails = (CustomUserDetails) userDetailService.loadUserByUsername(username);

			return switch (userDetails.getStatus()) {
				case ACTIVE -> {
					//비밀번호 불일치인 경우
					if (!passwordEncoder.matches(userPassword, userDetails.getPassword())) {
						if (userDetails.getFailureCount() >= 5) {    //로그인 실패를 5번한 경우
							throw BusinessException.of(
									MBR_LIMIT_EXCEEDED,
									DeveloperLogInfo.of(work, "로그인 횟수 초과", Member.class, DeveloperLogInfo.valueOf("username", username, "password", "********")),
									"연속적인 로그인 실패로 오늘은 로그인이 불가능합니다. 내일 다시 시도해주세요."
							);
						}

						throw BusinessException.of(
										MBR_INVALID_CREDENTIALS,
										DeveloperLogInfo.of(work, "비밀번호 불일치", Member.class, DeveloperLogInfo.valueOf("username", username, "failureCount", userDetails.getFailureCount())),
										"아이디 또는 비밀번호를 확인해주세요."
								);
					}

					yield new UsernamePasswordAuthenticationToken(userDetails, userPassword, userDetails.getAuthorities());
				}
				case LOCKED -> throw BusinessException.of(
						MBR_ACCOUNT_LOCKED,
						DeveloperLogInfo.of(work, "잠긴 계정으로 로그인", Member.class, DeveloperLogInfo.valueOf("username", username, "status", userDetails.getStatus().name())),
						"계정이 잠겨있어 로그인이 불가능합니다. 내일 다시 시도해주세요."
				);
				case REPAIR -> throw BusinessException.of(
						MBR_ACCOUNT_DISABLED,
						DeveloperLogInfo.of(work, "탈퇴 계정으로 로그인", Member.class, DeveloperLogInfo.valueOf("username", username, "status", userDetails.getStatus().name())),
						"해당 계정은 탈퇴된 상태로 로그인이 불가능합니다. 가입하실 때 입력하신 이메일로 임시 비밀번호를 보내드렸으니, 다시 한 번 로그인 부탁드립니다."
				);
				case DELETE -> throw BusinessException.of(
						MBR_ACCOUNT_DELETED,
						DeveloperLogInfo.of(work, "탈퇴 계정으로 로그인", Member.class, DeveloperLogInfo.valueOf("username", username, "status", userDetails.getStatus().name())),
						"회원가입 하지 않는 아이디입니다. 회원가입을 진행해 주세요."
				);
				case UNKNOWN -> throw BusinessException.of(
						MBR_FORBIDDEN,
						DeveloperLogInfo.of(work, "권한 없는 계정으로 로그인", Member.class, DeveloperLogInfo.valueOf("username", username, "status", userDetails.getStatus().name())),
						"알 수 없는 회원 계정 상태입니다. 잠시 후 다시 시도해주세요."
				);
			};
		}catch (EmptyResultDataAccessException e) {
			throw BusinessException.of(
					MBR_INVALID_CREDENTIALS,
					DeveloperLogInfo.of(work, "없는 계정으로 로그인", "username", username),
					"아이디 또는 비밀번호를 확인해주세요."
			);
		}

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}

