package com.moneymanager.domain.ledger.enums;


import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.enums<br>
 * 파일이름       : FixCycle<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 24.<br>
 * 설명              : 가계부 고정주기를 정의한 클래스
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
 * 		 	  <td>25. 11. 24.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public enum FixCycle {
		WEEKLY("일주일", "W"),
		MONTHLY("한달", "M"),
		YEARLY("일년", "Y");

	private final String label;
	private final String value;

	FixCycle(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public static FixCycle of(String cycle) {
		for( FixCycle type : values() ) {
			if(type.value.equalsIgnoreCase(cycle)) return type;
		}

		throw new IllegalArgumentException(
				"reason=허용값아님   |   field=fixCycle   |   allowedValues=" + allowedFixCycle() + "   |   value=" + cycle
		);
	}

	private static String allowedFixCycle() {
		return Arrays.stream(FixCycle.values())
				.map(Enum::name)
				.collect(Collectors.joining(", "));
	}
}
