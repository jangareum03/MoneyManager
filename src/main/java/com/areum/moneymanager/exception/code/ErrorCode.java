package com.areum.moneymanager.exception.code;

import lombok.Getter;


@Getter
public enum ErrorCode {
	/** 로그인 에러메시지 **/
	LOGIN_ID_MISSING("C010101", "아이디를 입력해주세요."),
	LOGIN_ID_FORMAT("C010102", "4~15자 사이의 영어와 숫자만 입력 가능합니다."),
	LOGIN_PASSWORD_MISSING("C010201", "비밀번호를 입력해주세요."),
	LOGIN_PASSWORD_FORMAT("C010202", "8~20자 사이의 영어,숫자,특수문자(!%#^*)만 입력 가능합니다."),
	LOGIN_ACCOUNT_MISMATCH("C010004", "아이디 또는 비밀번호가 일치하지 않습니다."),
	LOGIN_ACCOUNT_UNAUTHORIZED_RESTORE("C010007", "해당 계정은 탈퇴된 상태로 로그인이 불가능합니다. 가입하실 때 입력하신 이메일로 임시 비밀번호를 보내드렸으니, 다시 한 번 로그인 부탁드립니다."),
	LOGIN_ACCOUNT_UNAUTHORIZED_RESIGN("C010007", "회원가입 하지 않는 아이디입니다. 회원가입을 진행해 주세요."),
	LOGIN_ACCOUNT_UNAUTHORIZED_LOCKED("C010007", "로그인 횟수를 초과로 로그인 할 수 없는 상태입니다. 가입하신 이메일로 임시 비밀번호를 보내드렸으니, 다시 한 번 로그인 부탁드립니다."),
	/** 회원가입 에러메시지 **/
	SIGNUP_ID_MISSING("C020101", "아이디를 입력해주세요"),
	SIGNUP_ID_FORMAT("C020102", "4~15자 사이의 영어와 숫자만 입력 가능합니다."),
	SIGNUP_ID_DUPLICATE("C020103", "이미 사용중인 아이디입니다."),
	SIGNUP_PASSWORD_MISSING("C020201", "비밀번호를 입력해주세요."),
	SIGNUP_PASSWORD_FORMAT("C020202", "8~20자 사이의 영어,숫자,특수문자(!%#^*)만 입력 가능합니다."),
	SIGNUP_PASSWORD_MISMATCH("C020204", "비밀번호가 일치하지 않습니다. 다시 확인해주세요."),
	SIGNUP_RE_PASSWORD_MISSING("C020301", "비밀번호를 입력해주세요."),
	SIGNUP_RE_PASSWORD_FORMAT("C020302", "8~20자 사이의 영어,숫자,특수문자(!%#^*)만 입력 가능합니다."),
	SIGNUP_NAME_MISSING("C020401", "이름을 입력해주세요."),
	SIGNUP_NAME_FORMAT("C020402", "2~5글자 사이의 한글만 입력해야 합니다."),
	SIGNUP_BIRTH_MISSING("C020501", "생년월일을 입력해주세요."),
	SIGNUP_BIRTH_FORMAT("C020502", "생년월일 형식에 맞게 입력해야 합니다."),
	SIGNUP_NICKNAME_MISSING("C020601", "닉네임을 입력해주세요."),
	SIGNUP_NICKNAME_FORMAT("C020602", "2 ~ 10자 사이의 한글, 영문자, 숫자만 입력해야 합니다."),
	SIGNUP_NICKNAME_DUPLICATE("C020603", "이미 사용중인 닉네임입니다."),
	SIGNUP_EMAIL_MISSING("C020701", "이메일을 입력해주세요."),
	SIGNUP_EMAIL_FORMAT("C020702", "이메일 형식에 맞게 입력해야 합니다."),
	SIGNUP_EMAIL_DUPLICATE("C020703", "이미 사용중인 이메일입니다."),
	SIGNUP_CODE_MISSING("C020801", "인증코드를 입력해주세요."),
	SIGNUP_CODE_FORMAT("C020802", "6글자의 영문자, 숫자만 입력해야 합니다."),
	SIGNUP_CODE_MISMATCH("C020804", "인증코드가 일치하지 않습니다 .다시 입력해주세요."),
	SIGNUP_CODE_TIMEOUT("C020806", "시간 초과되었습니다. 다시 전송해주세요."),
	SIGNUP_GENDER_MISSING("C020901", "성별을 선택해주세요."),
	SIGNUP_AGREE_MISSING("C021001", "이용 약관에 동의해주세요"),
	/** 계정 찾기 에러 메시지 **/
	HELP_ACCOUNT_ID_MISSING("C030101", "아이디를 입력해주세요."),
	HELP_ACCOUNT_ID_FORMAT("C030102", "4~15자 사이의 영어와 숫자만 입력 가능합니다."),
	HELP_ACCOUNT_NAME_MISSING("C030401", "이름을 입력해주세요."),
	HELP_ACCOUNT_NAME_FORMAT("C030402", "2~5글자 사이의 한글만 입력해야 합니다."),
	HELP_ACCOUNT_BIRTH_MISSING("C030501", "생년월일을 입력해주세요."),
	HELP_ACCOUNT_BIRTH_FORMAT("C030502", "생년월일 형식에 맞게 입력해야 합니다."),
	/** 출석 에러 메시지 **/
	ATTEND_TODAY_INVALID("C040005", "오늘 날짜가 아니므로 출석이 불가능합니다."),
	ATTEND_TODAY_DUPLICATE("C040003", "오늘은 이미 출석 완료했습니다. 내일 다시 출석해주세요!"),
	/**	가게부 작성 에러메시지 **/
	BUDGET_WRITE_TYPE_MISSING("C050101", "수입 또는 지출 중 하나를 선택해주세요."),
	BUDGET_WRITE_TYPE_FORMAT("C050102", "현재 선택된 가계부 유형은 작성할 수 없습니다. 수입 또는 지출만 작성 가능합니다."),
	BUDGET_WRITE_DATE_MISSING("C050201", "작성할 가계부 날짜가 없어 작성할 수 없습니다."),
	BUDGET_WRITE_DATE_FORMAT("C050202", "가계부 날짜는 연도+월+일 형식의 8자리 숫자(예: 20250101)여야 합니다."),
	BUDGET_WRITE_FIX_MISSING("C050301", "고정 또는 변동 중 하나를 선택해주세요."),
	BUDGET_WRITE_FIX_INVALID("C050305", "선택하신 값은 유효하지 않습니다. 다시 선택해주세요."),
	BUDGET_WRITE_OPTION_MISSING("C050301", "고정 주기 중 하나를 선택해주세요."),
	BUDGET_WRITE_OPTION_INVALID("C050305", "선택하신 값은 유효하지 않습니다. 다시 선택해주세요."),
	BUDGET_WRITE_CATEGORY_MISSING("C050401", "카테고리를 선택해주세요."),
	BUDGET_WRITE_CATEGORY_INVALID("C050405", "선택하신 값은 유효하지 않습니다. 다시 선택해주세요."),
	BUDGET_WRITE_PRICE_MISSING("C050701", "금액을 입력해주세요."),
	BUDGET_WRITE_PRICE_FORMAT("C050702", "금액은 숫자만 입력가능합니다."),
	BUDGET_WRITE_PAY_TYPE_MISSING("C050801", "금액유형 중 하나를 선택해주세요."),
	BUDGET_WRITE_PAY_TYPE_INVALID("C050805", "선택하신 값은 유효하지 않습니다. 다시 선택해주세요."),
	BUDGET_WRITE_PHOTO_MISSING("C050901", "사진이 첨부되지 않았습니다."),
	BUDGET_WRITE_PHOTO_EMPTY("C050908", "사진 크기가 0입니다. 다른 사진으로 변경해주세요."),
	BUDGET_WRITE_PHOTO_CORRUPTED("C050909", "사진이 손상되어 등록할 수 없습니다."),
	BUDGET_WRITE_PHOTO_NOT_SUPPORTED("C050910", "지원하지 않은 사진 형식입니다."),
	BUDGET_WRITE_PHOTO_SIZE_EXCEEDED("C050911", "사진 크기가 너무 큽니다."),
	/**	가게부 수정 에러메시지 **/
	BUDGET_UPDATE_FIX_MISSING("C060301", "고정 또는 변동 중 하나를 선택해주세요."),
	BUDGET_UPDATE_OPTION_MISSING("C060301", "고정 주기 중 하나를 선택해주세요."),
	BUDGET_UPDATE_CATEGORY_MISSING("C060401", "카테고리를 선택해주세요."),
	BUDGET_UPDATE_CATEGORY_INVALID("C060405", "선택하신 값은 유효하지 않습니다. 다시 선택해주세요."),
	BUDGET_UPDATE_PRICE_MISSING("C060701", "금액을 입력해주세요."),
	BUDGET_UPDATE_PRICE_FORMAT("C060702", "금액은 숫자만 입력가능합니다."),
	BUDGET_UPDATE_PAY_TYPE_MISSING("C060801", "금액유형 중 하나를 선택해주세요."),
	BUDGET_UPDATE_PAY_TYPE_MISMATCH("C060804", "선택하신 값은 유효하지 않습니다. 다시 선택해주세요."),
	BUDGET_UPDATE_PHOTO_MISSING("C060901", "사진이 첨부되지 않았습니다."),
	BUDGET_UPDATE_PHOTO_EMPTY("C060908", "사진 크기가 0입니다. 다른 사진으로 변경해주세요."),
	BUDGET_UPDATE_PHOTO_CORRUPTED("C060909", "사진이 손상되어 등록할 수 없습니다."),
	BUDGET_UPDATE_PHOTO_NOT_SUPPORTED("C060910", "지원하지 않은 사진 형식입니다."),
	BUDGET_UPDATE_PHOTO_SIZE_EXCEEDED("C060911", "사진 크기가 너무 큽니다."),
	/**	가계부 조회 에러메시지 **/
	BUDGET_READ_START_PERIOD_MISSING("C070201", "시작날짜를 선택해주세요."),
	BUDGET_READ_END_PERIOD_MISSING("C070201", "종료날짜를 선택해주세요."),
	BUDGET_READ_ID_INVALID("C070005", "존재하지 않은 가계부입니다. 다시 확인해주세요."),
	/**	가게부 삭제 에러메시지 **/
	BUDGET_DELETE_MISSING("C080001", "삭제할 가계부를 선택해주세요."),
	BUDGET_DELETE_UNAUTHORIZED("C080007", "가계부 작성자가 아니라서 삭제가 불가능합니다."),
	BUDGET_DELETE_NOT_FOUND("C080012", "이미 삭제된 가계부입니다."),
	/**	회원수정 에러메시지 **/
	MEMBER_UPDATE_NAME_MISSING("C090401", "이름을 입력해주세요."),
	MEMBER_UPDATE_NAME_FORMAT("C090402", "2~5글자 사이의 한글만 입력해야 합니다."),
	MEMBER_UPDATE_NAME_DUPLICATE("C090403", "기존 이름과 동일합니다. 다른 이름으로 다시 입력해주세요."),
	MEMBER_UPDATE_GENDER_MISSING("C090901", "성별을 선택해주세요."),
	MEMBER_UPDATE_GENDER_INVALID("C090905", "선택하신 값은 유효하지 않습니다. 다시 선택해주세요."),
	MEMBER_UPDATE_EMAIL_MISSING("C090701", "이메일을 입력해주세요."),
	MEMBER_UPDATE_EMAIL_FORMAT("C090702", "이메일 형식에 맞게 입력해야 합니다."),
	MEMBER_UPDATE_EMAIL_DUPLICATE("C090703", "이미 사용중인 이메일입니다."),
	MEMBER_UPDATE_CODE_MISSING("C090801", "인증코드를 입력해주세요."),
	MEMBER_UPDATE_CODE_FORMAT("C090802", "6글자의 영문자, 숫자만 입력해야 합니다."),
	MEMBER_UPDATE_CODE_MISMATCH("C090804", "인증코드가 일치하지 않습니다 .다시 입력해주세요."),
	MEMBER_UPDATE_CODE_TIMEOUT("C090806", "시간 초과되었습니다. 다시 전송해주세요."),
	MEMBER_UPDATE_PROFILE_MISSING("C091101", "프로필 사진이 첨부되지 않았습니다.", "사용자: {}, 원인: 프로필 사진 없음"),
	MEMBER_UPDATE_PROFILE_EMPTY("C091108", "프로필 사진 크기가 0입니다. 다른 사진으로 변경해주세요.", "사용자: {}, 원인: 프로필 사진 크기 0"),
	MEMBER_UPDATE_PROFILE_CORRUPTED("C091109", "프로필 사진이 손상되어 등록할 수 없습니다.", "사용자: {}, 원인: 손상된 사진"),
	MEMBER_UPDATE_PROFILE_NOT_SUPPORTED("C091110", "지원하지 않은 사진 형식입니다.", "사용자: {}, 원인: 미지원하는 형식"),
	MEMBER_UPDATE_PROFILE_SIZE_EXCEEDED("C091111", "사진 크기가 너무 큽니다. (최대크기: 5MB)", "사용자: {}, 원인: 사진 용량 초과"),
	MEMBER_UPDATE_PASSWORD_MISSING("C090201", "비밀번호를 입력해주세요."),
	MEMBER_UPDATE_PASSWORD_FORMAT("C090202", "8~20자 사이의 영어,숫자,특수문자(!%#^*)만 입력 가능합니다."),
	MEMBER_UPDATE_PASSWORD_MISMATCH("C090204", "비밀번호가 일치하지 않습니다. 다시 확인해주세요."),
	MEMBER_UPDATE_NEW_PASSWORD_MISSING("C090201", "비밀번호를 입력해주세요."),
	MEMBER_UPDATE_NEW_PASSWORD_FORMAT("C090202", "8~20자 사이의 영어,숫자,특수문자(!%#^*)만 입력 가능합니다."),
	MEMBER_UPDATE_NEW_PASSWORD_DUPLICATE("C090203", "현재 비밀번호와 동일합니다. 다른 비밀번호를 입력해주세요."),
	MEMBER_UPDATE_RE_PASSWORD_MISSING("C090301", "새 비밀번호를 먼저 입력해주세요."),
	MEMBER_UPDATE_RE_PASSWORD_FORMAT("C090302", "8~20자 사이의 영어,숫자,특수문자(!%#^*)만 입력 가능합니다."),
	MEMBER_UPDATE_RE_PASSWORD_MISMATCH("C090304", "새 비밀번호와 일치하지 않습니다. 다시 입력해주세요."),
	/**	회원 탈퇴  에러메시지 **/
	MEMBER_DELETE_ID_MISSING("C100101", "삭제할 아이디를 알 수 없어 탈퇴가 불가능합니다."),
	MEMBER_DELETE_PASSWORD_MISSING("C100201", "비밀번호를 입력해주세요."),
	MEMBER_DELETE_PASSWORD_FORMAT("C1O0202", "8~20자 사이의 영어,숫자,특수문자(!%#^*)만 입력 가능합니다."),
	MEMBER_DELETE_PASSWORD_MISMATCH("C100204", "비밀번호가 일치하지 않습니다. 다시 확인해주세요."),
	MEMBER_DELETE_REASON_MISSING("C101301", "탈퇴 사유를 선택해주세요."),
	MEMBER_DELETE_REASON_INVALID("C101305", "선택하신 값은 유효하지 않습니다. 다시 선택해주세요."),
	MEMBER_DELETE_ETC_MISSING("C101301", "기타사항을 입력해주세요."),
	MEMBER_DELETE_AGREE_MISSING("C101001", "탈퇴 동의를 선택해주세요"),
	/**	문의사항 작성  에러메시지 **/
	INQUIRY_WRITE_TITLE_MISSING("C110101", "제목을 입력해주세요."),
	INQUIRY_WRITE_TITLE_FORMAT("C110102", "특수문자로 시작 불가하며, 20글자까지만 입력 가능합니다."),
	INQUIRY_WRITE_CONTENT_MISSING("C110301", "내용을 입력해주세요."),
	INQUIRY_WRITE_CONTENT_FORMAT("C110302", "1~300자까지만 입력 가능합니다."),
	/**	문의사항 수정  에러메시지 **/
	INQUIRY_UPDATE_TITLE_MISSING("C120101", "제목을 입력해주세요."),
	INQUIRY_UPDATE_TITLE_FORMAT("C120102", "특수문자로 시작 불가하며, 20글자까지만 입력 가능합니다."),
	INQUIRY_UPDATE_CONTENT_MISSING("C120301", "내용을 입력해주세요."),
	INQUIRY_UPDATE_CONTENT_FORMAT("C120302", "1~300자까지만 입력 가능합니다."),
	/**	문의사항 조회  에러메시지 **/
	INQUIRY_READ_UNAUTHORIZED("C130007", "작성자가 아니라서 접근이 불가능합니다."),



