package com.moneymanager.global.security;

import com.moneymanager.member.service.application.TokenAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

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

	private final TokenAuthService tokenAuthService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		//1. 인증 성공한 회원정보 조회
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		//2. 쿠키에 토큰 저장
		tokenAuthService.issueTokens(userDetails, response);

		//3. 리디렉션 경로 지정
		response.sendRedirect("/home");
	}

}
