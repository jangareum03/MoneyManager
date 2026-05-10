package com.moneymanager.exception.error;

import lombok.Getter;


/**
 * <p>
 * 패키지이름    : com.areum.moneymanager.exception.code<br>
 * 파일이름       : ErrorCode<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 7. 15<br>
 * 설명              : 서비스 문제 상황을 에러코드로 정의한 클래스
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
 * 		 	  <td>22. 7. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>22. 7. 23</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>에러코드 규칙 변경</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>22. 8. 28</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 추가] fromName() - 에러이름과 일치하는 에러코드 얻기</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public enum ErrorCode {
	/**
	 * 회원 관련 오류
	 */
	MEMBER_TARGET_MISSING("C-10-01-02", "필수값이 없습니다."),
	MEMBER_AUTHORITY_UNAUTHORIZED("C-10-06-03", "인증 정보가 없습니다."),
	MEMBER_AUTHORITY_FAILED("C-10-06-02", "인증 정보가 올바르지 않습니다."),
	MEMBER_ETC_DB_ERROR("S-10-09-02", "요청 처리 중에 문제가 발생했습니다. 잠시 후 다시 시도해주세요."),

	/**
	 * 가계부 관련 오류
	 */
	LEDGER_TARGET_NOT_FOUND("S-20-01-01", "찾을 수 없는 가계부입니다."),
	LEDGER_TARGET_MISSING("S-20-01-02", "필요한 값이 없습니다."),
	LEDGER_TARGET_FORMAT("S-20-01-04", "형식이 올바르지 않습니다."),
	LEDGER_TARGET_RANGE("S-20-01-06", "범위가 벗어났습니다."),

	LEDGER_INPUT_MISSING("C-20-02-01", "요청하신 가계부가 없습니다."),
	LEDGER_INPUT_INVALID("C-20-02-02", "가계부 입력값이 올바르지 않습니다."),
	LEDGER_INPUT_FORMAT("C-20-02-03", "가계부 입력 형식이 올바르지 않습니다."),
	LEDGER_INPUT_RANGE("C-20-02-05", "가계부 입력 범위를 벗어났습니다."),
	LEDGER_INPUT_LENGTH("C-20-02-06", "가계부 입력 길이가 초과했습니다."),
	LEDGER_INPUT_NULL("C-20-02-07", "가계부 필수값이 없습니다."),

	LEDGER_COLLISION_UNIQUE_CONSTRAINT("S-20-05-03", "가계부 등록할 수 없습니다. 입력한 정보를 다시 확안해주세요."),

	LEDGER_POLICY_NOT_ALLOWED("S-20-07-03", "처리할 수 없는 가계부 작업입니다."),

	LEDGER_ETC_DB_ERROR("S-20-09-02", "데이터베이스 관련 문제가 발생했습니다."),

	/**
	 * 가계부 카테고리 관련 오류
	 */
	LEDGER_CATEGORY_TARGET_NOT_FOUND("C-21-01-01", "찾을 수 없는 카테고리입니다."),

	/**
	 * 거래내역 관련 오류
	 */
	LEDGER_HISTORY_INPUT_FORMAT("C-22-02-03", "가계부 내역조회 입력 형식이 올바르지 않습니다."),
	LEDGER_HISTORY_INPUT_NULL("C-22-02-07", "가계부 내역조회 필수값이 없습니다."),
	LEDGER_HISTORY_INPUT_CONFLICT("C-22-02-09", "가계부 내역조회 입력값이 안맞습니다."),

	LEDGER_HISTORY_POLICY_VIOLATION("S-22-07-01", "가계부 내역조회 규칙위반 관련 문제가 발생했습니다."),

	/**
	 * 파일 관련 오류
	 */
	FILE_TARGET_MISSING("S-50-01-02", "사용할 수 없는 파일입니다."),
	FILE_TARGET_INVALID("S-50-01-03", "문제가 있는 파일입니다."),

	FILE_INPUT_FORMAT("C-50-02-03", "파일 형식이 올바르지 않습니다."),
	FILE_INPUT_TYPE("C-50-02-04", "파일 타입이 올바르지 않습니다."),
	FILE_INPUT_EMPTY("C-50-02-08", "파일이 비어있습니다."),
	FILE_INPUT_ETC("C-50-02-10", "파일이 잘못되었습니다."),

	FILE_STATE_INVALID("S-50-04-01", "파일 상태가 잘못되었습니다."),

	FILE_COLLISION_ALREADY_EXISTS("S-50-05-02", "이미 존재하는 파일입니다."),

	FILE_POLICY_LIMIT_EXCEEDED("S-50-07-02", "허용하는 범위를 벗어난 파일입니다."),
	FILE_POLICY_NOT_ALLOWED("S-50-07-03", "지원하지 않은 파일입니다."),

	FILE_ETC_UNKNOWN("S-50-09-04", "알 수 없는 파일 오류입니다."),
	FILE_ETC_RESOURCE_ERROR("S-50-09-10", "파일과 관련된 자원이 부족합니다.");


	private final String code;
	private final String defaultMessage;

	ErrorCode(String code, String defaultMessage) {
		this.code = code;
		this.defaultMessage = defaultMessage;
	}
	}
