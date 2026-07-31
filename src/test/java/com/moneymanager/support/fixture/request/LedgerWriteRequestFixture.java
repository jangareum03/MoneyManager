package com.moneymanager.support.fixture.request;

import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.support.fixture.file.ImageFixture;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public final class LedgerWriteRequestFixture {

	private LedgerWriteRequestFixture() {}

	public static LedgerWriteRequest.LedgerWriteRequestBuilder builder() {
		return LedgerWriteRequest.builder()
				.date(LedgerTestData.DATE)
				.categoryCode(CategoryTestData.SALARY_CODE)
				.fixed(LedgerTestData.FIX_N)
				.fixCycle(null)
				.amount(LedgerTestData.AMOUNT)
				.paymentType(LedgerTestData.PAYMENT_TYPE);
	}

	public static LedgerWriteRequest.LedgerWriteRequestBuilder withImages(int size) {
		List<MultipartFile> imageList = List.of(
				ImageFixture.jpg("test1"),
				ImageFixture.png("test2."),
				ImageFixture.jpg("test3")
		);


		return builder()
				.images(
						new ArrayList<>(imageList.subList(0, size))
				);
	}

	public static LedgerWriteRequest.LedgerWriteRequestBuilder withPlace() {
		return builder()
				.placeName(LedgerTestData.PLACE_NAME)
				.roadAddress(LedgerTestData.ROAD_ADDRESS)
				.detailAddress(LedgerTestData.DETAIL_ADDRESS);
	}

	public static LedgerWriteRequest create() {
		return builder().build();
	}

}
