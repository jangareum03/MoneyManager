package com.moneymanager.delete.controller.web.main;

import com.moneymanager.delete.service.main.FaqService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.controller.web.main<br>
 *  * 파일이름       : FaqController<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 자주묻는질문 관련 화면을 처리하는 클래스
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
