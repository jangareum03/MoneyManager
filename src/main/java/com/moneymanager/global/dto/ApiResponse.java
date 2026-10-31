package com.moneymanager.global.dto;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.domain.dto.response<br>
 * 파일이름       : ApiResponse<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-10-28<br>
 * 역할             : API 결과 정보를 담은 응답 클래스
 * </p>
 */
public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> failure(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }

}