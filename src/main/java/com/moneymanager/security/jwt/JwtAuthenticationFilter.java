package com.moneymanager.security.jwt;

import com.moneymanager.dao.member.MemberTokenDao;
import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.MemberToken;
import com.moneymanager.security.CustomUserDetailService;
import com.moneymanager.utils.DateTimeUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
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
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider tokenProvider;
	private final CustomUserDetailService userDetailService;
	private final MemberTokenDao tokenDao;

	public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailService userDetailService, MemberTokenDao tokenDao) {
		this.tokenProvider = tokenProvider;
		this.userDetailService = userDetailService;
		this.tokenDao = tokenDao;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String accessToken = resolveToken(request);

		String uri = request.getRequestURI();
		if( uri.startsWith("/css") || uri.startsWith("/js") || uri.startsWith("/image")) {		//css, js, image 요청은 제외
			filterChain.doFilter(request, response);
			return;
		}

		if( accessToken != null ) {
			if( tokenProvider.validateToken(accessToken) ) {
				authenticationMember(accessToken);
			}else {
				//accessToken 만료
				String username = tokenProvider.getUserName(accessToken);
				String refreshToken = tokenDao.findRefreshToken(username);

				if( refreshToken != null && tokenProvider.validateToken(refreshToken) ) {	//refresh 토큰 유효한 경우
					String newAccessToken = tokenProvider.reissueAccessToken(username);

					tokenDao.updateAccessToken(
							MemberToken.builder().member(Member.builder().userName(username).build())
									.accessToken(newAccessToken)
									.accessExpireAt(DateTimeUtils.getLocalDateTime(tokenProvider.getExpiration(newAccessToken)))
									.build()
					);

					//쿠키 갱신
					Cookie newCookie = new Cookie("accessToken", newAccessToken);
					newCookie.setHttpOnly(true);
					newCookie.setPath("/");
					newCookie.setMaxAge(60 * 60);

					response.addCookie(newCookie);

					authenticationMember(newAccessToken);
				}else {
					tokenDao.updateTokenIsNull(username);

					//쿠키 초기화
					Cookie clearCookie = new Cookie("accessToken", null);
					clearCookie.setMaxAge(0);
					clearCookie.setPath("/");

					response.addCookie(clearCookie);
				}
			}
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


	/**
	 * 토큰을 이용하여 인증객체 생성 후 SecurityContextHolder에 저장합니다.
	 *
	 * @param token	토큰
	 */
	private void authenticationMember(String token) {
		String username = tokenProvider.getUserName(token);
		UserDetails userDetails = userDetailService.loadUserByUsername(username);

		UsernamePasswordAuthenticationToken authentication
				= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
