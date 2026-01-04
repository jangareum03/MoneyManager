package com.moneymanager.exception;


import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.exception.custom.ServerException;

import static com.moneymanager.utils.LoggerUtil.logSystemError;
import static com.moneymanager.utils.LoggerUtil.logUserWarn;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception<br>
 * 파일이름       : ErrorUtil<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 1.<br>
 * 설명              : 공통적으로 검증할 때 필요한 클래스
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
 * 		 	  <td>25. 8. 1</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 11. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[클래스 이름] ValidationUtil → ErrorUtil</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public final class ErrorUtil {

	private ErrorUtil() {}


	public static RuntimeException createClientException(ErrorCode code, String message) {
		return createClientException(code, message, null);
	}

	public static RuntimeException createClientException(ErrorCode code, String message, Object data) {
		ErrorDTO errorDTO = ErrorDTO.builder()
				.errorCode(code)
				.message(message)
				.build();

		return new ClientException(errorDTO);
	}

	public static RuntimeException createServerException(ErrorCode code) {
		return createServerException(code, null, null);
	}

	public static RuntimeException createServerException(ErrorCode code, String message) {
		return createServerException(code, message, null);
	}

	public static  RuntimeException createServerException(ErrorCode code, String message, Object data) {
		ErrorDTO errorDTO = ErrorDTO.builder()
				.errorCode(code)
				.message(message)
				.build();

		return new ServerException(errorDTO);
	}

	public static RuntimeException throwClientException(ErrorCode code, String serviceName, String message, String data) {
		ErrorDTO errorDTO = ErrorDTO.builder()
				.errorCode(code)
				.message(message)
				.data(data)
				.build();

		logUserWarn(errorDTO, serviceName);

		throw new ClientException(errorDTO);
	}

	public static void throwServerException(ErrorCode code, String logMessage, String data) {
		ErrorDTO errorDTO = ErrorDTO.builder()
				.errorCode(code)
				.message(logMessage)
				.data(data)
				.build();

		logSystemError(errorDTO);

		throw new ServerException(errorDTO);
	}
}
