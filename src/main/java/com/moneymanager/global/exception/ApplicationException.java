package com.moneymanager.global.exception;

import com.moneymanager.global.enums.ErrorCode;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.exception<br>
 * 파일이름       : ApplicationException<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-06-30<br>
 * 역할             : 애플리케이션에서 발생하는 예외 클래스
 * </p>
 */
@Getter
public class ApplicationException extends RuntimeException {

	private final ErrorCode errorCode;									//에러코드
	private final String messageKey;										//메시지 키

	public ApplicationException(ErrorCode errorCode, String messageKey) {
		this.errorCode = errorCode;
		this.messageKey = messageKey;
	}

}