package com.areum.moneymanager.controller.web.main;

import com.areum.moneymanager.dto.request.main.QnARequestDTO;
import com.areum.moneymanager.dto.request.main.SupportRequestDTO;
import com.areum.moneymanager.dto.response.main.QnAResponseDTO;
import com.areum.moneymanager.dto.response.main.SupportResponseDTO;
import com.areum.moneymanager.service.main.InquiryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/support/inquiry")
public class InquiryController {

	private final Logger logger = LogManager.getLogger(this);
	private final InquiryService inquiryService;

	public InquiryController(InquiryService inquiryService ) {
		this.inquiryService = inquiryService;
	}



	/**
	 *	Q&A 내역 조회 페이지 요청을 처리합니다<br>
	 * 비밀글 여부에 따라 제목이 안보여질 수도 있습니다.
	 *
	 * @param session			사용자 식별 및 정보를 저장하는 객체
	 * @param model				뷰에 전달할 객체
	 * @return	Q&A 리스트 페이지
	 */
	@GetMapping
	public String getInquiryPage( HttpSession session, Model model, SupportRequestDTO.Page page  ) {
		//검색어 초기화
		SupportRequestDTO.Search search = SupportRequestDTO.Search.builder().mode("all").keyword(null).build();

		SupportResponseDTO.InquiryList list = inquiryService.getInquiriesByPage( page, search );

		model.addAttribute("inquiries", list.getInquiries());
		model.addAttribute("page", list.getPage());
		model.addAttribute("search", search);

		return "/main/support_inquiry";
	}



	/**
	 * 질문 작성 페이지 요청을 처리합니다.
	 *
	 * @return	질문 작성 페이지
	 */
	@GetMapping("/write")
	public String getInquiryWritePage() {
		return "/main/support_inquiry_write";
	}



	@PostMapping
	public String postWrite( HttpSession session, QnARequestDTO.Create question ){
		Long id = inquiryService.createQuestion( (String)session.getAttribute("mid"), question );

		return "redirect:/support/inquiry/" + id;
	}



	@PutMapping
	public String putInquiryUpdate( @ModelAttribute QnARequestDTO.Update update, HttpSession session ) {
		Long id = inquiryService.updateQuestion( (String)session.getAttribute("mid"), update );

		return "redirect:/support/inquiry/" + id;
	}


	@GetMapping("/{id}")
	public String getInquiryWritePage( @PathVariable Long id, Model model, HttpSession session ) {
		QnAResponseDTO.Detail detail = inquiryService.getQnA( id );

		model.addAttribute("question", detail.getQuestion());
		model.addAttribute("answer", detail.getAnswer());
		model.addAttribute("isWriter", inquiryService.isWriter( new QnARequestDTO.CheckWriter( id, (String)session.getAttribute("mid") )) );

		return "/main/support_inquiry_detail";
	}



	@GetMapping("/edit/{id}")
	public String getInquiryEditPage( @PathVariable Long id, Model model, HttpSession session ) {
		QnAResponseDTO.Update question = inquiryService.getQuestionById( id );

		model.addAttribute("question", question);
		model.addAttribute("id", id);

		return "/main/support_inquiry_update";
	}

}
