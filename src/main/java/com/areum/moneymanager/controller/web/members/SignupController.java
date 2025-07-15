package com.areum.moneymanager.controller.web.members;

import com.areum.moneymanager.dto.request.member.LoginRequestDTO;
import com.areum.moneymanager.exception.ErrorException;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * 회원의 회원가입과 관련된 화면하는 클래스</br>
 * 회원가입 등의 화면을 처리
 *
 * @version 1.0
 */
@Controller
@RequestMapping("/signup")
public class SignupController {

	private final Logger logger = LogManager.getLogger(this);

	private final MemberServiceImpl memberService;

	public SignupController( MemberServiceImpl memberService ) {
		this.memberService = memberService;
	}



	/**
	 * 회원가입을 진행하기 위해 회원가입 페이지 요청을 처리합니다.
	 *
	 * @param model		View 전달할 데이터 객체
	 * @return 회원가입 페이지
	 */
	@GetMapping
	public String getSignUpPage( Model model ) {

		model.addAttribute("member", LoginRequestDTO.SignUp.builder().gender("n").build());

		return "/member/signup";
	}



	/**
	 *	입력한 정보로 회원가입 진행 요청을 처리합니다.
	 *
	 * @param signUp			회원가입 정보
	 * @param redirect		View 전달할 데이터 객체
	 * @return	로그인 페이지
	 */
	@PostMapping
	public String postSignUp(@ModelAttribute("member") LoginRequestDTO.SignUp signUp, RedirectAttributes redirect ) {
		try{
			memberService.createMember(signUp);

			redirect.addFlashAttribute("member", LoginRequestDTO.Login.builder().id(signUp.getId()).build());

			return "redirect:/";
		}catch ( ErrorException e ) {
			redirect.addAttribute("error", e.getErrorMessage());
			redirect.addAttribute("method", "get");
			redirect.addAttribute("url", "/signup");

			return "alert";
		}
	}
}
