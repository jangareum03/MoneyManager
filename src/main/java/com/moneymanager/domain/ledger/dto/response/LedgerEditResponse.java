package com.moneymanager.domain.ledger.dto.response;

import com.moneymanager.domain.global.enums.DatePatterns;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.domain.ledger.enums.AmountType;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.utils.date.DateTimeUtils;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
	private final String date;												//가계부 날짜
	private final String memo;												//메모
	private final List<CategoryResponse> category;			//카테고리
	private final List<String> images;									//가계부 사진

	private final CategoryType type;										//가계부 유형
	private final LedgerFixedResponse fixed;					//가계부 고정여부

	private Long amount;													//금액
	private AmountType paymentType;								//금액 유형

	private String placeName;								//장소명
	private String roadAddress;							//기본주소
	private String detailAddress;						//상세주소

	public static LedgerEditResponse from(Ledger ledger, Category category, List<CategoryResponse> categories, List<LedgerImage> images) {
		LocalDate date = DateTimeUtils.parseDateFromYyyyMMdd(ledger.getDate());
		Place place = ledger.getPlace();

		LedgerEditResponse.LedgerEditResponseBuilder builder =
				LedgerEditResponse.builder()
						.date(DateTimeUtils.formatDate(date, DatePatterns.DATE_DOT_WITH_DAY.getPattern()))
						.type(CategoryType.fromCategoryCode(category.getCode()))
						.fixed(LedgerFixedResponse.from(ledger))
						.category(categories)
						.memo(ledger.getMemo())
						.amount(ledger.getAmount())
						.paymentType(ledger.getAmountType())
						.placeName(place.getName())
						.roadAddress(place.getRoadAddress())
						.detailAddress(place.getDetailAddress());

		if( !images.isEmpty() ) {
			builder.images(
					toImagePaths(images)
			);
		}

		return builder.build();
	}

	//LedgerImage 리스트를 String 리스트로 변환
	private static List<String> toImagePaths(List<LedgerImage> images) {
		return images.stream()
				.map( image ->
						image == null ? null : image.getImagePath()
				)
				.collect(Collectors.toList());
	}
}
