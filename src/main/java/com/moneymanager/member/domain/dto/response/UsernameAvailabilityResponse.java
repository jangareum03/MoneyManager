package com.moneymanager.member.domain.dto.response;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.dto.response<br>
 * 파일이름       : UsernameAvailabilityResponse<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-10-28<br>
 * 역할             : 아이디 중복 확인 결과를 담은 응답 클래스
 * </p>
 */
public record UsernameAvailabilityResponse(
        boolean available
) {
}