package com.moneymanager.service.validation;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ValidationException;

import static com.moneymanager.exception.ErrorCode.*;
import static com.moneymanager.utils.ValidationUtils.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.validation<br>
 * 파일이름       : LedgerValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 22<br>
 * 설명              : 가계부 관련 검증 로직을 처리하는 클래스
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
public class LedgerValidator {

	public void validateForRegister(LedgerWriteRequest request) {
		String action = "가계부 작성";

		validateDate(action, request.getDate());
		validateCategory(action, request.getCategory());
	}

	public void validateForUpdate(LedgerUpdateRequest request){
		String action = "가계부 수정";
	}

	private void validateDate(String action, String date) {
		ErrorCode errorCode = null;

		if(isNullOrBlank(date)) {
			errorCode = LEDGER_DATE_MISSING;
		}else if(isInvalidPattern(date, "\\d{8}")) {
			errorCode = LEDGER_DATE_LENGTH;
		}

		if(errorCode != null) {
			ErrorDTO errorDTO = ErrorDTO.builder()
					.errorCode(errorCode.getCode())
					.serviceName(action)
					.logMessage(errorCode.getLog() + "	|	date=" + date)
					.build();

			throwException(errorDTO, "잠시 후 다시 시도해주세요.");
		}
	}

	private void validateCategory(String action, String category) {
		ErrorCode errorCode = null;

		if(isNullOrBlank(category)) {
			errorCode = LEDGER_CATEGORY_MISSING;
		}else if(isInvalidPattern(category, "\\d{6}")) {
			errorCode = LEDGER_CATEGORY_FORMAT;
		}
	}

	private void throwException(ErrorDTO errorDTO, String userMessage) {
		throw new ValidationException(errorDTO, userMessage);
	}


}