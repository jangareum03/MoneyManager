package com.moneymanager.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


/**
 * <p>
 * 패키지이름    : com.areum.moneymanager.config<br>
 * 파일이름       : SecurityConfig<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2025-07-15<br>
 * 역할             : 요청과 인증 및 인가를 관리
 * </p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.cors(Customizer.withDefaults())
				.headers(headers -> headers
						.xssProtection(Customizer.withDefaults())
						.contentSecurityPolicy(csp -> csp.policyDirectives(
								"default-src 'self'; " +
										"script-src 'self' https://dapi.kakao.com https://t1.daumcdn.net https://www.gstatic.com 'unsafe-eval'; " +
										"connect-src 'self' https://dapi.kakao.com; " +
										"font-src 'self' https://cdn.jsdelivr.net; " +
										"style-src 'self' 'unsafe-inline' https://www.gstatic.com; " +
										"img-src 'self' blob: https://t1.daumcdn.net https://mts.daumcdn.net; " +
										"object-src 'none';"
						))
				)
				.sessionManagement( session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) )
				.authorizeHttpRequests(auth -> auth
						.antMatchers("/css/**", "/js/**", "/image/**").permitAll()
						.antMatchers("/api/accounts/**").permitAll()
						.anyRequest().hasRole("USER")
				);

		return http.build();
	}
}