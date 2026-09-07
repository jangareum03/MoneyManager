package com.moneymanager.member.service.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.email<br>
 * 파일이름       : EmailSenderTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 6<br>
 * 설명              : EmailSender 클래스 로직을 검증하는 단위 테스트 클래스
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
@ExtendWith(MockitoExtension.class)
class EmailSenderTest {

    @InjectMocks
    EmailSender target;

    @Mock
    JavaMailSender mailSender;

    @Nested
    @DisplayName("인증코드 메일 전송할 때")
    class SendCode {
        
        @Test
        @DisplayName("이메일로 인증코드를 전송한다.")
        void sendsVerificationCode_whenEmailIsValid() {
        	//given
            String email = "test@test.com";
            String code = "123456";
        	
        	//when
            target.sendVerificationCode(email, code);
        	
        	//then
            ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

            verify(mailSender).send(captor.capture());

            SimpleMailMessage message = captor.getValue();

            assertThat(message.getTo()).containsExactly(email);
            assertThat(message.getSubject()).isEqualTo("[돈매니저] 이메일 인증코드");
            assertThat(message.getText())
                    .contains("인증코드: " +code)
                    .contains("인증코드는 5분 동안 유효");
        }
        
    }

}