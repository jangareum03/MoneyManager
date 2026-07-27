package com.moneymanager.support.fixture.response;

import com.moneymanager.domain.ledger.dto.response.HistoryDashboardResponse;
import com.moneymanager.domain.ledger.dto.response.HistoryItem;
import com.moneymanager.domain.ledger.dto.response.LedgerStatistics;
import com.moneymanager.domain.ledger.dto.response.MenuItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class HistoryDashboardResponseFixture {

	private HistoryDashboardResponseFixture() {}

	public static HistoryDashboardResponse create() {
		return HistoryDashboardResponse.of(
			"제목",
			menu(),
			statistics(),
			historyGroups()
		);
	}

	public static HistoryDashboardResponse create(Map<String, List<HistoryItem>> historyGroups) {
		return HistoryDashboardResponse.of(
				"제목",
				menu(),
				statistics(),
				from(historyGroups)
		);
	}

	private static Map<LocalDate, List<HistoryItem>> historyGroups() {
		return Map.of(
			LocalDate.of(2026, 1, 1), List.of(HistoryItemFixture.income()),
			LocalDate.of(2026, 1, 2), List.of(HistoryItemFixture.outlay())
		);
	}

	private static Map<LocalDate, List<HistoryItem>> from(Map<String, List<HistoryItem>> historyGroups) {
		return historyGroups.entrySet().stream()
				.collect(Collectors.toMap(
						entry -> LocalDate.parse(entry.getKey()),
						Map.Entry::getValue
				));
	}

	private static List<MenuItem> menu() {
		return List.of(
				new MenuItem("메뉴1", "메뉴1"),
				new MenuItem("메뉴2", "메뉴2")
		);
	}

	private static LedgerStatistics statistics() {
		return LedgerStatistics.of(10000L, 5000L);
	}

}