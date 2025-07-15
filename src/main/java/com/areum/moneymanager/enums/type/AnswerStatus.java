package com.areum.moneymanager.enums.type;

import lombok.Getter;

@Getter
public enum AnswerStatus {

	YES('Y', "답변 완료"), NO('N', "답변 준비");

	private final char type;
	private final String text;

	AnswerStatus( char type, String text ) {
		this.type = type;
		this.text = text;
	}

	public static String match( char type ) {
		for( AnswerStatus status : values() ) {
			if( status.type== type ) {
				return status.getText();
			}
		}

		return AnswerStatus.NO.getText();
	}
}
