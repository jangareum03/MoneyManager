package com.moneymanager.unit.fixture.ledger;


import com.moneymanager.domain.ledger.dto.response.HistoryDashboardResponse;
import com.moneymanager.domain.ledger.dto.response.HistoryItem;
import com.moneymanager.domain.ledger.dto.response.LedgerStatistics;
import com.moneymanager.domain.ledger.dto.response.MenuItem;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
public class HistoryDashboardResponseFixture {
	private String title;
	private List<MenuItem> menu;
	private LedgerStatistics statistics;
	private Map<LocalDate, List<HistoryItem>> data;

	public static HistoryDashboardResponseFixture defaultFixture() {
		return HistoryDashboardResponseFixture.builder()
				.title("제목")
				.menu(MenuItemFixture.create())
				.statistics(LedgerStatistics.of(0L, 0L))
				.data(Map.of())
				.build();
	}

	public HistoryDashboardResponse toResponse() {
		return HistoryDashboardResponse.of(
				title, menu, statistics, data
		);
	}
}
