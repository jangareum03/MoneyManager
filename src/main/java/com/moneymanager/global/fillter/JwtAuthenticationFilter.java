package com.moneymanager.global.fillter;

import com.moneymanager.member.service.application.TokenAuthService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.security.jwt<br>
 * 파일이름       : JwtAuthenticationFilter<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 6.<br>
 * 설명              : JWT 토큰 처리와 인증된 요청을 처리하는 클래스
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
 * 		 	  <td>25. 11. 6.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenAuthService tokenAuthService;

    public JwtAuthenticationFilter(TokenAuthService authService) {
        this.tokenAuthService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //1. 정적 리소스 인증 제외
        if (canPassParameterToUri(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        //2. 쿠키에서 accessToken 가져오기
        String accessToken = getAccessTokenFromCookie(request);

        //3. 쿠키에 token이 없는 경우 인증 통과
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        //4. 인증정보 재설정
        if(!tokenAuthService.authenticate(accessToken, response)) {
            return;
        }

        //5,다음 필터로 넘어감
        filterChain.doFilter(request, response);
    }


    //===== 보조 메서드 =====
    protected boolean canPassParameterToUri(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/image/");
    }

    private String getAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

}