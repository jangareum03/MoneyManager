package com.moneymanager.ledger.domain.dto.response.item;

import com.moneymanager.ledger.domain.enums.FixCycle;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response.item<br>
 * 파일이름       : FixCycleItem<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 15<br>
 * 설명              : 가계부 고정 주기 정보를 담는 객체
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
 * 		 	  <td>26. 8. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class FixCycleItem {

	private final String label;			//화면에 보여줄문구
	private final String value;			//실제 값

	private FixCycleItem(String label, String value) {
		this.label = label;
		this.value = value.toLowerCase();
	}

	public static List<FixCycleItem> findAll() {
		return Arrays.stream(FixCycle.values())
				.map(FixCycleItem::from)
				.toList();

	}


	//==== findAll 보조 메서드 =====
	private static FixCycleItem from(FixCycle cycle) {
		return new FixCycleItem(cycle.getLabel(), cycle.getValue());
	}

}