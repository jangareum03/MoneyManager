package com.moneymanager.ledger.service.validation;

import com.moneymanager.global.exception.exception.ExternalException;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.validation.BaseImageValidator;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.moneymanager.global.exception.code.CommonErrorCode.FILE_NOT_FOUND;
import static com.moneymanager.global.exception.code.CommonErrorCode.FILE_READ_FAILED;


/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.validation<br>
 * 파일이름       : LedgerImageValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 18<br>
 * 설명              : 가계부 이미지  검증을 처리하는 클래스
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
 * 		 	  <td>26. 8. 18</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class LedgerImageValidator extends BaseImageValidator {

    private final List<String> allowedHeaders = List.of("89504E47", "FFD8FFE0");
    private final List<String> allowedExtensions = List.of("jpg", "jpeg", "png");

    public void validate(MultipartFile file) {
        if(file == null) {
            throw ValidationException.of(
                    FILE_NOT_FOUND,
                    LogContent.of(
                            "가계부 이미지 검증",
                            MultipartFile.class
                    )
            );
        }

        try{
            validateHeader(file, allowedHeaders);
            validateContentType(file.getContentType());
            validateExtension(file, allowedExtensions);
            validateSize(file.getSize());
        }catch (IOException e) {
            throw ExternalException.of(
                    FILE_READ_FAILED,
                    LogContent.of(
                            "가계부 이미지 검증",
                            LedgerWriteRequest.class,
                            "images",
                            file.getOriginalFilename()
                    ),
                    e
            );
        }
    }

}