package com.moneymanager.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.controller<br>
 * 파일이름       : AuthController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 16<br>
 * 설명              : 회원 인증/계정 관련 화면 요청을 처리하는 클래스
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
 * 		 	  <td>26. 8. 16</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	@GetMapping("/login")
	public String login(@SessionAttribute(value = "loginError", required = false)String error, Model model) {
		model.addAttribute("loginError", error);

		return "/member/member_login";
	}

	@GetMapping("/signup")
	public String showSignupForm() {
		return "/member/signup";
	}

}