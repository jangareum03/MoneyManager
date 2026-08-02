package com.moneymanager.ledger.controller;

import com.moneymanager.global.domain.response.ApiResponse;
import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.dto.response.LedgerDetailResponse;
import com.moneymanager.ledger.service.command.LedgerCommandService;
import com.moneymanager.ledger.service.validator.LedgerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.controller.web.ledger<br>
 * 파일이름       : LedgerApiController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 5. 11<br>
 * 설명              : 가계부 관련 데이터 요청을 처리하는 클래스
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
 * 		 	  <td>26. 5. 11</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ledgers")
public class LedgerApiController {

	private final LedgerCommandService commandService;

	private final LedgerValidator validator;

	@PatchMapping("/{code}")
	public ApiResponse<LedgerDetailResponse> update(@PathVariable String code, @RequestPart("ledger") LedgerUpdateRequest request, @RequestPart(value = "images", required = false) List<MultipartFile> fileList) {
		//1. 요청 검증
		validator.update(request);

		//2. 가계부 이미지 반영
		request.attachImages(fileList);

		//3. 가계부 수정 후 반환
		LedgerDetailResponse response = commandService.update(code, request);

		//4. 수정된 가계부 객체 반환
		return ApiResponse.success("가계부 수정 완료했습니다.", response);
	}

}
