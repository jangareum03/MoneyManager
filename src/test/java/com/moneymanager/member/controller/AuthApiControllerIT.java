package com.moneymanager.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneymanager.global.domain.dto.response.AccessToken;
import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.global.security.jwt.JwtTokenProvider;
import com.moneymanager.member.domain.dto.MemberAuth;
import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.repository.EmailVerificationRedisRepository;
import com.moneymanager.member.service.application.MemberService;
import com.moneymanager.member.service.email.EmailSender;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;

import javax.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller<br>
 * 파일이름       : AuthApiControllerIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 7<br>
 * 설명              : AuthApiController 클래스 요청을 검증하는 통합 테스트 클래스
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
class AuthApiControllerIT extends IntegrationTest {

    private final String BASE_URL = "/api/auth";

    @MockBean
    EmailSender emailSender;

    @MockBean
    MemberService memberService;

    @Autowired
    EmailVerificationRedisRepository redisRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    ObjectMapper mapper;

    @Nested
    @DisplayName("인증코드 요청할 때")
    class SendEmailCode {

        String URL = BASE_URL + "/email/send-code";

        @Test
        @DisplayName("신규 이메일이면 200 코드와 성공 메시지를 반환한다.")
        void returnsSuccessResponse_whenEmailIsNew() throws Exception {
            //when
            mockMvc.perform(
                            post(URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "email": "test@test.com"
                                            }
                                            """)
                    )
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("유효하지 않은 이메일이면 400 에러 코드를 반환한다.")
        void returnsBadRequest_whenEmailIsInvalid() throws Exception {
            mockMvc.perform(
                            post(URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "email": "test"
                                            }
                                            """)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("이메일 주소 형식을 확인해주세요."));
        }

        @Test
        @DisplayName("기존에 가입된 이메일이면 40 에러 코드를 반환한다.")
        void returnsConflict_whenEmailAlreadyExists() throws Exception {
            //given
            memberRepository.insert(
                    MemberTestFixture.builder()
                            .withMemberInfo(MemberInfoTestFixture.builder())
                            .build()
            );

            //when
            mockMvc.perform(
                            post(URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "email": "test@test.com"
                                            }
                                            """)
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("이미 가입된 이메일입니다."));
        }

        @Test
        @DisplayName("이메일 인증코드 전송이 실패하면 502코드와 메시지를 반환한다.")
        void returnsBadGateway_whenEmailVerificationCodeSendFails() throws Exception {
            //given
            String email = MemberTestData.DEFAULT_EMAIL;

            doThrow(new MailSendException("메일 전송 실패") {
            })
                    .when(emailSender)
                    .sendVerificationCode(eq(email), anyString());

            //when
            mockMvc.perform(
                            post(URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "email": "test@test.com"
                                            }
                                            """)
                    )
                    .andExpect(status().isBadGateway())
                    .andExpect(jsonPath("$.message").value("인증코드를 발송하지 못했습니다. 잠시 후 다시 시도해 주세요."));

        }

    }


    @Nested
    @DisplayName("이메일 인증할 때")
    class VerificationEmail {

        String URL = BASE_URL + "/email/verify-code";

        @Test
        @DisplayName("인증을 성공하면 토큰과 함께 성공 메시지를 반환한다.")
        void returnsTokenAndSuccessMessage_whenAuthenticationSucceeds() throws Exception {
            //given
            String email = MemberTestData.DEFAULT_EMAIL;
            String code = "123456";
            String hash = passwordEncoder.encode(code);

            redisRepository.saveCode(email, hash);

            //when
            mockMvc
                    .perform(
                            post(URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "email": "%s",
                                                "code": "%s"
                                            }
                                            """.formatted(email, code)
                                    )
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("이메일 인증에 성공했습니다."))
                    .andExpect(jsonPath("$.data").exists());
        }
        
        @Test
        @DisplayName("인증코드 형식이 유효하지 않으면 404 코드와 메시지를 반환한다.")
        void returns404AndMessage_whenAuthCodeIsInvalid() throws Exception {
            //given
            String email = MemberTestData.DEFAULT_EMAIL;
            String code = "abcde";

            //when
            mockMvc
                    .perform(
                            post(URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "email": "%s",
                                                "code": "%s"
                                            }
                                            """.formatted(email, code)
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("6자리 숫자만 입력해주세요.")); 
        }
        
        @Test
        @DisplayName("인증코드가 불일치하면 400 코드와 메시지를 반환한다.")
        void returns400AndMessage_whenAuthCodeDoesNotMatch() throws Exception {
            //given
            String email = MemberTestData.DEFAULT_EMAIL;
            String code = "123456";
            String hash = passwordEncoder.encode(code);

            redisRepository.saveCode(email, hash);

            //when
            mockMvc
                    .perform(
                            post(URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "email": "%s",
                                                "code": "%s"
                                            }
                                            """.formatted(email, "111111")
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("인증코드가 일치하지 않습니다. 다시 확인해주세요."));
        }
        
        @Test
        @DisplayName("인증 시간이 만료하면 404 코드와 메시지를 반환한다.")
        void returns404AndMessage_whenAuthCodeIsExpired() throws Exception {
        	//given
            String email = MemberTestData.DEFAULT_EMAIL;
            String code = "123456";

            //when
            mockMvc
                    .perform(
                            post(URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "email": "%s",
                                                "code": "%s"
                                            }
                                            """.formatted(email, "111111")
                                    )
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("인증 시간이 만료되었습니다. 다시 요청해주세요."));
        }
    }


    @Nested
    @DisplayName("토큰 재발급 요청할 때")
    class RefreshToken {

        private final String URL = BASE_URL + "/refresh";
        
        @Test
        @DisplayName("Refresh토큰으로 Access토큰을 재발급한다.")
        void reissuesAccessToken_whenRefreshTokenIsGiven() throws Exception {
        	//given
            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .build();

            insertMember(member);

            Cookie oldToken = accessTokenCookie(member.getMemberNumber());
            AccessToken refreshToken = tokenProvider.generateRefreshToken(
                    new CustomUserDetails(
                            MemberAuth.builder().build()
                    )
            );
        	
        	//when
            mockMvc.perform(
                    post(URL)
                            .cookie(oldToken)
                            .cookie(new Cookie("refreshToken", refreshToken.getToken()))
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").exists());
        }

        @Test
        @DisplayName("쿠키에 토큰이 없다면 400을 반환한다.")
        void rejectsRequest_whenTokenCookieIsMissing() throws Exception {
            //given
            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .build();

            insertMember(member);

            //when
            mockMvc.perform(
                            post(URL)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

    }

    @Nested
    @DisplayName("회원가입 요청할 때")
    class SignUp {

        String URL = BASE_URL + "/signup";

        @Test
        @DisplayName("가입하지 않은 사용자면 회원가입을 성공한다.")
        void createsUser_whenUserDoesNotExist() throws Exception {
        	//given
            MemberSignUpRequest request = MemberSignUpRequest.of(
                    MemberTestData.DEFAULT_USERNAME,
                    MemberTestData.DEFAULT_PASSWORD,
                    "apple",
                    MemberTestData.DEFAULT_BIRTHDATE,
                    MemberTestData.DEFAULT_NICKNAME,
                    MemberTestData.DEFAULT_EMAIL,
                    "token",
                    MemberTestData.DEFAULT_GENDER.getValue()
            );

            doNothing()
                    .when(memberService)
                    .processSignUp(request);
        	
        	//when
            mockMvc.perform(
                    post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request))
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("회원가입을 완료했습니다."))
                    .andExpect(jsonPath("$.next").value("/auth/login"));
        }

    }

}