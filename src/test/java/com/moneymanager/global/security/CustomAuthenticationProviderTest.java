package com.moneymanager.global.security;

import com.moneymanager.member.service.application.AccountService;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    AccountService accountService;

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

            when(accountService.login(username, password))
                    .thenReturn(userDetails);

            //when
            Authentication result = target.authenticate(authentication);

            //then
            assertInstanceOf(
                    UsernamePasswordAuthenticationToken.class,
                    result
            );

            assertThat(userDetails).isSameAs(result.getPrincipal());
            assertThat(result.getCredentials()).isNull();
        }
        
        @Test
        @DisplayName("계정이 유효하지 않으면 예외를 발생시킨다.")
        void throwsException_whenAccountIsInvalid() {
        	//given
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);

            when(accountService.login(username, password))
                    .thenThrow(BadCredentialsException.class);

        	//when
            assertThatThrownBy(() -> target.authenticate(authentication))
                    .isInstanceOf(AuthenticationException.class);
        }

    }

}