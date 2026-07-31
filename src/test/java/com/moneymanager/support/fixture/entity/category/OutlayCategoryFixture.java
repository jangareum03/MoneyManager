package com.moneymanager.support.fixture.entity.category;

import com.moneymanager.ledger.domain.entity.Category;

import java.util.List;

public class OutlayCategoryFixture {

	public static List<Category> createMiddleAll() {
		Category root = CategoryFixture.outlay();

		return List.of(
				CategoryFixture.create("020100", "중간1", root),
				CategoryFixture.create("020200", "중간2", root),
				CategoryFixture.create("020300", "중간3", root)
		);
	}

	public static List<Category> createLowAll() {
		Category root = CategoryFixture.outlay();

		Category mid1 = CategoryFixture.create("020100", "중간1", root);
		Category mid2 = CategoryFixture.create("020200", "중간2", root);
		Category mid3 = CategoryFixture.create("020300", "중간3", root);

		Category low1 = CategoryFixture.create("020101", "하위1", mid1);
		Category low2 = CategoryFixture.create("020201", "하위2", mid2);
		Category low3 = CategoryFixture.create("020202", "하위3", mid2);
		Category low4 = CategoryFixture.create("020301", "하위4", mid3);
		Category low5 = CategoryFixture.create("020302", "하위5", mid3);
		Category low6 = CategoryFixture.create("020303", "하위6", mid3);

		return List.of(
				low1, low2, low3, low4, low5, low6
		);
	}

}