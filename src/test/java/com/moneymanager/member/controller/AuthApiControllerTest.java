package com.moneymanager.member.controller;

import com.moneymanager.global.domain.dto.response.api.ApiBody;
import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.MessageUtil;
import com.moneymanager.member.domain.dto.request.EmailVerifyRequest;
import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.member.domain.dto.request.SendVerificationCodeRequest;
import com.moneymanager.member.service.application.EmailVerificationService;
import com.moneymanager.member.service.application.MemberAuthService;
import com.moneymanager.member.service.application.MemberService;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import static com.moneymanager.global.exception.code.ErrorCode.EXTERNAL_API_ERROR;
import static com.moneymanager.global.exception.code.ErrorCode.REQUIRED_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller<br>
 * 파일이름       : AuthApiControllerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 7<br>
 * 설명              : AuthApiController 클래스 요청을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 9. 7.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class AuthApiControllerTest {

    @InjectMocks
    AuthApiController target;

    @Mock
    MemberService memberService;
    
    @Mock
    EmailVerificationService emailVerificationService;

    @Mock
    MemberAuthService authService;

    @Mock
    MessageUtil messageUtil;

    @Nested
    @DisplayName("인증코드 요청할 때")
    class SendEmailCode {

        SendVerificationCodeRequest request = new SendVerificationCodeRequest(MemberTestData.DEFAULT_EMAIL);
        LogContent content = LogContent.of(
                "테스트",
                "email",
                "email"
        );

        @Test
        @DisplayName("정상적인 이메일이면 200 코드를  반환한다.")
        void returns200_whenEmailIsValid() {
            //when
            ApiBody<Void> result = target.sendCode(request);

            //then
            assertThat(result.getStatus()).isSameAs(HttpStatus.OK);

            InOrder inOrder = inOrder(emailVerificationService);

            inOrder.verify(emailVerificationService).validateEmail(request.email());
            inOrder.verify(emailVerificationService).sendVerificationCode(request.email());
        }

        @Test
        @DisplayName("유효하지 않은 이메일이면 예외를 전파한다.")
        void throwsException_whenEmailIsInvalid() {
            //given
            doThrow(new ApplicationException(
                    REQUIRED_VALUE,
                    content
            ))
                    .when(emailVerificationService)
                    .validateEmail(request.email());

            //when
            assertThatThrownBy(() -> target.sendCode(request))
                    .isInstanceOf(ApplicationException.class);

            verify(emailVerificationService, never()).sendVerificationCode(request.email());
        }

        @Test
        @DisplayName("중복 이메일이면 예외를 전파한다.")
        void throwsException_whenEmailAlreadyExists() {
            //given
            doThrow(new ApplicationException(
                    EXTERNAL_API_ERROR,
                    content
            ))
                    .when(emailVerificationService)
                    .sendVerificationCode(request.email());

            //when
            assertThatThrownBy(() -> target.sendCode(request))
                    .isInstanceOf(ApplicationException.class);
        }

    }


    @Nested
    @DisplayName("이메일 인증할 때")
    class VerificationEmail {

        String email = MemberTestData.DEFAULT_EMAIL;
        String code = "123456";

        EmailVerifyRequest request = new EmailVerifyRequest(email, code);

        @Test
        @DisplayName("성공 시 성공 메시지와 토큰을 반환한다.")
        void returnsTokenAndSuccessMessage_whenSuccessfully() {
            //given
            when(emailVerificationService.verifyEmailCode(email, code))
                    .thenReturn("token");

            when(messageUtil.get(anyString()))
                    .thenReturn("성공");

            //when
            ApiBody<String> result = target.verifyEmail(request);

            //then
            assertThat(result.getStatus()).isSameAs(HttpStatus.OK);
            assertThat(result.getMessage()).isEqualTo("성공");
            assertThat(result.getData()).isEqualTo("token");
        }

        @Test
        @DisplayName("실패 시 예외를 전파한다.")
        void throwsException_whenFails() {
            //given
            when(emailVerificationService.verifyEmailCode(email, code))
                    .thenThrow(new ApplicationException(
                            REQUIRED_VALUE,
                            LogContent.of(
                                    "이메일 인증코드 검증",
                                    "emailCode",
                                    code
                            )
                    ).withUserMessage("실패"));

            //when
            assertThatThrownBy(() -> target.verifyEmail(request))
                    .isInstanceOf(ApplicationException.class);
        }

    }


    @Nested
    @DisplayName("토큰 재발급 요청할 때")
    class RefreshToken {

        @Test
        @DisplayName("토큰 재발급 중 오류가 발생하면 예외를 전파한다.")
        void throwsException_whenReissueFails() {
        	//given
            String refreshToken = "refresh-token";
            MockHttpServletResponse response = new MockHttpServletResponse();

            doThrow(ApplicationException.class)
                    .when(authService)
                    .reissueToken(refreshToken, response);
        	
        	//when
        	assertThatThrownBy(() -> target.verifyRefreshToken(refreshToken, response))
                    .isInstanceOf(ApplicationException.class);
        }

    }

    @Nested
    @DisplayName("회원가입 요청할 때")
    class SignUp {
        
        MemberSignUpRequest request = mock(MemberSignUpRequest.class);
        
        @Test
        @DisplayName("정상적인 요청이면 회원가입을 호출한다.")
        void callsSignUp_whenRequestIsValid() {
        	//when
            target.signUp(request);
        	
        	//then
        	verify(memberService).processSignUp(request);
        }
        
        @Test
        @DisplayName("정상적인 요청이면 성공 메시지와 이동할 URL을 반환한다.")
        void returnsSuccessResponseWithRedirectUrl_whenRequestIsValid() {
            //given
            when(messageUtil.get(anyString()))
                    .thenReturn("회원가입 성공");

        	//when
            ApiBody<Void> result = target.signUp(request);
        	
        	//then
            assertThat(result.getStatus()).isSameAs(HttpStatus.OK);
        	assertThat(result.getMessage()).isEqualTo("회원가입 성공");
        	assertThat(result.getNext()).isEqualTo("/auth/login");
        }
        
        @Test
        @DisplayName("비정상적인 요청이면 예외를 전파시킨다.")
        void throwsException_whenRequestIsInvalid() {
        	//given
            doThrow(ApplicationException.class)
                    .when(memberService)
                    .processSignUp(request);
        	
        	//when
        	assertThatThrownBy(() -> target.signUp(request))
                    .isInstanceOf(ApplicationException.class);
        }

    }

}