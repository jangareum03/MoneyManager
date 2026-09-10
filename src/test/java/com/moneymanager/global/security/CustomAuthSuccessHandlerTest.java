package com.moneymanager.global.security;

import com.moneymanager.member.service.application.SideBarMemberService;
import com.moneymanager.member.service.application.TokenAuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    TokenAuthService tokenAuthService;

    @Mock
    SideBarMemberService sideBarMemberService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Authentication authentication;

    @Test
    @DisplayName("인증 성공하면 토큰 생성 후 홈으로 리다이렉트한다.")
    void generatesToken_whenAuthenticationSucceeds() throws IOException {
        //given
        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(userDetails.getMemberNumber())
                .thenReturn("memberNumber");

        //when
        target.onAuthenticationSuccess(request, response, authentication);

        //then
        verify(tokenAuthService).issueTokens(userDetails, response);
        verify(sideBarMemberService).saveSideBarInfo("memberNumber");

        verify(response).sendRedirect("/home");
    }

}