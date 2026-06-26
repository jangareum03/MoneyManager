package com.moneymanager.fixture.ledger;

import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;

/**
 * <p>
 * 패키지이름    : com.moneymanager.fixture.ledger<br>
 * 파일이름       : LedgerUpdateRequestFixture<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 5. 14.<br>
 * 설명              :
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
 * 		 	  <td>26. 5. 14.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class LedgerUpdateRequestFixture {

	public static LedgerUpdateRequest.LedgerUpdateRequestBuilder createRequest() {
		return LedgerUpdateRequest.builder()
				.categoryCode("010101")
				.fixed(false)
				.amount(10000L)
				.paymentType("none");
	}

	public static LedgerUpdateRequest.LedgerUpdateRequestBuilder withPlace() {
		return createRequest()
				.placeName("CGV 강남점")
				.roadAddress("서울특별시 강남구 강남대로 438 스타플렉스")
				.detailAddress("4층");
	}

}
