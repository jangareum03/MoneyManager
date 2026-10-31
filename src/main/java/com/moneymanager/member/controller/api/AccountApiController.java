package com.moneymanager.member.controller.api;


import com.moneymanager.global.annotation.ApiController;
import com.moneymanager.global.dto.ApiResponse;
import com.moneymanager.member.domain.dto.response.UsernameAvailabilityResponse;
import com.moneymanager.member.service.application.MemberInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller.api<br>
 * 파일이름       : AccountApiController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-09-22<br>
 * 역할             : 계정 자체 요청을 처리하는 API 컨트롤러
 * </p>
 */
@ApiController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountApiController {

    private final MemberInfoService memberInfoService;

    @GetMapping("/username-availability")
    public ResponseEntity<ApiResponse<UsernameAvailabilityResponse>> usernameAvailability(@RequestParam String username) {
        String message = "error.member.username.duplicate";

        UsernameAvailabilityResponse response = memberInfoService.processUsernameVerification(username);
        if(response.available()) {
            message = "success.member.username";
        }

        return ResponseEntity.ok(
                ApiResponse.success(message, response)
        );
    }

}