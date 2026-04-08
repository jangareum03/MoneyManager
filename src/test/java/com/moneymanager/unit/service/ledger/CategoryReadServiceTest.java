package com.moneymanager.unit.service.ledger;

import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.repository.ledger.CategoryRepository;
import com.moneymanager.domain.ledger.dto.response.CategoryResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.service.ledger.CategoryReadService;
import com.moneymanager.unit.fixture.LedgerCategoryFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.ledger<br>
 * 파일이름       : CategoryReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 7<br>
 * 설명              : CategoryReadService 클래스 로직을 검증하는 테스트 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 * 		<thead>
 * 		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 * 		 	  	<td>날짜</td>
 * 		 	  	<td>작성자</td>
 * 		 	  	<td>변경내용</td>
 * 		 	</tr>
 * 		</thead>
 * 		<tbody>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 1. 7.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class CategoryReadServiceTest {

	@InjectMocks	private CategoryReadService service;

	@Mock				private CategoryRepository categoryRepository;

	//==================[ generateStoredName ]==================
	@Test
	@DisplayName("카테고리의 모든 정보를 조회한다.")
	void  getAllCategories_success(){
		//given
		List<Category> categories = List.of(
				Category.builder().code("010000").name("수입").parentCode(null).build()
		);
		when(categoryRepository.findAllCategory()).thenReturn(categories);

		//when
		service.getAllCategories();

		//then
		verify(categoryRepository, times(1)).findAllCategory();
	}


	@Disabled
	@Test
	@DisplayName("저장된 카테고리가 없으면 빈 리스트를 반환한다.")
	void getAllCategories_failure() {
		//given
		when(categoryRepository.findAllCategory()).thenReturn(Collections.emptyList());

		//when
		List<Category> result = service.getAllCategories();

		//then
		assertThat(result).isEmpty();
	}


	//==================[ getCategoriesByTypeAndLevel ]==================
	@ParameterizedTest(name = "[{index}] level={0}, type={1}")
	@MethodSource("provideValidCategoryLevelByType")
	@DisplayName("카테고리 유형과 레벨이 따른 카테고리 목록을 조회한다.")
	void getCategoriesByTypeAndLevel_success(CategoryLevel level, CategoryType type, List<Category> expectedCategories) {
		//given
		when(categoryRepository.findAllCategory()).thenReturn(LedgerCategoryFixture.allCategories());

		//when
		List<CategoryResponse> result = service.getCategoriesByTypeAndLevel(type, level);

		//then
		assertThat(result)
				.extracting(CategoryResponse::getCode)
				.containsExactlyElementsOf(
						expectedCategories.stream()
								.map(Category::getCode)
								.toList()
				);
	}

	static Stream<Arguments> provideValidCategoryLevelByType() {
		return Stream.of(
			Arguments.of(
					CategoryLevel.TOP,
					null,
					LedgerCategoryFixture.topCategories()
			),
			Arguments.of(
					CategoryLevel.MIDDLE,
					CategoryType.INCOME,
					LedgerCategoryFixture.middleCategoriesByIncome()
			),
			Arguments.of(
					CategoryLevel.MIDDLE,
					CategoryType.OUTLAY,
					LedgerCategoryFixture.middleCategoriesByOutlay()
			),
			Arguments.of(
					CategoryLevel.LOW,
					CategoryType.INCOME,
					LedgerCategoryFixture.lowCategoriesByIncome()
			),
			Arguments.of(
					CategoryLevel.LOW,
					CategoryType.OUTLAY,
					LedgerCategoryFixture.lowCategoriesByOutlay()
			)
		);
	}
}
