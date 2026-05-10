package com.moneymanager.mapper;

import com.moneymanager.domain.global.enums.DatePatterns;
import com.moneymanager.domain.ledger.dto.response.*;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.utils.date.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.mapper<br>
 * 파일이름       : LedgerMapper<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 27<br>
 * 설명              : Ledger 변환 기능을 담당하는 클래스
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
 * 		 	  <td>26. 4. 27</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class LedgerMapper {

	public LedgerDetailResponse toDetailDto(Ledger ledger, Category category, List<String> imageList) {
		Place place = ledger.getPlace();

		String placeName = null;
		String roadAddress = null;
		String detailAddress = null;

		if(place != null) {
			placeName = place.getName();
			roadAddress = place.getRoadAddress();
			detailAddress = place.getDetailAddress();
		}

		return LedgerDetailResponse.builder()
				.date(
						DateTimeUtils.formatDate(ledger.getDate(), DatePatterns.DATE_DOT_WITH_DAY.getPattern())
				)
				.type(CategoryType.fromCode(ledger.getCategory()))
				.category(CategoryItem.from(category))
				.memo(ledger.getMemo())
				.amount(ledger.getAmount())
				.paymentType(ledger.getAmountType())
				.placeName(placeName)
				.roadAddress(roadAddress)
				.detailAddress(detailAddress)
				.images(imageList)
				.build();
	}


	public LedgerEditResponse toEditDto(Ledger ledger, List<ImageSlot> imageList, CategoryEditInfo categoryInfo) {
		Place place = ledger.getPlace();

		String placeName = null;
		String roadAddress = null;
		String detailAddress = null;

		if(place != null) {
			placeName = place.getName();
			roadAddress = place.getRoadAddress();
			detailAddress = place.getDetailAddress();
		}

		return LedgerEditResponse.builder()
				.date(
						DateTimeUtils.formatDate(ledger.getDate(), DatePatterns.KOREAN_DATE_WITH_DAY.getPattern())
				)
				.memo(ledger.getMemo())
				.type(CategoryType.fromCode(ledger.getCategory()))
				.fixed(LedgerFixed.from(ledger))
				.amount(ledger.getAmount())
				.paymentType(ledger.getAmountType())
				.placeName(placeName)
				.roadAddress(roadAddress)
				.detailAddress(detailAddress)
				.images(imageList)
				.categoryEditInfo(categoryInfo)
				.build();
	}

}
