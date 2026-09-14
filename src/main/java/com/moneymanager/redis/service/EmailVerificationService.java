package com.moneymanager.redis.service;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.service.generator.RandomCodeGenerator;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.member.service.util.EmailMasker;
import com.moneymanager.member.service.validation.MemberValidator;
import com.moneymanager.redis.MemberRedisKey;
import com.moneymanager.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.moneymanager.global.exception.code.ErrorCode.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : EmailVerificationService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 6<br>
 * 설명              : 이메일 인증 로직 흐름을 관리하는 클래스
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
 * 		 	  <td>26. 9. 6</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final Duration CODE_TTL = Duration.ofMinutes(5);
    private final Duration TOKEN_TTL = Duration.ofMinutes(10);
    private final int EMAIL_CODE_LENGTH = 6;

    private final RedisService redisService;
    private final MemberRedisKey redisKey;

    private final MemberReadService memberReadService;

    private final RandomCodeGenerator codeGenerator;
    private final MemberValidator memberValidator;

    public String generateAuthCode() {
        return codeGenerator.generateNumeric(EMAIL_CODE_LENGTH);
    }

    public void saveCode(String email, String code) {
        redisService.set(
                redisKey.emailVerificationCode(email),
                code,
                CODE_TTL
        );
    }

    public void saveToken(String email, String token) {
        redisService.set(
                redisKey.emailVerificationToken(email),
                token,
                TOKEN_TTL
        );
    }

    public String getCode(String email) {
        return redisService.get(redisKey.emailVerificationCode(email));
    }

    public String getToken(String email) {
        return redisService.get(redisKey.emailVerificationToken(email));
    }

    public void deleteCode(String email) {
        redisService.delete(redisKey.emailVerificationCode(email));
    }

    public void deleteToken(String email) {
        redisService.delete(redisKey.emailVerificationToken(email));
    }

    public void validateEmail(String email) {
        memberValidator.validateEmail(email);

        if (memberReadService.checkEmailExists(email)) {
            throw new ApplicationException(
                    DUPLICATE_DATA,
                    LogContent.of(
                            "이메일 검증",
                            "email",
                            EmailMasker.mask(email)
                    ).withCause("중복 이메일")
            ).withMessageKey("member.email.duplicate");
        }
    }

    public void validateCode(String code) {
        if(StringUtil.isNullOrBlank(code)) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            "이메일 인증코드 검증",
                            "emailCode",
                            code
                    )
            ).withMessageKey("member.email.code.required");
        }

        if(!"[0-9]{%d}".formatted(EMAIL_CODE_LENGTH).matches(code)) {
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

    public void validateEmailToken(String email, String token) {
        String storedToken = getToken(email);

        if(storedToken == null) {
            throw new ApplicationException(
                    DATA_NOT_FOUND,
                    LogContent.of(
                            "이메일 토큰 검증",
                            "email",
                            EmailMasker.mask(email)
                    ).withCause("미인증된 이메일")
            ).withMessageKey("member.signup.failed");
        };

        if (!storedToken.equals(token)) {
            throw new ApplicationException(
                    MISMATCH,
                    LogContent.of(
                            "이메일 토큰 검증",
                            "token",
                            StringUtil.masking(token, 3, token.length() - 3)
                    )
            ).withMessageKey("member.signup.failed");
        }
    }

}