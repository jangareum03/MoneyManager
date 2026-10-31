package com.moneymanager.global.handler;

import com.moneymanager.global.annotation.ApiController;
import com.moneymanager.global.dto.ApiResponse;
import com.moneymanager.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.exception.handler<br>
 * 파일이름       : RestExceptionHandler<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-08-23<br>
 * 역할             : API 컨트롤러에서 발생하는 예외를 처리
 * </p>
 */
@RestControllerAdvice(annotations = ApiController.class)
@RequiredArgsConstructor
public class RestExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.failure("잘못된 요청입니다.", null)
                );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(
                        ApiResponse.failure("지원하지 않은 요청입니다.", null)
                );
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleApplicationException(ApplicationException e, Locale locale) {
        String message = e.getMessageKey() == null
                ? null
                : messageSource.getMessage(
                e.getMessageKey(),
                null,
                locale
        );

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(
                        ApiResponse.failure(message, null)
                );
    }

}