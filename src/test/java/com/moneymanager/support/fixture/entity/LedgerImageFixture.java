package com.moneymanager.support.fixture.entity;

import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.domain.ledger.entity.LedgerImage;

public final class LedgerImageFixture {

	private LedgerImageFixture() {}

	public static LedgerImage.LedgerImageBuilder builder(Long id, Long ledgerId, int order) {
		return LedgerImage.builder()
				.id(id)
				.ledgerId(ledgerId)
				.imagePath("image" + order + ".jpg")
				.sortOrder(order);
	}

	public static LedgerImage newImage(Long ledgerId, int order) {
		return builder(null, ledgerId, order)
				.build();
	}

	public static LedgerImage savedImage(Long id, Long ledgerId, int order) {
		return builder(id, ledgerId, order)
				.createdAt(LedgerTestData.CREATED_DATE)
				.build();
	}

	public static LedgerImage updatedImage(Long id, Long ledgerId, int order) {
		return builder(id, ledgerId, order)
				.createdAt(LedgerTestData.CREATED_DATE)
				.updatedAt(LedgerTestData.UPDATED_DATE)
				.build();
	}

}