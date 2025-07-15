package com.areum.moneymanager.exception.handler;

import com.areum.moneymanager.dto.request.ErrorRequestDTO;
import com.areum.moneymanager.dto.response.ErrorResponseDTO;
import com.areum.moneymanager.exception.code.ErrorCode;
import com.areum.moneymanager.exception.custom.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.areum.moneymanager.controller.api")
public class ApiExceptionHandler {

	@ExceptionHandler(ClientException.class)
	public ResponseEntity<ErrorResponseDTO> handleClientException( ClientException ce ) {
		ErrorCode error = ce.getErrorCode();
		ErrorRequestDTO requestDTO = ce.getRequestDTO();

		log.warn("[{}] {}", error.getCode(), String.format(error.getLogMessage(), requestDTO.getKey()));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( ErrorResponseDTO.builder().success(false).code(error.getCode()).message(error.getMessage()).build());
	}

}
