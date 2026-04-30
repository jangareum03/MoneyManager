package com.moneymanager.mapper;

import com.moneymanager.domain.global.enums.DatePatterns;
import com.moneymanager.domain.ledger.dto.response.CategoryResponse;
import com.moneymanager.domain.ledger.dto.response.LedgerDetailResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
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

	public LedgerDetailResponse toDetailDto(Ledger ledger, Category category, List<LedgerImage> images) {
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
				.type(CategoryType.fromCategoryCode(ledger.getCategory()))
				.category(CategoryResponse.from(category))
				.memo(ledger.getMemo())
				.amount(ledger.getAmount())
				.paymentType(ledger.getAmountType())
				.placeName(placeName)
				.roadAddress(roadAddress)
				.detailAddress(detailAddress)
				.images(toImageUrl(ledger.getMemberId(), images))
				.build();
	}

	private List<String> toImageUrl(String memberId, List<LedgerImage> images) {
		return images.stream()
				.map(i -> {
					if( i == null ) {
						return null;
					}

					return	memberId + "/" + i.getImagePath();
				})
				.toList();
	}

}
