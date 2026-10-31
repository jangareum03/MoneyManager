package com.moneymanager.global.resolver;

import com.moneymanager.global.dto.ApiResponse;
import com.moneymanager.global.annotation.ApiController;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.resolver<br>
 * 파일이름       : MessageResolver<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-09-29<br>
 * 역할             : 메시지 키를 실제 메시지로 변환
 * </p>
 */
@RestControllerAdvice(annotations = ApiController.class)
@RequiredArgsConstructor
public class MessageResolver implements ResponseBodyAdvice<ApiResponse<?>> {

    private final MessageSource messageSource;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return ResponseEntity.class.isAssignableFrom(returnType.getParameterType())
                || ApiResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public ApiResponse<?> beforeBodyWrite(ApiResponse<?> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(body == null || body.message() == null) {
            return body;
        }

        String message = messageSource.getMessage(
                body.message(),
                null,
                LocaleContextHolder.getLocale()
        );

        return ApiResponse.success(message, body.data());
    }

}