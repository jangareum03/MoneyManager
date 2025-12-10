package com.moneymanager.domain.ledger.dto;

import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.vo.AmountInfo;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.utils.DateTimeUtils;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.moneymanager.utils.ValidationUtils.isNullOrBlank;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.domain.ledger.dto<br>
 * * 파일이름       : LedgerDetailResponse<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 26.<br>
 * * 설명              : 가계부 단건 조회 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 26.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class LedgerDetailResponse {
	private String date;										//가계부 날짜
	private CategoryResponse category;			//카테고리
	private String memo;										//메모
	private AmountInfo amount;							//금액정보
	private List<String> image;							//가계부 사진
	private Place place;										//위치

	public static LedgerDetailResponse from(Ledger ledger, String path) {
		LocalDate date = ledger.getTransActionDate();

		LedgerDetailResponseBuilder builder =
				LedgerDetailResponse.builder()
						.date(DateTimeUtils.formatDateAsString(date, "yyyy. MM. dd (E)"))
						.category(CategoryResponse.from(ledger.getCategory()))
						.memo(ledger.getMemo())
						.amount(ledger.getAmountInfo())
						.image(Collections.emptyList())
						.place(ledger.getPlace());

		if( !isNullOrBlank(path) ) {
			builder.image(List.of( String.join(path, ledger.getImage1()) ));
		}

		return builder.build();
	}
}
