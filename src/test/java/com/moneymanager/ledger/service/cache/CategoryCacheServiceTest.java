package com.moneymanager.ledger.service.cache;

import com.moneymanager.global.exception.code.CategoryErrorCode;
import com.moneymanager.global.exception.exception.InternalException;
import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.ledger.repository.CategoryRepository;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.fixture.entity.category.CategoryFixture;
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

import static org.assertj.core.api.Assertions.*;
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
	@DisplayName("전체 카테고리를 조회할 때")
	class GetCache {

		@Nested
		@DisplayName("성공")
		class Success {

			@Test
			@DisplayName("데이터베이스에 저장된 카테고리가 있다면 Map으로 반환한다.")
			void returnsCategoryMap_whenCategoriesExist() {
				//given
				List<Category> incomeHierarchy = CategoryHierarchyFixture.incomeHierarchy();

				when(repository.findAllCategory())
						.thenReturn(incomeHierarchy);
				
				//when
				Map<String, Category> result = target.getCategoryMap();
				
				//then: 중복된 코드가 없고, 코드와 이름이 매칭이 잘 된다.
				assertThat(result)
						.hasSize(incomeHierarchy.size());

				assertThat(result.get(CategoryTestData.INCOME_CODE))
						.extracting(Category::getCode, Category::getName)
						.containsExactly(CategoryTestData.INCOME_CODE, CategoryTestData.INCOME_NAME);
			}
			
			@Test
			@DisplayName("categoryRepository를 한 번만 호출한다.")
			void validatesRepositoryCallCount_whenDataIsRequested() {
				//given
				Category top = CategoryFixture.income();
				Category middle = IncomeCategoryFixture.createMiddleAll().get(0);

				when(repository.findAllCategory()).thenReturn(
						List.of(
								top, middle
						)
				);

				//when
				target.getCategoryMap();
				
				//then: 카테고리 조회가 한 번 요청된다.
				verify(repository, times(1)).findAllCategory();
			}

		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("중복된 카테고리가 있다면 예외가 발생한다.")
			void throwsIllegalStateException_whenDuplicateCategoriesExist() {
				//given: DB에서 카테고리 조회하면 동일한 카테고리 코드가 존재한다.
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

			@Test
			@DisplayName("데이터베이스에 저장된 카테고리가 없다면 예외가 발생한다.")
			void throwInternalException_whenCategoriesDoNotExist() {
				//given
				when(repository.findAllCategory())
						.thenReturn(Collections.emptyList());

				//when & then
				ApplicationExceptionAssert.assertThatApplicationException(
						catchThrowable(() -> target.getCategoryMap())
				)
						.isInstanceOf(InternalException.class)
						.hasErrorCode(CategoryErrorCode.DATA_NOT_FOUND)
						.hasWork("전체 카테고리 조회")
						.hasCauseMessage("카테고리 없음");
			}
			
		}
		
	}

}