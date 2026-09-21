package com.moneymanager.member.service.validation;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.moneymanager.global.exception.code.ErrorCode.*;
import static com.moneymanager.global.util.string.StringUtil.isNullOrBlank;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.validation<br>
 * 파일이름       : MemberProfileValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 18.<br>
 * 설명              :
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
 * 		 	  <td>26. 9. 18.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class MemberProfileValidator {

    private final String work = "회원 프로필 파일 검증";

    public void validate(MultipartFile file) {
        validateContentType(file.getContentType());
        validateExtension(file, List.of("jpg", "jpeg", "png"));
        validateHeader(file, List.of("89504E47", "FFD8FFE0"));
        validateSize(file.getSize());
    }

    private void validateContentType(String contentType) {
        if (contentType == null || !contentType.contains("image")) {
            throw new ApplicationException(
                    UNSUPPORTED_FILE_TYPE,
                    LogContent.of(
                            work,
                            MultipartFile.class,
                            "contentType",
                            contentType
                    )
            ).withMessageKey("file.type.invalid");
        }
    }

    private void validateExtension(MultipartFile file, List<String> allowedExtensions) {
        String fileName = file.getOriginalFilename();

        if (isNullOrBlank(fileName)) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            work,
                            MultipartFile.class,
                            "originalFilename",
                            fileName
                    )
            ).withMessageKey("file.extension.missing");
        }

        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (!allowedExtensions.contains(ext)) {
            throw new ApplicationException(
                    UNSUPPORTED_FILE_TYPE,
                    LogContent.of(
                            work,
                            MultipartFile.class,
                            "originalFilename",
                            fileName
                    ).withOption("allowed", allowedExtensions)
            ).withMessageKey("file.type.invalid");
        }
    }

    protected void validateHeader(MultipartFile file, List<String> allowedHeaders) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = is.readNBytes(4);

            String hex = byteToHex(header);

            if (!allowedHeaders.contains(hex)) {
                throw new ApplicationException(
                        UNSUPPORTED_FILE_TYPE,
                        LogContent.of(
                                work,
                                MultipartFile.class,
                                "header",
                                hex
                        )
                ).withMessageKey("file.type.invalid");
            }
        } catch (IOException e) {
            throw new ApplicationException(
                    FILE_READ_FAILED,
                    LogContent.of(
                            work,
                            MultipartFile.class,
                            "file", file.getOriginalFilename()
                    )
            ).withMessageKey("file.read.failed");
        }
    }

    private String byteToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }

        return sb.toString();
    }

    private void validateSize(long size) {
        long max = 1024 * 1024 * 2;

        if (size > max) {
            throw new ApplicationException(
                    FILE_TOO_LARGE,
                    LogContent.of(
                            work,
                            MultipartFile.class,
                            "size",
                            size
                    ).withOption("max", max)
            )
                    .withMessageKey("image.size.exceeded")
                    .withMessageArgs(String.valueOf(2));
        }
    }

}