package com.moneymanager.support.fixture.response;

import com.moneymanager.ledger.domain.dto.response.LedgerTypeResponse;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep1Response;

import java.util.List;
import java.util.stream.IntStream;

public final class LedgerWriteStep1ResponseFixture {

	private LedgerWriteStep1ResponseFixture() {}

	public static LedgerWriteStep1Response create() {
		return LedgerWriteStep1Response.builder()
				.types(createTypes())
				.years(createYears())
				.months(createMonths())
				.days(createDays())
				.currentYear(2026)
				.currentMonth(1)
				.currentDay(1)
				.displayDate("2026-01-01")
				.build();
	}

	private static List<LedgerTypeResponse> createTypes() {
		return LedgerTypeResponse.fromEnum();
	}

	private static List<Integer> createYears() {
		return List.of(2026, 2025, 2024);
	}

	private static List<Integer> createMonths() {
		return IntStream.rangeClosed(1, 12)
				.boxed().toList();
	}

	private static List<Integer> createDays() {
		return IntStream.rangeClosed(1, 30)
				.boxed().toList();
	}

}