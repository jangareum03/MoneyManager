package com.moneymanager.ledger.service.validation;

import com.moneymanager.global.exception.code.CommonErrorCode;
import com.moneymanager.global.exception.exception.InternalException;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.enums.CategoryType;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static com.moneymanager.global.domain.enums.RegexPattern.*;
import static com.moneymanager.global.util.string.StringUtil.isNullOrBlank;
import static com.moneymanager.global.util.string.StringUtil.matchesPattern;

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

    private final LedgerImageValidator imageValidator;

    public LedgerRegisterValidator(LedgerImageValidator imageValidator) {
        this.imageValidator = imageValidator;
    }

    public void validate(LedgerWriteRequest request) {
        if(request == null) {
            throw InternalException.of(
                    CommonErrorCode.REQUIRED_NOT_EXIST,
                    LogContent.of(
                            "가계부 등록 요청 검증",
                            LedgerWriteRequest.class
                    )
            );
        }

        validateRequiredFields(request);
        validateOptionalFields(request);
    }


    //===== validate 보조 메서드 =====
    private void validateRequiredFields(LedgerWriteRequest request) {
        validateDate(request.getDate());
        validateCategory(request.getCategoryCode());
        validateFix(request.getFixed());
        validateAmount(request.getAmount());
        validatePaymentType(request.getPaymentType());
    }

    private void validateOptionalFields(LedgerWriteRequest request) {
        validateFixCycle(request.getFixed(), request.getFixCycle());
        validateImages(request.getImages());
        validatePlace(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress());
    }

    private void validateDate(String date) {
        if (isNullOrBlank(date)) {
            throwRequiredException("date", date);
        }

        String DATE_FORMAT = "^[12]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$";
        if (!matchesPattern(date, DATE_FORMAT)) {
            throwFormatException("date", date, "yyyyMMdd (예: 20260101)");
        }
    }

    private void validateCategory(String categoryCode) {
        if (isNullOrBlank(categoryCode)) {
            throwRequiredException("category", categoryCode);
        }

        if (!matchesPattern(categoryCode, "\\d{6}")) {
            throwFormatException("category", categoryCode, "6자리 숫자 (예: 123456)");
        }

        List<String> codes = Arrays.stream(CategoryType.values())
                                   .map(CategoryType::getPrefix)
                                   .toList();

        if (!codes.contains(categoryCode.substring(0, 2))) {
            throwValueException("category", categoryCode, codes.toString());
        }
    }

    private void validateAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throwRequiredException("amount", amount);
        }
    }

    private void validatePaymentType(String type) {
        if (isNullOrBlank(type)) {
            throwRequiredException("paymentType", type);
        }
    }

    private void validateFix(String fix) {
        if (isNullOrBlank(fix)) {
            throwRequiredException("fix", fix);
        }

        List<String> fixes = Arrays.stream(FixedType.values())
                                   .map(t -> t.getValue().toLowerCase())
                                   .toList();

        if(!fixes.contains(fix)) {
            throwValueException("fix", fix, fixes.toString());
        }
    }

    private void validateFixCycle(String fix, String fixCycle) {
        if(fix.equalsIgnoreCase(FixedType.VARIABLE.getValue())) {
            return;
        }

        if (isNullOrBlank(fixCycle)) {
            throwRequiredException("fixCycle", fixCycle);
        }

        List<String> cycles = Arrays.stream(FixCycle.values())
                .map(t -> t.getValue().toLowerCase())
                .toList();

        if(!cycles.contains(fixCycle)) {
            throwValueException("fixCycle", fixCycle, cycles.toString());
        }
    }

    private void validateImages(List<MultipartFile> multipartFiles) {
        if(multipartFiles == null || multipartFiles.isEmpty()) {
            return;
        }

        for(MultipartFile file : multipartFiles) {
            imageValidator.validate(file);
        }
    }

    private void validatePlace(String placeName, String roadAddress, String detailAddress) {
         //1. 장소명 검증
        if (!StringUtil.matchesPattern(placeName, ADDRESS_PLACE_NAME.getPattern())) {
            throwFormatException("placeName", placeName, "한글, 영문, 숫자, 공백, 괄호, 하이픈, 점");
        }

        //2. 기본주소 검증
        if (!StringUtil.matchesPattern(roadAddress, ADDRESS_ROAD_NAME.getPattern())) {
            throwFormatException("roadAddress", roadAddress, "한글, 영문, 숫자, 공백, 하이픈");
        }

        //3. 상세주소 검증
        if (!isNullOrBlank(detailAddress) && !StringUtil.matchesPattern(detailAddress, ADDRESS_DETAIL_NAME.getPattern())) {
            throwFormatException("detailAddress", detailAddress, "한글, 영문, 숫자, 공백, 하이픈, 괄호, 쉼표, 슬래시, 점, #");
        }
    }


    //===== 유틸 메서드 =====
    private void throwRequiredException(String field, Object value) {
        throw ValidationException.of(
                CommonErrorCode.REQUIRED_VALUE,
                LogContent.of(
                        "가계부 등록 요청 검증",
                        LedgerWriteRequest.class,
                        field,
                        value
                )
        );
    }

    private void throwFormatException(String field, Object value, String format) {
        throw ValidationException.of(
                CommonErrorCode.INVALID_FORMAT,
                LogContent.of(
                        "가계부 등록 요청 검증",
                        LedgerWriteRequest.class,
                        field,
                        value
                ).withOption("format", format)
        );
    }

    private void throwValueException(String field, Object value, String allowedValues) {
        throw ValidationException.of(
                CommonErrorCode.INVALID_VALUE,
                LogContent.of(
                        "가계부 등록 요청 검증",
                        LedgerWriteRequest.class,
                        field,
                        value
                ).withOption("allowed", allowedValues)
        );
    }

}