package com.areum.moneymanager.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponseDTO {
	private boolean success;
	private String message;
}
