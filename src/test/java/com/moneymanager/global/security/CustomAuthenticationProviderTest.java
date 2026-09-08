package com.moneymanager.global.security;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.exception.code.ErrorCode;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.member.service.validation.AuthValidator;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.security<br>
 * 파일이름       : CustomAuthenticationProviderTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 8<br>
 * 설명              : EmailVerificationRedisKey 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 9. 8</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class CustomAuthenticationProviderTest {

    @InjectMocks
    CustomAuthenticationProvider target;

    @Mock
    UserDetailsService userDetailsService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthValidator authValidator;

    @Nested
    @DisplayName("사용자 조회할 때")
    class Authenticate {

        String username = MemberTestData.DEFAULT_USERNAME;
        String password = MemberTestData.DEFAULT_PASSWORD;

        @Test
        @DisplayName("정상적은 로그인 정보라면 사용자 토큰을 반환한다.")
        void returnsUserToken_whenLoginInfoIsValid() {
            //given
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);

            CustomUserDetails userDetails = mock(CustomUserDetails.class);

            when(userDetailsService.loadUserByUsername(username))
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
                    .thenReturn(true);

            //when
            Authentication result = target.authenticate(authentication);

            //then
            assertInstanceOf(
                    UsernamePasswordAuthenticationToken.class,
                    result
            );

            assertThat(userDetails).isSameAs(result.getPrincipal());
            assertThat(result.getCredentials()).isNull();

            verify(authValidator).login(username, password);
            verify(userDetailsService).loadUserByUsername(username);
            verify(passwordEncoder).matches(password, "encodedPassword");
        }

        @Test
        @DisplayName("만료된 상태면 예외를 발생시킨다.")
        void throwsException_whenStateIsExpired() {
        	//given
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);

            CustomUserDetails userDetails = mock(CustomUserDetails.class);

            when(userDetailsService.loadUserByUsername(username))
                    .thenReturn(userDetails);

            when(userDetails.isAccountNonExpired())
                    .thenReturn(false);
        	
        	//when
            assertThatThrownBy(() -> target.authenticate(authentication))
                    .isInstanceOf(DisabledException.class)
                    .hasMessage("member.login.not_found");
        }
        
        @Test
        @DisplayName("잠긴 상태면 예외를 발생시킨다.")
        void throwsException_whenStateIsLocked() {
            //given
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);

            CustomUserDetails userDetails = mock(CustomUserDetails.class);

            when(userDetailsService.loadUserByUsername(username))
                    .thenReturn(userDetails);

            when(userDetails.isAccountNonExpired())
                    .thenReturn(true);
            when(userDetails.isAccountNonLocked())
                    .thenReturn(false);

            //when
            assertThatThrownBy(() -> target.authenticate(authentication))
                    .isInstanceOf(LockedException.class)
                    .hasMessage("member.login.locked");
        }
        
        @Test
        @DisplayName("비활성화 상태면 예외를 발생시킨다.")
        void throwsException_whenStateIsDisabled() {
            //given
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);

            CustomUserDetails userDetails = mock(CustomUserDetails.class);

            when(userDetailsService.loadUserByUsername(username))
                    .thenReturn(userDetails);

            when(userDetails.isAccountNonExpired())
                    .thenReturn(true);
            when(userDetails.isAccountNonLocked())
                    .thenReturn(true);
            when(userDetails.isEnabled())
                    .thenReturn(false);

            //when
            assertThatThrownBy(() -> target.authenticate(authentication))
                    .isInstanceOf(DisabledException.class)
                    .hasMessage("member.login.restricted");
        }

        @Test
        @DisplayName("계정 검증에 실패하면 예외를 발생시킨다.")
        void throwsException_whenAccountValidationFails() {
            //given
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);

            doThrow(new ApplicationException(
                    ErrorCode.INVALID_VALUE,
                    LogContent.of("work", "field", "value")
            ).withUserMessage("사용자 메시지"))
                    .when(authValidator)
                    .login(username, password);

            //when
            assertThatThrownBy(() -> target.authenticate(authentication))
                    .isInstanceOf(AuthenticationServiceException.class)
                    .hasMessage("사용자 메시지");
        }

        @Test
        @DisplayName("비밀번호가 불일치하면 예외를 발생시킨다.")
        void throwsException_whenPasswordMismatch() {
        	//given
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);

            CustomUserDetails userDetails = mock(CustomUserDetails.class);

            when(userDetailsService.loadUserByUsername(username))
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
            assertThatThrownBy(() -> target.authenticate(authentication))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("member.login.failed");
        }

    }

}