package com.moneymanager.member.service.validation;

import com.moneymanager.global.annotation.WorkStep;
import com.moneymanager.global.exception.ApplicationException;
import org.springframework.stereotype.Component;

import static com.moneymanager.global.enums.ErrorCode.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.validation<br>
 * 파일이름       : MemberFieldValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-09-04<br>
 * 역할             : 회원 필드값의 유효성 검증
 * </p>
 */
@Component
public class MemberFieldValidator {

    private static final String USERNAME_ALLOWED_CHARS_REGEX = "^[a-zA-Z0-9]*$";
    private static final String USERNAME_STARTS_WITH_LETTER_REGEX = "^[a-zA-Z].*$";

    @WorkStep("아이디 유효성 검사")
    public void validateUsername(String username) {
        //필수값
        if(username == null || username.trim().isBlank()) {
            throw new ApplicationException(REQUIRED_VALUE, "error.member.username.required");
        }

        //길이 검증
        if(username.length() < 4 || username.length() > 15 ) {
            throw new ApplicationException(INVALID_LENGTH, "error.member.username.length");
        }

        //형식 검증
        if (!username.matches(USERNAME_ALLOWED_CHARS_REGEX)) {
            throw new ApplicationException(INVALID_FORMAT, "error.member.username.invalidFormat");
        }else if (!username.matches(USERNAME_STARTS_WITH_LETTER_REGEX)) {
            throw new ApplicationException(INVALID_FORMAT, "error.member.username.invalidStart");
        }
    }

}