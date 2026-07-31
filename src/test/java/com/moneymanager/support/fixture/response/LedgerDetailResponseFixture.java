package com.moneymanager.support.fixture.response;

import com.moneymanager.ledger.domain.dto.response.CategoryItem;
import com.moneymanager.ledger.domain.dto.response.LedgerDetailResponse;
import com.moneymanager.ledger.domain.enums.CategoryType;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.fixture.entity.category.CategoryFixture;

import java.util.Collections;
import java.util.List;

public final class LedgerDetailResponseFixture {

	public static LedgerDetailResponse create() {
		return LedgerDetailResponse.builder()
				.date("날짜")
				.type(CategoryType.INCOME)
				.category(CategoryItem.from(CategoryFixture.salary()))
				.memo(null)
				.images(Collections.emptyList())
				.amount(1L)
				.paymentType(PaymentType.NONE)
				.placeName(null)
				.roadAddress(null)
				.detailAddress(null)
				.build();
	}

	public static LedgerDetailResponse withPlace() {
		return LedgerDetailResponse.builder()
				.date("날짜")
				.type(CategoryType.INCOME)
				.category(CategoryItem.from(CategoryFixture.salary()))
				.memo(null)
				.images(Collections.emptyList())
				.amount(1L)
				.paymentType(PaymentType.NONE)
				.placeName(LedgerTestData.PLACE_NAME)
				.roadAddress(LedgerTestData.ROAD_ADDRESS)
				.detailAddress(LedgerTestData.DETAIL_ADDRESS)
				.build();
	}

	public static LedgerDetailResponse withImage() {
		return LedgerDetailResponse.builder()
				.date("날짜")
				.type(CategoryType.INCOME)
				.category(CategoryItem.from(CategoryFixture.salary()))
				.memo(null)
				.images(List.of(
						"이미지", "비어있음", "잠김"
				))
				.amount(1L)
				.paymentType(PaymentType.NONE)
				.placeName(null)
				.roadAddress(null)
				.detailAddress(null)
				.build();
	}

	public static LedgerDetailResponse withImages() {
		return LedgerDetailResponse.builder()
				.date("날짜")
				.type(CategoryType.INCOME)
				.category(CategoryItem.from(CategoryFixture.salary()))
				.memo(null)
				.images(List.of(
						"이미지", "이미지", "잠김"
				))
				.amount(1L)
				.paymentType(PaymentType.NONE)
				.placeName(null)
				.roadAddress(null)
				.detailAddress(null)
				.build();
	}

}