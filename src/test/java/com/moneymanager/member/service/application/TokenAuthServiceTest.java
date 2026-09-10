package com.moneymanager.member.service.application;

import com.moneymanager.global.domain.dto.response.AccessToken;
import com.moneymanager.global.security.CustomUserDetailService;
import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.global.security.jwt.JwtTokenProvider;
import com.moneymanager.member.repository.MemberTokenRepository;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.moneymanager.global.exception.code.ErrorCode.EXPIRED_TOKEN;
import static com.moneymanager.global.exception.code.ErrorCode.INVALID_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : TokenAuthServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 10<br>
 * 설명              : TokenAuthService 클래스 로직을 검증하는 단위 테스트 클래스
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
class TokenAuthServiceTest {

    @InjectMocks
    TokenAuthService target;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    CustomUserDetailService userDetailService;

    @Mock
    MemberTokenRepository tokenRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    MockHttpServletResponse response;


    @Nested
    @DisplayName("인증 정보 설정할 때")
    class Authenticate {

        @BeforeEach
        void setUp() {
            response = new MockHttpServletResponse();
        }

        @Test
        @DisplayName("Access토큰이 정상이면 인증 정보를 설정한다.")
        void setsAuthentication_whenAccessTokenIsValid() throws IOException {
            //given
            String accessToken = "accessToken";

            CustomUserDetails customUserDetails = mock(CustomUserDetails.class);

            Jws jws = mock(Jws.class);
            Claims claims = mock(Claims.class);

            when(jwtTokenProvider.parseToken(accessToken))
                    .thenReturn(jws);

            when(jws.getPayload())
                    .thenReturn(claims);

            when(claims.getSubject())
                    .thenReturn(MemberTestData.DEFAULT_NUMBER);

            when(userDetailService.loadUserByMemberNumber(MemberTestData.DEFAULT_NUMBER))
                    .thenReturn(customUserDetails);

            //when
            boolean result = target.authenticate(accessToken, response);

            //then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Access토큰이 만료되면 인증 정보 설정을 호출하지 않는다.")
        void doesNothing_whenAccessTokenIsExpired() throws IOException {
            //given
            String accessToken = "accessToken";

            when(jwtTokenProvider.parseToken(accessToken))
                    .thenThrow(ExpiredJwtException.class);

            //when
            boolean result = target.authenticate(accessToken, response);

            //then
            assertThat(result).isFalse();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
            assertThat(response.getContentType()).contains("application/json");
            assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");
            assertThat(response.getContentAsString()).contains("ACCESS_TOKEN_EXPIRED");
        }

        @Test
        @DisplayName("Access토큰이 유효하지 않으면 인증 정보 설정을 호출하지 않는다.")
        void doesNothing_whenAccessTokenIsInvalid() throws IOException {
            //given
            String accessToken = "accessToken";

            when(jwtTokenProvider.parseToken(accessToken))
                    .thenThrow(JwtException.class);

            //when
            boolean result = target.authenticate(accessToken, response);

            //then
            assertThat(result).isFalse();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
            assertThat(response.getContentType()).contains("application/json");
            assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");
            assertThat(response.getContentAsString()).contains("INVALID_ACCESS_TOKEN");
        }
    }


    @Nested
    @DisplayName("토큰 처음 발급할 때")
    class IssueToken {

        @BeforeEach
        void setUp() {
            response = mock(MockHttpServletResponse.class);
        }

        @Test
        @DisplayName("회원가입한 회원으로 토큰을 발급한다.")
        void createsToken_whenUserExists() {
            //given
            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            AccessToken accessToken = mock(AccessToken.class);
            AccessToken refreshToken = mock(AccessToken.class);

            Date expirationDate = mock(Date.class);

            when(jwtTokenProvider.generateAccessToken(userDetails))
                    .thenReturn(accessToken);

            when(jwtTokenProvider.generateRefreshToken(userDetails))
                    .thenReturn(refreshToken);

            when(userDetails.getId())
                    .thenReturn(MemberTestData.DEFAULT_ID);

            when(accessToken.getToken())
                    .thenReturn("access-token");

            when(refreshToken.getToken())
                    .thenReturn("refresh-token");

            when(passwordEncoder.encode(refreshToken.getToken()))
                    .thenReturn("hashRefreshToken");

            when(refreshToken.getExpiration())
                    .thenReturn(expirationDate);

            //when
            target.issueTokens(userDetails, response);

            //then
            verify(tokenRepository).saveToken(MemberTestData.DEFAULT_ID, "hashRefreshToken", expirationDate);

            //쿠키 검증
            ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);

            verify(response, times(2)).addCookie(captor.capture());

            List<Cookie> cookieList = captor.getAllValues();

            Cookie accessCookie = cookieList.stream()
                    .filter(c -> c.getName().equals("accessToken"))
                    .findFirst()
                    .orElseThrow();

            assertThat(accessCookie.getPath()).isEqualTo("/");
            assertThat(accessCookie.getValue()).isEqualTo("access-token");
            assertThat(accessCookie.getMaxAge()).isEqualTo(60 * 60);

            Cookie refreshCookie = cookieList.stream()
                    .filter(c -> c.getName().equals("refreshToken"))
                    .findFirst()
                    .orElseThrow();

            assertThat(refreshCookie.getPath()).isEqualTo("/api/auth/refresh");
            assertThat(refreshCookie.getValue()).isEqualTo("refresh-token");
            assertThat(refreshCookie.getMaxAge()).isEqualTo(60 * 60 * 24);
        }

    }


    @Nested
    @DisplayName("토큰 재발급할 때")
    class ReissueToken {

