package com.moneymanager.controller.web.members;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * <p>
 * * 패키지이름    : com.moneymanager.controller.web.main<br>
 * * 파일이름       : LoginController<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 15<br>
 * * 설명              : 회원 로그인 관련 화면을 처리하는 클래스
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
 * 		 	  <td>25. 7. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Controller
public class LoginController {


	/**
	 * 로그인 페이지 요청을 처리합니다.
	 *
	 * @return 로그인 페이지
	 */
	@GetMapping("/")
	public String getLoginPage(HttpSession session, Model model) {
		String error = (String) session.getAttribute("error");
		if( error != null ) {
			model.addAttribute("error", error);
			session.removeAttribute("error");
		}

		return "member/member_login";
	}


	/**
	 * 로그아웃을 요청합니다.<p>
	 * 로그아웃 후 로그인 페이지로 이동합니다.
	 *
	 * @param request http 요청정보를 담은 객체
	 * @return 로그인 페이지
	 */
	@GetMapping("/logout")
	public String getLogout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session != null) {
			session.invalidate();
		}

		return "redirect:/";
	}
}
