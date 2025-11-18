package com.moneymanager.controller.web.members;

import com.moneymanager.domain.member.dto.MemberSignUpRequest;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.service.member.MemberServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.controller.web.main<br>
 *  * 파일이름       : SignupController<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원가입 관련 화면을 처리하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
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
	 * @return 회원가입 페이지
	 */
	@GetMapping
	public String getSignUpPage( ) {
		return "/member/signup";
	}



	/**
	 *	입력한 정보로 회원가입 요청을 처리합니다.
	 *
	 * @param signUp			회원가입 정보
	 * @param redirect		View 전달할 데이터 객체
	 * @return	로그인 페이지
	 */
	@PostMapping
	public String postSignUp(@ModelAttribute("member") MemberSignUpRequest signUp, RedirectAttributes redirect ) {
		try{
			memberService.createMember(signUp);

			redirect.addFlashAttribute("member", signUp.getId());

			return "redirect:/";
		}catch ( ClientException e ) {
			redirect.addAttribute("error", e.getMessage());
			redirect.addAttribute("method", "get");
			redirect.addAttribute("url", "/signup");

			return "alert";
		}
	}
}
