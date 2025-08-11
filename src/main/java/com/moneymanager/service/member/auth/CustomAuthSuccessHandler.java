package com.moneymanager.service.member.auth;

import com.moneymanager.dto.member.response.MemberLoginResponse;
import com.moneymanager.service.member.AuthService;
import com.moneymanager.utils.LoggerUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
public class CustomAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final AuthService authService;

	public CustomAuthSuccessHandler( AuthService authService ) {
		this.authService = authService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		//인증된 사용자 정보
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		//로그인 성공한 회원 정보를 세션 저장
		MemberLoginResponse.Success member = authService.getLoginMember(userDetails.getUsername());
		HttpSession session = request.getSession();
		session.setAttribute("mid", member.getMemberId());
		session.setAttribute("nickName", member.getNickName());
		session.setAttribute("profile", member.getMemberId());

		LoggerUtil.logSystemInfo("로그인 성공 - 사용자ID: {}", userDetails.getUsername());

		//로그인 성공 후 이동할 페이지 리다이렉트
		response.sendRedirect("/attendance");
	}


}
