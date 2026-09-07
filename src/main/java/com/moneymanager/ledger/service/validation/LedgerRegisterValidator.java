package com.moneymanager.ledger.service.validation;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.enums.FixedType;
import org.springframework.stereotype.Component;

import static com.moneymanager.global.exception.code.ErrorCode.REQUIRED_NOT_EXIST;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.validation<br>
 * 파일이름       : LedgerRegisterValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 17<br>
 * 설명              : 가계부 등록 요청 검증을 처리하는 클래스
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
 * 		 	  <td>26. 8. 17</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class LedgerRegisterValidator {

    private final String work = "가계부 등록 요청 검증";

    private final LedgerFieldValidator fieldValidator;

    public LedgerRegisterValidator(LedgerFieldValidator fieldValidator) {
        this.fieldValidator = fieldValidator;
    }

    public void validate(LedgerWriteRequest request) {
        if(request == null) {
            throw new ApplicationException(
                    REQUIRED_NOT_EXIST,
                    LogContent.of(
                            work,
                            LedgerWriteRequest.class
                    )
            );
        }

        validateRequiredFields(request);
        validateOptionalFields(request);
    }


    //===== validate 보조 메서드 =====
    private void validateRequiredFields(LedgerWriteRequest request) {
        fieldValidator.validateDate(request.getDate(), work);
        fieldValidator.validateCategory(request.getCategoryCode(), work);
        fieldValidator.validateFix(request.getFixed(), work);
        fieldValidator.validateAmount(request.getAmount(), work);
        fieldValidator.validatePaymentType(request.getPaymentType(),work);
    }

    private void validateOptionalFields(LedgerWriteRequest request) {
        if(request.getFixed().equalsIgnoreCase(FixedType.REPEAT.getValue())) {
            fieldValidator.validateFixCycle(request.getFixCycle(), work);
        }

        if(request.hasImage()) {
            fieldValidator.validateImages(request.getImages());
        }

        fieldValidator.validatePlace(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress(), work);
    }

}