package com.moneymanager.ledger.service;

import com.moneymanager.ledger.service.cache.CategoryCacheService;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.fixture.entity.category.CategoryFixture;
import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.ledger.repository.CategoryRepository;
import com.moneymanager.support.fixture.entity.category.CategoryHierarchyFixture;
import com.moneymanager.support.fixture.entity.category.IncomeCategoryFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service<br>
 * 파일이름       : CategoryCacheServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 19<br>
 * 설명              : CategoryCacheService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 7. 19</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class CategoryCacheServiceTest {

	@InjectMocks
	private CategoryCacheService target;

	@Mock
	private CategoryRepository repository;

	@Nested
	@DisplayName("캐시 조회")
	class GetCache {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("조회 결과가 데이터가 존재하면 카테고리 정보가 담긴 Map이 반환된다.")
			void returnsCategoryMap_whenCategoriesExist() {
				//given: 여러 개의 카테고리 정보가 저장되어 있다.
				List<Category> incomeHierarchy = CategoryHierarchyFixture.incomeHierarchy();

				when(repository.findAllCategory())
						.thenReturn(incomeHierarchy);
				
				//when: 모든 카테고리를 조회한다.
				Map<String, Category> result = target.getCategoryMap();
				
				//then: 저장된 모든 카테고리가 반환된다.
				assertThat(result).hasSize(3);

				assertThat(result.get(CategoryTestData.INCOME_CODE)).isEqualTo(incomeHierarchy.get(0));
				assertThat(result.get(CategoryTestData.EARNED_CODE)).isEqualTo(incomeHierarchy.get(1));
				assertThat(result.get(CategoryTestData.SALARY_CODE)).isEqualTo(incomeHierarchy.get(2));
			}

			@Test
			@DisplayName("조회 결과가 1개의 데이터가 존재하면 Map이 반환된다.")
			void returnsCategoryMap_whenSingleCategoryExists() {
				//given: 카테고리가 1개 저장되어 있다.
				Category top = CategoryFixture.income();

				when(repository.findAllCategory()).thenReturn(
						List.of(top)
				);

				//when: 모든 카테고리를 조회한다.
				Map<String, Category> result = target.getCategoryMap();
				
				//then: 한 개의 카테고리가 반환된다.
				assertThat(result)
						.hasSize(1)
						.containsKeys(CategoryTestData.INCOME_CODE)
						.containsValue(top);
			}
			
			@Test
			@DisplayName("조회 결과가 비어있으면 빈 Map이 반환된다.")
			void returnsEmptyMap_whenCategoryIsEmpty() {
				//given: 조회 결과가 비어있으면 List로 반환되게 동작이 정의되어 있다.
				when(repository.findAllCategory())
						.thenReturn(Collections.emptyList());

				//when: 모든 카테고리를 조회한다.
				Map<String, Category> result = target.getCategoryMap();
				
				//then: 빈 Map이 반환된다.
				assertThat(result).isEmpty();
			}
			
			@Test
			@DisplayName("Repository가 정확히 한 번 호출된다.")
			void validatesRepositoryCallCount_whenDataIsRequested() {
				//given: 여러 개의 카테고리 정보가 저장되어 있다.
				Category top = CategoryFixture.income();
				Category middle = IncomeCategoryFixture.createMiddleAll().get(0);

				when(repository.findAllCategory()).thenReturn(
						List.of(
								top, middle
						)
				);

				//when: 모든 카테고리를 조회한다.
				target.getCategoryMap();
				
				//then: 카테고리 조회가 한 번 요청된다.
				verify(repository, times(1)).findAllCategory();
			}
			
		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("카테고리 코드가 중복되면 예외가 발생한다.")
			void throwsException_whenCategoryCodeIsDuplicate() {
				//given: 동일한 카테고리 코드가 2개가 저장되어 있다.
				Category category = CategoryFixture.income();

				when(repository.findAllCategory())
						.thenReturn(
								List.of(
										category, category
								)
						);
				
				//when & then: 모든 카테고리 조회 중 IllegalStateException이 발생한다.
				assertThatThrownBy(() -> target.getCategoryMap())
						.isInstanceOf(IllegalStateException.class);
			}
			
		}
		
	}

}