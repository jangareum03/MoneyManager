package com.areum.moneymanager.exception.custom;

import com.areum.moneymanager.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
	private final ErrorCode errorCode;

	public ServerException( ErrorCode errorCode ) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

}
