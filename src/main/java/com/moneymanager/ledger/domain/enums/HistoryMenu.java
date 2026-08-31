package com.moneymanager.ledger.domain.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.enums<br>
 * 파일이름       : HistoryMenu<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 22<br>
 * 설명              : 가계부 내역 메뉴를 정의한 클래스
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
 * 		 	  <td>26. 4. 22</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public enum HistoryMenu {

	ALL("전체", "all"),
	CATEGORY("수입/지출", "type"),
	SUB_CATEGORY("카테고리", "category"),
	MEMO("메모", "memo"),
	PERIOD("기간", "period");

	private final String label;
	private final String value;

	HistoryMenu(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public static HistoryMenu from(String name) {
		return Arrays.stream(values())
				.filter(m -> m.value.equalsIgnoreCase(name))
				.findFirst()
				.orElseThrow();
	}

}