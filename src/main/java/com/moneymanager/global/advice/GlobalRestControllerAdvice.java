package com.moneymanager.global.advice;

import com.moneymanager.global.domain.dto.response.api.ErrorBody;
import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.exception.annotation.ApiController;
import com.moneymanager.global.log.AuditLogger;
import com.moneymanager.global.log.operation.OperationContext;
import com.moneymanager.global.log.operation.holder.OperationContextHolder;
import com.moneymanager.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.exception.advice<br>
 * 파일이름       : GlobalRestControllerAdvice<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 23<br>
 * 설명              : API 컨트롤러에서 처리하는 로직을 보조하는 클래스
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
 * 		 	  <td>26. 8. 23</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@RestControllerAdvice(annotations = ApiController.class)
@RequiredArgsConstructor
public class GlobalRestControllerAdvice {

    private final MessageUtil messageUtil;

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorBody> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponseEntity
                .badRequest()
                .body(ErrorBody.of("잘못된 요청입니다."));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorBody> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorBody.of("지원하지 않은 요청입니다."));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorBody> handleApplicationException(ApplicationException e) {
        String message = messageUtil.get(e);

        writeLog(e);

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorBody.of(message));
    }


    //===== 보조 메서드 =====
    private void writeLog(ApplicationException e) {
        OperationContext context = OperationContextHolder.get();

        try{
            context.addOption("error", e.getErrorCode().getCode());
            context.addOption("log", e.getLogContent());
            context.addOption("message", e.getUserMessage());

            AuditLogger.debug(context);

        }finally {
            AuditLogger.info(context);

            OperationContextHolder.clear();
        }
    }

}