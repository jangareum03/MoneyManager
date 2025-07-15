package com.areum.moneymanager.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidationResponseDTO {
	private boolean success;
	private String message;
}
