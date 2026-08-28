package com.moneymanager.support.fixture.response;

import com.moneymanager.ledger.domain.dto.response.item.HistoryItem;
import com.moneymanager.ledger.domain.enums.LedgerType;
import com.moneymanager.ledger.domain.query.LedgerHistoryQuery;

import java.time.LocalDate;
import java.util.UUID;

public final class HistoryItemTestFixture {

    private final String code = "code-" + UUID.randomUUID().toString().substring(0, 5);
    private Long amount = 10000L;
    private String memo;
    private LedgerType categoryType = LedgerType.INCOME;
    private String categoryName = "월급";

    private HistoryItemTestFixture() {}

    public static HistoryItemTestFixture builder() {
        return new HistoryItemTestFixture();
    }

    public HistoryItemTestFixture categoryName(String categoryName) {
        this.categoryName = categoryName;

        return this;
    }

    public HistoryItem build(LocalDate date, String categoryCode) {
        return HistoryItem.from(
                LedgerHistoryQuery.of(
                        code,
                        date,
                        categoryCode,
                        categoryName,
                        amount,
                        memo
                )
        );
    }

}