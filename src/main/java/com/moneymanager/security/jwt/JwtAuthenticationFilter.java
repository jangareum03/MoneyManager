package com.moneymanager.security.jwt;

import com.moneymanager.security.CustomUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.security.jwt<br>
 * 파일이름       : JwtAuthenticationFilter<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 6.<br>
 * 설명              : JWT 토큰 처리와 인증된 요청을 처리하는 클래스
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
 * 		 	  <td>25. 11. 6.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider tokenProvider;
	private final CustomUserDetailService userDetailService;

	public JwtAuthenticationFilter( JwtTokenProvider tokenProvider, CustomUserDetailService userDetailService ) {
		this.tokenProvider = tokenProvider;
		this.userDetailService = userDetailService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = resolveToken(request);

		if( token != null && tokenProvider.validateToken(token) ) {	//유효한 토큰인 경우
			String username = tokenProvider.getUserName(token);
			UserDetails userDetails = userDetailService.loadUserByUsername(username);

			UsernamePasswordAuthenticationToken authenticationToken
					= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();

		if( cookies != null ) {
			for(Cookie cookie : cookies ) {
				if( "accessToken".equals(cookie.getName()) ) {
					return cookie.getValue();
				}
			}
		}

		return null;
	}
}
