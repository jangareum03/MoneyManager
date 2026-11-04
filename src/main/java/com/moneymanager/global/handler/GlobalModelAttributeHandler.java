package com.moneymanager.global.handler;

import com.moneymanager.global.security.filter.CspNonceFilter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.handler<br>
 * 파일이름       : GlobalModelAttributeHandler<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-11-08<br>
 * 역할             : 공통적으로 Model 객체의 속성을 처리
 * </p>
 */
@ControllerAdvice
public class GlobalModelAttributeHandler {

    @ModelAttribute("cspNonce")
    public String addCspNonce(HttpServletRequest request) {
        return request.getAttribute(
                CspNonceFilter.CSP_NONCE_KEY
        ).toString();
    }
}