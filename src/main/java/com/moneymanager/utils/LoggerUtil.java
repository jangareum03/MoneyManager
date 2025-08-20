package com.moneymanager.utils;

import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.exception.code.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * <p>
 * 패키지이름    : com.moneymanager.utils<br>
 * 파일이름       : LoggerUtil<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 1.<br>
 * 설명              : 공통 로깅 유틸리티 클래스
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
 * 		 	  <td>25. 8. 20</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 추가] logSystemError - 시스템 오류 기록</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
public class LoggerUtil {

	/**
	 * 사용자 경고 로그를 작성합니다.
	 *
	 * @param <T>	요청 데이터(requestData)에 들어가는 객체의 타입
	 */
	public static <T> void logUserWarn(ErrorDTO<T> errorDTO) {
		ErrorCode errorCode = errorDTO.getErrorCode();

		log.warn("[USER_WARN] errorCode={}, LogMessage={}", errorCode.getCode(), errorCode.getLogMessage());
		log.warn("▶ errorId={}", errorDTO.getErrorId());
		log.warn("▶ UserMessage={}", errorCode.getMessage());
		log.warn("▶ requestData={}", Objects.isNull(errorDTO.getRequestData()) ? "없음" : errorDTO.getRequestData());
	}


	/**
	 * 서비스 정상 로그를 작성합니다.
	 *
	 * @param message	로그 메시지
	 * @param args			로그 값
	 */
	public static void logSystemInfo(String message, Object... args) {
		log.info(String.format("[SYSTEM_INFO] %s", message), args);
	}


	/**
	 * 서비스 에러 로그를 작성합니다.
	 *
	 * @param errorDTO	에러 정보를 담은 객체
	 * @param <T>	요청 데이터 타입
	 */
	public static <T> void logSystemError(ErrorDTO<T> errorDTO) {
		ErrorCode errorCode = errorDTO.getErrorCode();

		log.error("[ERROR] errorCode={}, LogMessage={}", errorCode.getCode(), errorCode.getLogMessage());
		log.error("▶ errorId={}", errorDTO.getErrorId());
		log.error("▶ UserMessage={}", errorCode.getMessage());
		log.error("▶ requestData={}", Objects.isNull(errorDTO.getRequestData()) ? "없음" : errorDTO.getRequestData());
	}
}
