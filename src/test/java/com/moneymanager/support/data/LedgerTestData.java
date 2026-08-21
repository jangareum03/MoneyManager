package com.moneymanager.support.data;

import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedType;
import com.moneymanager.ledger.domain.enums.PaymentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LedgerTestData {

	public static final String CODE = "ABCDE-abcde-1234567890cOdE";
	public static final String DATE = "20260101";
	public static final Long AMOUNT = 10000L;
	public static final PaymentType PAYMENT_TYPE = PaymentType.NONE;
	public static final FixedType FIX_N = FixedType.VARIABLE;
	public static final FixedType FIX_Y = FixedType.REPEAT;
	public static final FixCycle FIX_CYCLE = FixCycle.MONTHLY;

	public static final String MEMO = "메모";

	public static final String PLACE_NAME = "CGV 강남점";
	public static final String ROAD_ADDRESS = "서울특별시 강남구 강남대로 438 스타플렉스";
	public static final String DETAIL_ADDRESS = "4층";

	public static final LocalDate LOCAL_DATE = LocalDate.parse(DATE, DateTimeFormatter.ofPattern("yyyyMMdd"));
	public static final LocalDateTime CREATED_DATE = LocalDateTime.of(2026, 5, 1, 9, 30);
	public static final LocalDateTime UPDATED_DATE = LocalDateTime.of(2026, 11, 11, 15, 10);

}