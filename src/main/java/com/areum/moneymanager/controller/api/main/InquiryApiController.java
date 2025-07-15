package com.areum.moneymanager.controller.api.main;

import com.areum.moneymanager.dto.request.main.QnARequestDTO;
import com.areum.moneymanager.dto.request.main.SupportRequestDTO;
import com.areum.moneymanager.dto.response.ApiResponseDTO;
import com.areum.moneymanager.dto.response.main.SupportResponseDTO;
import com.areum.moneymanager.exception.ErrorException;
import com.areum.moneymanager.service.main.InquiryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/support")
public class InquiryApiController {

	private final Logger logger = LogManager.getLogger(this);
	private final InquiryService inquiryService;

	public InquiryApiController(InquiryService inquiryService) {
		this.inquiryService = inquiryService;
	}


	//질문 검색
	@PutMapping("/search")
	public ResponseEntity<SupportResponseDTO.InquiryList> putSearch(@RequestBody SupportRequestDTO.SearchAPI searchAPI ) {
		return ResponseEntity.ok().body(inquiryService.getInquiriesBySearch( searchAPI.getPagination(), searchAPI.getSearch()));
	}


	//작성자 확인
	@GetMapping("/{id}/writer")
	public ResponseEntity<Boolean> getWriter(HttpSession session, @PathVariable Long id ){
		QnARequestDTO.CheckWriter writer = new QnARequestDTO.CheckWriter( id, (String)session.getAttribute("mid") );

		return ResponseEntity.ok().body(inquiryService.isWriter(writer));
	}



	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id, HttpSession session ) {
		try{
			inquiryService.deleteInquiry( (String)session.getAttribute("mid"), id );

			return ResponseEntity.ok( ApiResponseDTO.builder().success(true).message("문의사항을 삭제했습니다.").build() );
		}catch ( ErrorException e ) {
			return ResponseEntity.ok( ApiResponseDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}
}

