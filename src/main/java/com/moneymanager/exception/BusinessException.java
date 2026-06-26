package com.moneymanager.exception;


import com.moneymanager.exception.log.DetailLogInfo;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception<br>
 * 파일이름       : BusinessException<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 6<br>
 * 설명              : 사용자 원인으로 발생하는 예외 클래스
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
 * 		 	  <td>26. 3. 6.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;
	private final DetailLogInfo logInfo;
	private final ServiceAction serviceAction;
	private final String userMessage;

	private BusinessException(ErrorCode errorCode, DetailLogInfo logInfo, ServiceAction serviceAction, String userMessage, Throwable cause) {
		super(userMessage, cause);

		this.errorCode = errorCode;
		this.logInfo = logInfo;
		this.serviceAction = serviceAction;
		this.userMessage = userMessage;
	}

	public static BusinessException of(ErrorCode errorCode, DetailLogInfo logInfo) {
		return new BusinessException(errorCode, logInfo, null, errorCode.getDefaultMessage(), null);
	}

	public BusinessException withService(ServiceAction service) {
		return new BusinessException(
				this.errorCode,
				this.logInfo,
				serviceAction,
				this.userMessage,
				this.getCause()
		);
	}

	public BusinessException withUserMessage(String message) {
		return new BusinessException(
				this.errorCode,
				this.logInfo,
				this.serviceAction,
				message,
				this.getCause()
		);
	}

	public BusinessException withCause(Throwable cause) {
		return new BusinessException(
				this.errorCode,
				this.logInfo,
				this.serviceAction,
				this.userMessage,
				cause
		);
	}

}