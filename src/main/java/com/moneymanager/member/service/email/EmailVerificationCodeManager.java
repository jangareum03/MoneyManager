package com.moneymanager.member.service.email;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.generator.HashGenerator;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.service.generator.EmailCodeGenerator;
import com.moneymanager.member.service.redis.EmailVerificationRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.moneymanager.global.exception.code.ErrorCode.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.email<br>
 * 파일이름       : EmailVerificationCodeManager<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 21<br>
 * 설명              : 이메일 인증코드와 관련된 제공하는 클래스
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
 * 		 	  <td>26. 9. 21</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
@RequiredArgsConstructor
public class EmailVerificationCodeManager {

    private static final int EMAIL_CODE_LENGTH = 6;
    private static final String EMAIL_CODE_FORMAT = "[0-9]{%d}".formatted(EMAIL_CODE_LENGTH);

    private final EmailVerificationRedisService redisService;

    private final EmailCodeGenerator codeGenerator;
    private final HashGenerator hashGenerator;

    public String createCode() {
        return codeGenerator.generate(EMAIL_CODE_LENGTH);
    }

    public void saveCode(String email, String code) {
        String hashCode = hashGenerator.sha256(code);

        redisService.saveCode(email, hashCode);
    }

    public Optional<String> getCode(String email) {
        if(existsCode(email)) {
            return Optional.of(redisService.getCode(email));
        }

        return Optional.empty();
    }

    public boolean existsCode(String email) {
        return redisService.getCode(email) != null;
    }

    public void deleteCode(String email) {
        redisService.deleteCode(email);
    }

    public void validateEmailCode(String code) {
        if (StringUtil.isNullOrBlank(code)) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            "이메일 인증코드 검증",
                            "emailCode",
                            code
                    )
            ).withMessageKey("member.email.code.required");
        }

        if (!EMAIL_CODE_FORMAT.matches(code)) {
            throw new ApplicationException(
                    INVALID_FORMAT,
                    LogContent.of(
                            "이메일 인증코드 검증",
                            "emailCode",
                            code
                    )
            ).withMessageKey("member.email.code.invalid");
        }
    }

    public void verifyCode(String email, String code) {
        Optional<String> redisCode = getCode(email);

        if(redisCode.isEmpty()) {
            throw new ApplicationException(
                    DATA_NOT_FOUND,
                    LogContent.of(
                            "인증코드 조회",
                            "email",
                            EmailMasker.mask(email)
                    ).withCause("이메일에 해당하는 인증코드 없음")
            ).withMessageKey("email.verification.code.expired");
        }

        String hashCode = hashGenerator.sha256(code);

        if(!redisCode.get().equals(hashCode)) {
            throw new ApplicationException(
                    MISMATCH,
                    LogContent.of(
                            "인증코드 일치 검증",
                            "emailCode",
                            code
                    )
            ).withMessageKey("email.verification.code.invalid");
        }
    }

}