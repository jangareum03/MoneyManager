package com.moneymanager.fixture;


import com.moneymanager.domain.ledger.dto.response.*;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.fixture.category.CategoryTreeFixture;
import com.moneymanager.fixture.ledger.ImageSlotFixture;

import java.time.LocalDate;
import java.util.List;

public class LedgerResponseFixture {

	private static final LocalDate today = LocalDate.now();
	private static final String title = "테스트 제목";

	public static LedgerWriteStep1Response step1() {
		int currentYear = today.getYear();

		return LedgerWriteStep1Response.builder()
				.types(LedgerTypeResponse.fromEnum())
				.years(List.of(currentYear, currentYear - 1, currentYear -2))
				.months(List.of(1, 2, 3))
				.days(List.of(1, 2, 3, 4, 5))
				.currentYear(currentYear)
				.currentMonth(today.getMonthValue())
				.currentDay(today.getDayOfMonth())
				.displayDate(title)
				.build();
	}


	public static LedgerWriteStep2Response step2(String type) {
		return type.equalsIgnoreCase("income") ? step2Income() : step2Outlay();
	}

	public static  LedgerWriteStep2Response step2Income() {
		return LedgerWriteStep2Response.ofDataByIncome(
				title,
				CategoryItem.from(CategoryTreeFixture.middle().get(CategoryType.INCOME)),
				List.of(ImageSlotFixture.emptySlot(), ImageSlotFixture.lockedSlot(), ImageSlotFixture.lockedSlot())
		);
	}

	private static  LedgerWriteStep2Response step2Outlay() {
		return LedgerWriteStep2Response.ofDataByOutlay(
				title,
				CategoryItem.from(CategoryTreeFixture.middle().get(CategoryType.OUTLAY)),
				List.of(ImageSlotFixture.emptySlot(), ImageSlotFixture.lockedSlot(), ImageSlotFixture.lockedSlot())
		);
	}

}
