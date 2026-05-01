package com.moneymanager.fixture.history;


import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.domain.ledger.dto.query.LedgerHistoryQuery;
import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public class LedgerHistoryQueryFixture {
	private String code;
	private LocalDate date;
	private Long amount;
	private String memo;
	private String categoryName;
	private String categoryCode;

	public static LedgerHistoryQueryFixture defaultFixture() {
		return LedgerHistoryQueryFixture.builder()
				.code(UlidCreator.getUlid().toString())
				.date(LocalDate.now())
				.amount(10000L)
				.categoryName("월급")
				.categoryCode("010101")
				.build();
	}

	public LedgerHistoryQuery build() {
		return new LedgerHistoryQuery(code, date, amount, memo, categoryName, categoryCode);
	}
}
