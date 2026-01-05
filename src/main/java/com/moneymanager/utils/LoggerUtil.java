package com.moneymanager.utils;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.exception.ErrorCode;
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
	 * 사용자 경고 로그를 기록합니다.
	 * <p>
	 *     사용자의 요청으로 인해 발생한 경미한 문제나 주의가 필요한 상황을 기록할 때 사용합니다. <br>
	 *     로그에는 에러ID, 에러코드, 로그가 포함됩니다.
	 * </p>
	 *
	 * @param errorDTO   	에러 정보를 담은 객체
	 */
	public static void logUserWarn(ErrorDTO errorDTO) {
		ErrorCode errorCode = errorDTO.getErrorCode();

		log.warn("[{}] errorCode={}, errorType={}", errorDTO.getErrorId(), errorCode.getCode(), errorCode.getType());
		log.warn("[{}] service={}, cause={}", errorDTO.getErrorId(), errorDTO.getServiceName(), errorDTO.getMessage());
	}


	/**
	 * 서비스 정상 로그를 기록합니다.
	 * <p>
	 *     서비스가 의도대로 정상 동작했음을 기록할 때 사용합니다. <br>
	 *     운영 환경에서 시스템 상태 확인, 배치 작업 완료, 정상 API 요청 처리 등 다양한 상황에 활용 가능합니다.
	 * </p>
	 *
	 * @param message	로그 메시지
	 * @param args			로그에 포함할 값들
	 */
	public static void logSystemInfo(String message, Object... args) {
		log.info(String.format("[SYSTEM_INFO] %s", message), args);
	}


	/**
	 * 사용자 입력 관련 디버그 로그를 기록합니다.
	 * <p>
	 *     주로 개발 및 테스트 환경에서 사용자의 요청 데이터나 입력값을 확인할 때 사용합니다. <br>
	 *     운영 환경에서는 보통 DEBUG 레벨 로그를 사용하지 않습니다.
	 * </p>
	 *
	 * @param message	로그 메시지
	 * @param args			로그에 포함할 값들
	 */
	public static void logUserDebug(String message, Object... args) {
		log.debug(String.format("[USER_INPUT] %s", message), args);
	}


	/**
	 * 시스템 에러 로그를 기록합니다.
	 * <p>
	 *     서버나 시스템 내부에서 발생한 치명적인 오류를 기록할 때 사용합니다.<br>
	 *     로그에는 에러ID, 에러 코드, 에러메시지, 사용자 메시지, 요청 데이터를 포합합니다.
	 * </p>
	 *
	 * @param errorDTO	에러 정보를 담은 객체
	 */
	public static void logSystemError(ErrorDTO errorDTO) {
		ErrorCode errorCode = errorDTO.getErrorCode();

		log.error("[{}] errorCode={}, errorType={}", errorDTO.getErrorId(), errorCode.getCode(), errorCode.getType());
		log.error("[{}] service={}, cause={}", errorDTO.getErrorId(), errorDTO.getServiceName(), errorDTO.getMessage());
	}
}
