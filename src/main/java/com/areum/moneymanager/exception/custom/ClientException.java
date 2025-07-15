package com.areum.moneymanager.exception.custom;

import com.areum.moneymanager.dto.request.ErrorRequestDTO;
import com.areum.moneymanager.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class ClientException extends RuntimeException {
    private final ErrorCode errorCode;
	private final ErrorRequestDTO requestDTO;

	public ClientException( ErrorCode errorCode, ErrorRequestDTO requestDTO ) {
		super(errorCode.getMessage());

		this.errorCode = errorCode;
		this.requestDTO = requestDTO;
	}

}
