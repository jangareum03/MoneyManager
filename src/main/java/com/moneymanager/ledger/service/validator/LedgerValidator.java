package com.moneymanager.ledger.service.validator;

import com.moneymanager.global.validation.BaseImageValidator;
import com.moneymanager.global.validation.DateValidator;
import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.validation<br>
 * 파일이름       : LedgerValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 22<br>
 * 설명              : 가계부 요청 관련 검증 로직을 처리하는 클래스
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
 * 		 	  <td>26. 1. 22.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class LedgerValidator extends BaseImageValidator {

	private final String work = "가계부 요청 검증";

	public void register(LedgerWriteRequest request) {
		//필수정보 검증
		DateValidator.validateLedgerDate(request.getDate());
		validateCategory(request.getCategoryCode());
		validateAmount(request.getAmount());
		validatePaymentType(request.getPaymentType());

		//선택정보 검증
		validateFixCycle(request.getFixCycle());
	}

	public void update(LedgerUpdateRequest request) {
		//2. 필수정보 검증
		validateCategory(request.getCategoryCode());
		validateAmount(request.getAmount());
		validatePaymentType(request.getPaymentType());

		//3. 선택정보 검증
		validateFixCycle(request.getFixCycle());
	}

	//가계부 카테고리 검증
	private void validateCategory(String categoryCode) {
	}

	//가계부 금액 검증
	private void validateAmount(Long amount){

	}

	private void validatePaymentType(String paymentType) {

	}

	//가계부 고정주기 검증
	private void validateFixCycle(String fixCycle) {

	}

	@Override
	public void validateImage(MultipartFile file) {
		checkSize(file.getSize());
		checkExtension(file.getOriginalFilename(), List.of("png", "jpg", "jpeg"));
		checkIsImage(file.getContentType());
		checkHeader(file, List.of("89504E47", "FFD8FFE0"));	//png(89504E47), jpg(FFD8FFE0)
	}

}