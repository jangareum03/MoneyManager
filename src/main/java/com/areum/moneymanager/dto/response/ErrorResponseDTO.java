package com.areum.moneymanager.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponseDTO {
	private boolean success;
	private String code;
	private String message;
}
