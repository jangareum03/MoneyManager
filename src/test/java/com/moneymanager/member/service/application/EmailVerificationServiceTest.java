package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.member.repository.EmailVerificationRedisRepository;
import com.moneymanager.member.service.email.EmailCodeGenerator;
import com.moneymanager.member.service.email.EmailSender;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.member.service.validation.AccountValidator;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.moneymanager.global.exception.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : EmailVerificationServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 6<br>
 * 설명              : EmailVerificationService 클래스 로직을 검증하는 단위 테스트 클래스
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
class EmailVerificationServiceTest {

    @InjectMocks
    EmailVerificationService target;

    @Mock
    EmailVerificationRedisRepository redisRepository;
    
    @Mock
    MemberReadService memberReadService;

    @Mock
    EmailCodeGenerator codeGenerator;
    
    @Mock
    AccountValidator accountValidator;

    @Spy
    PasswordEncoder passwordEncoder;

    @Mock
    EmailSender emailSender;


    @Nested
    @DisplayName("이메일 인증코드 발송할 때")
    class Send {
        
        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("인증코드 생성 → 인증코드 변환 → 저장소 저장 → 이메일 전송 순서로 진행한다.")
            void processSend_whenRequestIsValid() {
                //given
                String email = "test@test.com";

                String code = "123456";
                String hashCode = "hash123456";

                when(codeGenerator.generate())
                        .thenReturn(code);

                when(passwordEncoder.encode(code))
                        .thenReturn(hashCode);

                //when
                assertDoesNotThrow(() -> target.sendVerificationCode(email));

                //then
                InOrder inOrder = Mockito.inOrder(redisRepository, codeGenerator, passwordEncoder, emailSender);

                inOrder.verify(codeGenerator).generate();
                inOrder.verify(passwordEncoder).encode(code);
                inOrder.verify(redisRepository).saveCode(email, hashCode);
                inOrder.verify(emailSender).sendVerificationCode(email, code);

                verify(redisRepository, never()).deleteCode(email);
            }

            @Test
            @DisplayName("생성된 인증코드는 해시코드로 변환한다.")
            void convertsCodeToHashCode_whenCodeIsGenerated() {
                //given
                String email = "test@test.com";
                String code = "123456";

                when(codeGenerator.generate())
                        .thenReturn(code);

                //when
                target.sendVerificationCode(email);

                //then
                ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

                verify(redisRepository).saveCode(eq(email), captor.capture());

                assertThat(code).isNotEqualTo(captor.getValue());
            }

            @Test
            @DisplayName("전송할 이메일에서는 원본 인증코드를 전달한다.")
            void sendsEmailWithRawCode_whenRequestIsValid() {
                //given
                String email = "test@test.com";
                String code = "123456";
                String hashCode = "hash123456";

                when(codeGenerator.generate())
                        .thenReturn(code);

                when(passwordEncoder.encode(code))
                        .thenReturn(hashCode);

                //when
                target.sendVerificationCode(email);

                //then
                ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

                verify(emailSender).sendVerificationCode(eq(email), captor.capture());

                assertThat(captor.getValue()).isEqualTo(code);
            }

