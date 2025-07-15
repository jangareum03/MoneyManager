package com.areum.moneymanager.controller.api.main.validation;

import com.areum.moneymanager.dto.request.main.validation.QnaRequestDTO;
import com.areum.moneymanager.dto.response.ValidationResponseDTO;
import com.areum.moneymanager.exception.ErrorException;
import com.areum.moneymanager.service.main.validation.QnaValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/qna")
public class QnaValidationApiController {


	/**
	 * 질문 제목 형식이 적합한지 확인 요청을 처리합니다.
	 *
	 * @param title		질문 제목
	 * @return	안내 메시지
	 */
	@PostMapping("/validation/title")
	public ValidationResponseDTO validationTitle(@RequestBody QnaRequestDTO.Title title ) {
		try{
			QnaValidationService.checkTitleAvailability(title.getTitle());

			return ValidationResponseDTO.builder().success(true).build();
		}catch ( ErrorException e ) {
			return ValidationResponseDTO.builder().success(false).message(e.getErrorMessage()).build();
		}
	}



	/**
	 * 질문 내용 형식이 적합한지 확인 요청을 처리합니다.
	 *
	 * @param content		질문 내용
	 * @return	안내 메시지
	 */
	@PostMapping("/validation/content")
	public ValidationResponseDTO validationContent( @RequestBody QnaRequestDTO.Content content ) {
		try{
			QnaValidationService.checkContentAvailability(content.getContent());

			return ValidationResponseDTO.builder().success(true).build();
		}catch ( ErrorException e ) {
			return ValidationResponseDTO.builder().success(false).message(e.getErrorMessage()).build();
		}
	}

}