        String accessToken = "access_token";
        String refreshToken = "refresh_token";
        String memberNumber = MemberTestData.DEFAULT_NUMBER;

        @BeforeEach
        void setUp() {
            response = new MockHttpServletResponse();
        }

        @Test
        @DisplayName("Refresh 토큰으로 AccessToken을 재발급한다.")
        void returnsAccessToken_whenRefreshTokenIsValid() {
            //given
            when(jwtTokenProvider.isRefreshToken(refreshToken))
                    .thenReturn(true);

            when(jwtTokenProvider.parseToken(refreshToken).getPayload().getSubject())
                    .thenReturn(memberNumber);

            when(jwtTokenProvider.createAccessToken(memberNumber))
                    .thenReturn(accessToken);

            //when
            target.reissueToken(refreshToken, response);

            //then
            Cookie cookie = response.getCookie("accessToken");

            assertThat(cookie).isNotNull();
            assertThat(cookie.getValue()).isEqualTo(accessToken);
        }

        @Test
        @DisplayName("토큰이 유효하지 않으면 예외를 발생시킨다.")
        void throwsException_whenTokenIsInvalid() {
            //given
            doThrow(UnsupportedJwtException.class)
                    .when(jwtTokenProvider)
                    .parseToken(refreshToken);

            //when
            Throwable throwable = catchThrowable(() -> target.reissueToken(refreshToken, response));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_TOKEN)
                    .hasWork("Refresh 토큰 검증")
                    .hasField("refreshToken")
                    .hasValue("refres*******")
                    .hasCauseMessage("유효하지 않은 refreshToken")
                    .hasUserMessage("member.token.failed");
        }

        @Test
        @DisplayName("토큰이 만료되면 예외를 발생시킨다.")
        void throwsException_whenTokenIsExpired() {
            //given
            doThrow(ExpiredJwtException.class)
                    .when(jwtTokenProvider)
                    .parseToken(refreshToken);

            //when
            Throwable throwable = catchThrowable(() -> target.reissueToken(refreshToken, response));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(EXPIRED_TOKEN)
                    .hasWork("Refresh 토큰 검증")
                    .hasField("refreshToken")
                    .hasValue("refres*******")
                    .hasCauseMessage("만료된 refreshToken")
                    .hasUserMessage("member.token.expired");
        }

        @Test
        @DisplayName("Refresh 토큰이 아니면 예외를 발생시킨다.")
        void throwsException_whenTokenIsNotRefreshToken() {
            //given
            when(jwtTokenProvider.isRefreshToken(refreshToken))
                    .thenReturn(false);

            //when
            Throwable throwable = catchThrowable(() -> target.reissueToken(refreshToken, response));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_TOKEN)
                    .hasWork("Refresh 토큰 검증")
                    .hasField("refreshToken")
                    .hasValue("refres*******")
                    .hasCauseMessage("refreshToken 아님")
                    .hasUserMessage("member.token.failed");
        }

    }


    @Nested
    @DisplayName("로그아웃 할 때")
    class Logout {
        
        String refreshToken = "refresh-token";
        String memberNumber = MemberTestData.DEFAULT_NUMBER;
        
        Jws<Claims> jws = mock(Jws.class);
        Claims claims = mock(Claims.class);
        
        @BeforeEach
        void setUp() {
            when(jwtTokenProvider.parseToken(refreshToken))
                    .thenReturn(jws);
            
            when(jws.getPayload())
                    .thenReturn(claims);
            
            when(claims.getSubject())
                    .thenReturn(memberNumber);
        }
        
        @Test
        @DisplayName("Refresh토큰이 있다면 토큰 비교 후 삭제한다.")
        void deletesToken_whenRefreshTokenExists() {
            //given
            when(tokenRepository.findRefreshTokenByMemberNumber(memberNumber))
                    .thenReturn("hash-refresh-token");
            
            when(passwordEncoder.matches(refreshToken, "hash-refresh-token"))
                    .thenReturn(true);
            
            when(tokenRepository.deleteRefreshToken("hash-refresh-token"))
                    .thenReturn(1);
            
            //when
            target.logout(refreshToken);
            
            //then
            verify(tokenRepository).findRefreshTokenByMemberNumber(memberNumber);
            verify(passwordEncoder).matches(refreshToken, "hash-refresh-token");
            verify(tokenRepository).deleteRefreshToken("hash-refresh-token");
        }
        
        @Test
        @DisplayName("Refresh토큰이 없다면 비교를 하지 않는다.")
        void doesNothing_whenRefreshTokenDoesNotExist() {
            //given
            when(tokenRepository.findRefreshTokenByMemberNumber(memberNumber))
                    .thenReturn(null);

            //when
            target.logout(refreshToken);

            //then
            verify(tokenRepository).findRefreshTokenByMemberNumber(memberNumber);

            verify(passwordEncoder, never()).matches(any(), any());
            verify(tokenRepository, never()).deleteRefreshToken(refreshToken);
        }
        
        @Test
        @DisplayName("Refresh토큰이 일치하지 않는다면 삭제하지 않는다.")
        void doesNothing_whenRefreshTokenIsInvalid() {
        	//given
            when(tokenRepository.findRefreshTokenByMemberNumber(memberNumber))
                    .thenReturn("hash-refresh-token");

            when(passwordEncoder.matches(refreshToken, "hash-refresh-token"))
                    .thenReturn(false);

            //when
            target.logout(refreshToken);

            //then
            verify(tokenRepository).findRefreshTokenByMemberNumber(memberNumber);
            verify(passwordEncoder).matches(refreshToken, "hash-refresh-token");

            verify(tokenRepository, never()).deleteRefreshToken(refreshToken);
        }
    }

}