package com.moneymanager.global.config;

import com.moneymanager.global.log.TraceIdFilter;
import com.moneymanager.global.security.CustomAuthFailureHandler;
import com.moneymanager.global.security.CustomAuthSuccessHandler;
import com.moneymanager.global.security.CustomAuthenticationProvider;
import com.moneymanager.global.security.CustomUserDetailService;
import com.moneymanager.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;


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

	private final CustomAuthSuccessHandler successHandler;
	private final CustomAuthFailureHandler failureHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final TraceIdFilter traceIdFilter;


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CustomAuthenticationProvider authenticationProvider(CustomUserDetailService userDetailService ) {
		return new CustomAuthenticationProvider(userDetailService, passwordEncoder());
	}

	@Bean
	public AuthenticationManager authenticationManager(CustomAuthenticationProvider authenticationProvider) {
		return new ProviderManager(authenticationProvider);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
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
						.antMatchers("/", "/signup", "/api/members/**", "/recovery/id", "/recovery/password").permitAll()
						.antMatchers("/auth/login", "/auth/refresh").permitAll()
						.anyRequest().hasRole("USER")
				)
				.authenticationManager(authenticationManager)
				.formLogin(login -> login
						.loginPage("/")
						.loginProcessingUrl("/login")
						.usernameParameter("username")
						.passwordParameter("password")
						.successHandler(successHandler)
						.failureHandler(failureHandler)
						.permitAll()
				)
				.exceptionHandling(exception -> exception
						.accessDeniedPage("/403")
				)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll()
				)
				.addFilterBefore(
						traceIdFilter,
						SecurityContextHolderFilter.class
				)
				.addFilterBefore(
						jwtAuthenticationFilter,
						UsernamePasswordAuthenticationFilter.class
				);
		return http.build();
	}
}