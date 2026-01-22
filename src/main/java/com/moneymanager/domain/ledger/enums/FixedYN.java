package com.moneymanager.domain.ledger.enums;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.enums<br>
 * 파일이름       : FixedYN<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 19<br>
 * 설명              : 가계부 고정여부를 정의한 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 * 		<thead>
 * 		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 * 		 	  	<td>날짜</td>
 * 		 	  	<td>작성자</td>
 * 		 	  	<td>변경내용</td>
 * 		 	</tr>
 * 		</thead>
 * 		<tbody>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 12. 19.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public enum FixedYN {
	REPEAT("반복", "Y"),
	VARIABLE("일회", "N");

	private final String label;		//화면에 표시될 문구
	private final String value;		//DB 값

	FixedYN(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public static FixedYN of(String value) {
		for( FixedYN fixed : values() ) {
			if( fixed.value.equalsIgnoreCase(value) ) return fixed;
		}

		return null;
	}

	public static FixedYN of(boolean fixed) {
		return fixed ? FixedYN.REPEAT : FixedYN.VARIABLE;
	}

	public boolean isFixed(){
		return this == REPEAT;
	}
}