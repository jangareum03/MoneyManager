package com.moneymanager.support.fixture.response;

import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.domain.ledger.dto.query.LedgerHistoryQuery;
import com.moneymanager.domain.ledger.dto.response.HistoryItem;

import java.time.LocalDate;

public class HistoryItemFixture {

	public static HistoryItem income() {
		return create(50000L, "메모", CategoryTestData.SALARY_NAME, CategoryTestData.SALARY_CODE);
	}

	public static HistoryItem outlay() {
		return create(10000L, "메모", CategoryTestData.FOOD_NAME, CategoryTestData.FOOD_CODE);
	}

	public static HistoryItem create(Long amount, String memo, String categoryName, String categoryCode) {
		return HistoryItem.from(
				new LedgerHistoryQuery(
						LedgerTestData.CODE,
						LocalDate.of(2026, 1, 1),
						amount,
						memo,
						categoryName,
						categoryCode
				)
		);
	}

}
