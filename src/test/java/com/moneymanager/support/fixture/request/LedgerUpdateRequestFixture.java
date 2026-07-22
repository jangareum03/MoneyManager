package com.moneymanager.support.fixture.request;

import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.support.fixture.file.ImageFixture;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public final class LedgerUpdateRequestFixture {

	private LedgerUpdateRequestFixture() {}

	public static LedgerUpdateRequest from(Ledger ledger) {
		return LedgerUpdateRequest.builder()
				.categoryCode(ledger.getCategory())
				.memo(ledger.getMemo())
				.fixed(ledger.getFix().getValue())
				.amount(ledger.getMoney().getAmount())
				.paymentType(ledger.getMoney().getPaymentType().name())
				.fixCycle(
						ledger.getFixCycle() == null
						? null
						: ledger.getFixCycle().getValue()
				)
				.placeName(
						ledger.getPlace() == null
						? null
						: ledger.getPlace().getPlaceName()
				)
				.roadAddress(
						ledger.getPlace() == null
								? null
								: ledger.getPlace().getRoadAddress()
				)
				.detailAddress(
						ledger.getPlace() == null
								? null
								: ledger.getPlace().getDetailAddress()
				)
				.build();
	}

	public static LedgerUpdateRequest.LedgerUpdateRequestBuilder builder() {
		return LedgerUpdateRequest.builder()
				.categoryCode(CategoryTestData.SALARY_CODE)
				.fixed(LedgerTestData.FIX_N)
				.amount(LedgerTestData.AMOUNT)
				.paymentType(LedgerTestData.PAYMENT_TYPE);
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

	public static LedgerUpdateRequest create() {
		return builder().build();
	}

}
