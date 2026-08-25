package com.moneymanager.support.fixture.entity;

import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.FixedType;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public final class LedgerTestFixture {

	private String code = "ledger-" + UUID.randomUUID().toString().substring(0, 15);
	private String memberId = MemberTestData.DEFAULT_ID;
	private LocalDate date = LedgerTestData.DEFAULT_LOCAL_DATE;
	private FixedType fix = LedgerTestData.DEFAULT_FIX;
	private String category = LedgerTestData.DEFAULT_CATEGORY;
	private Money money = Money.of(LedgerTestData.DEFAULT_AMOUNT, LedgerTestData.DEFAULT_PAYMENT_TYPE);
	private Place place = null;
	private final LocalDateTime createdAt = LedgerTestData.DEFAULT_CREATED_DATE;

	private Long id;
	private String memo;

	private LedgerTestFixture() {}

	public static LedgerTestFixture builder() {
		return new LedgerTestFixture();
	}

	public LedgerTestFixture code(String code) {
		this.code = code;

		return this;
	}

	public LedgerTestFixture memberId(String memberId) {
		this.memberId = memberId;

		return this;
	}

	public LedgerTestFixture date(LocalDate date) {
		this.date = date;

		return this;
	}

	public LedgerTestFixture category(String category) {
		this.category = category;

		return this;
	}

	public LedgerTestFixture money(Long money, PaymentType paymentType) {
		this.money = Money.of(money, paymentType);

		return this;
	}

	public LedgerTestFixture memo(String memo) {
		this.memo = memo;

		return this;
	}

	public LedgerTestFixture withMemo() {
		this.memo = LedgerTestData.MEMO;

		return this;
	}

	public LedgerTestFixture withPlace() {
		this.place = Place.ofOrNull(LedgerTestData.PLACE_NAME, LedgerTestData.ROAD_ADDRESS, LedgerTestData.DETAIL_ADDRESS);

		return this;
	}

	public Ledger build() {
		return Ledger.of(
			code, memberId, date, category, fix, null, memo,
			money, place
		);
	}

	public Ledger buildExisting(Long id, String code) {
		return Ledger.restore(
				id, code, memberId, date, category, fix, null, memo,
				money, place, createdAt, null
		);
	}

}