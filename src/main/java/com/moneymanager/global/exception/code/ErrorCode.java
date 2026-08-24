package com.moneymanager.global.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.code<br>
 * 파일이름       : ErrorCode<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 30<br>
 * 설명              : 오류 코드 정의한 클래스
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
 * 		 	  <td>26. 6. 30.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public enum ErrorCode {
	/** 요청 오류 **/
	REQUIRED_NOT_EXIST("001", "요청 객체 없음", HttpStatus.BAD_REQUEST),
	REQUIRED_VALUE("002", "필수값 누락",  HttpStatus.BAD_REQUEST),
	INVALID_FORMAT("003", "형식 불일치", HttpStatus.BAD_REQUEST),
	INVALID_VALUE("004", "허용하지 않은 값",  HttpStatus.BAD_REQUEST),
	OUT_OF_RANGE("005", "범위 초과", HttpStatus.BAD_REQUEST),
	OUT_OF_LENGTH("006", "길이 초과", HttpStatus.BAD_REQUEST),
	
	/** 데이터 오류 **/
	DATA_NOT_FOUND("100", "데이터 없음", HttpStatus.NOT_FOUND),

	/** 인증 오류 **/
	UNAUTHORIZED("200", "인증되지 않은 사용자",  HttpStatus.UNAUTHORIZED),

	/** 인가 오류**/
	OWNER_ONLY("303", "소유자가 아닌 사용자", HttpStatus.FORBIDDEN),

	/** 정책 오류 **/
	POLICY_VIOLATION("400", "정책 위반", HttpStatus.FORBIDDEN),

	/** 파일 오류 **/
	FILE_NOT_FOUND("500", "파일 없음", HttpStatus.NOT_FOUND),
	FILE_UPLOAD_FAILED("501", "업로드 불가", HttpStatus.INTERNAL_SERVER_ERROR),
	FILE_READ_FAILED("503", "파일 읽기 불가",  HttpStatus.INTERNAL_SERVER_ERROR),
	FILE_TOO_LARGE("505", "파일 용량 초과", HttpStatus.PAYLOAD_TOO_LARGE),
	UNSUPPORTED_FILE_TYPE("508", "미지원 파일", HttpStatus.UNSUPPORTED_MEDIA_TYPE),

	/** 기타 오류 **/
	INTERVAL_SERVER_ERROR("900", "내부 오류", HttpStatus.INTERNAL_SERVER_ERROR),;


	private final String code;
	private final String reason;
	private final HttpStatus status;

	ErrorCode(String code, String reason, HttpStatus status) {
		this.code = code;
		this.reason = reason;
		this.status = status;
	}

	public String getCode() {
		return "ERR-" + code;
	}
}