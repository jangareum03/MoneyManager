package com.areum.moneymanager.exception.custom;

import com.areum.moneymanager.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class NetworkException extends RuntimeException {
    private final ErrorCode errorCode;

	public NetworkException( ErrorCode errorCode )  {
		super(errorCode.getMessage());
        this.errorCode = errorCode;
	}

}
