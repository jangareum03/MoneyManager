package com.moneymanager.unit.fixture;

import com.moneymanager.domain.ledger.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class LedgerCategoryFixture {

	public static List<Category> allCategories() {
		List<Category> result = new ArrayList<>(topCategories());

		result.addAll(middleCategories());
		result.addAll(lowCategories());

		return result;
	}

	public static List<Category> topCategories() {
		return List.of(
				Category.builder().code("010000").name("수입").parentCode(null).build(),
				Category.builder().code("020000").name("지출").parentCode(null).build()
		);
	}

	public static List<Category> middleCategories() {
		List<Category> result = new ArrayList<>();

		result.addAll(middleCategoriesByIncome());
		result.addAll(middleCategoriesByOutlay());

		return result;
	}

	public static List<Category> middleCategoriesByIncome() {
		return List.of(
				Category.builder().code("010100").name("소득").parentCode("010000").build(),
				Category.builder().code("010200").name("저축").parentCode("010000").build(),
				Category.builder().code("010300").name("차입").parentCode("010000").build()
		);
	}

	public static List<Category> middleCategoriesByOutlay() {
		return List.of(
				Category.builder().code("020100").name("식비").parentCode("020000").build(),
				Category.builder().code("020500").name("교육").parentCode("020000").build(),
				Category.builder().code("020900").name("저축").parentCode("020000").build()
		);
	}

	public static List<Category> lowCategories() {
		List<Category> result = new ArrayList<>();

		result.addAll(lowCategoriesByIncome());
		result.addAll(lowCategoriesByOutlay());

		return result;
	}

	public static List<Category> lowCategoriesByIncome() {
		return List.of(
			Category.builder().code("010101").name("월급").parentCode("010100").build(),
			Category.builder().code("010103").name("알바").parentCode("010100").build(),
			Category.builder().code("010201").name("예금만기").parentCode("010200").build(),
			Category.builder().code("010301").name("빌린돈").parentCode("010300").build()
		);
	}

	public static List<Category> lowCategoriesByOutlay() {
		return List.of(
				Category.builder().code("020101").name("식재료").parentCode("020100").build(),
				Category.builder().code("020106").name("기타").parentCode("020100").build(),
				Category.builder().code("020501").name("학비").parentCode("020500").build(),
				Category.builder().code("020505").name("기타").parentCode("020500").build(),
				Category.builder().code("020901").name("예금").parentCode("020900").build(),
				Category.builder().code("020904").name("기타").parentCode("020900").build()
		);
	}
}
