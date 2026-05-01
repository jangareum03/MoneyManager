package com.moneymanager.fixture.ledger;

import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.AmountType;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.fixture.category.CategoryTreeFixture;

import java.time.LocalDate;

public class LedgerFixture {

	private static final String DEFAULT_CODE = "test-code";
	private static final String DEFAULT_MEMBER_ID = "test-mid";

	public static Ledger.LedgerBuilder defaultLedger() {
		return Ledger.builder()
				.id(1L)
				.code(DEFAULT_CODE)
				.memberId(DEFAULT_MEMBER_ID)
				.date(LocalDate.of(2026, 1, 1))
				.category(CategoryTreeFixture.incomeSalary().getCode())
				.fix(FixedYN.VARIABLE)
				.amount(10000L)
				.amountType(AmountType.NONE);
	}

	public static Ledger create() {
		return defaultLedger().build();
	}

	public static Ledger.LedgerBuilder withPlace() {
		Place place = new Place("CGV 강남점", "서울특별시 강남구 강남대로 438 스타플렉스", "4층");

		return defaultLedger()
				.place(place);
	}

}
