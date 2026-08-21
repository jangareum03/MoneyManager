package com.moneymanager.global.exception.exception;

import com.moneymanager.global.exception.code.ErrorCode;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.exception<br>
 * 파일이름       : ApplicationException<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 30<br>
 * 설명              : 애플리케이션에서 발생하는 예외 클래스
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
 * 		 	  <td>26. 6. 30</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public abstract class ApplicationException extends RuntimeException {

	private final ErrorCode errorCode;							//에러코드
	private final LogContent logContent;				//개발 로그정보
	private final String userMessage;								//안내 메시지

	public ApplicationException(ErrorCode errorCode, LogContent logContent, Throwable throwable) {
		super(throwable);

		this.errorCode = errorCode;
		this.logContent = resolveLogContent(errorCode, logContent);
		this.userMessage =  null;
	}

	private ApplicationException(ErrorCode errorCode, LogContent logContent, String userMessage, Throwable throwable) {
		super(throwable);

		this.errorCode = errorCode;
		this.logContent = logContent;
		this.userMessage = userMessage;
	}

	public ApplicationException withUserMessage(String userMessage) {
		return recreate(errorCode, logContent, userMessage, getCause());
	}

	protected abstract  ApplicationException recreate(ErrorCode errorCode, LogContent logContent, String userMessage, Throwable cause);

	private static LogContent resolveLogContent(ErrorCode errorCode, LogContent content) {
		if(content == null) {
			return null;
		}

		if(!StringUtil.isNullOrBlank(content.getCause())) {
			return content;
		}

		return content.toBuilder()
				.cause(errorCode.getReason())
				.build();
	}

}