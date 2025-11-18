package com.moneymanager.domain.ledger.dto;

import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.domain.global.dto.ImageDTO;
import lombok.*;

import java.util.List;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.budgetBook.request<br>
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
	private String date;
	private LedgerFixResponse fix;
	private String category;
	private String memo;
	private Long price;
	private String paymentType;
	private List<ImageDTO> image;
	private Place place;

	public LedgerUpdateRequest() {
		this.fix = LedgerFixResponse.defaultValue();
		this.place = null;
	}


	/**
	 * 기존 요청객체(update)와 이미지 리스트를 기반으로 새로운 UpdateDTO 객체를 생성합니다.
	 *
	 * @param update				기존 가계부 정보
	 * @param imageList			수정할 이미지 리스트
	 * @return	새 UpdateDTO 객체
	 */
	public static LedgerUpdateRequest from(LedgerUpdateRequest update, List<ImageDTO> imageList ) {
		return LedgerUpdateRequest.builder()
		.fix(LedgerFixResponse.builder().option(update.getFix().getOption()).cycle(update.getFix().getCycle()).build())
		.place(Place.builder().placeName(update.getPlace().getPlaceName()).roadAddress(update.getPlace().getRoadAddress()).detailAddress(update.getPlace().getDetailAddress()).build())
		.date(update.getDate()).category(update.getCategory()).memo(update.getMemo())
		.price(update.getPrice()).paymentType(update.getPaymentType()).image(imageList)
		.build();
	}

}
