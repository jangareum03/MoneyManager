package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.security.jwt.JwtTokenProvider;
import com.moneymanager.global.util.string.StringUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static com.moneymanager.global.exception.code.ErrorCode.EXPIRED_TOKEN;
import static com.moneymanager.global.exception.code.ErrorCode.INVALID_TOKEN;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberAuthService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 9<br>
 * 설명              : 회원 인증 흐름을 관리하는 클래스
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
@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final JwtTokenProvider tokenProvider;

    public void reissueToken(String refreshToken, HttpServletResponse response) {
        //1. Refresh 토큰 검증
        validateRefreshToken(refreshToken);

        //2.subject 조회
        String memberNumber = tokenProvider.getMemberNumber(refreshToken);

        //3. 새로운 AccessToken 생성
        String accessToken = tokenProvider.createAccessToken(memberNumber);

        //4. 쿠키에 새로 발급된 AccessToken 추가
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) JwtTokenProvider.ACCESS_TOKEN_EXPIRATION_SECONDS);

        response.addCookie(cookie);
    }


    //===== reissueToken 보조 메서드 =====
    private void validateRefreshToken(String refreshToken) {
        try{
            tokenProvider.validateToken(refreshToken);

            if(!tokenProvider.isRefreshToken(refreshToken)) {
                throw new ApplicationException(
                        INVALID_TOKEN,
                        LogContent.of(
                                "Refresh 토큰 검증",
                                "refreshToken",
                                StringUtil.masking(refreshToken, 6, refreshToken.length() - 6)
                        ).withCause("refreshToken 아님")
                ).withUserMessage("member.token.failed");
            }
        }catch (ExpiredJwtException e){
            throw new ApplicationException(
                    EXPIRED_TOKEN,
                    LogContent.of(
                            "Refresh 토큰 검증",
                            "refreshToken",
                            StringUtil.masking(refreshToken, 6, refreshToken.length() - 6)
                    ).withCause("만료된 refreshToken")
            ).withUserMessage("member.token.expired");
        }catch (JwtException e) {
            throw new ApplicationException(
                    INVALID_TOKEN,
                    LogContent.of(
                            "Refresh 토큰 검증",
                            "refreshToken",
                            StringUtil.masking(refreshToken, 6, refreshToken.length() - 6)
                    ).withCause("유효하지 않은 refreshToken")
            ).withUserMessage("member.token.failed");
        }
    }

}