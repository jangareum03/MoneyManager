package com.areum.moneymanager.controller.web.members;

import com.areum.moneymanager.dto.request.member.LoginRequestDTO;
import com.areum.moneymanager.dto.response.member.LoginResponseDTO;
import com.areum.moneymanager.exception.ErrorException;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 회원 계정과 관련된 화면을 담당하는 클래스</br>
 * 아이디 찾기, 비밀번호 찾기 등의 화면을 처리
 *
 * @version 1.0
 */
@Controller
@RequestMapping("/recovery")
public class AccountRecoveryController {

	private final Logger logger = LogManager.getLogger(this);
	private final MemberServiceImpl memberService;

	public AccountRecoveryController( MemberServiceImpl memberService ) {
		this.memberService = memberService;
	}



	/**
	 * 아이디 찾기 페이지 요청을 처리합니다.
	 *
	 * @return 아이디 찾기 페이지
	 */
	@GetMapping("/id")
	public String getIdPage() {
		return "/member/recovery_id";
	}



	/**
	 * 입력한 정보로 아이디 찾기 요청을 처리합니다.<p>
	 * 입력한 정보에 대한 검증을 수행하며 검증이 실패하면 아이디 찾기 요청이 진행되지 않습니다.<br>
	 * 아이디가 존재하지 않으면 실패 페이지로 이동하며, 아이디가 존재하면 성공 페이지로 이동하나 회원 상태에 따라 안내 문구가 달라집니다.
	 *
	 * @param findID		아이디 찾기 정보
	 * @param model		뷰에 전달할 객체
	 * @return	아이디가 있으면 아이디 찾기 성공 페이지, 없으면 아이디 찾기 실패 페이지
	 */
	@PostMapping("/id")
	public String postId( LoginRequestDTO.FindID findID, Model model ) {
		String page = "/member/recovery_id_fail";

		try{
			LoginResponseDTO.FindID id = memberService.findMaskedIdAndMessage( findID );
			model.addAttribute("find", id );

			page = "/member/recovery_id_success";
		}catch ( ErrorException e ) {
			logger.debug("아이디 찾기에 실패했습니다. ({}: {})", e.getErrorCode(), e.getErrorMessage());
		}

		return page;
	}



	/**
	 * 비밀번호 찾기 페이지 요청을 처리합니다.
	 *
	 * @return 비밀번호 찾기 페이지
	 */
	@GetMapping("/password")
	public String getPasswordPage() {
		return "/member/recovery_password";
	}



	/**
	 * 입력한 정보로 비밀번호 찾기 요청을 처리합니다.<p>
	 * 입력한 정보에 대한 검증을 수행하며 검증이 실패하면 비밀번호 찾기 요청이 진행되지 않습니다.<br>
	 * 입력한 정보가 없으면 실패 페이지로 이동하며, 정보가 있으면 성공 페이지로 이동하나 회원 상태에 따라 안내 문구가 달라집니다.
	 *
	 *
	 * @param request			http 요청정보를 담은 객체
	 * @param findPwd		비밀번호 찾기 정보
	 * @param model 				뷰에 전달할 객체
	 * @return 정보가 있으면 비밀번호 성공 페이지, 없으면 비밀번호 실패 페이지
	 */
	@PostMapping("/password")
	public String findPassword( HttpServletRequest request, LoginRequestDTO.FindPwd findPwd, Model model ) {
		String page = "/member/recovery_password_fail";

		try{
			LoginResponseDTO.FindPwd email = memberService.sendTemporaryPassword( request, findPwd);
			model.addAttribute("find", email);

			page = "/member/recovery_password_success";
		}catch ( ErrorException e ) {
			logger.debug("비밀번호 찾기에 실패했습니다. ({}: {})", e.getErrorCode(), e.getErrorMessage());
		}


		return page;
	}
}
