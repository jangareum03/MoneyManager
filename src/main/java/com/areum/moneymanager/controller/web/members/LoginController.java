package com.areum.moneymanager.controller.web.members;

import com.areum.moneymanager.dto.request.member.LoginRequestDTO;
import com.areum.moneymanager.dto.response.member.LoginResponseDTO;
import com.areum.moneymanager.exception.ErrorException;
import com.areum.moneymanager.service.member.AuthService;
import com.areum.moneymanager.service.member.ImageServiceImpl;
import org.apache.logging.log4j.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Objects;


/**
 * 회원의 서비스 이용과 관련된 화면을 담당하는 클래스</br>
 * 로그인, 로그아웃 등의 화면을 처리
 *
 * @version 1.0
 */
@Controller
public class LoginController {

	private final Logger logger = LogManager.getLogger(this);


	private final AuthService authService;
	private final ImageServiceImpl imageService;


	public LoginController( AuthService authService, ImageServiceImpl imageService ) {
		this.authService  = authService;
		this.imageService = imageService;
	}


	/**
	 *  로그인 페이지 요청을 처리합니다.
	 *
	 * @return 로그인 페이지
	 */
	@GetMapping
	public String getLoginPage( @ModelAttribute("member") LoginRequestDTO.Login member ) {
		return "index";
	}



	/**
	 * 입력한 정보로 서비스 접속 요청을 처리합니다.
	 * <p>
	 * 접속 요청 정보를 기록하며, 회원의 상태에 따라 서비스 접속 여부가 달라집니다.
	 *
	 * @param request http 요청정보를 담은 객체
	 * @param model 		뷰에 전달할 객체
	 * @param login 		서비스 접속 요청하는 로그인 정보
	 *
	 * @return 서비스 메인 페이지, 접속 실패하면 로그인 페이지
	 */
	@PostMapping("/login")
	public String postLogin(HttpServletRequest request, Model model, @ModelAttribute("member") LoginRequestDTO.Login login, BindingResult bindingResult ) {
		String page = "index";

		try{
			LoginResponseDTO.Login member = authService.login( login, request );

			if( Objects.nonNull(member) ) {	//로그인 가능한 경우
				LocalDate today = LocalDate.now();

				HttpSession session = request.getSession();
				session.setAttribute("mid", member.getMemberId());
				session.setAttribute("nickName", member.getNickName());
				session.setAttribute("profile", imageService.findImage(member.getMemberId()));

				page = "redirect:/attendance?year=" + today.getYear() + "&month=" + today.getMonthValue();
			}
		}catch ( ErrorException e ) {
			logger.debug("{} 아이디는 로그인 실패했습니다. ({}: {})", login.getId(), e.getErrorCode(), e.getErrorMessage());

			switch ( e.getErrorCode() ) {
				case "MEMBER_101" :
					bindingResult.rejectValue("id", "empty.id", e.getErrorMessage());
					break;
				case "MEMBER_102" :
					bindingResult.rejectValue("id", "format.id", e.getErrorMessage());
					break;
				case "MEMBER_103" :
					bindingResult.rejectValue("password", "empty.password", e.getErrorMessage());
					break;
				case "MEMBER_104" :
					bindingResult.rejectValue("password", "format.password", e.getErrorMessage());
					break;
				case "MEMBER_105" :
					bindingResult.rejectValue("id", "mismatch,id", "");
					bindingResult.rejectValue("password", "mismatch.password", e.getErrorMessage());
					break;
			}

		}


		return page;
	}



	/**
	 *	로그아웃을 요청합니다.<p>
	 * 로그아웃 후 로그인 페이지로 이동합니다.
	 *
	 * @param request http 요청정보를 담은 객체
	 * @return 로그인 페이지
	 */
	@GetMapping("/logout")
	public String getLogout( HttpServletRequest request ) {
		HttpSession session = request.getSession(false);

		if( session != null ) {
			session.invalidate();
		}

		return "redirect:/";
	}
}
