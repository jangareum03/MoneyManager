package com.moneymanager.support.fixture.vo;

import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.domain.dto.vo.Money;

public final class MoneyFixture {

	private MoneyFixture() {}

	public static Money from(LedgerUpdateRequest request) {
		return Money.of(
			request.getAmount(),
				PaymentType.from(request.getPaymentType())
		);
	}

}
