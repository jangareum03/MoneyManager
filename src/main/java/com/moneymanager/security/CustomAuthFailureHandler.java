package com.moneymanager.security;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.exception.custom.LoginException;
import com.moneymanager.service.member.MemberServiceImpl;
import com.moneymanager.utils.LoggerUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.member.auth<br>
 * 파일이름       : CustomAuthFailureHandler<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 5.<br>
 * 설명              : 로그인 실패 시 원하는 동작을 정의하는 클래스
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
 * 		 	  <td>25. 8. 5.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

	private MemberServiceImpl memberService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		LoginException loginException = (LoginException) exception;

		ErrorDTO errorDTO = loginException.getErrorDTO();
		request.getSession().setAttribute("error", errorDTO.getMessage());

		LoggerUtil.logUserWarn(errorDTO, "회원 로그인");
		LoggerUtil.logSystemInfo("로그인 실패 - 사용자ID: {}", request.getParameter("username") );
		response.sendRedirect("/");
	}
}
