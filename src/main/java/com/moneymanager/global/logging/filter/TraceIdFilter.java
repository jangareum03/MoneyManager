package com.moneymanager.global.logging.filter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.logging.filter<br>
 * 파일이름       : TraceIdFilter<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-10-31<br>
 * 역할             : 요청 구분을 위한 Trace ID 생성
 * </p>
 */
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String traceId = getTraceId();

        try{
            MDC.put("traceId", traceId);

            response.setHeader("X-Trace-Id", traceId);

            filterChain.doFilter(request, response);
        }finally {
            MDC.remove("traceId");
        }
    }

    private String getTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

}