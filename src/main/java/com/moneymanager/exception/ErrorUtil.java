package com.moneymanager.exception;


import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.exception.custom.ServerException;
import com.moneymanager.utils.LoggerUtil;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception<br>
 * 파일이름       : ErrorUtil<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 1.<br>
 * 설명              : 공통적인 에러 처리 할 때 필요한 클래스
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

	public static RuntimeException createClientException(ErrorDTO errorDTO, String message) {
		//로그 작성
		LoggerUtil.logUserWarn(errorDTO);

		return new ClientException(message);
	}

	public static RuntimeException createServerException(ErrorDTO errorDTO, String message) {
		//로그 작성
		LoggerUtil.logSystemError(errorDTO);

		return new ServerException(message);
	}
}
