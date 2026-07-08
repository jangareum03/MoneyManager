package com.moneymanager.exception.code;

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
public enum CommonErrorCode implements ErrorCode {

	REQUIRED_VALUE("CM-001", "필수 값입니다."),
	INVALID_FORMAT("CM-002", "형식이 올바르지 않습니다."),
	INVALID_VALUE("CM-003", "허용되지 않은 값입니다."),
	OUT_OF_RANGE("CM-004", "허용되는 범위나 길이가 아닙니다."),

	UNAUTHORIZED("CM-200", "로그인을 진행해주세요."),

	FILE_UPLOAD_FAILED("CM-501", "업로드가 실패했습니다."),
	FILE_READ_FAILED("CM-503", "파일을 읽을 수 없습니다."),
	FILE_TOO_LARGE("CM-505", "파일이 용량 초과되었습니다."),
	UNSUPPORTED_FILE_TYPE("CM-506", "지원하지 않은 파일입니다."),
	FILE_CORRUPTED("CM-507", "손상된 파일입니다."),

	DATABASE_ERROR("CM-700", "데이터베이스 문제가 있습니다.");

	private final String code;
	private final String defaultMessage;

	CommonErrorCode(String errorCode, String defaultMessage) {
		this.code = errorCode;
		this.defaultMessage = defaultMessage;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getDefaultMessage() {
		return defaultMessage;
	}

}
