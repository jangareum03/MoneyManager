package com.moneymanager.support.fixture.entity;

import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.domain.ledger.entity.Category;

import java.util.Map;

public class CategoryFixture {

	public static Category top() {
		return Category.topCategory(CategoryTestData.INCOME_CODE, CategoryTestData.INCOME_NAME);
	}

	public static Category middle(Category parent) {
		return Category.childCategory(CategoryTestData.EARNED_CODE, CategoryTestData.EARNED_NAME, parent);
	}

	public static Category low(Category parent) {
		return Category.childCategory(CategoryTestData.SALARY_CODE, CategoryTestData.SALARY_NAME, parent);
	}

	public static Category.CategoryBuilder builder() {
		return Category.builder()
				.code(CategoryTestData.INCOME_CODE)
				.name(CategoryTestData.INCOME_NAME);
	}

	public static Map<String, Category> hierarchyMap() {
		Category top = top();
		Category middle = middle(top);
		Category low = low(middle);

		return Map.of(
				top.getCode(), top,
				middle.getCode(), middle,
				low.getCode(), low
		);
	}

}
