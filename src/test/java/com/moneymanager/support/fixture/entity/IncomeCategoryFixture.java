package com.moneymanager.support.fixture.entity;

import com.moneymanager.domain.ledger.entity.Category;

import java.util.List;

public final class IncomeCategoryFixture {

	public static List<Category> createMiddleAll() {
		Category root = CategoryFixture.income();

		return List.of(
				CategoryFixture.create("010100", "중간1", root),
				CategoryFixture.create("010200", "중간2", root)
		);
	}

	public static List<Category> createLowAll() {
		Category root = CategoryFixture.income();

		Category mid1 = CategoryFixture.create("010100", "중간1", root);
		Category mid2 = CategoryFixture.create("010200", "중간2", root);

		Category low1 = CategoryFixture.create("010101", "하위1", mid1);
		Category low2 = CategoryFixture.create("010201", "하위2", mid2);
		Category low3 = CategoryFixture.create("010202", "하위3", mid2);

		return List.of(
				root,
				mid1, mid2,
				low1, low2, low3
		);
	}

}