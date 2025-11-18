package com.moneymanager.controller.api.main;

import com.moneymanager.domain.global.dto.ApiResultDTO;
import com.moneymanager.domain.sub.dto.InquiryAccessRequest;
import com.moneymanager.domain.sub.dto.InquirySearchRequest;
import com.moneymanager.domain.sub.dto.InquiryListResponse;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.service.main.InquiryService;
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
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
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
	public ResponseEntity<InquiryListResponse> putSearch(@RequestBody InquirySearchRequest search ) {
		return ResponseEntity.ok().body(inquiryService.getInquiriesBySearch( search ));
	}


	//작성자 확인
	@GetMapping("/{id}/writer")
	public ResponseEntity<Boolean> getWriter(HttpSession session, @PathVariable Long id ){
		InquiryAccessRequest writer = new InquiryAccessRequest(id, (String)session.getAttribute("mid"));

		return ResponseEntity.ok().body(inquiryService.isWriter(writer));
	}



	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResultDTO> delete(@PathVariable Long id, HttpSession session ) {
		try{
			inquiryService.deleteInquiry( (String)session.getAttribute("mid"), id );

			return ResponseEntity.ok( ApiResultDTO.builder().success(true).message("문의사항을 삭제했습니다.").build() );
		}catch ( ClientException e ) {
			return ResponseEntity.ok( ApiResultDTO.builder().success(false).message(e.getMessage()).build() );
		}
	}
}

