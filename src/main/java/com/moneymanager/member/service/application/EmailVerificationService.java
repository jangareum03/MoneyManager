package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.AuditLogger;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.repository.EmailVerificationRedisRepository;
import com.moneymanager.member.service.email.EmailCodeGenerator;
import com.moneymanager.member.service.email.EmailMasker;
import com.moneymanager.member.service.email.EmailSender;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.member.service.validation.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

    private final EmailVerificationRedisRepository redisRepository;
    private final MemberReadService memberReadService;

    private final EmailCodeGenerator codeGenerator;
    private final AuthValidator authValidator;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender sender;


    public void sendVerificationCode(String email) {
        //1. 이메일 인증코드 생성
        String code = codeGenerator.generate();

        //2. Redis에 저장할 인증코드 변환
        String hashCode = passwordEncoder.encode(code);

        //3. 변환된 해시코드 Redis 저장소에 저장
        redisRepository.saveCode(email, hashCode);

        //4. 이메일 전송
        try {
            sender.sendVerificationCode(email, code);
        } catch (MailException e) {
            AuditLogger.warn("{} 메일로 인증코드 전송에 실패했습니다.", EmailMasker.mask(email));

            redisRepository.deleteCode(email);

            throw new ApplicationException(
                    EXTERNAL_API_ERROR,
                    LogContent.of(
                            "이메일 전송",
                            "email",
                            EmailMasker.mask(email)
                    ).withCause("인증코드 이메일 발송 오류"),
                    e
            ).withUserMessage("email.verification.code.send");
        }
    }

    public void validateEmail(String email) {
        authValidator.validateEmail(email);

        if (memberReadService.checkEmailExists(email)) {
            throw new ApplicationException(
                    DUPLICATE_DATA,
                    LogContent.of(
                            "이메일 검증",
                            "email",
                            EmailMasker.mask(email)
                    ).withCause("중복 이메일")
            ).withUserMessage("member.email.duplicate");
        }
    }

    public String verifyEmailCode(String email, String code) {
        //1. 인증코드 검증
        authValidator.validateEmailCode(code);

        //2. Redis 저장소에서 인증코드 조회
        String hashCode = redisRepository.getCode(email)
                .orElseThrow(() ->
                        new ApplicationException(
                                DATA_NOT_FOUND,
                                LogContent.of(
                                        "인증코드 조회",
                                        "email",
                                        EmailMasker.mask(email)
                                ).withCause("이메일에 해당하는 인증코드 없음")
                        ).withUserMessage("email.verification.code.expired")
                );

        //3. 인증코드 일치여부 확인
        boolean matches = passwordEncoder.matches(code, hashCode);
        if (!matches) {
            throw new ApplicationException(
                    MISMATCH,
                    LogContent.of(
                            "인증코드 일치 검증",
                            "emailCode",
                            code
                    )
            ).withUserMessage("email.verification.code.invalid");
        }

        //4. Redis 저장소에서 인증코드 삭제
        redisRepository.deleteCode(email);

        //5. 이메일 인증 완료 토큰 발급
        String token = UUID.randomUUID().toString();

        redisRepository.saveToken(email, token);

        return token;
    }

    public void verifyEmail(String email, String token) {
        String storedToken = redisRepository.getToken(email)
                .orElseThrow(() -> new ApplicationException(
                                DATA_NOT_FOUND,
                                LogContent.of(
                                        "이메일 토큰 검증",
                                        "email",
                                        EmailMasker.mask(email)
                                ).withCause("미인증된 이메일")
                        ).withUserMessage("member.signup.failed")
                );

        if (!storedToken.equals(token)) {
            throw new ApplicationException(
                    MISMATCH,
                    LogContent.of(
                            "이메일 토큰 검증",
                            "token",
                            StringUtil.masking(token, 3, token.length() - 3)
                    )
            ).withUserMessage("member.signup.failed");
        }
    }

    public void deleteTokenByEmail(String email) {
        redisRepository.deleteToken(email);
    }

}