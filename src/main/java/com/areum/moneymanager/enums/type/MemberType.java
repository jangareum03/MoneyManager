package com.areum.moneymanager.enums.type;

import lombok.Getter;

@Getter
public enum MemberType {
	NORMAL('C', "일반회원"), KAKAO('K', "카카오"), GOOGLE('G', "구글"), NAVER('N', "네이버");

	private final char value;
	private final String text;

	MemberType( char value, String text ) {
		this.value = value;
		this.text = text;
	}

	public static String match( char value ) {
		for( MemberType m : values() ) {
			if( m.getValue() == value ) {
				return m.getText();
			}
		}

		return MemberType.NORMAL.getText();
	}
}
