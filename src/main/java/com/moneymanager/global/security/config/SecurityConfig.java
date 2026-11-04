package com.moneymanager.global.security.config;

import com.moneymanager.global.security.filter.CspNonceFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;


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
				)
				.sessionManagement( session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) )
				.authorizeHttpRequests(auth -> auth
						.antMatchers("/css/**", "/js/**", "/image/**").permitAll()
						.antMatchers("/signup").permitAll()
						.antMatchers("/api/accounts/**").permitAll()
						.anyRequest().hasRole("USER")
				)
				.addFilterBefore(new CspNonceFilter(), HeaderWriterFilter.class);

		return http.build();
	}
}