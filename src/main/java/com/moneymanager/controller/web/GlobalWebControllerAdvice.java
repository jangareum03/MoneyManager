package com.moneymanager.controller.web;

import com.moneymanager.dto.member.response.MemberLoginResponse;
import com.moneymanager.security.jwt.JwtTokenProvider;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 패키지이름    : com.moneymanager.controller<br>
 * 파일이름       : GlobalWebControllerAdvice<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 7.<br>
 * 설명              : 전역 화면에서 공통적인 기능을 처리하는 클래스
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
 * 		 	  <td>25. 11. 7.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ControllerAdvice
public class GlobalWebControllerAdvice {

	private final JwtTokenProvider tokenProvider;

	public GlobalWebControllerAdvice(JwtTokenProvider jwtTokenProvider) {
		this.tokenProvider = jwtTokenProvider;
	}

	@ModelAttribute("member")
	public MemberLoginResponse.Success getMemberInfo(HttpServletRequest request) {
		String token = getTokenFromCookies(request);

		if( token != null && tokenProvider.validateToken(token) ) {
			return MemberLoginResponse.Success.builder()
					.nickName(tokenProvider.getNickName(token))
					.profile(tokenProvider.getProfile(token))
					.build();
		}

		return null;
	}

	private String getTokenFromCookies(HttpServletRequest request) {
		if( request.getCookies() == null) return  null;

		for(Cookie cookie : request.getCookies() ) {
			if( "accessToken".equals(cookie.getName()) ) {
				return cookie.getValue();
			}
		}

		return null;
	}
}
