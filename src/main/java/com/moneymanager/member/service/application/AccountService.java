package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.AuditLogger;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.security.CustomUserDetailService;
import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.domain.dto.request.FindIdRequest;
import com.moneymanager.member.domain.dto.request.FindPwdRequest;
import com.moneymanager.member.domain.dto.response.FindIdResponse;
import com.moneymanager.member.domain.dto.response.FindPwdResponse;
import com.moneymanager.member.domain.query.MemberFindIdQuery;
import com.moneymanager.member.service.command.EmailSender;
import com.moneymanager.member.service.command.MemberCommandService;
import com.moneymanager.member.service.generator.HashGenerator;
import com.moneymanager.member.service.generator.UuidGenerator;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.member.service.util.EmailMasker;
import com.moneymanager.member.service.validation.MemberValidator;
import com.moneymanager.redis.service.EmailVerificationService;
import com.moneymanager.redis.service.PasswordResetService;
import com.sun.mail.smtp.SMTPSendFailedException;
import com.sun.mail.util.MailConnectException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

import static com.moneymanager.global.exception.code.ErrorCode.*;


/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : AccountService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 10<br>
 * 설명              : 계정 로직을 관리하는 오케스트라 클래스
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
 * 		 	  <td>26. 9. 10</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final CustomUserDetailService userDetailService;
    private final MemberReadService memberReadService;
    private final MemberCommandService memberCommandService;
    private final PasswordResetService passwordResetService;

    private final EmailSender emailSender;
    private final MemberValidator memberValidator;
    private final PasswordEncoder passwordEncoder;
    private final UuidGenerator uuidGenerator;
    private final HashGenerator hashGenerator;

    private final EmailVerificationService emailVerification;


    public CustomUserDetails login(String username, String password) {
        try {
            //1. 아이디와 비밀번호 검증
            memberValidator.validateLogin(username, password);
        } catch (ApplicationException e) {
            throw new AuthenticationServiceException(e.getMessageKey());
        }

        //2. 사용자 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) userDetailService.loadUserByUsername(username);
        throwsAuthenticationException(userDetails);

        //3. 비밀번호 일치여부 검증
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("member.login.failed");
        }

        return userDetails;
    }

    public FindIdResponse findId(FindIdRequest request) {
        //1. 이름과 이메일 입력 검증
        memberValidator.validateFindId(request);

        //2. 아이디 + 회원상태 조회
        MemberFindIdQuery memberFindIdQuery = memberReadService.getMemberStatus(request.getName(), request.getEmail());

        //3. 마스킹 처리
        String id = memberFindIdQuery.getUsername();
        String maskingId = StringUtil.masking(id, 1, id.length() / 2);

        return new FindIdResponse(maskingId, memberFindIdQuery.getStatus());
    }

    public FindPwdResponse findPassword(FindPwdRequest request) {
        //1. 이름과 아이디 입력 검증
        memberValidator.validateFindPassword(request);

        //2. 이메일 조회
        String email = memberReadService.getEmail(request.getName(), request.getUsername());

        //3. 토큰 생성
        String token = uuidGenerator.generate();
        String hashToken = hashGenerator.sha256(token);

        //4. Redis 저장
        passwordResetService.saveToken(hashToken);

        //5. 이메일 발송
        sendEmail(email, token);

        //5. 이메일 마스킹
        String maskedEmail = memberCommandService.getMaskedEmail(email);

        return new FindPwdResponse(maskedEmail);
    }

    public void verifyEmail(String email) {
        //1. 이메일 검증
        emailVerification.validateEmail(email);

        //2. 인증코드 생성
        String code = emailVerification.generateAuthCode();

        //3. 인증코드 hash값 생성
        String hashCode = passwordEncoder.encode(code);

        //4. Redis 저장
        emailVerification.saveCode(email, hashCode);

        //5. 이메일 전송
        sendEmailCode(email, code);
    }

    public String verifyEmailCode(String email, String code) {
        //1. 이메일 및 코드 검증
        emailVerification.validateEmail(email);
        emailVerification.validateCode(code);

        //2.. Redis 인증코드 조회
        String hashCode = emailVerification.getCode(email);

        if(hashCode == null) {
            throw new ApplicationException(
                    DATA_NOT_FOUND,
                    LogContent.of(
                            "인증코드 조회",
                            "email",
                            EmailMasker.mask(email)
                    ).withCause("이메일에 해당하는 인증코드 없음")
            ).withMessageKey("email.verification.code.expired");
        }

        //3. 인증코드 일치여부 확인
        if (passwordEncoder.matches(code, hashCode)) {
            throw new ApplicationException(
                    MISMATCH,
                    LogContent.of(
                            "인증코드 일치 검증",
                            "emailCode",
                            code
                    )
            ).withMessageKey("email.verification.code.invalid");
        }

        //4. 인증코드 삭제
        emailVerification.deleteCode(code);

        //5. 이메일 인증 완료확인 토큰 발급 및 저장
        String token = uuidGenerator.generate();
        emailVerification.saveToken(email, token);

        return token;
    }


    //===== login 보조 메서드 =====
    private void throwsAuthenticationException(CustomUserDetails userDetails) throws AuthenticationException {
        if (!userDetails.isAccountNonExpired()) {
            throw new DisabledException("member.login.not_found");
        }

        if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("member.login.locked");
        }

        if (!userDetails.isEnabled()) {
            throw new DisabledException("member.login.restricted");
        }
    }


    //==== findPassword 보조 메서드 ====
    private void sendEmail(String from, String token) {
        for (int i = 0; i < 3; i++) {
            try {
                emailSender.sendPasswordResetLink(from, token);
                return;
            } catch (MessagingException e) {
                if (e instanceof MailConnectException) {
                    AuditLogger.warn("SMTP 서버에 연결하지 못 했습니다. eamil={}, attempts={}", EmailMasker.mask(from), String.valueOf(i + 1));
                } else if (e instanceof SMTPSendFailedException) {
                    int code = ((SMTPSendFailedException) e).getReturnCode();

                    if (!(code >= 400 && code < 500)) {
                        break;
                    }

                    AuditLogger.warn("메일을 전송하지 못 했습니다. eamil={}, attempts={}", EmailMasker.mask(from), String.valueOf(i + 1));
                } else {
                    break;
                }

                if (i < 2) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        AuditLogger.error("메일 전송에 실패했습니다. type={}, eamil={}", "비밀번호 변경", EmailMasker.mask(from));

        throw new ApplicationException(
                EXTERNAL_API_ERROR,
                LogContent.of(
                        "메일 발송",
                        "email",
                        EmailMasker.mask(from)
                )
        )
                .withMessageKey("email.send.failed")
                .withMessageArgs("비밀번호 변경");
    }


    //==== verifyEmail 보조 메서드 ====
    private void sendEmailCode(String email, String code) {
        try {
            emailSender.sendVerificationCode(email, code);
        } catch (MailException e) {
            AuditLogger.warn("{} 메일로 인증코드 전송에 실패했습니다.", EmailMasker.mask(email));

            emailVerification.deleteCode(email);

            throw new ApplicationException(
                    EXTERNAL_API_ERROR,
                    LogContent.of(
                            "이메일 전송",
                            "email",
                            EmailMasker.mask(email)
                    ).withCause("인증코드 이메일 발송 오류"),
                    e
            ).withMessageKey("email.verification.code.send");
        }
    }

}