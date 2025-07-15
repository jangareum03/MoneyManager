package com.areum.moneymanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
