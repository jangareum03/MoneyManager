package com.moneymanager.exception.handler;

import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.exception.custom.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.handler<br>
 * 파일이름       : GlobalExceptionHandler<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 5<br>
 * 설명              : 공통적으로 예외 발생 시 처리하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 * 		<thead>
 * 		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 * 		 	  	<td>날짜</td>
 * 		 	  	<td>작성자</td>
 * 		 	  	<td>변경내용</td>
 * 		 	</tr>
 * 		</thead>
 * 		<tbody>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 1. 5.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(ClientException.class)
	public ResponseEntity<String> handleClientException(ClientException e) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(e.getMessage());
	}

	@ExceptionHandler(ServerException.class)
	public ResponseEntity<String> handleServerException(ServerException e) {
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("일시적인 오류가 발생했어요. 잠시 후 다시 시도해주세요");
	}

}
