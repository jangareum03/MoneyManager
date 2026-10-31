package com.moneymanager.global.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.code<br>
 * 파일이름       : ErrorCode<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-06-30<br>
 * 역할             : 오류 코드 정의
 * </p>
 */
@Getter
public enum ErrorCode {
	/** 요청 오류 **/
	REQUIRED_VALUE("002", "필수값 누락",  HttpStatus.BAD_REQUEST),
	INVALID_FORMAT("003", "형식 불일치", HttpStatus.BAD_REQUEST),
	INVALID_LENGTH("006", "허용길이 초과",  HttpStatus.BAD_REQUEST),;

	private final String code;
	private final String reason;
	private final HttpStatus status;

	ErrorCode(String code, String reason, HttpStatus status) {
		this.code = code;
		this.reason = reason;
		this.status = status;
	}

}