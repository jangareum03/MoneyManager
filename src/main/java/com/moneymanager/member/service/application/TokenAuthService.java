package com.moneymanager.member.service.application;

import com.moneymanager.global.domain.dto.response.AccessToken;
import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.AuditLogger;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.security.CustomUserDetailService;
import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.global.security.jwt.JwtTokenProvider;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.repository.MemberTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.moneymanager.global.exception.code.ErrorCode.EXPIRED_TOKEN;
import static com.moneymanager.global.exception.code.ErrorCode.INVALID_TOKEN;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : TokenAuthService<br>
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
public class TokenAuthService {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailService userDetailService;
    private final MemberTokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    public boolean authenticate(String accessToken, HttpServletResponse response) throws IOException {
        try{
            //1. Access 토큰 검증
            tokenProvider.parseToken(accessToken);
        }catch (ExpiredJwtException e) {
            unauthorized(response,"ACCESS_TOKEN_EXPIRED", "Access Token 만료");
            return false;
        } catch (JwtException e) {
            unauthorized(response,"INVALID_ACCESS_TOKEN", "유효하지 않은 Access Token");
            return false;
        }

        //2. SecurityContext에 Authentication 설정
        setAuthentication(accessToken);
        return true;
    }

    public void issueTokens(CustomUserDetails userDetails, HttpServletResponse response) {
        //1. Access/Refresh 토큰 생성
        AccessToken accessToken = tokenProvider.generateAccessToken(userDetails);
        AccessToken refreshToken = tokenProvider.generateRefreshToken(userDetails);

        //2. Refresh 토큰 암호화 후 DB 저장
        String refreshTokenHash = passwordEncoder.encode(refreshToken.getToken());
        tokenRepository.saveToken(userDetails.getId(), refreshTokenHash, refreshToken.getExpiration());

        //3. 쿠키 설정
        Cookie accessCookie = addCookieByToken(accessToken, "accessToken", "/", (int) JwtTokenProvider.ACCESS_TOKEN_EXPIRATION_SECONDS);
        Cookie refreshCookie = addCookieByToken(refreshToken, "refreshToken", "/api/auth/refresh", (int) JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_SECONDS);

        //4. 응답 토큰 저장
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    public void reissueToken(String refreshToken, HttpServletResponse response) {
        //1. Refresh 토큰 검증
        validateRefreshToken(refreshToken);

        //2.subject 조회
        String memberNumber = tokenProvider.parseToken(refreshToken).getPayload().getSubject();

        //3. 새로운 AccessToken 생성
        String accessToken = tokenProvider.createAccessToken(memberNumber);

        //4. 쿠키에 새로 발급된 AccessToken 추가
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) JwtTokenProvider.ACCESS_TOKEN_EXPIRATION_SECONDS);

        response.addCookie(cookie);
    }

    public void logout(String refreshToken) {
        String memberNumber = tokenProvider.parseToken(refreshToken).getPayload().getSubject();

        String hashRefreshToken = tokenRepository.findRefreshTokenByMemberNumber(memberNumber);
        if (hashRefreshToken == null) {
            AuditLogger.warn("로그아웃 중 Refresh Token를 DB에서 찾을 수 없습니다. memberNumber={}", memberNumber);
            return;
        }

        if(!passwordEncoder.matches(refreshToken, hashRefreshToken)) {
            AuditLogger.warn("로그아웃 요청한 Refresh Token이 일치하지 않습니다. memberNumber={}", memberNumber);
            return;
        }

        int deleted = tokenRepository.deleteRefreshToken(hashRefreshToken);

        if(deleted == 0) {
            AuditLogger.warn("DB에 Refresh Token가 없어 삭제하지 못합니다. memberNumber={}", memberNumber);
        }
    }

    public void deleteTokens(HttpServletResponse response) {
        response.addCookie(deleteCookie("accessToken", "/"));
        response.addCookie(deleteCookie("refreshToken", "/api/auth/refresh"));
    }


    //===== issueToken 보조 메서드 =====
    private void setAuthentication(String token) {
        String memberNumber = tokenProvider.parseToken(token).getPayload().getSubject();

        CustomUserDetails userDetails = (CustomUserDetails) userDetailService.loadUserByMemberNumber(memberNumber);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void unauthorized(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                """
                        {
                            "code": "%s",
                            "message": "%S"
                        }
                """.formatted(code, message)
        );
    }


    //===== reissueToken 보조 메서드 =====
    private void validateRefreshToken(String refreshToken) {
        try{
            tokenProvider.parseToken(refreshToken);

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


    //===== 유틸 메서드 =====
    private Cookie addCookieByToken(AccessToken token, String tokenName, String path, int maxAge) {
        Cookie cookie = new Cookie(tokenName, token.getToken());

        cookie.setHttpOnly(true);			//html에서만 쿠키 조회 가능
        cookie.setSecure(true);				//https에서만 전송 가능
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);

        return cookie;
    }

    private Cookie deleteCookie(String tokenName, String path) {
        Cookie cookie = new Cookie(tokenName, "");

        cookie.setPath(path);
        cookie.setMaxAge(0);

        return cookie;
    }

}