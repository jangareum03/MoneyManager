package com.moneymanager.support.fixture.response;

import com.moneymanager.ledger.domain.dto.response.item.CategoryItem;
import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep2Response;
import com.moneymanager.ledger.domain.enums.CategoryType;
import com.moneymanager.support.fixture.entity.category.CategoryFixture;

import java.util.List;

public final class LedgerWriteStep2ResponseFixture {

	private LedgerWriteStep2ResponseFixture() {}

	public static LedgerWriteStep2Response create() {
		return LedgerWriteStep2Response.of(
				"제목",
				CategoryType.INCOME,
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