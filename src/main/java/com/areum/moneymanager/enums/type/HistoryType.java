package com.areum.moneymanager.enums.type;

import lombok.Getter;


/**
 *	회원과 서비스 수정내역의 유형을 정의
 *
 */
@Getter
public enum HistoryType {
	INSERT_JOIN("회원가입"),
	UPDATE_NONE(""),UPDATE_NAME("이름"), UPDATE_GENDER("성별"), UPDATE_EMAIL("이메일"), UPDATE_PROFILE("프로필"), UPDATE_PASSWORD("비밀번호");
	//TODO: 회원 탈퇴인 경우에 내역남기기

	private final String item;

	HistoryType( String  update ) {
		this.item = update;
	}

}
