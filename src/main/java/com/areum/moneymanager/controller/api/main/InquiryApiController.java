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



/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.controller.api.main.<br>
 *  * 파일이름       : WebConfig<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 문의사항 관련 API를 제공하는 클래스
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
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
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

