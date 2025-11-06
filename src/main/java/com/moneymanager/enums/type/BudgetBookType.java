package com.moneymanager.domain.enums.type;


import lombok.Getter;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.enums.type<br>
 *  * 파일이름       : BudgetBookType<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 7. 15<br>
 *  * 설명              : 가계부 유형을 정의한 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>22. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
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
