package com.moneymanager.domain.ledger.dto.request;

import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.*;
import com.moneymanager.domain.global.dto.ImageDTO;
import lombok.*;

import java.util.List;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.domain.ledger.dto<br>
 * * 파일이름       : LedgerUpdateRequest<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              : 가계부 수정 요청을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 25.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
@AllArgsConstructor
public class LedgerUpdateRequest {
	private String id;										//가계부 번호
	private String date;									//가계부 날짜
	private FixedStatus fix;								//가계부 고정정보
	private String category;							//카테고리
	private String memo;									//메모
	private Long price;									//가격
	private PaymentType paymentType;			//결제유형
	private List<ImageDTO> image;					//이미지
	private Place place;									//장소


	/**
	 * 기존 요청객체(update)와 이미지 리스트를 기반으로 새로운 UpdateDTO 객체를 생성합니다.
	 *
	 * @param id						수정할 가계부 번호
	 * @param update				기존 가계부 정보
	 * @param imageList			수정할 이미지 리스트
	 * @return	새 UpdateDTO 객체
	 */
	public static LedgerUpdateRequest of(String id, LedgerUpdateRequest update, List<ImageDTO> imageList ) {
		return LedgerUpdateRequest.builder()
				.id(id)
				.fix(update.getFix())
				.place(update.getPlace())
				.date(update.getDate()).category(update.getCategory()).memo(update.getMemo())
				.price(update.getPrice()).paymentType(update.getPaymentType()).image(imageList)
				.build();
	}

	public Ledger toEntity() {
		return Ledger.builder()
				.id(id)
				.date(date)
				.category(category)
				.memo(memo)
				.placeName(place.getPlaceName())
				.roadAddress(place.getRoadAddress())
				.detailAddress(place.getDetailAddress())
				.build();
	}
}
