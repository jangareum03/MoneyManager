package com.areum.moneymanager.enums.type;


import lombok.Getter;

/**
 * 가계부 유형을 정의
 * @version 1.0
 */
@Getter
public enum BudgetBookType {

	INCOME("income"), LAYOUT("outlay");

	private final String type;

	BudgetBookType( String type) {
		this.type = type;
	}


	/**
	 * 가계부 유형을 확인 후 일치하는 유형을 반환합니다. <br>
	 * 일치하는 유형이 없으면 {@link IllegalArgumentException} 이 발생합니다.
	 *
	 * @param inputType		확인할 가게부 유형
	 * @return	가계부 유형
	 */
	public static BudgetBookType getBudgetBookType( String inputType ) {
		for( BudgetBookType bookType : BudgetBookType.values() ) {
			if( bookType.getType().equalsIgnoreCase(inputType) ) {
				return bookType;
			}
		}

		throw new IllegalArgumentException("잘못된 가계부 유형입니다. (입력: " + inputType + ")");
	}

}
