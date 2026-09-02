package com.moneymanager.ledger.domain.dto.response;

import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.enums.LedgerType;
import com.moneymanager.ledger.domain.enums.PaymentType;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response<br>
 * 파일이름       : LedgerDetailResponse<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 2<br>
 * 설명              : 가계부 상세 정보 응답을 위한 클래스
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
 * 		 	  <td>26. 9. 2</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LedgerDetailResponse {

    private final LedgerType type;
    private final String date;
    private final String amount;
    private final String category;
    private final PaymentType payment;
    private final String memo;
    private final List<ImageSlot> images;
    private final Place place;

    private LedgerDetailResponse(LedgerType type, String date, String amount, String category, PaymentType payment, String memo, List<ImageSlot> images, Place place) {
        this.type = type;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.payment = payment;
        this.memo = memo;
        this.images = images;
        this.place = place;
    }

    public static LedgerDetailResponse of(LedgerType type, String date, Long amount, String category, PaymentType paymentType, String memo, List<ImageSlot> imageSlots, Place place) {
        return new LedgerDetailResponse(type, date, String.valueOf(amount), category, paymentType, memo, imageSlots, place);
    }

}