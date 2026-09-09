package com.moneymanager.global.fillter;

import com.moneymanager.global.security.CustomUserDetailService;
import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.global.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.fillter<br>
 * 파일이름       : JwtAuthenticationFilterTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 9<br>
 * 설명              : JwtAuthenticationFilter 클래스 요청을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 9. 9</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    JwtAuthenticationFilter target;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    CustomUserDetailService userDetailService;

    @Mock
    FilterChain chain;

    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        target = new JwtAuthenticationFilter(jwtTokenProvider, userDetailService);
    }

    @Test
    @DisplayName("정적 리소스 요청이면 필터를 통과한다.")
    void passesFilter_whenRequestIsStaticResource() throws ServletException, IOException {
    	//given
        request.setRequestURI("/js/index.js");
    	
    	//when
        target.doFilterInternal(request, response, chain);
    	
    	//then
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(userDetailService);

    	verify(chain).doFilter(request, response);
    }
    
    @Test
    @DisplayName("accessToken이 없으면 필터를 통과한다.")
    void passesFilter_whenAccessTokenDoesNotExist() throws ServletException, IOException {
    	//given
        request.setRequestURI("/api/ledgers");
        request.setCookies();
    	
    	//when
        target.doFilterInternal(request, response, chain);
    	
    	//then
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(userDetailService);
        
    	verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("정상적인 accessToken이면 인증객체를 생성한다.")
    void createsAuthentication_whenAccessTokenIsValid() throws ServletException, IOException {
    	//given
        String accessToken = "access-token";

        request.setRequestURI("/api/ledgers");
        request.setCookies(
                new Cookie("accessToken", accessToken)
        );

        UserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");

        doNothing()
                .when(jwtTokenProvider)
                .validateToken(accessToken);

        when(jwtTokenProvider.getMemberNumber(accessToken))
                .thenReturn("member-number");

        when(userDetailService.loadUserByMemberNumber("member-number"))
                .thenReturn(userDetails);
    	
    	//when
        target.doFilterInternal(request, response, chain);
    	
    	//then
    	verify(jwtTokenProvider).validateToken(accessToken);
        verify(jwtTokenProvider).getMemberNumber(accessToken);
        verify(userDetailService).loadUserByMemberNumber("member-number");

        verify(chain).doFilter(request, response);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();
        assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authentication.getName()).isEqualTo("user");
    }
    
    @Test
    @DisplayName("만료된 accessToken이면 401을 반환한다.")
    void returns401_whenAccessTokenIsExpired() throws ServletException, IOException {
    	//given
        String accessToken = "expired-token";

        request.setRequestURI("/api/ledgers");
        request.setCookies(
                new Cookie("accessToken", accessToken)
        );

        doThrow(ExpiredJwtException.class)
                .when(jwtTokenProvider)
                .validateToken(accessToken);
    	
    	//when
        target.doFilterInternal(request, response, chain);
    	
    	//then
    	assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);

        verify(jwtTokenProvider).validateToken(accessToken);
        verify(chain, never()).doFilter(request, response);
        verifyNoInteractions(userDetailService);
    }
    
    @Test
    @DisplayName("잘못된 accessToken이면 401을 반환한다.")
    void returns401_whenAccessTokenIsInvalid() throws ServletException, IOException {
        //given
        String accessToken = "no-token";

        request.setRequestURI("/api/ledgers");
        request.setCookies(
                new Cookie("accessToken", accessToken)
        );

        doThrow(JwtException.class)
                .when(jwtTokenProvider)
                .validateToken(accessToken);

        //when
        target.doFilterInternal(request, response, chain);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);

        verify(jwtTokenProvider).validateToken(accessToken);
        verify(chain, never()).doFilter(request, response);
        verifyNoInteractions(userDetailService);
    }

}