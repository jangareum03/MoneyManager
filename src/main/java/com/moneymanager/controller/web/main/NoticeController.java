package com.moneymanager.controller.web.main;

import com.moneymanager.domain.global.dto.PageRequest;
import com.moneymanager.domain.sub.dto.NoticeListResponse;
import com.moneymanager.service.main.NoticeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.controller.web.main<br>
 *  * 파일이름       : NoticeController<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 공지사항 관련 화면을 처리하는 클래스
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
@RequestMapping("/support/notice")
public class NoticeController {

	private final Logger logger = LogManager.getLogger(this);
	private final NoticeService noticeService;

	public NoticeController( NoticeService noticeService ) {
		this.noticeService = noticeService;
	}

	@GetMapping
	public String getNoticePage(Model model, PageRequest page) {
		NoticeListResponse noticeList = noticeService.getNoticesByPage( page );

		model.addAttribute("notices", noticeList.getList() );
		model.addAttribute("page", noticeList.getPage());

		return "/main/support_notice";
	}



	@GetMapping("/{id}")
	public String getNoticeDetail( Model model, @PathVariable String id ) {
		try{
			model.addAttribute("notice", noticeService.getNoticeById(id));

			return "/main/support_notice_detail";
		}catch ( IllegalArgumentException e ) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("method", "get");
			model.addAttribute("url", "/support/notice");

			return "alert";
		}
	}
}
