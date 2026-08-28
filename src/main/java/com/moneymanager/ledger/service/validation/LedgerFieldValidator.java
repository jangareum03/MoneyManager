package com.moneymanager.ledger.service.validation;

import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.ledger.domain.enums.LedgerType;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static com.moneymanager.global.domain.enums.RegexPattern.*;
import static com.moneymanager.global.exception.code.ErrorCode.*;
import static com.moneymanager.global.util.string.StringUtil.isNullOrBlank;
import static com.moneymanager.global.util.string.StringUtil.matchesPattern;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.validation<br>
 * 파일이름       : LedgerFieldValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 22<br>
 * 설명              : 가계부 필드 검증을 처리하는 클래스
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
 * 		 	  <td>26. 8. 22</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class LedgerFieldValidator {

    private final LedgerImageValidator imageValidator;

    public LedgerFieldValidator(LedgerImageValidator imageValidator) {
        this.imageValidator = imageValidator;
    }

    void validateDate(String date, String work) {
        if (isNullOrBlank(date)) {
            throwRequiredException(work, "date", date);
        }

        String DATE_FORMAT = "^[12]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$";
        if (!matchesPattern(date, DATE_FORMAT)) {
            throwFormatException(work, "date", date, "yyyyMMdd (예: 20260101)");
        }
    }

    void validateCategory(String categoryCode, String work) {
        if (isNullOrBlank(categoryCode)) {
            throwRequiredException(work, "category", categoryCode);
        }

        if (!matchesPattern(categoryCode, "\\d{6}")) {
            throwFormatException(work, "category", categoryCode, "6자리 숫자 (예: 123456)");
        }

        List<String> codes = Arrays.stream(LedgerType.values())
                .map(LedgerType::getPrefix)
                .toList();

        if (!codes.contains(categoryCode.substring(0, 2))) {
            throwValueException(work, "category", categoryCode, codes.toString());
        }
    }

    void validateAmount(Long amount, String work) {
        if (amount == null || amount <= 0) {
            throwRequiredException(work, "amount", amount);
        }
    }

    void validatePaymentType(String type, String work) {
        if (isNullOrBlank(type)) {
            throwRequiredException(work, "paymentType", type);
        }
    }

    void validateFix(String fix, String work) {
        if (isNullOrBlank(fix)) {
            throwRequiredException(work, "fix", fix);
        }

        List<String> fixes = Arrays.stream(FixedType.values())
                .map(t -> t.getValue().toLowerCase())
                .toList();

        if(!fixes.contains(fix)) {
            throwValueException(work, "fix", fix, fixes.toString());
        }
    }

    void validateFixCycle(String fixCycle, String work) {
        if (isNullOrBlank(fixCycle)) {
            throwRequiredException(work, "fixCycle", fixCycle);
        }

        List<String> cycles = Arrays.stream(FixCycle.values())
                .map(t -> t.getValue().toLowerCase())
                .toList();

        if(!cycles.contains(fixCycle)) {
            throwValueException(work, "fixCycle", fixCycle, cycles.toString());
        }
    }

    void validateImages(List<MultipartFile> multipartFiles) {
        if(multipartFiles == null || multipartFiles.isEmpty()) {
            return;
        }

        for(MultipartFile file : multipartFiles) {
            imageValidator.validate(file);
        }
    }

    void validatePlace(String placeName, String roadAddress, String detailAddress, String work) {
        //1. 장소명 검증
        if (!StringUtil.matchesPattern(placeName, ADDRESS_PLACE_NAME.getPattern())) {
            throwFormatException(work, "placeName", placeName, "한글, 영문, 숫자, 공백, 괄호, 하이픈, 점");
        }

        //2. 기본주소 검증
        if (!StringUtil.matchesPattern(roadAddress, ADDRESS_ROAD_NAME.getPattern())) {
            throwFormatException(work, "roadAddress", roadAddress, "한글, 영문, 숫자, 공백, 하이픈");
        }

        //3. 상세주소 검증
        if (!isNullOrBlank(detailAddress) && !StringUtil.matchesPattern(detailAddress, ADDRESS_DETAIL_NAME.getPattern())) {
            throwFormatException(work, "detailAddress", detailAddress, "한글, 영문, 숫자, 공백, 하이픈, 괄호, 쉼표, 슬래시, 점, #");
        }
    }


    //===== 유틸 메서드 =====
    private void throwRequiredException(String work, String field, Object value) {
        throw new ApplicationException(
                REQUIRED_VALUE,
                LogContent.of(
                        work,
                        field,
                        value
                )
        );
    }

    private void throwFormatException(String work, String field, Object value, String format) {
        throw new ApplicationException(
                INVALID_FORMAT,
                LogContent.of(
                        work,
                        field,
                        value
                ).withOption("format", format)
        );
    }

    private void throwValueException(String work, String field, Object value, String allowedValues) {
        throw new ApplicationException(
                INVALID_VALUE,
                LogContent.of(
                        work,
                        field,
                        value
                ).withOption("allowed", allowedValues)
        );
    }

}