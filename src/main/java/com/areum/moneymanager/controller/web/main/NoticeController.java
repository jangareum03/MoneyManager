package com.areum.moneymanager.controller.web.main;

import com.areum.moneymanager.dto.request.main.SupportRequestDTO;
import com.areum.moneymanager.dto.response.main.SupportResponseDTO;
import com.areum.moneymanager.service.main.NoticeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/support/notice")
public class NoticeController {

	private final Logger logger = LogManager.getLogger(this);
	private final NoticeService noticeService;

	public NoticeController( NoticeService noticeService ) {
		this.noticeService = noticeService;
	}

	@GetMapping
	public String getNoticePage(Model model, SupportRequestDTO.Page noticePage) {
		SupportResponseDTO.NoticeList noticeList = noticeService.getNoticesByPage( noticePage );

		model.addAttribute("notices", noticeList.getNotices() );
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
