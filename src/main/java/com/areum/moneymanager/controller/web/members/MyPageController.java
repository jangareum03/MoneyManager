package com.areum.moneymanager.controller.web.members;

import com.areum.moneymanager.dto.response.main.FaqResponseDTO;
import com.areum.moneymanager.dto.response.member.MemberResponseDTO;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import com.areum.moneymanager.service.member.PointService;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * 회원 정보 및 설정과 관련된 화면을 담당하는 클래스</br>
 * 내정보, 포인트, 설정 등의 화면을 처리
 *
 * @version 1.0
 */
@Controller
@RequestMapping("/mypage")
public class MyPageController {

	private MemberServiceImpl memberService;
	private PointService pointService;

	public MyPageController(MemberServiceImpl memberService, PointService pointService) {
		this.memberService = memberService;
		this.pointService = pointService;
	}



	/**
	 * 마이페이지 화면 요청을 처리합니다.
	 *
	 * @return	마이페이지
	 */
	@GetMapping
	public String getMyPagePage( HttpSession session, Model model ) {

		String memberId = (String)session.getAttribute("mid");

		//사용자에게 전달할 정보
		model.addAttribute("info", memberService.getMemberSummary(memberId) );
		model.addAttribute("point", pointService.getMemberPointSummary(memberId) );
		model.addAttribute("setting", null );

		return "/member/mypage_index";
	}



	/**
	 * 내 정보 화면 요청을 처리합니다.
	 *
	 * @param session	사용자 식별 및 정보를 저장하는 객체
	 * @param model		View 전달할 데이터 객체
	 * @return	내정보 페이지
	 */
	@GetMapping("/info")
	public String getMyInfoPage( HttpSession session, Model model ) {
		//회원 정보 조회
		String memberId = (String)session.getAttribute("mid");

		MemberResponseDTO.MyPage summary = memberService.getMemberSummary( memberId );
		MemberResponseDTO.Info info = memberService.getMemberInfo(memberId);

		//사용자에게 전달할 정보
		model.addAttribute("summary", summary);
		model.addAttribute("member", info);

		return "/member/mypage_info";
	}



	/**
	 * 비밀번호 변경 팝업 화면 요청을 처리합니다.
	 *
	 * @return	비밀번호 변경 팝업 페이지
	 */
	@GetMapping("/password")
	public String getChangePasswordPage( ) {
		return "/include/popup_pwdChange";
	}



	/**
	 *	회원탈퇴 팝업 화면 요청을 처리합니다.
	 *
	 * @param session	사용자 식별 및 정보를 저장하는 객체
	 * @param model		View 전달할 데이터 객체
	 * @return	회원탈퇴 팝업 페이지
	 */
	@GetMapping("/delete")
	public String getMemberDeletePage( HttpSession session, Model model ) {
		model.addAttribute("id", memberService.getId( (String)session.getAttribute("mid") ));

		return "/member/mypage_delete";
	}



	@GetMapping("/point")
	public String getMemberPointPage( HttpSession session, Model model ) {
		String memberId = (String)session.getAttribute("mid");

		model.addAttribute("point", pointService.getMemberPointSummary(memberId) );
		model.addAttribute("list", null);

		return "/member/mypage_point";
	}
}
