package com.areum.moneymanager.enums;

import lombok.Getter;


@Getter
public enum ErrorCode {

	/** 로그인 에러메시지 **/
	MEMBER_ID_NONE("MEMBER_101", "아이디를 입력해주세요."),
	MEMBER_ID_FORMAT("MEMBER_102", "4~15자 사이의 영어와 숫자만 입력 가능합니다."),
	MEMBER_PASSWORD_NONE("MEMBER_103", "비밀번호를 입력해주세요."),
	MEMBER_PASSWORD_FORMAT("MEMBER_104", "8~20자 사이의 영어,숫자,특수문자(!%#^*)만 입력 가능합니다."),
	MEMBER_LOGIN_MISMATCH("MEMBER_105", "아이디와 비밀번호가 일치하지 않습니다."),
	MEMBER_LOGIN_RESTORE("MEMBER_106", "해당 계정은 탈퇴된 상태로 로그인이 불가능합니다. 가입하실 때 입력하신 이메일로 임시 비밀번호를 보내드렸으니, 다시 한 번 로그인 부탁드립니다."),
	MEMBER_LOGIN_RESIGN("MEMBER_107", "회원가입 하지 않는 아이디입니다. 회원가입을 진행해 주세요."),
	MEMBER_LOGIN_LOCKED("MEMBER_108","로그인 횟수를 초과로 로그인 할 수 없는 상태입니다. 가입하신 이메일로 임시 비밀번호를 보내드렸으니, 다시 한 번 로그인 부탁드립니다."),
	MEMBER_LOGIN_UNKNOWN("MEMBER_201", "현재 일시적인 문제로 로그인을 할 수 없는 상태입니다. 고객 센터로 문의 부탁드립니다."),
	/** 회원가입 에러메시지 **/
	MEMBER_ID_EXITS("MEMBER_", "이미 사용중인 아이디입니다."),
	MEMBER_PASSWORD_MISMATCH("MEMBER_", "비밀번호가 일치하지 않습니다. 다시 확인해주세요."),
	MEMBER_NAME_NONE("MEMBER_", "이름을 입력해주세요."),
	MEMBER_NAME_FORMAT("MEMBER_", "2~5글자 사이의 한글만 입력해야 합니다."),
	MEMBER_BIRTH_NONE("MEMBER_", "생년월일을 입력해주세요."),
	MEMBER_BIRTH_FORMAT("MEMBER_", "생년월일 형식에 맞게 입력해야 합니다."),
	MEMBER_NICKNAME_NONE("MEMBER_", "닉네임을 입력해주세요."),
	MEMBER_NICKNAME_FORMAT("MEMBER_", "2 ~ 10자 사이의 한글, 영문자, 숫자만 입력해야 합니다."),
	MEMBER_NICKNAME_EXITS("MEMBER_", "이미 사용중인 닉네임입니다."),
	MEMBER_EMAIL_NONE("MEMBER_", "이메일을 입력해주세요."),
	MEMBER_EMAIL_FORMAT("MEMBER_", "이메일 형식에 맞게 입력해야 합니다."),
	MEMBER_EMAIL_EXITS("MEMBER_", "이미 사용중인 이메일입니다."),
	MEMBER_CODE_MISMATCH("MEMBER_", "이메일 인증코드가 일치하지 않습니다. 다시 입력해주세요."),
	MEMBER_CODE_FORMAT("MEMBER_", "6글자의 영문자, 숫자만 입력해야 합니다."),
	MEMBER_JOIN_TIMEOUT("MEMBER_", "시간 초과되었습니다. 다시 전송해주세요."),
	MEMBER_JOIN_DISAGREE("MEMBER_", "이용 약관에 동의해주세요."),
	MEMBER_JOIN_UNKNOWN("MEMBER_", "회원가입 중 오류가 발생하였습니다. 잠시 후 다시 시도해주세요."),
	/** 출석 에러 메시지 **/
	MEMBER_ATTENDANCE_COMPLETE("MEMBER_", "오늘은 이미 출석하셨어요. 내일 다시 출석해주세요!"),
	MEMBER_DATE_FORMAT("MEMBER_", "오늘 날짜가 아니므로 출석이 불가능합니다."),
	MEMBER_ATTENDANCE_UNKNOWN("MEMBER_", "일시적인 문제로 출석이 불가능합니다 .잠시 후 다시 시도해주세요."),
	/**
	 * 회원정보 찾기 에러 메시지
	 */
	MEMBER_FIND_NONE("MEMBER_123", "입력하신 정보로 일치하는 사용자가 없습니다."),
	MEMBER_FIND_ID("MEMBER_203", "현재 일시적인 문제로 아이디 찾기가 불가능합니다. 잠시 후 다시 시도해 주세요."),
	MEMBER_FIND_PASSWORD("MEMBER_204", "현재 일시적인 문제로 비밀번호 찾기가 불가능합니다. 잠시 후 다시 시도해주세요."),
	/** 회원정보 변경 에러메시지 **/
	MEMBER_UPDATE_UNKNOWN("MEMBER_205", "현재 일시적인 문제로 회원정보 변경이 불가능합니다. 잠시 후 다시 시도해 주세요.</br>문제가 지속될 경우, 고객센터로 문의해 주시길 바랍니다."),
	MEMBER_UPDATE_PROFILE("MEMBER_206", "프로필 사진을 변경할 수 없습니다. 잠시 후 다시 변경해주세요."),
	MEMBER_UPDATE_PASSWORD("MEMBER_124", "현재 비밀번호와 동일합니다. 다른 비밀번호를 입력해주세요."),
	/**
	 * 회원 탈퇴 에러메시지
	 */
	MEMBER_EXIT_REASON("MEMBER_125", "탈퇴 사유를 선택해주세요."),
	MEMBER_EXIT_ETC("MEMBER_126", "기타 사항을 입력해주세요."),
	MEMBER_EXIT_UNKNOWN("MEMBER_207", "일시적인 문제로 탈퇴가 불가능합니다. 잠시 후 다시 시도해주세요."),
	/** 이메일 에러 메시지 **/
	EMAIL_SEND_FORMAT("MEMBER_208", "이메일 형식 문제로 전송할 수 없습니다. 잠시 후 다시 시도해주세요.</br>문제가 지속될 경우, 고객센터로 문의해 주시길 바랍니다."),
	EMAIL_SEND_UNKNOWN("MEMBER_209", "일시적인 문제로 이메일을 전송할 수 없습니다. 잠시 후 다시 시도해주세요."),
	/**
	 * 포인트 에러 메시지
	 * */
	POINT_ADD_NONE("MEMBER_", "추가할 포인트가 0이므로 추가할 수 없습니다."),
	/**	가계부 타입 에러 메시지 **/
	BUDGET_TYPE_NONE("BUDGET_", "가계부는 년, 월, 주 단위만 조회가 가능합니다. 입력하신 조회 유형은 존재하지 않아 월 단위로 자동 조회됩니다."),
	/** 가계부 날짜 에러 메시지 **/
	BUDGET_YEAR_NONE("BUDGET_101", "년도를 입력하세요."),
	BUDGET_YEAR_FORMAT("BUDGET_102", "0으로 시작하지 않은 4글자 숫자로만 입력 가능합니다."),
	BUDGET_YEAR_RANGE("BUDGET_103", "년도가 범위에서 벗어났습니다. 현재 년도 기준으로 최대 5년 전까지만 입력 가능합니다."),
	BUDGET_MONTH_NONE("BUDGET_104", "월을 입력하세요."),
	BUDGET_MONTH_FORMAT("BUDGET_105", "2글자 숫자로만 입력 가능합니다."),
	BUDGET_MONTH_RANGE("BUDGET_106", "월의 범위에서 벗어났습니다. 1 ~ 12 사이만 입력 가능합니다."),
	BUDGET_WEEK_NONE("BUDGET_107", "주를 입력하세요."),
	BUDGET_WEEK_FORMAT("BUDGET_108", "1 ~ 5 사이에 숫자 1개만 입력 가능합니다."),
	/**
	 * 가계부 에러 메시지
	 * */
	BUDGET_REGISTER_UNKNOWN("BUDGET_202", "가계부를 등록할 수 없습니다. 잠시 후 다시 시도해주세요."),
	BUDGET_REGISTER_IMAGE("BUDGET_", "가계부 이미지를 등록할 수 없습니다. 잠시 후 다시 등록해주세요."),
	BUDGET_AUTHOR_MISMATCH("BUDGET_204", "가계부 작성자가 아니여서 접근이 불가능합니다."),
	BUDGET_UPDATE_UNKNOWN("BUDGET_205", "가계부를 수정할 수 없습니다. 잠시 후 다시 시도해주세요."),
	BUDGET_DELETE_UNKNOWN("BUDGET_206", "가계부를 삭제할 수 없습니다. 잠시 후 다시 시도해주세요."),
	/**
	 * Q&A 에러 메시지
	 */
	QUESTION_REGISTER_UNKNOWN("QUESTION_201", "문의사항을 등록할 수 없습니다. 잠시 후 다시 시도해주세요."),
	QUESTION_TITLE_NONE("QUESTION_101", "제목을 입력해주세요."),
	QUESTION_TITLE_FORMAT("QUESTION_102", "제목은 특수문자로 시작 불가하며, 20글자까지만 입력 가능합니다."),
	QUESTION_CONTENT_NONE("QUESTION_103", "내용을 입력해주세요."),
	QUESTION_CONTENT_FORMAT("QUESTION_104", "내용은 300글자까지만 입력 가능합니다."),
	QUESTION_DELETE_UNKNOWN("QUESTION", "문의사항을 삭제할 수 없습니다. 잠시 후 다시 시도해주세요."),
	/**
	 *  공지사항 에러 메시지 
	 *  */
	NOTICE_FIND_NONE("NOTICE_201", "존재하지 않는 공지사항입니다."),
	/* 데이터베이스 에러 메시지 */
	DB_PK_FOUND("PK_ABSENT", "테이블의 데이터 추가로 생성된 PK값을 찾지 못 했습니다.");

	private final String code;
	private final String message;


	ErrorCode( String code, String message ) {
		this.code = code;
		this.message = message;
	}

}