            @Test
            @DisplayName("이메일 전송에 성공하면 저장소 삭제를 수행하지 않는다.")
            void doesNotDeleteStorage_whenEmailSendingSucceeds() {
                //given
                String email = "test@test.com";

                String code = "123456";
                String hashCode = "hash123456";

                when(codeGenerator.generate())
                        .thenReturn(code);

                when(passwordEncoder.encode(code))
                        .thenReturn(hashCode);

                //when
                target.sendVerificationCode(email);

                //then
                verify(redisRepository, never()).deleteCode(email);
            }
            
        }
        
        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("이메일 전송에 실패하면 저장소 삭제를 수행한 뒤 예외를 발생시킨다.")
            void throwsException_whenEmailSendingFails() {
            	//given
                String email = "test123@test.com";
                String code = "123456";
                String hashCode = "hash123456";

                when(codeGenerator.generate())
                        .thenReturn(code);

                when(passwordEncoder.encode(code))
                        .thenReturn(hashCode);

            	doThrow(new MailSendException("메일 전송 실패"))
                        .when(emailSender)
                        .sendVerificationCode(email, code);

            	//when
                Throwable throwable = catchThrowable(() -> target.sendVerificationCode(email));

            	//then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(EXTERNAL_API_ERROR)
                        .hasWork("이메일 전송")
                        .hasCauseMessage("인증코드 이메일 발송 오류")
                        .hasField("email")
                        .hasValue("te*****@test.com");

                verify(emailSender).sendVerificationCode(email, code);
                verify(redisRepository).deleteCode(email);
            }
            
        }
        
    }


    @Nested
    @DisplayName("이메일 검증할 때")
    class Validation {
        
        @Test
        @DisplayName("형식 검증 → 중복 여부 확인 순서로 진행한다.")
        void doesNotThrowAnyException_whenValueIsValidAndUnique() {
        	//given
            String email = MemberTestData.DEFAULT_EMAIL;
        	
        	//when
            assertDoesNotThrow(() -> target.validateEmail(email));
        	
        	//then
        	InOrder inOrder = inOrder(accountValidator, memberReadService);
            
            inOrder.verify(accountValidator).validateEmail(email);
            inOrder.verify(memberReadService).checkEmailExists(email);
        }
        
        @Test
        @DisplayName("형식 검증 실패하면 중복 여부를 수행하지 않는다.")
        void throwsException_whenFormatIsInvalid() {
        	//given
            String email = MemberTestData.DEFAULT_EMAIL;
            
            doThrow(ApplicationException.class)
                .when(accountValidator)
                    .validateEmail(email);
        	
        	//when
            assertThatThrownBy(() -> target.validateEmail(email))
                    .isInstanceOf(ApplicationException.class);
        	
        	//then
        	verify(memberReadService, never()).checkEmailExists(email);
        }
        
        @Test
        @DisplayName("중복 여부에서 TRUE를 반환하면 예외를 발생시킨다.")
        void throwsException_whenValueIsDuplicated() {
        	//given
            String email = MemberTestData.DEFAULT_EMAIL;

            when(memberReadService.checkEmailExists(email))
                    .thenReturn(true);
        	
        	//when
            Throwable throwable = catchThrowable(() -> target.validateEmail(email));
        	
        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(DUPLICATE_DATA)
                    .hasWork("이메일 검증")
                    .hasCauseMessage("중복 이메일")
                    .hasField("email")
                    .hasValue("te**@test.com");
        }
    }


    @Nested
    @DisplayName("이메일 인증코드 검증할 때")
    class VerificationCode {

        String email = MemberTestData.DEFAULT_EMAIL;
        String code = "123456";
        String hashCode = "hash123456";

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("검증 → 인증코드 → 일치확인 → 인증코드 삭제 → 토큰 생성 → 토큰 저장 순서로 진행한다.")
            void verifiesEmailCode_whenRequestIsValid() {
                //given
                when(redisRepository.getCode(email))
                        .thenReturn(Optional.of(hashCode));

                when(passwordEncoder.matches(code, hashCode))
                        .thenReturn(Boolean.TRUE);

                //when
                assertDoesNotThrow(() -> target.verifyEmailCode(email, code));

                //then
                InOrder inOrder = inOrder(accountValidator, redisRepository, passwordEncoder);

                inOrder.verify(accountValidator).validateEmailCode(code);
                inOrder.verify(redisRepository).getCode(email);
                inOrder.verify(passwordEncoder).matches(code, hashCode);
                inOrder.verify(redisRepository).deleteCode(email);
                inOrder.verify(redisRepository).saveToken(eq(email), anyString());
            }

            @Test
            @DisplayName("검증에 실패하면 인증코도 조회는 수행하지 않는다.")
            void doesNotFetchCode_whenValidationFails() {
                //given
                doThrow(ApplicationException.class)
                        .when(accountValidator)
                        .validateEmailCode(code);

                //when
                assertThatThrownBy(() -> target.verifyEmailCode(email, code))
                        .isInstanceOf(ApplicationException.class);

                //then
                verify(accountValidator).validateEmailCode(code);
                verify(redisRepository, never()).getCode(email);
            }

            @Test
            @DisplayName("인증코드 조회 실패하면 일치여부는 수행하지 않는다.")
            void doesNotVerifyCode_whenCodeNotFound() {
                //given
                when(redisRepository.getCode(email))
                        .thenReturn(Optional.empty());

                //when
                assertThatThrownBy(() -> target.verifyEmailCode(email, code))
                        .isInstanceOf(ApplicationException.class);

                //then
                verify(passwordEncoder, never()).matches(code, hashCode);
            }

            @Test
            @DisplayName("인증코드가 일치하지 않으면 인증코드 삭제는 수행하지 않는다.")
            void doesNotDeleteCode_whenCodeIsInvalid() {
                //given
                when(redisRepository.getCode(email))
                        .thenReturn(Optional.of(hashCode));

                when(passwordEncoder.matches(code, hashCode))
                        .thenReturn(Boolean.FALSE);

                //when
                assertThatThrownBy(() -> target.verifyEmailCode(email, code))
                        .isInstanceOf(ApplicationException.class);

                //then
                verify(redisRepository, never()).deleteCode(email);
                verify(redisRepository, never()).saveToken(eq(email), anyString());
            }
            
        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("인증코드 조회 시 없으면 예외를 발생시킨다.")
            void throwsException_whenCodeDoesNotExist() {
                //given
                when(redisRepository.getCode(email))
                        .thenReturn(Optional.empty());

                //when
                Throwable throwable = catchThrowable(() -> target.verifyEmailCode(email, code));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(DATA_NOT_FOUND)
                        .hasWork("인증코드 조회")
                        .hasCauseMessage("이메일에 해당하는 인증코드 없음")
                        .hasField("email")
                        .hasValue("te**@test.com");
            }
            
            @Test
            @DisplayName("인증코드가 일치하지 않으면 예외를 발생시킨다.")
            void throwsException_whenCodeIsInvalid() {
            	//given
                when(redisRepository.getCode(email))
                        .thenReturn(Optional.of(hashCode));

                when(passwordEncoder.matches(code, hashCode))
                        .thenReturn(Boolean.FALSE);
            	
            	//when
                Throwable throwable = catchThrowable(() -> target.verifyEmailCode(email, code));
            	
            	//then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(MISMATCH)
                        .hasWork("인증코드 일치 검증")
                        .hasField("emailCode")
                        .hasValue(code);
            }
            
        }

    }


    @Nested
    @DisplayName("이메일 인증 확인할 때")
    class VerificationEmail {

        String email = MemberTestData.DEFAULT_EMAIL;
        
        @Test
        @DisplayName("저장된 토큰과 일치하면 예외가 발생하지 않는다.")
        void doesNotThrowException_whenTokenMatches() {
        	//given
            when(redisRepository.getToken(email))
                    .thenReturn(Optional.of("token"));
        	
        	//when
            assertDoesNotThrow(() -> target.verifyEmail(email, "token"));
        }
        
        @Test
        @DisplayName("이메일에 해당하는 토큰이 없으면 예외를 발생시킨다.")
        void throwsException_whenTokenDoesNotExistForEmail() {
        	//given
            when(redisRepository.getToken(email))
                    .thenReturn(Optional.empty());
        	
        	//when
            Throwable throwable = catchThrowable(() -> target.verifyEmail(email, "token"));
        	
        	//then
        	ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(DATA_NOT_FOUND)
                    .hasWork("이메일 토큰 검증")
                    .hasCauseMessage("미인증된 이메일")
                    .hasField("email")
                    .hasValue("te**@test.com");
        }
        
        @Test
        @DisplayName("저장된 토큰과 일치하지 않으면 예외를 발생시킨다.")
        void throwsException_whenTokenDoesNotMatchInRedis() {
        	//given
            when(redisRepository.getToken(email))
                    .thenReturn(Optional.of("storedToken"));
        	
        	//when
            Throwable throwable = catchThrowable(() -> target.verifyEmail(email, "token"));
        	
        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(MISMATCH)
                    .hasWork("이메일 토큰 검증")
                    .hasField("token")
                    .hasValue("tok**");
        }
    }

}