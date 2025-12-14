package com.moneymanager.domain.ledger.dto;

import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.FixedPeriod;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.domain.ledger.vo.AmountInfo;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.utils.DateTimeUtils;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.moneymanager.utils.ValidationUtils.isNullOrBlank;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto<br>
 * 파일이름       : LedgerEditResponse<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 5<br>
 * 설명              : 가계부 단건 수정 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 12. 5.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class LedgerEditResponse {
	private String date;												//가계부 날짜
	private LedgerType type;										//가계부 유형
	private boolean isCycle;											//고정유무
	private List<FixedPeriod> fixed;							//고정주기
	private List<CategoryResponse> category;			//카테고리
	private String memo;												//메모
	private AmountInfo amountInfo;							//금액
	private String images;											//가계부 사진
	private Place place;												//위치

	public static LedgerEditResponse from(Ledger ledger, List<CategoryResponse> categories) {
		LocalDate date = ledger.getTransActionDate();
		LedgerEditResponse.LedgerEditResponseBuilder builder =
				LedgerEditResponse.builder()
						.date(DateTimeUtils.formatDateAsString(date, "yyyy. MM. dd (E)"))
						.type(LedgerType.fromCode(ledger.getCategory().getCode()))
						.isCycle(ledger.isReturning())
						.category(categories)
						.memo(ledger.getMemo())
						.amountInfo(ledger.getAmountInfo())
						.place(ledger.getPlace());

		if( ledger.isReturning() ) {
			builder.fixed(List.of(FixedPeriod.values()));
		}

		//TODO: 이미지 업로드 추가

		return builder.build();
	}
}
