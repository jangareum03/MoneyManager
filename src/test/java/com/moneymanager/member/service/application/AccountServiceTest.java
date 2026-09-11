package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.exception.code.ErrorCode;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.security.CustomUserDetailService;
import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.member.domain.dto.request.FindIdRequest;
import com.moneymanager.member.domain.dto.response.FindIdResponse;
import com.moneymanager.member.domain.query.MemberFindIdQuery;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.member.service.validation.AccountValidator;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    AccountValidator accountValidator;

    @Mock
    PasswordEncoder passwordEncoder;

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
            ).withUserMessage("사용자 메시지"))
                    .when(accountValidator)
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
                        .when(accountValidator)
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

}