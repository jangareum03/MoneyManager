package com.areum.moneymanager.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
public class MailService {
    private JavaMailSender javaMailSender;

    private final Logger LOGGER = LogManager.getLogger(MailService.class);

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    //이메일 인증코드
    private String emailKey;
    //임시 비밀번호
    private String password;
    @Value("${spring.mail.email}")
    private String address;

    //인증코드 메일 내용 작성 및 설정
    public MimeMessage createEmailCode( String to ) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("[돈매니저] 회원가입 이메일 인증코드 보내드립니다.");

        this.emailKey = createCode();

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

    //임시 비밀번호 메일 내용 작성
    public MimeMessage createPassword( String to ) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("[돈매니저] 임시 비밀번호 보내드립니다.");

        this.password = createPassword();

        String content = "<html><body>" +
                "<meta http-equiv='Content-type' content='text/html; charset=utf-8'>" +
                "<h1>임시 비밀번호</h1><br>" +
                "안녕하세요. 돈매니저입니다.<br>" +
                "요청하신 임시 비밀번호 보내드립니다. 해당 비밀번호로 로그인 해주시길 바랍니다.<br>" +
                "<hr>" +
                "임시 비밀번호    " + "<span style='font-weight: bold; color: #8DB48C'>" +
                password +
                "</span><hr>" +
                "고객님 본인이 요청하신 경우가 아니라면 고객센터로 문의하시길 바랍니다.<br>" +
                "감사하빈다.<br> 돈매니저 드림" +
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

    //임시 비밀번호 생성
    private String createPassword() {
        StringBuilder pwd = new StringBuilder();
        Random random = new Random();

        for( int i=0; i<8; i++ ) {
            int index = random.nextInt(3);

            switch ( index ) {
                case 0:
                    pwd.append( (char)((int)random.nextInt(26) + 97) );
                    break;
                case 1:
                    pwd.append( (char)(int)(random.nextInt(26) + 65) );
                    break;
                case 2:
                    pwd.append(random.nextInt(10));
                    break;
            }
        }

        return pwd.toString();
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
    public String sendMail(String to, String type) throws Exception {
        MimeMessage message;

        if( type.equals("email") ) {
            message = createEmailCode(to);
            LOGGER.debug("전송한 메일: {}, 인증코드: {}", to, emailKey);
        }else{
            message = createPassword(to);
            LOGGER.debug("전송한 메일: {}, 임시비밀번호: {}", to, password);
        }

        javaMailSender.send(message);


        return type.equals("email") ? emailKey : password;
    }
}
