package com.moneymanager.support.fixture.response;

import com.moneymanager.ledger.domain.query.LedgerHistoryQuery;

import java.time.LocalDate;
import java.util.UUID;

public final class LedgerHistoryQueryTestFixture {

    private final String code = "code-" + UUID.randomUUID().toString().substring(0, 5);
    private LocalDate date = LocalDate.now();
    private Long amount = 10000L;
    private String memo;
    private String categoryName = "월급";
    private String categoryCode = "010101";

    private LedgerHistoryQueryTestFixture() {}

    public static LedgerHistoryQueryTestFixture builder() {
        return new LedgerHistoryQueryTestFixture();
    }

    public LedgerHistoryQueryTestFixture date(LocalDate date) {
        this.date = date;

        return this;
    }

    public LedgerHistoryQueryTestFixture amount(Long amount) {
        this.amount = amount;

        return this;
    }

    public LedgerHistoryQueryTestFixture categoryCode(String categoryCode) {
        this.categoryCode = categoryCode;

        return this;
    }

    public LedgerHistoryQuery build() {
        return LedgerHistoryQuery.of(
                code,
                date,
                categoryCode,
                categoryName,
                amount,
                memo
        );
    }

}