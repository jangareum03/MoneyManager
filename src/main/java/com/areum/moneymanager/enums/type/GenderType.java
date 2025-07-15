package com.areum.moneymanager.enums.type;

import lombok.Getter;

@Getter
public enum GenderType {

	DEFAULT('N', "선택없음"), MAN('M', "남자"), WOMAN('F', "여자");

	private char type;
	private String text;

	GenderType( char type, String text ) {
		this.type = type;
		this.text= text;
	}


	public static String match( char type ) {
		for( GenderType g : values() ) {
			if( g.getType() == type ) {
				return g.getText();
			}
		}

		return GenderType.DEFAULT.getText();
	}
}
