package com.moneymanager.support.fixture.entity.category;

import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.support.data.CategoryTestData;

public final class CategoryFixture {

	public static Category income() {
		return create(CategoryTestData.INCOME_CODE, CategoryTestData.INCOME_NAME, null);
	}

	public static Category outlay() {
		return create(CategoryTestData.OUTLAY_CODE, CategoryTestData.OUTLAY_NAME, null);
	}

	public static Category salary() {
		return create(
				CategoryTestData.SALARY_CODE,
				CategoryTestData.SALARY_NAME,
				income()
		);
	}

	public static Category snack() {
		return create(
				CategoryTestData.SNACK_CODE,
				CategoryTestData.SNACK_NAME,
				outlay()
		);
	}

	static Category create(String code, String name, Category parent) {
		if(parent == null) {
			return Category.topCategory(code, name);
		}

		return Category.childCategory(code, name, parent);
	}

}