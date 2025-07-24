package com.areum.moneymanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain( HttpSecurity http ) throws Exception{
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
										.loginProcessingUrl("/login")
										.successForwardUrl("/login")
										.failureForwardUrl("/login")
										.permitAll()
						)
						.logout(logout -> logout // 로그아웃 설정
										.logoutUrl("/logout")
										.logoutSuccessUrl("/")
										.permitAll()
						)
						.requiresChannel().anyRequest().requiresSecure();	//https 요청으로 변경
		return http.build();
	}


}
