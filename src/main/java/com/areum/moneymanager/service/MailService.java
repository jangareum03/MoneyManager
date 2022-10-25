package com.areum.moneymanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    //이메일 인증코드
    private final String emailKey = createCode();

    @Value("${spring.mail.email}")
    private String address;

    //메일 내용 작성 및 설정
    public MimeMessage createEmailCode( String to ) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("[돈매니저] 회원가입 이메일 인증코드 보내드립니다.");

        String content = "<html><body>" +
                "<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>" +
                "<h1>회원가입 인증번호</h1><br>" +
                "안녕하세요. 돈매니저입니다.<br>" +
                "아래와 같이 인증번호를 발급하오니 해당 인증번호를 입력해주시기 바랍니다.<br>" +
                "<hr>" +
                "인증코드      " + "<span style='font-weight: bold; color: #8DB48C'>" +
                emailKey +
                "</span><hr>" +
                "고객님 본인이 요청하신 경우가 아니라면 고객센터로 문의하시길 바랍니다.<br>" +
                "감사합니다.<br> 돈매니저 드림" +
                "</body></html>";

        message.setText(content, "UTF-8", "html");
        message.setFrom(new InternetAddress(address, "돈매니저"));

        return message;
    }

    //인증코드 생성
    private String createCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for( int i=0; i<6; i++ ) {
            int index = random.nextInt(3);

            switch ( index ) {
                case 0:
                    code.append((char)((int)random.nextInt(26) + 97));
                    break;
                case 1:
                    code.append((char)((int)random.nextInt(26) + 65));
                    break;
                case 2:
                    code.append(random.nextInt(10));
                    break;
            }
        }

        return code.toString();
    }

    //인증코드 확인
    public String emailCodeCheck(HttpSession session, String to, String userCode, String time) {
        String code = (String) session.getAttribute(""+ to);

        if( time.equals("00 : 00") ) {
            return "out";
        }else{
            return code.equals(userCode) ? "yes" : "no";
        }

    }

    //메일발송
    public String sendMail(String to) throws Exception {
        MimeMessage message = createEmailCode(to);

        javaMailSender.send(message);

        return emailKey;
    }
}
