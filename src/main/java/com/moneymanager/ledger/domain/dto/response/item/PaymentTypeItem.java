package com.moneymanager.ledger.domain.dto.response.item;

import com.moneymanager.ledger.domain.enums.PaymentType;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response<br>
 * 파일이름       : PaymentTypeItem<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 10<br>
 * 설명              : 결제 유형 정보를 담는 객체
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
public class PaymentTypeItem {

	private final String label;		//화면에 보여줄 문구
	private final String value;		//실제 유형 값
	private final String svg;		//이미지 태그

	private PaymentTypeItem(String label, String value, String svg) {
		this.label = label;
		this.value = value.toLowerCase();
		this.svg = svg;
	}

	public static List<PaymentTypeItem> findAll() {
		return Arrays.stream(PaymentType.values())
						.map(PaymentTypeItem::from)
						.toList();
	}


	//===== findAll 보조 메서드 =====
	private static PaymentTypeItem from(PaymentType type) {
		return new PaymentTypeItem(type.getLabel(), type.name(), type.getSvg());
	}

}