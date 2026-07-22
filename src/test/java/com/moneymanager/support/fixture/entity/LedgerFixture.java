package com.moneymanager.support.fixture.entity;

import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.Money;
import com.moneymanager.domain.ledger.vo.Place;

public final class LedgerFixture {

	private LedgerFixture() {}

	public static Ledger.LedgerBuilder builder() {
		return Ledger.builder()
				.code(LedgerTestData.CODE)
				.memberId(MemberTestData.MEMBER_ID)
				.date(LedgerTestData.LOCAL_DATE)
				.category(CategoryTestData.EARNED_CODE)
				.fix(FixedYN.from(LedgerTestData.FIX_N))
				.money(Money.of(LedgerTestData.AMOUNT, PaymentType.from(LedgerTestData.PAYMENT_TYPE)));
	}

	public static Ledger.LedgerBuilder builderWithPlace() {
		return builder()
				.place(
						Place.of(
								LedgerTestData.PLACE_NAME,
								LedgerTestData.ROAD_ADDRESS,
								LedgerTestData.DETAIL_ADDRESS
						)
				);
	}

	public static Ledger newLedger() {
		return builder().id(null).build();
	}

	public static Ledger newLedger(String memberId) {
		return builder()
				.id(null)
				.memberId(memberId)
				.build();
	}

	public static Ledger newLedger(String memberId, String code) {
		return builder()
				.id(null)
				.code(code)
				.memberId(memberId)
				.build();
	}

	public static Ledger savedLedger() {
		return builder().id(1L).build();
	}

}
