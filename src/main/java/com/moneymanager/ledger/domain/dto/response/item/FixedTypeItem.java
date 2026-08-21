package com.moneymanager.ledger.domain.dto.response.item;

import com.moneymanager.ledger.domain.enums.FixedType;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response<br>
 * 파일이름       : FixedTypeItem<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 10<br>
 * 설명              : 가계부 고정 유형 정보를 담는 객체
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
 * 		 	  <td>26. 8. 10</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class FixedTypeItem {

	private final String label;		//화면에 보여줄 문구
	private final String value;		//실제 유형 값

	private FixedTypeItem(String label, String value) {
		this.label = label;
		this.value = value.toLowerCase();
	}

	public static List<FixedTypeItem> findAll() {
		return Arrays.stream(FixedType.values())
				.map(FixedTypeItem::from)
				.toList();
	}

	//===== findAll 보조 메서드 =====
	private static FixedTypeItem from(FixedType type) {
		return new FixedTypeItem(type.getLabel(), type.getValue());
	}

}