package com.moneymanager.controller.web.members;

import com.moneymanager.dto.member.request.MemberRecoveryRequest;
import com.moneymanager.dto.member.response.MemberRecoveryResponse;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.service.member.MemberServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.controller.web.main<br>
 *  * 파일이름       : AccountRecoveryController<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 계정 관련 화면을 처리하는 클래스
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
	public String postId(MemberRecoveryRequest.Id findID, Model model ) {
		String page = "/member/recovery_id_fail";

		try{
			MemberRecoveryResponse.Id result = memberService.findMaskedIdAndMessage( findID );
			model.addAttribute("find", result );

			page = "/member/recovery_id_success";
		}catch ( ClientException e ) {
			logger.debug("아이디 찾기에 실패했습니다. ({}: {})", e.getErrorCode(), e.getMessage());
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
	public String findPassword( HttpServletRequest request, MemberRecoveryRequest.Password findPwd, Model model ) {
		String page = "/member/recovery_password_fail";

		try{
			MemberRecoveryResponse.Password email = memberService.recoverPassword( request, findPwd);
			model.addAttribute("find", email);

			page = "/member/recovery_password_success";
		}catch ( ClientException e ) {
			logger.debug("비밀번호 찾기에 실패했습니다. ({}: {})", e.getErrorCode(), e.getMessage());
		}


		return page;
	}
}
