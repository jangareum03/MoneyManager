package com.moneymanager.member.service.application;

import com.moneymanager.global.annotation.WorkLog;
import com.moneymanager.member.domain.dto.response.UsernameAvailabilityResponse;
import com.moneymanager.member.service.validation.MemberDuplicateValidator;
import com.moneymanager.member.service.validation.MemberFieldValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberInfoService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-10-30<br>
 * 역할             : 회원의 정보 조회, 수정, 중복 검증 등 정보와 관련된 흐름을 관리하는 오케스트라 클래스
 * </p>
 */
@Service
@RequiredArgsConstructor
public class MemberInfoService {

    private final MemberFieldValidator fieldValidator;
    private final MemberDuplicateValidator duplicateValidator;

    @WorkLog("아이디 중복 확인")
    public UsernameAvailabilityResponse processUsernameVerification(String username) {
        fieldValidator.validateUsername(username);

        boolean isExist = duplicateValidator.existsUsername(username);

        return new UsernameAvailabilityResponse(!isExist);
    }

}