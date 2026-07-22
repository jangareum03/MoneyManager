package com.moneymanager.ledger.domain.dto.response;

import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.domain.ledger.dto.response.CategoryItem;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.support.fixture.entity.CategoryFixture;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto<br>
 * 파일이름       : CategoryItemTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 23.<br>
 * 설명              : CategoryItem 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 6. 23.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class CategoryItemTest {

	@Nested
	@DisplayName("CategoryItem 변환")
	class FromTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("Category 리스트는 CategoryItem 리스트로 변환한다.")
			void returnsCategoryItems_whenCategoriesExist() {
				//given: 변환할 카테고리 리스트를 준비한다.
				Category top = CategoryFixture.top();
				Category middle = CategoryFixture.middle(top);
				Category low = CategoryFixture.low(middle);

				List<Category> categories = List.of(
						top, middle, low
				);

				//when: CategoryItem으로 변환한다.
				List<CategoryItem> result = CategoryItem.from(categories);
				
				//then: 요청한 카테고리 이름과 코드가 일치한다.
				assertThat(result)
						.hasSize(3)
						.extracting(
								CategoryItem::getName,
								CategoryItem::getCode
						).containsExactly(
							Tuple.tuple(CategoryTestData.INCOME_NAME, CategoryTestData.INCOME_CODE),
							Tuple.tuple(CategoryTestData.EARNED_NAME, CategoryTestData.EARNED_CODE),
							Tuple.tuple(CategoryTestData.SALARY_NAME, CategoryTestData.SALARY_CODE)
						);
			}
			
			@ParameterizedTest
			@MethodSource("provideValidCategories")
			@DisplayName("Category가 CategoryItem으로 변환한다.")
			void returnsCategoryItem_whenCategoryIsGiven(Category category) {
				//when: CategoryItem으로 변환한다.
				CategoryItem result = CategoryItem.from(category);

				//then: 요청한 카테고리 이름과 코드가 일치한다.
				assertThat(result)
						.extracting(
								CategoryItem::getCode,
								CategoryItem::getName
						)
						.containsExactly(
								category.getCode(),
								category.getName()
						);
			}

			private static Stream<Arguments> provideValidCategories() {
				Category top = CategoryFixture.top();
				Category middle = CategoryFixture.middle(top);
				Category low = CategoryFixture.low(middle);

				return Stream.of(
						Arguments.of(named("상위 카테고리인 경우", top)),
						Arguments.of(named("중간 카테고리인 경우", middle)),
						Arguments.of(named("하위 카테고리인 경우", low))
				);
			}
			
			@Test
			@DisplayName("부모 카테고리가 null이여도 CategoryItem으로 변환한다.")
			void returnsCategoryItem_whenParentCategoryIsNull() {
				//given: 부모 카테고리가 null인 Category를 준비한다.
				Category category = CategoryFixture.top();

				//when: CategoryItem으로 변환한다.
				CategoryItem result = CategoryItem.from(category);

				//then: 요청한 카테고리 이름과 코드가 일치한다.
				assertThat(result)
						.extracting(
								CategoryItem::getCode,
								CategoryItem::getName
						)
						.containsExactly(
								CategoryTestData.INCOME_CODE,
								CategoryTestData.INCOME_NAME
						);
			}

		}


		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("Category 리스트 중 null이 포함되어 있으면 예외가 발생한다.")
			void throwsException_whenCategoryListContainsNull() {
				//given: null을 포함하는 Category 리스트를 준비한다.
				List<Category> categories = new ArrayList<>();

				categories.add(CategoryFixture.top());
				categories.add(null);

				//when & then: CategoryItem으로 변환 중 예외가 발생한다.
				assertThatThrownBy(() -> CategoryItem.from(categories))
						.isInstanceOf(NullPointerException.class);
			}
			
			@ParameterizedTest
			@NullSource
			@DisplayName("Category가 null이면 예외가 발생한다.")
			void throwsException_whenCategoryIsNull(Category category) {
				//when & then: CategoryItem으로 변환 중 예외가 발생한다.
				assertThatThrownBy(() -> CategoryItem.from(category))
						.isInstanceOf(NullPointerException.class);
			}

		}

	}

}
