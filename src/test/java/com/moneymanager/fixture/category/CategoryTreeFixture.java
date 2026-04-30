package com.moneymanager.fixture.category;

import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.enums.CategoryType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryTreeFixture {

	private static final List<Category> ALL = create();

	private static List<Category> create() {
		//top
		Category income = CategoryFixture.top("010000", "수입");
		Category outlay = CategoryFixture.top("020000", "지출");

		//middle
		Category incomeMid1 = CategoryFixture.middle("010100", "소득", income);
		Category incomeMid2 = CategoryFixture.middle("010200", "저축", income);
		Category incomeMid3 = CategoryFixture.middle("010300", "차입", income);

		Category outlayMid1 = CategoryFixture.middle("020100", "식비", outlay);
		Category outlayMid2 = CategoryFixture.middle("020500", "교육", outlay);
		Category outlayMid3 = CategoryFixture.middle("020900", "저축", outlay);

		//low
		Category incomeLow1 = CategoryFixture.low("010101", "월급", incomeMid1);
		Category incomeLow2 = CategoryFixture.middle("010201", "예금만기", incomeMid2);
		Category incomeLow3 = CategoryFixture.middle("010301", "빌린돈", incomeMid3);

		Category outlayLow1 = CategoryFixture.low("020101", "식재료", outlayMid1);
		Category outlayLow2 = CategoryFixture.low("020106", "기타", outlayMid1);
		Category outlayLow3 = CategoryFixture.low("020501", "학비", outlayMid2);
		Category outlayLow4 = CategoryFixture.low("020505", "기타", outlayMid2);
		Category outlayLow5 = CategoryFixture.low("020901", "예금", outlayMid3);
		Category outlayLow6 = CategoryFixture.low("020904", "기타", outlayMid3);

		return List.of(
				income, outlay,
				incomeMid1, incomeMid2, incomeMid3,
				outlayMid1, outlayMid2, outlayMid3,
				incomeLow1, incomeLow2, incomeLow3,
				outlayLow1, outlayLow2, outlayLow3, outlayLow4, outlayLow5, outlayLow6
		);
	}

	public static List<Category> all() {
		return ALL;
	}

	public static Map<String, Category> toMap() {
		return ALL.stream()
				.collect(Collectors.toMap(
						Category::getCode,
						c -> c
				));
	}

	public static List<Category> top() {
		return ALL.stream()
				.filter(c -> c.getParentCode() == null)
				.toList();
	}

	public static Map<CategoryType, List<Category>> middle() {
		return ALL.stream()
				.filter(c -> c.getCode().endsWith("00") && !c.getCode().endsWith("0000"))
				.collect(Collectors.groupingBy(
						c -> c.getCode().startsWith("01") ? CategoryType.INCOME : CategoryType.OUTLAY
				));
	}

	public static Map<CategoryType, List<Category>> low() {
		return ALL.stream()
				.filter(c -> !c.getCode().endsWith("00"))
				.collect(Collectors.groupingBy(
						c -> c.getCode().startsWith("01") ? CategoryType.INCOME : CategoryType.OUTLAY
				));
	}

	public static Category getByCode(String code) {
		return ALL.stream()
				.filter(c -> c.getCode().equals(code))
				.findFirst()
				.orElseThrow();
	}

	public static Category incomeSalary() {
		return getByCode("010101");
	}

	public static Category outlayFood() {
		return getByCode("020101");
	}
}
