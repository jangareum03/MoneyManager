package com.areum.moneymanager.enums.type;

import com.areum.moneymanager.enums.ErrorCode;
import com.areum.moneymanager.exception.ErrorException;
import lombok.Getter;


/**
 *	가계부 날짜 유형을 정의
 *
 * @version 1.0
 */
@Getter
public enum DateRangeType {

	YEAR("Y", "년"), MONTH("M", "월"), WEEK("W", "주");

	private final String type;
	private final String text;

	DateRangeType(String type, String text ) {
		this.type = type;
		this.text = text;
	}


	/**
	 * 문자열 type을 Enum 객체로 변환
	 *
	 * @param type	날짜 유형
	 * @return	type에 맞는 Enum 객체
	 */
	public static DateRangeType toEnum( String type ) {
		for( DateRangeType dateType : DateRangeType.values() ) {
			if( dateType.getType().equalsIgnoreCase(type) ) {
				return dateType;
			}
		}

		throw new ErrorException(ErrorCode.BUDGET_TYPE_NONE);
	}
}
