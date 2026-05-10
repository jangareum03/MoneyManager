package com.moneymanager.unit.service.ledger;

import com.moneymanager.BusinessExceptionAssert;
import com.moneymanager.domain.ledger.dto.response.CategoryItem;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.fixture.category.CategoryTreeFixture;
import com.moneymanager.service.ledger.CategoryCacheService;
import com.moneymanager.service.ledger.CategoryReadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.moneymanager.exception.error.ErrorCode.LEDGER_CATEGORY_TARGET_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

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

	@Spy
	@InjectMocks
	private CategoryReadService target;

	@Mock	private CategoryCacheService categoryCacheService;


	@Nested
	@DisplayName("유형과 레벨로 조회")
	class FindTypeAndLevelTest {

		@ParameterizedTest(name = "[{index}] level={0}, type={1}")
		@MethodSource("provideValidCategoryLevelByType")
		@DisplayName("카테고리 유형과 레벨에 따른 카테고리 목록을 조회한다.")
		void returnsCategoriesByTypeAndLevel_whenCategoriesExist(CategoryLevel level, CategoryType type, List<Category> expectedCategories) {
			//given
			when(categoryCacheService.getCategoryMap()).thenReturn(CategoryTreeFixture.toMap());

			//when
			List<CategoryItem> result = target.getCategoriesByTypeAndLevel(type, level);

			//then
			assertThat(result)
					.extracting(CategoryItem::getCode)
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
							CategoryTreeFixture.top()
					),
					Arguments.of(
							CategoryLevel.MIDDLE,
							CategoryType.INCOME,
							CategoryTreeFixture.middle().get(CategoryType.INCOME)
					),
					Arguments.of(
							CategoryLevel.MIDDLE,
							CategoryType.OUTLAY,
							CategoryTreeFixture.middle().get(CategoryType.OUTLAY)
					),
					Arguments.of(
							CategoryLevel.LOW,
							CategoryType.INCOME,
							CategoryTreeFixture.low().get(CategoryType.INCOME)
					),
					Arguments.of(
							CategoryLevel.LOW,
							CategoryType.OUTLAY,
							CategoryTreeFixture.low().get(CategoryType.OUTLAY)
					)
			);
		}

	}


	@Nested
	@DisplayName("부모 카테고리 조회")
	class FindParentCodeTest {

		@Test
		@DisplayName("유효한 카테고리 코드면 모든 부모 카테고리 목록을 반환한다.")
		void returnsParentCategories_whenCodeIsValid() {
			//given
			when(categoryCacheService.getCategoryMap()).thenReturn(CategoryTreeFixture.toMap());

			//when
			List<CategoryItem> result = target.findOrderedStepsByCategory("010101");

			//then
			assertThat(result).hasSize(3);

			assertThat(result.get(0))
					.extracting(CategoryItem::getName, CategoryItem::getCode)
					.containsExactly("수입", "010000");

			assertThat(result.get(1))
					.extracting(CategoryItem::getName, CategoryItem::getCode)
					.containsExactly("소득", "010100");

			assertThat(result.get(2))
					.extracting(CategoryItem::getName, CategoryItem::getCode)
					.containsExactly("월급", "010101");
		}

		@Test
		@DisplayName("부모 카테고리가 null이면 1개의 목록을 반환한다.")
		void returnsListWithNull_whenParentCodeIsNull() {
			//given
			Map<String, Category> map = new HashMap<>();
			map.put("010000", CategoryTreeFixture.top().get(CategoryType.INCOME));

			when(categoryCacheService.getCategoryMap()).thenReturn(map);

			//when
			List<CategoryItem> result = target.findOrderedStepsByCategory("010000");

			//when
			assertThat(result)
					.hasSize(1)
					.extracting(CategoryItem::getCode)
					.containsExactly("010000");
		}

		@Test
		@DisplayName("중간 카테고리 부모가 존재하지 않으면 BusinessException이 발생한다.")
		void throwsException_whenParentCodeDoesNotExists() {
			//given
			Category top = CategoryTreeFixture.top().get(CategoryType.OUTLAY);
			Category middle = Category.builder().parentCode("no-code").code("020100").name("임시").build();
			Category low = CategoryTreeFixture.low().get(CategoryType.OUTLAY).get(0);

			Map<String, Category> map = Map.of(
					"020000", top,
					"020100", middle,
					"020101", low
			);

			when(categoryCacheService.getCategoryMap()).thenReturn(map);

			//given
			assertThatThrownBy(() -> target.findOrderedStepsByCategory("020101"))
					.isInstanceOf(BusinessException.class);
		}

		@Test
		@DisplayName("없는 카테고리 코드면 BusinessException이 발생한다.")
		void throwsException_whenCodeDoesNotExists() {
			//given
			doThrow(BusinessException.of(LEDGER_CATEGORY_TARGET_NOT_FOUND, "카테고리 오류"))
					.when(target).getCategory("code");

			//when & then
			assertThatThrownBy(() -> target.getCategory("code"))
					.isInstanceOf(BusinessException.class);
		}

	}


	@Nested
	@DisplayName("code로 조회")
	class FindByCodeTest {

		@Test
		@DisplayName("유효한 카테고리 코드면 Category를 반환한다.")
		void returnsCategory_whenCodeIsValid() {
			//given
			Category category = CategoryTreeFixture.incomeSalary();

			when(categoryCacheService.getCategoryMap()).thenReturn(CategoryTreeFixture.toMap());

			//when
			Category result = target.getCategory("010101");

			//then
			assertThat(result).isSameAs(category);
		}

		@Test
		@DisplayName("없는 카테고리 코드면 이 발생한다.")
		void throwsException_whenCodeIsInvalid() {
			//given
			String code = "no-code";

			when(categoryCacheService.getCategoryMap()).thenReturn(CategoryTreeFixture.toMap());

			//when & then
			BusinessExceptionAssert.assertThatBusinessException(
					catchThrowable(() -> target.getCategory(code))
			)
					.hasErrorCode(LEDGER_CATEGORY_TARGET_NOT_FOUND)
					.hasUserMessage("않은 카테고리")
					.hasLogMessage("조회 실패", "code");
		}

	}

}
