package com.moneymanager.global.security.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.security.filter<br>
 * 파일이름       : CspNonceFilter<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-11-08<br>
 * 역할             : Nonce 생성 필터
 * </p>
 */
public class CspNonceFilter extends OncePerRequestFilter {

    public static final String CSP_NONCE_KEY = "cspNonce";

    private final SecureRandom random = new SecureRandom();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        byte[] nonceBytes = new byte[16];

        random.nextBytes(nonceBytes);

        String nonce = Base64.getEncoder().encodeToString(nonceBytes);

        request.setAttribute(CSP_NONCE_KEY, nonce);

        String csp = "script-src 'self' 'nonce-" + nonce + "' "
                + "https://dapi.kakao.com "
                + "https://t1.daumcdn.net "
                + "https://www.gstatic.com "
                + "'unsafe-eval'";

        response.setHeader("Content-Security-Policy", csp);

        filterChain.doFilter(request, response);
    }

}