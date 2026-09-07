package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.member.repository.EmailVerificationRedisRepository;
import com.moneymanager.member.service.email.EmailCodeGenerator;
import com.moneymanager.member.service.email.EmailSender;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.moneymanager.global.exception.code.ErrorCode.MISMATCH;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : EmailVerificationServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 7<br>
 * 설명              : EmailVerificationService 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 9. 7</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class EmailVerificationServiceIT extends IntegrationTest {

    @Autowired
    EmailVerificationService target;

    @Autowired
    EmailVerificationRedisRepository redisRepository;

    @MockBean
    EmailCodeGenerator codeGenerator;

    @MockBean
    EmailSender emailSender;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> "6379");
    }

    @Nested
    @DisplayName("이메일 인증코드 검증할 때")
    class VerificationCode {

        String email = MemberTestData.DEFAULT_EMAIL;
        String code = "123456";

        @BeforeEach
        void setUp() {
            when(codeGenerator.generate())
                    .thenReturn(code);

            doNothing()
                    .when(emailSender)
                    .sendVerificationCode(email, code);

            //인증코드 발송을 통해 Redis에 인증코드를 저장한다.
            target.sendVerificationCode(email);
        }
        
        @Test
        @DisplayName("인증코드 검증에 성공하면 인증코드를 삭제한다.")
        void deletesCode_whenVerificationSuccessfully() {
        	//when
            target.verifyEmailCode(email, code);
        	
        	//then
            assertThat(redisRepository.getCode(email)).isEmpty();
        }

        @Test
        @DisplayName("인증코드 검증에 성공하면 토큰을 생성 후 저장한다.")
        void generatesAndSavesToken_whenCodeIsValid() {
        	//when
            target.verifyEmailCode(email, code);
        	
        	//then
        	assertThat(redisRepository.getToken(email)).isPresent();
        }
        
        @Test
        @DisplayName("인증코드 검증에 실패하면 인증코드는 유지되며 예외를 전파한다.")
        void throwsExceptionAndRetainsCode_whenVerificationFails() {
        	//given
            String code = "012345";
        	
        	//when
            Throwable throwable = catchThrowable(() -> target.verifyEmailCode(email, code));
        	
        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(MISMATCH)
                    .hasWork("인증코드 일치 검증")
                    .hasField("emailCode")
                    .hasValue(code);

            assertThat(redisRepository.getCode(email)).isPresent();
        }
    }


    @Nested
    @DisplayName("이메일 인증 확인할 때")
    class VerificationEmail {

        String email = MemberTestData.DEFAULT_EMAIL;
        
        @Test
        @DisplayName("인증 성공한 이메일과 인증코드가 일치하다면 통과한다.")
        void verifiesEmailCode_whenCodeMatchesSuccessfully() {
        	//given
            redisRepository.saveToken(email, "storedToken");
        	
        	//when
            assertDoesNotThrow(() -> target.verifyEmail(email, "storedToken"));
            
            //then
            assertThat(redisRepository.getToken(email)).isPresent();
        }
        
        @Test
        @DisplayName("인증하지 않은 이메일이면 예외를 발생시킨다.")
        void throwsException_whenEmailIsNotVerified() {
        	//given
            String email = "notVerifiedEmail@test.com";

        	//when
            assertThatThrownBy(() -> target.verifyEmail(email, "storedToken"))
                    .isInstanceOf(ApplicationException.class);
        }
        
        @Test
        @DisplayName("이메일에 해당하는 토큰과 일치하지 않으면 예외를 발생시킨다.")
        void throwsException_whenTokenIsInvalid() {
            //given
            redisRepository.saveToken(email, "storedToken");

            //when
            assertThatThrownBy(() -> target.verifyEmail(email, "noMatchToken"))
                    .isInstanceOf(ApplicationException.class);
        }

    }

}