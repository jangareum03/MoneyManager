package com.moneymanager.ledger.domain.enums;


import lombok.Getter;

import java.util.Arrays;


/**
 * <p>
 *  * 패키지이름    : com.moneymanager.domain.ledger.enums<br>
 *  * 파일이름       : LedgerType<br>
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
public enum LedgerType {

	INCOME("수입", "01"),
	OUTLAY("지출", "02");

	private final String label;				//화면 표시용
	private final String prefix;			//카테고리 코드 (앞 2자리)


	LedgerType(String label, String prefix) {
		this.label = label;
		this.prefix= prefix;
	}

	public static LedgerType from(String name) {
		return Arrays.stream(values())
				.filter(t -> t.name().equalsIgnoreCase(name))
				.findFirst()
				.orElseThrow();
	}

	public static LedgerType fromCode(String prefix) {
		return Arrays.stream(values())
				.filter(t -> prefix.startsWith(t.prefix))
				.findFirst()
				.orElseThrow();
	}

}