	/** 회원정보 변경 에러메시지 **/

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
	DB_PK_FOUND("PK_ABSENT", "테이블의 데이터 추가로 생성된 PK값을 찾지 못 했습니다."),






	MEMBER_ATTENDANCE_COMPLETE("MEMBER_", "오늘은 이미 출석하셨어요. 내일 다시 출석해주세요!"),
	MEMBER_DATE_FORMAT("MEMBER_", "오늘 날짜가 아니므로 출석이 불가능합니다."),
	MEMBER_ATTENDANCE_UNKNOWN("MEMBER_", "일시적인 문제로 출석이 불가능합니다 .잠시 후 다시 시도해주세요."),
	MEMBER_FIND_NONE("MEMBER_123", "입력하신 정보로 일치하는 사용자가 없습니다."),
	MEMBER_FIND_ID("MEMBER_203", "현재 일시적인 문제로 아이디 찾기가 불가능합니다. 잠시 후 다시 시도해 주세요."),
	MEMBER_FIND_PASSWORD("MEMBER_204", "현재 일시적인 문제로 비밀번호 찾기가 불가능합니다. 잠시 후 다시 시도해주세요."),
	MEMBER_ID_NONE("MEMBER_101", "아이디를 입력해주세요."),
	MEMBER_ID_FORMAT("MEMBER_102", "4~15자 사이의 영어와 숫자만 입력 가능합니다."),
	MEMBER_PASSWORD_NONE("MEMBER_103", "비밀번호를 입력해주세요."),
	MEMBER_PASSWORD_FORMAT("MEMBER_104", "8~20자 사이의 영어,숫자,특수문자(!%#^*)만 입력 가능합니다."),
	MEMBER_LOGIN_MISMATCH("MEMBER_105", "아이디와 비밀번호가 일치하지 않습니다."),
	MEMBER_LOGIN_RESTORE("MEMBER_106", "해당 계정은 탈퇴된 상태로 로그인이 불가능합니다. 가입하실 때 입력하신 이메일로 임시 비밀번호를 보내드렸으니, 다시 한 번 로그인 부탁드립니다."),
	MEMBER_LOGIN_RESIGN("MEMBER_107", "회원가입 하지 않는 아이디입니다. 회원가입을 진행해 주세요."),
	MEMBER_LOGIN_LOCKED("MEMBER_108","로그인 횟수를 초과로 로그인 할 수 없는 상태입니다. 가입하신 이메일로 임시 비밀번호를 보내드렸으니, 다시 한 번 로그인 부탁드립니다."),
	MEMBER_LOGIN_UNKNOWN("MEMBER_201", "현재 일시적인 문제로 로그인을 할 수 없는 상태입니다. 고객 센터로 문의 부탁드립니다."),
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
	MEMBER_JOIN_UNKNOWN("MEMBER_", "회원가입 중 오류가 발생하였습니다. 잠시 후 다시 시도해주세요.");

	private final String code;
	private final String message;
	private final String logMessage;

	ErrorCode( String code, String message ) {
		this.code = code;
		this.message = message;
		this.logMessage = null;
	}

	ErrorCode( String code, String message, String logMessage) {
		this.code = code;
		this.message = message;
		this.logMessage = logMessage;
	}

}
