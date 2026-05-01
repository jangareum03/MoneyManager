package com.moneymanager.fixture.history;

import com.moneymanager.domain.ledger.dto.response.MenuItem;
import com.moneymanager.domain.ledger.enums.HistoryMenuType;

import java.util.List;

public class MenuItemFixture {

	public static List<MenuItem> create() {
		return List.of(
				create("전체", HistoryMenuType.ALL.name()),
				create("수입/지출", HistoryMenuType.CATEGORY.name()),
				create("카테고리", HistoryMenuType.SUB_CATEGORY.name()),
				create("메모", HistoryMenuType.MEMO.name()),
				create("기간", HistoryMenuType.DATE.name())
		);
	}

	private static MenuItem create(String label, String value) {
		return new MenuItem(label, value);
	}

}
