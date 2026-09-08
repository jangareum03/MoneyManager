package com.moneymanager.global.security;

import com.moneymanager.global.domain.dto.response.AccessToken;
import com.moneymanager.global.security.jwt.JwtTokenProvider;
import com.moneymanager.member.repository.MemberTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.member.auth<br>
 * 파일이름       : CustomAuthSuccessHandler<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 3.<br>
 * 설명              : 로그인 성공 시 원하는 동작을 정의하는 클래스
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
 * 		 	  <td>25. 8. 3.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
@RequiredArgsConstructor
public class CustomAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberTokenRepository tokenRepository;

	private final PasswordEncoder passwordEncoder;


	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		//1. 인증 성공한 회원정보 조회
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		//2. 토큰 생성
		AccessToken accessToken = jwtTokenProvider.generateAccessToken(userDetails);
		AccessToken refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

		//3. 토큰 암호화
		String refreshTokenHash = passwordEncoder.encode(refreshToken.getToken());
		tokenRepository.saveToken(userDetails.getId(), refreshTokenHash, refreshToken.getExpiration());

		//4.쿠키 설정
		Cookie accessCookie = new Cookie("accessToken", accessToken.getToken());
		accessCookie.setHttpOnly(true);			//html에서만 쿠키 조회 가능
		accessCookie.setSecure(true);				//https에서만 전송 가능
		accessCookie.setPath("/");
		accessCookie.setMaxAge((int) JwtTokenProvider.ACCESS_TOKEN_EXPIRATION_SECONDS);

		Cookie refreshCookie = new Cookie("refreshToken", refreshToken.getToken());
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(true);
		refreshCookie.setPath("/api/auth/refresh");
		refreshCookie.setMaxAge((int) JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_SECONDS);

		//5. access / refresh 토큰 저장
		response.addCookie(accessCookie);
		response.addCookie(refreshCookie);

		response.sendRedirect("/home");
	}

}
