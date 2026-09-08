package com.moneymanager.global.security;

import com.moneymanager.global.domain.dto.response.AccessToken;
import com.moneymanager.global.security.jwt.JwtTokenProvider;
import com.moneymanager.member.repository.MemberTokenRepository;
import com.moneymanager.support.data.MemberTestData;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.security<br>
 * 파일이름       : CustomAuthSuccessHandlerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 8<br>
 * 설명              : CustomAuthSuccessHandler 클래스 로직을 검증하는 단위 테스트 클래스
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
class CustomAuthSuccessHandlerTest {

    @InjectMocks
    CustomAuthSuccessHandler target;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    MemberTokenRepository tokenRepository;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Authentication authentication;

    @Mock
    CustomUserDetails userDetails;

    @Mock
    AccessToken accessToken;

    @Mock
    AccessToken refreshToken;

    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(userDetails.getId())
                .thenReturn(MemberTestData.DEFAULT_ID);

        when(jwtTokenProvider.generateAccessToken(userDetails))
                .thenReturn(accessToken);

        when(jwtTokenProvider.generateRefreshToken(userDetails))
                .thenReturn(refreshToken);

        when(accessToken.getToken())
                .thenReturn("access-value");

        when(refreshToken.getToken())
                .thenReturn("refresh-value");
    }

    @Test
    @DisplayName("인증 성공하면 토큰 생성 홈으로 리다이렉트한다.")
    void generatesToken_whenAuthenticationSucceeds() throws IOException {
        //given
        String memberId = MemberTestData.DEFAULT_ID;
        Date now = new Date();

        when(passwordEncoder.encode(refreshToken.getToken()))
                .thenReturn("refresh-token-value");

        when(refreshToken.getExpiration())
                .thenReturn(now);

        //when
        target.onAuthenticationSuccess(request, response, authentication);

        //then
        verify(jwtTokenProvider).generateAccessToken(userDetails);
        verify(jwtTokenProvider).generateRefreshToken(userDetails);
        verify(tokenRepository).saveToken(memberId, "refresh-token-value", now);

        verify(response).sendRedirect("/home");
    }

    @Test
    @DisplayName("인증 성공하면 쿠키가 설정한다.")
    void setsCookie_whenAuthenticationSucceeds() throws IOException {
        //when
        target.onAuthenticationSuccess(request, response, authentication);

        //then
        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);

        verify(response, times(2)).addCookie(captor.capture());

        List<Cookie> cookies = captor.getAllValues();

        assertThat(cookies.size()).isEqualTo(2);
        assertThat(cookies)
                .extracting(Cookie::isHttpOnly, Cookie::getSecure)
                .containsOnly(Tuple.tuple(true, true));

        Cookie accessCookie = cookies.get(0);
        assertThat(accessCookie.getName()).isEqualTo("accessToken");
        assertThat(accessCookie.getValue()).isEqualTo("access-value");
        assertThat(accessCookie.getPath()).isEqualTo("/api/auth");
        assertThat(accessCookie.getMaxAge()).isEqualTo(60 * 60);

        Cookie refreshCookie = cookies.get(1);
        assertThat(refreshCookie.getName()).isEqualTo("refreshToken");
        assertThat(refreshCookie.getValue()).isEqualTo("refresh-value");
        assertThat(refreshCookie.getPath()).isEqualTo("/api/auth");
        assertThat(refreshCookie.getMaxAge()).isEqualTo(60 * 60 * 24);
    }

}