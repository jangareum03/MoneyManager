package com.moneymanager.support.fixture.entity.category;

import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.support.data.CategoryTestData;

import java.util.List;

public final class CategoryHierarchyFixture {

	public static List<Category> incomeHierarchy() {
		Category income = CategoryFixture.income();

		return List.of(
				income,
				CategoryFixture.create(CategoryTestData.EARNED_CODE, CategoryTestData.EARNED_NAME, income),
				CategoryFixture.salary()
		);
	}

	public static List<Category> outlayHierarchy() {
		Category outlay = CategoryFixture.outlay();

		return List.of(
				outlay,
				CategoryFixture.create(CategoryTestData.FOOD_CODE, CategoryTestData.FOOD_NAME, outlay),
				CategoryFixture.snack()
		);
	}

}