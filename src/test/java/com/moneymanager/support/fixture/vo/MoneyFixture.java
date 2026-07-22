package com.moneymanager.support.fixture.vo;

import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.Money;

public final class MoneyFixture {

	private MoneyFixture() {}

	public static Money from(LedgerUpdateRequest request) {
		return Money.of(
			request.getAmount(),
				PaymentType.from(request.getPaymentType())
		);
	}

}
