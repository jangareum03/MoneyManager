package com.areum.moneymanager.controller.web.main;

import com.areum.moneymanager.dto.inquiry.QuestionDTO;
import com.areum.moneymanager.dto.inquiry.request.InquiryAccessRequest;
import com.areum.moneymanager.dto.inquiry.request.InquirySearchRequest;
import com.areum.moneymanager.dto.inquiry.request.InquiryUpdateRequest;
import com.areum.moneymanager.dto.inquiry.request.InquiryWriteRequest;
import com.areum.moneymanager.dto.inquiry.response.InquiryDetailResponse;
import com.areum.moneymanager.dto.inquiry.response.InquiryListResponse;
import com.areum.moneymanager.dto.inquiry.response.InquiryUpdateResponse;
import com.areum.moneymanager.service.main.InquiryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.controller.web.main<br>
 * * 파일이름       : InquiryController<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 15<br>
 * * 설명              : 문의사항 관련 화면을 처리하는 클래스
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
@Controller
@RequestMapping("/support/inquiry")
public class InquiryController {

	private final Logger logger = LogManager.getLogger(this);
	private final InquiryService inquiryService;

	public InquiryController(InquiryService inquiryService) {
		this.inquiryService = inquiryService;
	}


	/**
	 * 문의사항 조회 페이지 요청을 처리합니다<br>
	 * 비밀글 여부에 따라 제목이 안보여질 수도 있습니다.
	 *
	 * @param session 사용자 식별 및 정보를 저장하는 객체
	 * @param model   뷰에 전달할 객체
	 * @param search  요청된 검색 정보를 담은 객체
	 * @return Q&A 리스트 페이지
	 */
	@GetMapping
	public String getInquiryPage(HttpSession session, Model model, InquirySearchRequest search) {
		InquiryListResponse list = inquiryService.getInquiriesBySearch(search);

		model.addAttribute("inquiries", list.getList());
		model.addAttribute("page", list.getPage());
		model.addAttribute("search", list.getSearch());

		return "/main/support_inquiry";
	}


	/**
	 * 질문 작성 페이지 요청을 처리합니다.
	 *
	 * @return 질문 작성 페이지
	 */
	@GetMapping("/write")
	public String getInquiryWritePage() {
		return "/main/support_inquiry_write";
	}


	@PostMapping
	public String postWrite(HttpSession session, InquiryWriteRequest request) {
		QuestionDTO question = request.getQuestion();

		return "redirect:/support/inquiry/" + inquiryService.createQuestion((String) session.getAttribute("mid"), question);
	}


	@PutMapping
	public String putInquiryUpdate(HttpSession session, @ModelAttribute InquiryUpdateRequest update) {
		Long id = inquiryService.updateQuestion((String) session.getAttribute("mid"), update);

		return "redirect:/support/inquiry/" + id;
	}


	@GetMapping("/{id}")
	public String getInquiryWritePage(@PathVariable Long id, Model model, HttpSession session) {
		InquiryDetailResponse detail = inquiryService.getQnA(id);

		model.addAttribute("question", detail.getQuestion());
		model.addAttribute("answer", detail.getAnswer());
		model.addAttribute("isWriter", inquiryService.isWriter(new InquiryAccessRequest(id, (String) session.getAttribute("mid"))));

		return "/main/support_inquiry_detail";
	}


	@GetMapping("/edit/{id}")
	public String getInquiryEditPage(@PathVariable Long id, Model model) {
		InquiryUpdateResponse question = inquiryService.getQuestionById(id);

		model.addAttribute("question", question.getQuestion());
		model.addAttribute("id", id);

		return "/main/support_inquiry_update";
	}

}
