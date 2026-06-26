package com.moneymanager.fixture.ledger;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class LedgerRequestTestData {

	public static Stream<Arguments> provideValidMemos() {
		return Stream.of(
				Arguments.of("1자", "가"),
				Arguments.of("50자", "A".repeat(50)),
				Arguments.of("75자", "!".repeat(75)),
				Arguments.of("150자", "가나다123 @!★".repeat(15))
		);
	}

	public static Stream<Arguments> provideValidPlaceNames() {
		return Stream.of(
				Arguments.of("1자", "a"),
				Arguments.of("50자", "CGV 강남점 1층".repeat(10)),
				Arguments.of("100자", "가".repeat(100))
		);
	}

	public static Stream<Arguments> provideValidRoadAddresses() {
		return Stream.of(
				Arguments.of("1자", "가"),
				Arguments.of("150자", "가1 ·-".repeat(30)),
				Arguments.of("300자", "가".repeat(300))
		);
	}

	public static Stream<Arguments> provideValidDetailAddresses() {
		return Stream.of(
				Arguments.of("1자", "가"),
				Arguments.of("250자", "가나 aA12 !-".repeat(25)),
				Arguments.of("500자", "가".repeat(500))
		);
	}

	public static Stream<Arguments> provideInvalidDates() {
		return Stream.of(
				Arguments.of("한글이 포함된 8자 날짜", "2026년 10"),
				Arguments.of("한자가 포함된 8자 날짜", "01月 01日"),
				Arguments.of("구분자가 포함된 8자 날짜", "26.01.01"),
				Arguments.of("7자 날짜", "2026111"),
				Arguments.of("9자 날짜", "202601010")
		);
	}

	public static Stream<Arguments> provideInvalidCategories() {
		return Stream.of(
				Arguments.of("한글이 포함된 6자 카테고리", "01010가"),
				Arguments.of("영문자가 포함된 6자 카테고리", "0201Aa"),
				Arguments.of("특수문자 포함된 6자 카테고리", "01010@"),
				Arguments.of("5자 카테고리", "01010"),
				Arguments.of("7자 카테고리", "0101010")
		);
	}

	public static Stream<Arguments> provideInvalidFixCycles() {
		return Stream.of(
				Arguments.of("한글로 1자인 고정주기", "월"),
				Arguments.of("한자로 1자인 고정주기", "月"),
				Arguments.of("숫자로 1자인 고정주기", "1"),
				Arguments.of("영문자로 2자인 고정주기", "we")
		);
	}

	public static Stream<Arguments> provideInvalidLimitMemos() {
		return Stream.of(
				Arguments.of("151자 메모", "가나다123 @!★".repeat(15) + "a"),
				Arguments.of("210자 메모", "가나다라마12345 @#%^ ☆♥ 家羅".repeat(10))
		);
	}

	public static Stream<Arguments> provideInvalidLimitPlaceName() {
		return Stream.of(
				Arguments.of("101자", "가".repeat(101)),
				Arguments.of("200자", "가".repeat(200))
		);
	}

	public static Stream<Arguments> provideInvalidCharacterPlaceNames() {
		return Stream.of(
				Arguments.of("특수문자 포함된 장소명", "서울타워★#"),
				Arguments.of("한자가 포함된 장소명", "김家네 김밥")
		);
	}

	public static Stream<Arguments> provideInvalidLimitRoadAddress() {
		return Stream.of(
				Arguments.of("301자", "가".repeat(301)),
				Arguments.of("500자", "가".repeat(500))
		);
	}

	public static Stream<Arguments> provideInvalidCharacterRoadAddresses() {
		return Stream.of(
				Arguments.of("영어가 포함된 기본주소", "abc아파트"),
				Arguments.of("특수문자 포함된 기본주소", "서울타워★#"),
				Arguments.of("한자가 포함된 기본주소", "김家네 김밥")
		);
	}

	public static Stream<Arguments> provideInvalidLimitDetailAddress() {
		return Stream.of(
				Arguments.of("501자", "가".repeat(501)),
				Arguments.of("700자", "가".repeat(700))
		);
	}

}
