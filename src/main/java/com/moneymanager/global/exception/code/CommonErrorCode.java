package com.moneymanager.global.exception.code;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.code<br>
 * 파일이름       : CommonErrorCode<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 30.<br>
 * 설명              : 공통적으로 사용하는 에러코드
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
public enum CommonErrorCode implements ErrorCode {

	REQUIRED_NOT_EXIST("CM-001", "요청객체 없음"),
	REQUIRED_VALUE("CM-002", "필수값 누락"),
	INVALID_FORMAT("CM-003", "형식 불일치"),
	INVALID_VALUE("CM-004", "허용되지 않은 값"),
	OUT_OF_RANGE("CM-005", "길이 또는 범위 불일치"),

	UNAUTHORIZED("CM-200", "인증되지 않은 사용자"),

	FILE_NOT_FOUND("CM-500", "파일 없음"),
	FILE_UPLOAD_FAILED("CM-501", "업로드 불가"),
	FILE_READ_FAILED("CM-503", "파일 읽기 불가"),
	FILE_TOO_LARGE("CM-505", "파일 용량 초과"),
	UNSUPPORTED_FILE_TYPE("CM-508", "미지원 파일"),

	INTERVAL_SERVER_ERROR("CM-900", "내부 오류");

	private final String code;
	private final String reason;

	CommonErrorCode(String errorCode, String reason) {
		this.code = errorCode;
		this.reason = reason;
	}

}