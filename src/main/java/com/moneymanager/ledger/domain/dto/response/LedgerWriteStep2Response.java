package com.moneymanager.ledger.domain.dto.response;

import com.moneymanager.ledger.domain.dto.response.item.CategoryItem;
import com.moneymanager.ledger.domain.dto.response.item.FixCycleItem;
import com.moneymanager.ledger.domain.dto.response.item.FixedTypeItem;
import com.moneymanager.ledger.domain.dto.response.item.PaymentTypeItem;
import com.moneymanager.ledger.domain.enums.LedgerType;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto<br>
 * 파일이름       : LedgerWriteStep2Response<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 19<br>
 * 설명              : 가계부 상세 작성 응답을 위한 데이터 클래스
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
public class LedgerWriteStep2Response {
	private final String title;														//제목
	private final LedgerType type;												//가계부 유형
	private final List<FixedTypeItem> fixed;								//고정여부
	private final List<FixCycleItem> fixCycle;								//고정주기
	private final List<CategoryItem> categories;						//카테고리 리스트
	private final List<PaymentTypeItem> paymentTypes;			//결제유형
	private final List<ImageSlot> imageSlot;								//이미지 슬롯 정보

	private LedgerWriteStep2Response(String title, LedgerType type, List<CategoryItem> categories, List<ImageSlot> imageSlot) {
		this.title = title;
		this.categories = categories;
		this.imageSlot =imageSlot;
		this.type = type;

		this.fixCycle = FixCycleItem.findAll();
		this.fixed = FixedTypeItem.findAll();
		this.paymentTypes = PaymentTypeItem.findAll();
	}

	public static LedgerWriteStep2Response of(String title, LedgerType type, List<CategoryItem> categoryItems, List<ImageSlot> imageSlot) {
		return new LedgerWriteStep2Response(title, type, categoryItems, imageSlot);
	}

}