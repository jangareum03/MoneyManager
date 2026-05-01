package com.moneymanager.fixture.history;

import com.moneymanager.domain.ledger.dto.response.HistoryItem;
import com.moneymanager.domain.ledger.enums.CategoryType;
import lombok.Builder;

@Builder(toBuilder = true)
public class HistoryItemFixture {
	private String code;
	private String amount;
	private String memo;
	private CategoryType categoryType;
	private String categoryCode;
	private String categoryName;

	public static HistoryItemFixture defaultFixture() {
		return HistoryItemFixture.builder()
				.build();
	}

	public HistoryItem toHistoryItem() {
		LedgerHistoryQueryFixture.LedgerHistoryQueryFixtureBuilder queryFixtureBuilder = LedgerHistoryQueryFixture.defaultFixture()
				.toBuilder()
				.amount(amount != null ? Long.parseLong(amount) : 0L)
				.memo(memo)
				.categoryName(categoryName);

		if(categoryCode != null) {
			queryFixtureBuilder.categoryCode(categoryCode);
		}


		return HistoryItem.from(queryFixtureBuilder.build().build());
	}

}
