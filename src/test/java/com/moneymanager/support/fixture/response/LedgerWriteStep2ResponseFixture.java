package com.moneymanager.support.fixture.response;

import com.moneymanager.domain.ledger.dto.response.CategoryItem;
import com.moneymanager.domain.ledger.dto.response.ImageSlot;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep2Response;
import com.moneymanager.support.fixture.entity.CategoryFixture;

import java.util.List;

public final class LedgerWriteStep2ResponseFixture {

	private LedgerWriteStep2ResponseFixture() {}

	public static LedgerWriteStep2Response create() {
		return LedgerWriteStep2Response.ofDataByIncome(
				"제목",
				createCategoryItems(),
				createSlots()
		);
	}

	private static List<CategoryItem> createCategoryItems() {
		return List.of(
				CategoryItem.from(CategoryFixture.top()),
				CategoryItem.from(CategoryFixture.top()),
				CategoryItem.from(CategoryFixture.top())
		);
	}

	private static List<ImageSlot> createSlots() {
		return List.of(
				ImageSlot.ofEmptySlot(),
				ImageSlot.ofLockedSlot(),
				ImageSlot.ofLockedSlot()
		);
	}

}