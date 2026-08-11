package com.moneymanager.support.fixture.entity;

import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.FixedType;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;

public final class LedgerFixture {

	private LedgerFixture() {}

	public static Ledger.LedgerBuilder newLedger() {
		return builder().id(null);
	}

	public static Ledger.LedgerBuilder savedLedger(Long id) {
		return builder().id(id);
	}

	private static Ledger.LedgerBuilder builder() {
		return Ledger.builder()
				.code(LedgerTestData.CODE)
				.memberId(MemberTestData.MEMBER_ID)
				.date(LedgerTestData.LOCAL_DATE)
				.category(CategoryTestData.SALARY_CODE)
				.fix(FixedType.from(LedgerTestData.FIX_N))
				.money(Money.of(LedgerTestData.AMOUNT, PaymentType.from(LedgerTestData.PAYMENT_TYPE)));
	}

}