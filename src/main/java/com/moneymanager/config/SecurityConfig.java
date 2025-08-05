package com.moneymanager.config;

import com.moneymanager.service.member.AuthService;
import com.moneymanager.service.member.auth.CustomAuthFailureHandler;
import com.moneymanager.service.member.auth.CustomAuthSuccessHandler;
import com.moneymanager.service.member.auth.CustomAuthenticationProvider;
import com.moneymanager.service.member.auth.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.config<br>
 *  * 파일이름       : SecurityConfig<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 보안 설정과 관련된 작업하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomAuthFailureHandler failureHandler;
	private final CustomAuthSuccessHandler successHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider(CustomUserDetailService detailService) {
		return new CustomAuthenticationProvider( detailService, passwordEncoder() );
	}

	@Bean
	public SecurityFilterChain securityFilterChain( HttpSecurity http, AuthenticationProvider authenticationProvider ) throws Exception{
		http
						.csrf(csrf -> csrf.disable()) // CSRF 비활성화 (API 호출 시 필요)
						.cors(Customizer.withDefaults())
						.headers((headers) ->
								headers.xssProtection(Customizer.withDefaults())
										.contentSecurityPolicy( csp ->
												csp.policyDirectives("default-src 'self'; " +
																				 "script-src 'self' https://dapi.kakao.com https://t1.daumcdn.net https://www.gstatic.com 'unsafe-eval'; " +
																				 "connect-src 'self' https://dapi.kakao.com; " +
														 						 "font-src 'self' https://cdn.jsdelivr.net; " +
																				"style-src 'self' 'unsafe-inline' https://www.gstatic.com; " +
																				"img-src 'self' blob: https://t1.daumcdn.net https://mts.daumcdn.net; " +
																				"object-src 'none';")
										)
						)	 //xss설정
						.authorizeHttpRequests(auth -> auth
										.antMatchers("/css/**", "/js/**", "/image/**").permitAll()
										.antMatchers("/", "/signup", "/api/members/**","/recovery/id", "/recovery/password").permitAll()	//누구나 접근 가능
										.anyRequest().hasAuthority("USER") // 나머지는 인증 필요
						)
						.formLogin(login -> login // 기본 로그인 페이지 사용
										.loginPage("/") // 커스텀 로그인 페이지 설정 가능
										.usernameParameter("id")
										.passwordParameter("password")
										.successHandler(successHandler)
										.loginProcessingUrl("/login")														//로그인 처리
										.defaultSuccessUrl("/attendance")											//로그인 성공 시 URL
										.failureHandler(failureHandler)
										.failureUrl("/")							//로그인 실패 시 URL
										.permitAll()
						)
						.logout(logout -> logout // 로그아웃 설정
										.logoutUrl("/logout")
										.logoutSuccessUrl("/")
										.permitAll()
						)
						.authenticationProvider(authenticationProvider)
						.requiresChannel().anyRequest().requiresSecure();	//https 요청으로 변경
		return http.build();
	}

}
