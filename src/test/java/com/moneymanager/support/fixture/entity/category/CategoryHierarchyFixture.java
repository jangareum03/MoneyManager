package com.moneymanager.support.fixture.entity.category;

import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.support.data.CategoryTestData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class CategoryHierarchyFixture {

	public static Map<String, Category> buildFullCategoryMap() {
		Map<String, Category> categoryMap = new HashMap<String, Category>();

		List<Category> categories = new ArrayList<Category>();
		//최상위 카테고리
		categories.add(CategoryFixture.income());
		categories.add(CategoryFixture.outlay());

		//중간 카테고리
		categories.addAll(IncomeCategoryFixture.createMiddleAll());
		categories.addAll(OutlayCategoryFixture.createMiddleAll());

		//하위 카테고리
		categories.addAll(IncomeCategoryFixture.createLowAll());
		categories.addAll(OutlayCategoryFixture.createLowAll());

		return categories.stream()
						.collect(Collectors.toMap(
								Category::getCode,
								c -> c
						));
	}

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