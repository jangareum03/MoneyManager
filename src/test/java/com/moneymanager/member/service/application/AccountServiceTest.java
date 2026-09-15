package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.exception.code.ErrorCode;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.security.CustomUserDetailService;
import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.member.domain.dto.request.FindIdRequest;
import com.moneymanager.member.domain.dto.request.FindPwdRequest;
import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.member.domain.dto.response.FindIdResponse;
import com.moneymanager.member.domain.dto.response.FindPwdResponse;
import com.moneymanager.member.domain.entity.Member;
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
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import com.sun.mail.smtp.SMTPSendFailedException;
import com.sun.mail.util.MailConnectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.ErrorCode.EXTERNAL_API_ERROR;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Named.named;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : AccountServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 10<br>
 * 설명              : AccountService 클래스 요청을 검증하는 단위 테스트 클래스
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
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    AccountService target;

    @Mock
    CustomUserDetailService userDetailService;

    @Mock
    MemberReadService memberReadService;

    @Mock
    MemberCommandService memberCommandService;

    @Mock
    PasswordResetService passwordResetService;

    @Mock
    EmailSender emailSender;

    @Mock
    MemberValidator memberValidator;

    @Mock
    EmailVerificationService emailVerificationService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UuidGenerator uuidTokenGenerator;

    @Mock
    HashGenerator hashGenerator;

    @Mock
    EmailVerificationService emailVerification;


    @Nested
    @DisplayName("회원가입 진행할 때")
    class ProcessSignUp {

        MemberSignUpRequest request;

        @BeforeEach
        void setUp() {
            request = MemberSignUpRequest.of(
                    MemberTestData.DEFAULT_USERNAME,
                    MemberTestData.DEFAULT_PASSWORD,
                    MemberTestData.DEFAULT_NAME,
                    MemberTestData.DEFAULT_BIRTHDATE,
                    MemberTestData.DEFAULT_NICKNAME,
                    MemberTestData.DEFAULT_EMAIL,
                    "token",
                    MemberTestData.DEFAULT_GENDER.getValue()
            );
        }

        @Test
        @DisplayName("요청 검증 → 이메일 검증 여부 → 중복 검증 → 객체 변환 → 회원 저장 → 인증토큰 삭제 순서로 진행한다.")
        void processesSignUp_whenRequestIsValid() {
            //given
            Member member = mock(Member.class);

            when(memberCommandService.create(request))
                    .thenReturn(member);

            //when
            target.processSignUp(request);

            //then
            InOrder inOrder = Mockito.inOrder(memberCommandService, memberReadService, emailVerificationService, memberValidator);

            inOrder.verify(memberValidator).signUp(request);
            inOrder.verify(emailVerificationService).validateEmailToken(request.getEmail(), request.getToken());
            inOrder.verify(memberReadService).validateSignUpEligibility(request.getUsername(), request.getNickname());
            inOrder.verify(memberCommandService).create(request);
            inOrder.verify(memberCommandService).save(member);
            inOrder.verify(emailVerificationService).deleteToken(member.getEmail());
        }

        @Test
        @DisplayName("요청 정보 검증에 실패하면 이메일 검증 여부를 수행하지 않는다.")
        void doesNothing_whenRequestIsInvalid() {
            //given
            doThrow(ApplicationException.class)
                    .when(memberValidator)
                    .signUp(request);

            //when
            assertThatThrownBy(() -> target.processSignUp(request));

            //then
            verify(emailVerificationService, never()).validateEmailToken(request.getEmail(), request.getToken());
        }

        @Test
        @DisplayName("이메일 검증 여부가 실패하면 회원 중복 검증을 수행하지 않는다.")
        void doesNothing_whenTokenIsInvalid() {
            //given
            doThrow(ApplicationException.class)
                    .when(emailVerificationService)
                    .validateEmailToken(request.getEmail(), request.getToken());

            //when
            assertThatThrownBy(() -> target.processSignUp(request));

            //then
            verify(memberReadService, never()).validateSignUpEligibility(request.getUsername(), request.getNickname());
        }

        @Test
        @DisplayName("중복 검증에 실패하면 객체 변환을 수행하지 않는다.")
        void doesNothing_whenUserAlreadyExists() {
            //given
            doThrow(ApplicationException.class)
                    .when(memberReadService)
                    .validateSignUpEligibility(request.getUsername(), request.getNickname());

            //when
            assertThatThrownBy(() -> target.processSignUp(request));

            //then
            verify(memberCommandService, never()).create(request);
        }

        @Test
        @DisplayName("요청 객체 변환에 실패하면 저장하지 않는다.")
        void doesNothing_whenMappingFails() {
            //given
            when(memberCommandService.create(request))
                    .thenThrow(ApplicationException.class);

            //when
            assertThatThrownBy(() -> target.processSignUp(request));

            //then
            verify(memberCommandService, never()).save(any(Member.class));
        }

        @Test
        @DisplayName("회원 저장에 실패하면 토큰을 삭제하지 않는다.")
        void throwsException_whenSaveFails() {
            //given
            Member member = mock(Member.class);

            when(memberCommandService.create(request))
                    .thenReturn(member);

            doThrow(ApplicationException.class)
                    .when(memberCommandService)
                    .save(member);

            //when
            assertThatThrownBy(() -> target.processSignUp(request))
                    .isInstanceOf(ApplicationException.class);

            //then
            verify(emailVerificationService, never()).deleteToken(request.getEmail());
        }

    }


    @Nested
    @DisplayName("로그인 진행할 때")
    class Login {

        String username = MemberTestData.DEFAULT_USERNAME;
        String password = MemberTestData.DEFAULT_PASSWORD;

        @Test
        @DisplayName("정상적인 아이디와 비밀번호면 CustomUserDetails를 반환한다.")
        void returnsCustomUserDetails_whenCredentialsAreValid() {
            //given
            CustomUserDetails userDetails = mock(CustomUserDetails.class);

            when(userDetailService.loadUserByUsername(username))
                    .thenReturn(userDetails);

            when(userDetails.isAccountNonExpired())
                    .thenReturn(true);
            when(userDetails.isAccountNonLocked())
                    .thenReturn(true);
            when(userDetails.isEnabled())
                    .thenReturn(true);

            when(userDetails.getPassword())
                    .thenReturn("encodePassword");

            when(passwordEncoder.matches(password, userDetails.getPassword()))
                    .thenReturn(Boolean.TRUE);

            //when
            CustomUserDetails result = target.login(username, password);

            //then
            assertThat(result).isEqualTo(userDetails);
        }

        @Test
        @DisplayName("계정 검증에 실패하면 예외를 발생시킨다.")
        void throwsException_whenAccountValidationFails() {
            //given
            doThrow(new ApplicationException(
                    ErrorCode.INVALID_VALUE,
                    LogContent.of("work", "field", "value")
            ).withMessageKey("사용자 메시지"))
                    .when(memberValidator)
                    .validateLogin(username, password);

            //when
            assertThatThrownBy(() -> target.login(username, password))
                    .isInstanceOf(AuthenticationServiceException.class)
                    .hasMessage("사용자 메시지");
        }

        @Test
        @DisplayName("만료된 상태면 예외를 발생시킨다.")
        void throwsException_whenStateIsExpired() {
            //given
            CustomUserDetails userDetails = mock(CustomUserDetails.class);

            when(userDetailService.loadUserByUsername(username))
                    .thenReturn(userDetails);

            when(userDetails.isAccountNonExpired())
                    .thenReturn(false);

            //when
            assertThatThrownBy(() -> target.login(username, password))
                    .isInstanceOf(DisabledException.class)
                    .hasMessage("member.login.not_found");
        }

        @Test
        @DisplayName("잠긴 상태면 예외를 발생시킨다.")
        void throwsException_whenStateIsLocked() {
            //given
            CustomUserDetails userDetails = mock(CustomUserDetails.class);

            when(userDetailService.loadUserByUsername(username))
                    .thenReturn(userDetails);

            when(userDetails.isAccountNonExpired())
                    .thenReturn(true);
            when(userDetails.isAccountNonLocked())
                    .thenReturn(false);

            //when
            assertThatThrownBy(() -> target.login(username, password))
                    .isInstanceOf(LockedException.class)
                    .hasMessage("member.login.locked");
        }

        @Test
        @DisplayName("비활성화 상태면 예외를 발생시킨다.")
        void throwsException_whenStateIsDisabled() {
            //given
            CustomUserDetails userDetails = mock(CustomUserDetails.class);

            when(userDetailService.loadUserByUsername(username))
                    .thenReturn(userDetails);

            when(userDetails.isAccountNonExpired())
                    .thenReturn(true);
            when(userDetails.isAccountNonLocked())
                    .thenReturn(true);
            when(userDetails.isEnabled())
                    .thenReturn(false);

            //when
            assertThatThrownBy(() -> target.login(username, password))
                    .isInstanceOf(DisabledException.class)
                    .hasMessage("member.login.restricted");
        }

        @Test
        @DisplayName("비밀번호가 불일치하면 예외를 발생시킨다.")
        void throwsException_whenPasswordMismatch() {
            //given
            CustomUserDetails userDetails = mock(CustomUserDetails.class);

            when(userDetailService.loadUserByUsername(username))
                    .thenReturn(userDetails);

            when(userDetails.isAccountNonExpired())
                    .thenReturn(true);
            when(userDetails.isAccountNonLocked())
                    .thenReturn(true);
            when(userDetails.isEnabled())
                    .thenReturn(true);

            when(userDetails.getPassword())
                    .thenReturn("encodedPassword");

            when(passwordEncoder.matches(password, "encodedPassword"))
                    .thenReturn(false);

            //when
            assertThatThrownBy(() -> target.login(username, password))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("member.login.failed");
        }
    }


    @Nested
    @DisplayName("아이디 찾기 진행할 때")
    class FindId {

        private final FindIdRequest request = mock(FindIdRequest.class);

        @Nested
        @DisplayName("성공")
        class Success {

            @ParameterizedTest
            @MethodSource("validRequest")
            @DisplayName("아이디를 마스핑 처리 후 반환한다.")
            void returnsMaskedUsername_whenRequestIsValid(String username, String expectedMasked) {
                //given
                when(memberReadService.getMemberStatus(request.getName(), request.getEmail()))
                        .thenReturn(
                                new MemberFindIdQuery(
                                        username,
                                        MemberTestData.DEFAULT_STATUS
                                )
                        );

                //when
                FindIdResponse result = target.findId(request);

                //then
                assertThat(result)
                        .isNotNull()
                        .extracting(FindIdResponse::getUsername)
                        .isEqualTo(expectedMasked);
            }

            static Stream<Arguments> validRequest() {
                return Stream.of(
                        Arguments.of(
                                named("아이디가 2글자인 경우(경계값)", "ab"),
                                "a*"
                        ),
                        Arguments.of(
                                named("아이디가 3글자인 경우", "abc"),
                                "a*c"
                        ),
                        Arguments.of(
                                named("아이디가 4글자인 경우", "abcd"),
                                "a**d"
                        ),
                        Arguments.of(
                                named("아이디가 5글자인 경우(경계값)", "abcde"),
                                "a**de"
                        )
                );
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("입력값이 유효하지 않으면 예외를 전파시킨다.")
            void throwsException_whenRequestIsInvalid() {
                //given
                doThrow(ApplicationException.class)
                        .when(memberValidator)
                        .validateFindId(request);

                //when
                assertThatThrownBy(() -> target.findId(request))
                        .isInstanceOf(ApplicationException.class);
            }

            @Test
            @DisplayName("아이디가 조회되지 않으면 예외를 잔파시킨다.")
            void throwsException_whenIdDoesNotExist() {
                //given
                when(memberReadService.getMemberStatus(request.getName(), request.getEmail()))
                        .thenThrow(ApplicationException.class);

                //when
                assertThatThrownBy(() -> target.findId(request))
                        .isInstanceOf(ApplicationException.class);
            }

        }

    }


    @Nested
    @DisplayName("비밀번호 찾기 진행할 때")
    class FindPwd {

        FindPwdRequest request = new FindPwdRequest(
                MemberTestData.DEFAULT_NAME,
                MemberTestData.DEFAULT_USERNAME
        );
        
        String name = request.getName();
        String username = request.getUsername();
        String email = MemberTestData.DEFAULT_EMAIL;

        @Nested
        @DisplayName("성공")
        class Success {

            @BeforeEach
            void setUp() throws MessagingException {
                doNothing()
                        .when(memberValidator)
                                .validateFindPassword(request);

                doNothing()
                        .when(emailSender)
                        .sendPasswordResetLink(eq(email), anyString());
            }

            @Test
            @DisplayName("마스킹 처리된 이메일이 반환된다.")
            void returnsMaskedEmail_whenRequestIsValid() {
                //given
                when(memberReadService.getEmail(name, username))
                        .thenReturn(email);

                when(uuidTokenGenerator.generate())
                        .thenReturn("token");

                when(hashGenerator.sha256("token"))
                        .thenReturn("hash-token");

                when(memberCommandService.getMaskedEmail(email))
                        .thenReturn("maskedEmail");

            	//when
                FindPwdResponse result =  target.findPassword(request);
            	
            	//then
            	assertThat(result)
                        .isNotNull()
                        .extracting(FindPwdResponse::getEmail)
                        .isEqualTo("maskedEmail");
            }

            @Test
            @DisplayName("Redis 저장할 때 토큰은 해시값을 전달한다.")
            void savesTokenWithHash_whenSavedToRedis() {
                //given
                when(memberReadService.getEmail(name, username))
                        .thenReturn(email);

                when(uuidTokenGenerator.generate())
                        .thenReturn("token");

                when(hashGenerator.sha256("token"))
                        .thenReturn("hash-token");

                when(memberCommandService.getMaskedEmail(email))
                        .thenReturn("maskedEmail");

                //when
                target.findPassword(request);

                //then
                verify(passwordResetService).saveToken("hash-token");
            }

            @Test
            @DisplayName("이메일 발송할 때 원본 토큰을 전달한다.")
            void sendsEmailWithToken_whenEmailIsSent() throws MessagingException {
                //given
                when(memberReadService.getEmail(name, username))
                        .thenReturn(email);

                when(uuidTokenGenerator.generate())
                        .thenReturn("token");

                when(hashGenerator.sha256("token"))
                        .thenReturn("hash-token");

                when(memberCommandService.getMaskedEmail(email))
                        .thenReturn("maskedEmail");

                //when
                target.findPassword(request);

                //then
                verify(emailSender).sendPasswordResetLink(email, "token");
            }

            @Test
            @DisplayName("이메일 발송에 성공하면 재시도하지 않는다.")
            void doesNotRetry_whenEmailSendSucceeds() throws MessagingException {
                //given
                when(memberReadService.getEmail(name, username))
                        .thenReturn(email);

                when(uuidTokenGenerator.generate())
                        .thenReturn("token");

                when(hashGenerator.sha256("token"))
                        .thenReturn("hash-token");

                when(memberCommandService.getMaskedEmail(email))
                        .thenReturn("maskedEmail");

                //when
                target.findPassword(request);

                //then
                verify(emailSender, times(1)).sendPasswordResetLink(email, "token");
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("요청 입력값 검증 실패하면 이메일 조회를 진행하지 않는다.")
            void doesNothing_whenRequestIsInvalid() {
            	//given
                doThrow(ApplicationException.class)
                        .when(memberValidator)
                        .validateFindPassword(request);
            	
            	//when
                assertThatThrownBy(() -> target.findPassword(request))
                        .isInstanceOf(ApplicationException.class);
            	
            	//then
            	verify(memberReadService, never()).getEmail(name, username);
            }

            @Test
            @DisplayName("이메일 조회를 실패하면 Redis 저장을 진행하지 않는다.")
            void doesNotSaveToRedis_whenEmailFetchFails() {
                //given
                when(memberReadService.getEmail(name, username))
                        .thenThrow(ApplicationException.class);

                //when
                assertThatThrownBy(() -> target.findPassword(request))
                        .isInstanceOf(ApplicationException.class);

                //then
                verify(uuidTokenGenerator, never()).generate();
                verify(passwordResetService, never()).saveToken(anyString());
            }

            @Test
            @DisplayName("Redis 저장에 실패하면 이메일 발송을 진행하지 않는다.")
            void doesNotSendEmail_whenRedisSaveFails() throws MessagingException {
                //given:
                when(memberReadService.getEmail(name, username))
                        .thenReturn(MemberTestData.DEFAULT_EMAIL);

                when(uuidTokenGenerator.generate())
                        .thenReturn("token");

                when(hashGenerator.sha256("token"))
                        .thenReturn("hash-token");

                doThrow(RuntimeException.class)
                        .when(passwordResetService)
                        .saveToken("hash-token");

                //when
                assertThatThrownBy(() -> target.findPassword(request))
                        .isInstanceOf(RuntimeException.class);

                //then
                verify(emailSender, never()).sendPasswordResetLink(email, "token");
            }

            @Test
            @DisplayName("이메일 발송에 실패하면 이메일 마스킹을 진행하지 않는다.")
            void doesNotMaskEmail_whenEmailSendFails() throws MessagingException {
                //given
                when(memberReadService.getEmail(name, username))
                        .thenReturn(MemberTestData.DEFAULT_EMAIL);

                when(uuidTokenGenerator.generate())
                        .thenReturn("token");

                when(hashGenerator.sha256("token"))
                        .thenReturn("hash-token");

                doThrow(MessagingException.class)
                        .when(emailSender)
                        .sendPasswordResetLink(email, "token");

                //when
                assertThatThrownBy(() -> target.findPassword(request))
                        .isInstanceOf(ApplicationException.class);

                //then
                verify(memberCommandService, never()).getMaskedEmail(email);
            }

            @Test
            @DisplayName("MailConnectException 발생 시 재시도한다.")
            void retries_whenMailConnectExceptionOccurs() throws MessagingException {
                //given
                when(memberReadService.getEmail(name, username))
                        .thenReturn(MemberTestData.DEFAULT_EMAIL);

                when(uuidTokenGenerator.generate())
                        .thenReturn("token");

                when(hashGenerator.sha256("token"))
                        .thenReturn("hash-token");

                doThrow(MailConnectException.class)
                        .doNothing()
                        .when(emailSender)
                        .sendPasswordResetLink(email, "token");

                //when
                assertDoesNotThrow(() -> target.findPassword(request));

                //then
                verify(emailSender, times(2)).sendPasswordResetLink(email, "token");
            }

            @Test
            @DisplayName("SMTPSendFailedException 발생 시 재시도한다.")
            void retries_whenSMTPSendFailedExceptionOccurs() throws MessagingException {
                //given
                SMTPSendFailedException exception = mock(SMTPSendFailedException.class);

                when(memberReadService.getEmail(name, username))
                        .thenReturn(MemberTestData.DEFAULT_EMAIL);

                when(uuidTokenGenerator.generate())
                        .thenReturn("token");

                when(hashGenerator.sha256("token"))
                        .thenReturn("hash-token");

                when(exception.getReturnCode())
                        .thenReturn(400);

                doThrow(exception)
                        .doNothing()
                        .when(emailSender)
                        .sendPasswordResetLink(email, "token");

                //when
                assertDoesNotThrow(() -> target.findPassword(request));

                //then
                verify(emailSender, times(2)).sendPasswordResetLink(email, "token");
            }

            @Test
            @DisplayName("SMTPSendFailedException의 코드가 400번대가 아니면 재시도하지 않는다.")
            void doesNotRetry_whenSMTPErrorCodeIsOutOf4xxRange() throws MessagingException {
                //given
                SMTPSendFailedException exception = mock(SMTPSendFailedException.class);

                when(memberReadService.getEmail(name, username))
                        .thenReturn(MemberTestData.DEFAULT_EMAIL);

                when(uuidTokenGenerator.generate())
                        .thenReturn("token");

                when(hashGenerator.sha256("token"))
                        .thenReturn("hash-token");

                when(exception.getReturnCode())
                        .thenReturn(500);

                doThrow(exception)
                        .when(emailSender)
                        .sendPasswordResetLink(email, "token");

                //when
                assertThatThrownBy(() -> target.findPassword(request))
                        .isInstanceOf(ApplicationException.class);

                //then
                verify(emailSender, times(1)).sendPasswordResetLink(email, "token");
            }

            @Test
            @DisplayName("최대 3번까지 재시도한다.")
            void retriesUpToThreeTimes_whenSendFails() throws MessagingException {
                //given
                SMTPSendFailedException exception = mock(SMTPSendFailedException.class);

                when(memberReadService.getEmail(name, username))
                        .thenReturn(MemberTestData.DEFAULT_EMAIL);

                when(uuidTokenGenerator.generate())
                        .thenReturn("token");

                when(hashGenerator.sha256("token"))
                        .thenReturn("hash-token");

                when(exception.getReturnCode())
                        .thenReturn(400);

                doThrow(exception)
                        .doThrow(exception)
                        .doThrow(exception)
                        .when(emailSender)
                        .sendPasswordResetLink(email, "token");

                //when
                assertThatThrownBy(() -> target.findPassword(request))
                        .isInstanceOf(ApplicationException.class);

                //then
                verify(emailSender, times(3)).sendPasswordResetLink(email, "token");
            }

            @Test
            @DisplayName("재시도 대상이 아닌 MessagingException은 재시도하지 않는다.")
            void doesNotRetry_whenNonRetryableMessagingExceptionOccurs() throws MessagingException {
                //given
                when(memberReadService.getEmail(name, username))
                        .thenReturn(MemberTestData.DEFAULT_EMAIL);

                when(uuidTokenGenerator.generate())
                        .thenReturn("token");

                when(hashGenerator.sha256("token"))
                        .thenReturn("hash-token");

                doThrow(MessagingException.class)
                        .when(emailSender)
                        .sendPasswordResetLink(email, "token");

                //when
                assertThatThrownBy(() -> target.findPassword(request))
                        .isInstanceOf(ApplicationException.class);

                //then
                verify(emailSender, times(1)).sendPasswordResetLink(email, "token");
            }

            @Test
            @DisplayName("3번 재시도 후 실패하면 ApplicationException을 발생시킨다.")
            void throwsApplicationException_whenRetryFailsThreeTimes() throws MessagingException {
                //given
                SMTPSendFailedException exception = mock(SMTPSendFailedException.class);

                when(memberReadService.getEmail(name, username))
                        .thenReturn(MemberTestData.DEFAULT_EMAIL);

                when(uuidTokenGenerator.generate())
                        .thenReturn("token");

                when(hashGenerator.sha256("token"))
                        .thenReturn("hash-token");

                when(exception.getReturnCode())
                        .thenReturn(400);

                doThrow(exception)
                        .doThrow(exception)
                        .doThrow(exception)
                        .doThrow(exception)
                        .when(emailSender)
                        .sendPasswordResetLink(email, "token");

                //when
                Throwable throwable = catchThrowable(() -> target.findPassword(request));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(EXTERNAL_API_ERROR)
                        .hasWork("메일 발송")
                        .hasField("email")
                        .hasValue(EmailMasker.mask(email))
                        .hasMessageKey("email.send.failed")
                        .hasMessageArgs("비밀번호 변경");

                verify(emailSender, times(3)).sendPasswordResetLink(email, "token");
            }

        }

    }


    @Nested
    @DisplayName("이메일 인증코드 검증할 때")
    class VerifyEmail {

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

                when(emailVerification.generateAuthCode())
                        .thenReturn(code);

                when(passwordEncoder.encode(code))
                        .thenReturn(hashCode);

                //when
                assertDoesNotThrow(() -> target.verifyEmail(email));

                //then
                InOrder inOrder = Mockito.inOrder(emailVerification, passwordEncoder, emailVerification, emailSender);

                inOrder.verify(emailVerification).generateAuthCode();
                inOrder.verify(passwordEncoder).encode(code);
                inOrder.verify(emailVerification).saveCode(email, hashCode);
                inOrder.verify(emailSender).sendVerificationCode(email, code);

                verify(emailVerification, never()).deleteCode(email);
            }

            @Test
            @DisplayName("생성된 인증코드는 해시코드로 변환한다.")
            void convertsCodeToHashCode_whenCodeIsGenerated() {
                //given
                String email = "test@test.com";
                String code = "123456";

                when(emailVerification.generateAuthCode())
                        .thenReturn(code);

                //when
                target.verifyEmail(email);

                //then
                ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

                verify(emailVerification).saveCode(eq(email), captor.capture());

                assertThat(code).isNotEqualTo(captor.getValue());
            }

            @Test
            @DisplayName("전송할 이메일에서는 원본 인증코드를 전달한다.")
            void sendsEmailWithRawCode_whenRequestIsValid() {
                //given
                String email = "test@test.com";
                String code = "123456";
                String hashCode = "hash123456";

                when(emailVerification.generateAuthCode())
                        .thenReturn(code);

                when(passwordEncoder.encode(code))
                        .thenReturn(hashCode);

                //when
                target.verifyEmail(email);

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

                when(emailVerification.generateAuthCode())
                        .thenReturn(code);

                when(passwordEncoder.encode(code))
                        .thenReturn(hashCode);

                //when
                target.verifyEmail(email);

                //then
                verify(emailVerification, never()).deleteCode(email);
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

                when(emailVerification.generateAuthCode())
                        .thenReturn(code);

                when(passwordEncoder.encode(code))
                        .thenReturn(hashCode);

                doThrow(new MailSendException("메일 전송 실패"))
                        .when(emailSender)
                        .sendVerificationCode(email, code);

                //when
                Throwable throwable = catchThrowable(() -> target.verifyEmail(email));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(EXTERNAL_API_ERROR)
                        .hasWork("이메일 전송")
                        .hasCauseMessage("인증코드 이메일 발송 오류")
                        .hasField("email")
                        .hasValue("te*****@test.com");

                verify(emailSender).sendVerificationCode(email, code);
                verify(emailVerification).deleteCode(email);
            }

        }

    }

}