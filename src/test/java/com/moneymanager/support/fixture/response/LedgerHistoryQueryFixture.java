package com.moneymanager.support.fixture.response;

import com.moneymanager.ledger.domain.query.LedgerHistoryQuery;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;

import java.time.LocalDate;

public class LedgerHistoryQueryFixture {

	public static LedgerHistoryQuery incomeHistory(LocalDate date, int amount) {
		return create(date, (long) amount, CategoryTestData.SALARY_NAME, CategoryTestData.SALARY_CODE);
	}

	public static LedgerHistoryQuery outlayHistory(LocalDate date, int amount) {
		return create(date, (long) amount, CategoryTestData.SNACK_NAME, CategoryTestData.SNACK_CODE);
	}

	public static LedgerHistoryQuery create() {
		return create(LedgerTestData.LOCAL_DATE, LedgerTestData.AMOUNT, CategoryTestData.SALARY_NAME, CategoryTestData.SALARY_CODE);
	}

	private static LedgerHistoryQuery create(LocalDate date, Long amount, String categoryName, String categoryCode) {
		return new LedgerHistoryQuery(
				LedgerTestData.CODE,
				date,
				amount,
				LedgerTestData.MEMO,
				categoryName,
				categoryCode
		);
	}

}