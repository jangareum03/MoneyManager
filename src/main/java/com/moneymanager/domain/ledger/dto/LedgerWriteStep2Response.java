package com.moneymanager.domain.ledger.dto;

import com.moneymanager.domain.ledger.enums.FixedPeriod;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.domain.ledger.enums.PaymentType;
import lombok.Builder;
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
@Builder
@Getter
public class LedgerWriteStep2Response {
	private final String title;												//제목
	private final LedgerType type;										//가계부 유형
	private final List<FixedYN> fixed;								//고정여부
	private final List<CategoryResponse> categories;		//카테고리 리스트
	private final List<PaymentType> paymentTypes;		//결제유형
	private final List<Boolean> imageSlot;							//이미지 사용여부
}
