package com.moneymanager.support.fixture.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedType;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class LedgerFixture {

	private Long id;                                            //가계부 번호(내부용)
	private String code;                      			      //가계부 코드(외부용)
	private String memberId;               		     //작성자(회원 고유번호)
	private LocalDate date;                 		 	 //거래 날짜
	private String category;                             //카테고리 코드
	private String memo;                                   //메모

	private Money money;                                //금액정보
	private Place place;                                    //장소

	private FixedType fix;                                 //고정여부
	private FixCycle fixCycle;                            //고정주기

	private LocalDateTime createdAt;            //등록일
	private LocalDateTime updatedAt;            //수정일

	private LedgerFixture() {
		this.id = 1L;
		this.code = LedgerTestData.CODE;
		this.memberId = MemberTestData.MEMBER_ID;
		this.date = LedgerTestData.LOCAL_DATE;
		this.category = CategoryTestData.SALARY_CODE;
		this.money = Money.of(LedgerTestData.AMOUNT, LedgerTestData.PAYMENT_TYPE.name());
		this.fix = LedgerTestData.FIX_N;
		this.createdAt = LedgerTestData.CREATED_DATE;
	}

	public static LedgerFixture builder() {
		return new LedgerFixture();
	}

	public LedgerFixture id(Long id) {
		this.id = id;

		return this;
	}

	public LedgerFixture code(String code) {
		this.code = code;

		return this;
	}

	public LedgerFixture memberId(String memberId) {
		this.memberId = memberId;

		return this;
	}

	public LedgerFixture  date(LocalDate date) {
		this.date = date;

		return this;
	}

	public LedgerFixture category(String category) {
		this.category = category;

		return this;
	}

	public LedgerFixture memo(String memo) {
		this.memo = memo;

		return this;
	}

	public LedgerFixture money(Long amount, PaymentType paymentType) {
		this.money = Money.of(amount, paymentType);

		return this;
	}

	public LedgerFixture place(String placeName, String roadAddress, String detailAddress) {
		this.place = Place.ofOrNull(placeName, roadAddress, detailAddress);

		return this;
	}

	public LedgerFixture fix(FixedType fix) {
		this.fix = fix;

		return this;
	}

	public Ledger saved() {
		return create(id, code, memberId);
	}

	public Ledger create() {
		return create(null, UlidCreator.getUlid().toString(), memberId);
	}


	//===== 유틸 메서드 =====
	private Ledger create(Long id, String code, String memberId) {
		String fixCycleValue = Ledger.getValueOrNull(fixCycle, FixCycle::getValue);

		return Ledger.create(
				id, code, memberId, date, category,
				fix.getValue(), fixCycleValue, memo, money, place,
				createdAt, updatedAt
		);
	}

}