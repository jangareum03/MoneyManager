package com.moneymanager.member.service.application;

import com.moneymanager.global.security.jwt.JwtTokenProvider;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.Cookie;

import static com.moneymanager.global.exception.code.ErrorCode.EXPIRED_TOKEN;
import static com.moneymanager.global.exception.code.ErrorCode.INVALID_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberAuthServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 10<br>
 * 설명              : MemberAuthService 클래스 로직을 검증하는 단위 테스트 클래스
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
class MemberAuthServiceTest {

    @InjectMocks
    MemberAuthService target;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    MockHttpServletResponse response;

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

            when(jwtTokenProvider.getMemberNumber(refreshToken))
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
                    .validateToken(refreshToken);
        	
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
                    .validateToken(refreshToken);

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

}