package com.moneymanager.exception.exception;

import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.log.DeveloperLogInfo;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.exception<br>
 * 파일이름       : ExternalException<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 1<br>
 * 설명              : 외부 문제로 발생하는 예외 클래스
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
 * 		 	  <td>26. 7. 1</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class ExternalException extends ApplicationException {

	private Throwable throwable;

	private ExternalException(ErrorCode errorCode, DeveloperLogInfo logInfo, String userMessage) {
		super(errorCode, logInfo, userMessage);
	}

	private ExternalException(ErrorCode errorCode, DeveloperLogInfo logInfo, String userMessage, Throwable throwable) {
		super(errorCode, logInfo, userMessage, throwable);
	}

	public static ExternalException of(ErrorCode errorCode, DeveloperLogInfo logInfo, String userMessage) {
		return new ExternalException(errorCode, logInfo, userMessage);
	}

	public static ExternalException of(ErrorCode errorCode, DeveloperLogInfo logInfo, String userMessage, Throwable throwable) {
		return new ExternalException(errorCode, logInfo, userMessage, throwable);
	}

	@Override
	protected ExternalException newInstance(ErrorCode errorCode, DeveloperLogInfo logInfo, String userMessage) {
		return new ExternalException(errorCode, logInfo, userMessage);
	}

}
