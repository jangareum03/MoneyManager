package com.moneymanager.exception.code;

import com.moneymanager.exception.custom.ServerException;
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
	 * 공통 에러메시지
	 */
	COMMON_DATE_MISSING("C991001", "날짜 미입력"),
	COMMON_DATE_FORMAT("C991002", "날짜 형식 불일치"),
	COMMON_YEAR_MISSING("C991101","년도 미입력"),
	COMMON_YEAR_FORMAT("C991102", "년도 형식 불일치"),
	COMMON_YEAR_INVALID("C991103", "년도 범위 벗어나게 입력"),
	COMMON_MONTH_MISSING("C991201","월 미입력"),
	COMMON_MONTH_FORMAT("C991202", "월 형식 불일치"),
	COMMON_MONTH_FUTURE("C991203", "미래 월 입력"),
	COMMON_WEEK_MISSING("C991301", "주 미입력"),
	COMMON_WEEK_FORMAT("C991302", "주 형식 불일치"),
	COMMON_WEEK_INVALID("C991303", "주 범위 벗어나게 입력"),
	COMMON_DAY_MISSING("C001401","일 미입력"),
	COMMON_DAY_FORMAT("C001402", "일 형식 불일치"),
	COMMON_DAY_INVALID("C001403", "일 범위 벗어나게 입력"),
	/**
	 * 회원 에러메시지
	 */
	MEMBER_ID_MISSING("C010101","아이디 미입력"),
	MEMBER_ID_FORMAT("C010102", "아이디 형식 불일치"),
	MEMBER_ID_DUPLICATE("C010103", "중복된 아이디"),
	MEMBER_ID_NONE("C010104", "미가입한 아이디"),
	MEMBER_PASSWORD_MISSING("C010201", "비밀번호 미입력"),
	MEMBER_PASSWORD_FORMAT("C010202", "비밀번호 형식 불일치"),
	MEMBER_PASSWORD_MISMATCH("C010205", "비밀번호 불일치"),
	MEMBER_NAME_MISSING("C010501", "이름 미입력"),
	MEMBER_NAME_FORMAT("C010502", "이름 형식 불일치"),
	MEMBER_BIRTH_MISSING("C010601", "생년월일 미입력"),
	MEMBER_BIRTH_FORMAT("C010602", "생년월일 형식 불일치"),
	MEMBER_NICKNAME_MISSING("C010701", "닉네임 미입력"),
	MEMBER_NICKNAME_FORMAT("C010702", "닉네임 형식 불일치"),
	MEMBER_NICKNAME_DUPLICATE("C010703", "중복된 닉네임"),
	MEMBER_EMAIL_MISSING("C010801", "이메일 미입력"),
	MEMBER_EMAIL_FORMAT("C010802", "이메일 형식 불일치"),
	MEMBER_EMAIL_DUPLICATE("C010803", "중복된 이메일"),
	MEMBER_CODE_FORMAT("C010902","인증코드 형식 불일치"),
	MEMBER_CODE_MISMATCH("C010905","인증코드 불일치"),
	MEMBER_CODE_TIMEOUT("C010907", "인증코드 시간초과"),
	MEMBER_STATUS_UNAUTHORIZED("C011521", "아이디와 비밀번호 불일치"),
	MEMBER_STATUS_WITHDRAW_RECOVERABLE("C011523", "복구 가능한 계정으로 로그인 시도"),
	MEMBER_STATUS_WITHDRAW_NONRECOVERABLE("C011524", "복구 불가능한 계정으로 로그인 시도"),
	MEMBER_STATUS_LOCKED("C011525", "잠긴 계정으로 로그인 시도"),
	MEMBER_STATUS_UNKNOWN("C011508", "알 수 없는 상태"),
	/**
	 * 가계부 에러메시지
	 */
	BUDGET_DATE_MISSING("C030201", "가계부 날짜 미입력"),
	BUDGET_DATE_FORMAT("C030202", "가계부 날짜 형식 불일치"),

	SYSTEM_CODE_NULL("S010401", "없는 에러코드");



	private final String code;
	private final String logMessage;

	ErrorCode(String code, String logMessage) {
		this.code = code;
		this.logMessage = logMessage;
	}


	/**
	 *	지정된 이름으로 {@link ErrorCode} enum 상수를 반환합니다.
	 *<p>
	 *     매개변수 {@code name}을 대소문자 구분 없이 비교하여 일치하는 {@link ErrorCode} enum 상수를 반환합니다.
	 *</p>
	 *
	 * @param name	찾을 {@link ErrorCode} 상수 이름
	 * @return	이름과 일치하는 enum 상수
	 * @throws ServerException		{@code name}과 일치하는 상수가 없을 경우 발생
	 */
	public static ErrorCode fromName(String name) {
		for(ErrorCode e : values()) {
			if( e.name().equalsIgnoreCase(name) ) {
				return e;
			}
		}

		throw new ServerException(SYSTEM_CODE_NULL);
	}


	/**
	 * 에러코드 시작이 C인지 확인합니다.
	 *
	 * @return C로 시작하면 true, 아니면 false
	 */
	public boolean isClientError() {
		return code.startsWith("C");
	}

	/**
	 * 에러코드 시작이 S인지 확인합니다.
	 *
	 * @return S로 시작하면 true, 아니면 false
	 */
	public boolean isServerError() {
		return code.startsWith("S");
	}
}
