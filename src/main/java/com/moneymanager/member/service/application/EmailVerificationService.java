package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.email.EmailMasker;
import com.moneymanager.member.service.email.EmailSender;
import com.moneymanager.member.service.email.EmailVerificationCodeManager;
import com.moneymanager.member.service.email.EmailVerificationTokenManager;
import com.moneymanager.member.service.validation.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import static com.moneymanager.global.exception.code.ErrorCode.DUPLICATE_DATA;
import static com.moneymanager.global.exception.code.ErrorCode.EXTERNAL_API_ERROR;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : EmailVerificationService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 20<br>
 * 설명              : 이메일 인증 기능을 관리하는 클래스
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
 * 		 	  <td>26. 9. 20</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationCodeManager codeManager;
    private final EmailVerificationTokenManager tokenManager;
    private final MemberRepository memberRepository;

    private final EmailSender emailSender;
    private final MemberValidator validator;

    public void sendVerificationCode(String email) {
        //이메일 검증
        validator.validateEmail(email);

        //이메일 중복 확인
        if (memberRepository.existsByEmail(email)) {
            throw new ApplicationException(
                    DUPLICATE_DATA,
                    LogContent.of(
                            "이메일 검증",
                            "email",
                            EmailMasker.mask(email)
                    ).withCause("중복 이메일")
            ).withMessageKey("member.email.duplicate");
        }

        //인증코드 생성
        String code = codeManager.createCode();

        //Redis 저장
        codeManager.saveCode(email, code);

        //이메일 발송
        try {
            emailSender.sendVerificationCode(email, code);
        } catch (MailException e) {
            codeManager.deleteCode(code);

            throw new ApplicationException(
                    EXTERNAL_API_ERROR,
                    LogContent.of(
                            "이메일 전송",
                            "email",
                            EmailMasker.mask(email)
                    ).withCause("이메일 발송 실패")
            )
                    .withMessageKey("email.send.failed")
                    .withMessageArgs("인증코드");
        }
    }

    public String issueEmailToken(String email) {
        String token = tokenManager.createToken();

        //Redis 인증토큰 저장
        tokenManager.saveToken(email, token);

        return token;
    }

    public void verifyCode(String email, String code) {
        //이메일 인증코드 검증
        codeManager.validateEmailCode(code);

        //Redis 코드 조회 및 일치 확인
        codeManager.verifyCode(email, code);

        //코드 일치하면 Redis 삭제
        codeManager.deleteCode(email);
    }

    public void verifyAuthToken(String email, String token) {
        tokenManager.verifyToken(email, token);
    }

    public void deleteToken(String email) {
        tokenManager.deleteToken(email);
    }

}