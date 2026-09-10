package com.moneymanager.global.fillter;

import com.moneymanager.member.service.application.TokenAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;

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
    TokenAuthService tokenAuthService;

    @Mock
    FilterChain chain;

    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        target = new JwtAuthenticationFilter(tokenAuthService);
    }

    @Test
    @DisplayName("정적 리소스 요청이면 필터를 통과한다.")
    void passesFilter_whenRequestIsStaticResource() throws ServletException, IOException {
    	//given
        request.setRequestURI("/js/index.js");
    	
    	//when
        target.doFilterInternal(request, response, chain);
    	
    	//then
        verifyNoInteractions(tokenAuthService);

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
        verifyNoInteractions(tokenAuthService);
        
    	verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("정상적인 accessToken이면 다음 필터로 넘어간다.")
    void passesToNextFilter_whenAccessTokenIsValid() throws ServletException, IOException {
    	//given
        String accessToken = "access-token";

        request.setRequestURI("/api/ledgers");
        request.setCookies(
                new Cookie("accessToken", accessToken)
        );

        when(tokenAuthService.authenticate(accessToken, response))
                .thenReturn(Boolean.TRUE);

    	//when
        target.doFilterInternal(request, response, chain);
    	
    	//then
        verify(chain).doFilter(request, response);
    }
    
    @Test
    @DisplayName("만료된 accessToken이면 다음 필터로 넘어가지 않는다.")
    void doesNotProceedToNextFilter_whenAccessTokenIsExpired() throws ServletException, IOException {
    	//given
        String accessToken = "expired-token";

        request.setRequestURI("/api/ledgers");
        request.setCookies(
                new Cookie("accessToken", accessToken)
        );

        when(tokenAuthService.authenticate(accessToken, response))
                .thenReturn(Boolean.FALSE);
    	
    	//when
        target.doFilterInternal(request, response, chain);
    	
    	//then
        verify(chain, never()).doFilter(request, response);
    }
    
    @Test
    @DisplayName("잘못된 accessToken이면 다음 필터로 넘어가지 않는다.")
    void doesNotProceedToNextFilter_whenAccessTokenIsInvalid() throws ServletException, IOException {
        //given
        String accessToken = "no-token";

        request.setRequestURI("/api/ledgers");
        request.setCookies(
                new Cookie("accessToken", accessToken)
        );

        when(tokenAuthService.authenticate(accessToken, response))
                .thenReturn(Boolean.FALSE);

        //when
        target.doFilterInternal(request, response, chain);

        //then
        verify(chain, never()).doFilter(request, response);
    }

}