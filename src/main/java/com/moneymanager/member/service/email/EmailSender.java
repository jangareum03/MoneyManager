package com.moneymanager.member.service.email;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.AuditLogger;
import com.moneymanager.global.log.LogContent;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.moneymanager.global.exception.code.ErrorCode.EXTERNAL_API_ERROR;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.email<br>
 * 파일이름       : EmailSender<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 6<br>
 * 설명              : 이메일을 전송하는 클래스
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
@Component
@RequiredArgsConstructor
public class EmailSender {

    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 2000L;

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String email, String code) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            message.setTo(email);
            message.setSubject("[돈매니저] 이메일 인증코드");

            message.setText("""
                <html>
                <body>
                    <h3>[돈매니저] 이메일 인증코드</h3>

                    <p>
                        안녕하세요. 돈매니저 입니다.
                        이메일 인증을 위한 인증코드를 안내해드립니다.
                    </p>

                    <p>
                    ────────────────────────
                        인증코드: <b>%s</b>
                    ────────────────────────
                    </p>

                    <p>
                        위 링크는 5분 동안 유효합니다.
                        본인이 요청하지 않은 인증 메일이라면 이 메일을 무시해주세요.
                    </p>

                    <p>감사합니다. <br>돈매니저 드림</p>
                </body>
                </html>
                """.formatted(code), true);

            sendWithRetry(mimeMessage);
        }catch (MessagingException e){
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

    public void sendPasswordResetLink(String email, String token) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String resetUrl = "https://localhost:8080/auth/password/reset?token=" + token;

            message.setTo(email);
            message.setSubject("[돈매니저] 비밀번호 변경 안내");

            message.setText("""
                <html>
                <body>
                    <h3>[돈매니저] 비밀번호 변경 안내</h3>

                    <p>
                        안녕하세요. 돈매니저 입니다.
                        비밀번호 변경 요청으로 아래 버튼을 클릭하여 비밀번호를 변경해주세요.
                    </p>

                    <p>
                        <a href="%s" style="display:inline-block; padding: 12px 20px; background-color:#4CAF50; color: white; text-decoration:none; border-radius: 6px;">
                            비밀번호 변경하기
                        </a>
                    </p>

                    <p>
                        위 링크는 10분 동안 유효합니다.
                        본인이 요청하지 않은 비밀번호 변경 메일이라면 이 메일을 무시해주세요.
                    </p>

                    <p>감사합니다. <br>돈매니저 드림</p>
                </body>
                </html>
                """.formatted(resetUrl), true);

            sendWithRetry(mimeMessage);
        }catch (MessagingException e){
            throw new ApplicationException(
                    EXTERNAL_API_ERROR,
                    LogContent.of(
                            "메일 발송",
                            "email",
                            EmailMasker.mask(email)
                    )
            )
                    .withMessageKey("email.send.failed")
                    .withMessageArgs("비밀번호 변경");
        }
    }


    //==== 유틸 메서드 =====
    private void sendWithRetry(MimeMessage mimeMessage) {
        MailException exception = null;

        for(int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
            try{
                mailSender.send(mimeMessage);

                return;
            }catch (MailException e){
                exception = e;
                AuditLogger.warn("메일로 인증코드 전송 실패했습니다. 시도 횟수: {}/{}", String.valueOf(attempt), String.valueOf(MAX_RETRY_COUNT));

                if(attempt < MAX_RETRY_COUNT) {
                    sleep();
                }
            }
        }

        throw exception;
    }

    private void sleep() {
        try{
            Thread.sleep(RETRY_DELAY_MS);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

}