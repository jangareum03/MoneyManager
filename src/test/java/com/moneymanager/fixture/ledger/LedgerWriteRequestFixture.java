package com.moneymanager.fixture.ledger;


import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;

public class LedgerWriteRequestFixture {

	public static LedgerWriteRequest.LedgerWriteRequestBuilder createRequest() {
		return LedgerWriteRequest.builder()
				.date("20260101")
				.categoryCode("010101")
				.fixed(false)
				.amount(10000L)
				.amountType("none");
	}

	public static LedgerWriteRequest.LedgerWriteRequestBuilder withPlace() {
		return createRequest()
				.placeName("CGV 강남점")
				.roadAddress("서울특별시 강남구 강남대로 438 스타플렉스")
				.detailAddress("4층");
	}
}
