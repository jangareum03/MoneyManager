package com.areum.moneymanager.exception;

import com.areum.moneymanager.enums.ErrorCode;


public class ErrorException extends RuntimeException {
	private final ErrorCode errorCode;

	public ErrorException( ErrorCode errorCode ) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode.getCode();
	}

	public String getErrorMessage() {
		return errorCode.getMessage();
	}
}
