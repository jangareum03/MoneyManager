package com.moneymanager.member.service.validation;

import com.moneymanager.global.annotation.WorkStep;
import com.moneymanager.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.validation<br>
 * 파일이름       : MemberDuplicateValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-10-30<br>
 * 역할             : 회원의 중복 여부를 확인
 * </p>
 */
@Component
@RequiredArgsConstructor
public class MemberDuplicateValidator {

    private final MemberRepository memberRepository;

    @WorkStep("아이디 존재여부 검사")
    public boolean existsUsername(String username) {
        return memberRepository.existsByUsername(username) == 1;
    }

}