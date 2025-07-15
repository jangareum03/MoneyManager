package com.areum.moneymanager.enums.type;

import lombok.Getter;

/**
*	공지사항의 유형을 정의
*
 * @version 1.0
*/
@Getter
public enum NoticeType {
	NORMAL('N', "일반"), EVENT('E', "이벤트"), URGENCY('P', "긴급");

	private final char value;
	private final String text;

	NoticeType( char value, String text ) {
		this.value = value;
		this.text = text;
	}

	public static String match( char value ) {
		for( NoticeType n : values() ) {
			if( n.getValue() == value ) {
				return n.getText();
			}
		}

		return NoticeType.NORMAL.getText();
	}
}
