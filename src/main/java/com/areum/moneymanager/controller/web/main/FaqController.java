package com.areum.moneymanager.controller.web.main;

import com.areum.moneymanager.service.main.FaqService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/support/faq")
public class FaqController {

	private final FaqService faqService;

	public FaqController( FaqService faqService ) {
		this.faqService = faqService;
	}


	/**
	 * 자주 묻는 질문 조회 페이지 요청을 처리합니다.<br>
	 * 자주 묻는 질문과 답변은 파일에 저장되어 있으면 해당 파일(.json)을 읽어올 수 없다면 알럿이 노출됩니다.
	 *
	 * @param model			뷰에 전달할 객체
	 * @return	자주 묻는 질문 페이지
	 */
	@GetMapping
	public String getFAQPage( Model model ) throws IOException {
		model.addAttribute("faqList", faqService.getFqaList());

		return "/main/support_faq";
	}

}
