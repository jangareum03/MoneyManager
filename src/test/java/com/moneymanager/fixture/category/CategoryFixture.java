package com.moneymanager.fixture.category;

import com.moneymanager.domain.ledger.entity.Category;

public class CategoryFixture {

	public static Category category(String code, String name, String parentCode) {
		return Category.builder()
				.code(code)
				.name(name)
				.parentCode(parentCode)
				.build();
	}

	public static Category top(String code, String name) {
		return category(code, name, null);
	}

	public static Category middle(String code, String name, Category parent) {
		return category(code, name, parent.getCode());
	}

	public static Category low(String code, String name, Category parent) {
		return category(code, name, parent.getCode());
	}

}
