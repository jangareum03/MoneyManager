package com.moneymanager.unit.fixture;

import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.AmountType;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.vo.Place;


public class LedgerFixture {

	public static Ledger.LedgerBuilder defaultLedger() {
		String code = UlidCreator.getUlid().toString();

		return Ledger.builder()
				.code(code)
				.memberId("test")
				.date("20260101")
				.category("010101")
				.fix(FixedYN.VARIABLE)
				.amount(10000L)
				.amountType(AmountType.NONE);
	}

	public static Ledger.LedgerBuilder withPlace() {
		Place place = new Place("CGV 강남점", "서울특별시 강남구 강남대로 438 스타플렉스", "4층");

		return defaultLedger()
				.place(place);
	}
}
