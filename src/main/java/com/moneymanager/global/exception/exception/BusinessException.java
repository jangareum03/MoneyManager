package com.moneymanager.global.exception.exception;


import com.moneymanager.global.exception.code.ErrorCode;
import com.moneymanager.global.exception.log.DeveloperLogInfo;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception<br>
 * 파일이름       : BusinessException<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 6<br>
 * 설명              : 서비스 문제로 발생하는 예외 클래스
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
public class BusinessException extends ApplicationException {

	private BusinessException(ErrorCode errorCode, DeveloperLogInfo logInfo, String userMessage) {
		super(errorCode, logInfo, userMessage);
	}

	public static BusinessException of(ErrorCode errorCode, DeveloperLogInfo logInfo) {
		return of(errorCode, logInfo, errorCode.getDefaultMessage());
	}

	public static BusinessException of(ErrorCode errorCode, DeveloperLogInfo logInfo, String userMessage) {
		return new BusinessException(errorCode, logInfo, userMessage);
	}

	@Override
	protected BusinessException newInstance(ErrorCode errorCode, DeveloperLogInfo logInfo, String userMessage) {
		return new BusinessException(errorCode, logInfo, userMessage);
	}

}