package com.moneymanager.support.fixture.request;

import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.fixture.file.ImageFixture;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public final class LedgerUpdateRequestFixture {

	private LedgerUpdateRequestFixture() {}

	public static LedgerUpdateRequest.LedgerUpdateRequestBuilder builder() {
		return LedgerUpdateRequest.builder()
				.categoryCode(LedgerTestData.DEFAULT_CATEGORY)
				.fixed(LedgerTestData.DEFAULT_FIX.getValue().toLowerCase())
				.amount(LedgerTestData.DEFAULT_AMOUNT)
				.paymentType(LedgerTestData.DEFAULT_PAYMENT_TYPE.name().toLowerCase());
	}

	public static LedgerUpdateRequest.LedgerUpdateRequestBuilder withImages(int size) {
		List<MultipartFile> images = List.of(
				ImageFixture.jpg("test1"),
				ImageFixture.png("test2"),
				ImageFixture.jpg("test1")
		);

		return builder()
				.images(
						new ArrayList<>(images.subList(0, size))
				);
	}

	public static LedgerUpdateRequest.LedgerUpdateRequestBuilder withPlace() {
		return builder()
				.placeName(LedgerTestData.PLACE_NAME)
				.roadAddress(LedgerTestData.ROAD_ADDRESS)
				.detailAddress(LedgerTestData.DETAIL_ADDRESS);
	}

}