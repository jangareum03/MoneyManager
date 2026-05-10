package com.moneymanager.fixture.ledger;

import com.moneymanager.domain.ledger.dto.response.*;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.AmountType;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.domain.ledger.enums.FixCycle;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.fixture.category.CategoryTreeFixture;

import java.util.List;

public class LedgerEditResponseFixture {

	public static LedgerEditResponse income() {
		return categoryResponse(CategoryType.INCOME, List.of("010100", "010101"));
	}

	public static LedgerEditResponse outlay() {
		return categoryResponse(CategoryType.OUTLAY, List.of("020100", "020101"));
	}

	private static LedgerEditResponse categoryResponse(CategoryType type, List<String> selected) {
		return base()
				.type(type)
				.categoryEditInfo(
						CategoryEditInfo.builder()
								.selected(selected)
								.middleOptions(
										CategoryItem.from(CategoryTreeFixture.middle().get(type))
								)
								.lowOptions(
										CategoryItem.from(CategoryTreeFixture.low().get(type))
								)
								.build()
				)
				.build();
	}

	public static LedgerEditResponse withFix(FixCycle cycle) {
		LedgerFixed fixed
				= LedgerFixed.from(Ledger.builder().fix(FixedYN.REPEAT).fixCycle(cycle).build());

		return base()
				.fixed(fixed)
				.build();
	}

	public static LedgerEditResponse withPlace() {
		Place place = LedgerFixture.withPlace().build().getPlace();

		return base()
				.placeName(place.getName())
				.roadAddress(place.getRoadAddress())
				.detailAddress(place.getDetailAddress())
				.build();
	}

	public static LedgerEditResponse withImage() {
		List<ImageSlot> imageSlots = List.of(
				ImageSlotFixture.filledSlot("test.png"),
				ImageSlotFixture.emptySlot(),
				ImageSlotFixture.lockedSlot()
		);

		return base()
				.images(imageSlots)
				.build();
	}


	private static LedgerEditResponse.LedgerEditResponseBuilder base() {
		CategoryType type = CategoryType.INCOME;

		return LedgerEditResponse
				.builder()
				.date("2026년 01월 01일 목요일")
				.fixed(LedgerFixed.from(Ledger.builder().fix(FixedYN.VARIABLE).build()))
				.amount(10000L)
				.paymentType(AmountType.NONE)
				.type(type)
				.images(List.of(
						ImageSlotFixture.emptySlot(),
						ImageSlotFixture.lockedSlot(),
						ImageSlotFixture.lockedSlot()
				))
				.categoryEditInfo(
						CategoryEditInfo.builder()
								.selected(List.of("010100", "010101"))
								.middleOptions(
										CategoryItem.from(CategoryTreeFixture.middle().get(type))
								)
								.lowOptions(
										CategoryItem.from(CategoryTreeFixture.low().get(type))
								)
								.build()
				);
	}

}
