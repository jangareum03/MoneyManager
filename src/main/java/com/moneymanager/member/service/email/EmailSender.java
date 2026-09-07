package com.moneymanager.member.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
@Service
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("[돈매니저] 이메일 인증코드");

        message.setText("""
                [돈매니저] 이메일 인증코드
                
                안녕하세요. 돈매니저 입니다.
                이메일 인증을 위한 인증코드를 안내해드립니다.
                
                ────────────────────────
                인증코드: %s
                ────────────────────────
                
                위 인증코드는 5분 동안 유효합니다.
                본인이 요청하지 않은 인증 메일이라면 이 메일을 무시해주세요.
                
                감사합니다.
                돈매니저.
                """.formatted(code)
        );

        mailSender.send(message);
    }

}