package com.moneymanager.support.fixture.response;

import com.moneymanager.domain.ledger.dto.response.CategoryItem;
import com.moneymanager.domain.ledger.dto.response.ImageSlot;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep2Response;
import com.moneymanager.support.fixture.entity.category.CategoryFixture;

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
				CategoryItem.from(CategoryFixture.income()),
				CategoryItem.from(CategoryFixture.snack()),
				CategoryItem.from(CategoryFixture.salary())
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