package com.moneymanager.ledger.service;

import com.moneymanager.domain.ledger.dto.response.CategoryItem;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.exception.code.CategoryErrorCode;
import com.moneymanager.exception.exception.BusinessException;
import com.moneymanager.service.ledger.CategoryCacheService;
import com.moneymanager.service.ledger.CategoryReadService;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.fixture.entity.CategoryFixture;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service<br>
 * 파일이름       : CategoryReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 20<br>
 * 설명              : CategoryReadService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 7. 20</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class CategoryReadServiceTest {

	@InjectMocks
	private CategoryReadService target;

	@Mock
	private CategoryCacheService categoryCacheService;


	@Nested
	@DisplayName("최상위 카테고리 조회")
	class GetRootCategory {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("최상위 카테고리만 반환된다.")
			void returnsRootCategories_whenRootCategoriesExist() {
				//given: 캐시에서 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				Map<String, Category> categoryMap = new java.util.HashMap<>(CategoryFixture.hierarchyMap());
				categoryMap.put(
						CategoryTestData.OUTLAY_CODE,
						CategoryFixture.builder().code(CategoryTestData.OUTLAY_CODE).name("지출").build()
				);

				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);
				
				//when: 최상위 카테고리를 조회한다.
				List<CategoryItem> result = target.getRootCategories();
				
				//then: 최상위 카테고리만 반환된다.
				assertThat(result).hasSize(2);
				assertThat(result)
						.extracting(CategoryItem::getCode)
						.allSatisfy(code -> {
							assertThat(code).hasSize(6);
							assertThat(code.endsWith("0000")).isTrue();
						});
			}

			@Test
			@DisplayName("최상위 카테고리가 한 개만 있어도 반환된다.")
			void returnsRootCategories_whenSingleRootCategoryExists() {
				//given: 캐시에서 한 개의 카테고리만 반환되도록 동작이 정의되어 있다.
				Category category = CategoryFixture.top();

				when(categoryCacheService.getCategoryMap())
						.thenReturn(Map.of(
								category.getCode(), category
						));

				//when: 최상위 카테고리를 조회한다.
				List<CategoryItem> result = target.getRootCategories();
				
				//then: 저장된 카테고리 한 개가 반환된다.
				assertThat(result).hasSize(1);
				assertThat(result)
						.extracting(
								CategoryItem::getCode,
								CategoryItem::getName
						)
						.containsExactly(
								Tuple.tuple(CategoryTestData.INCOME_CODE, CategoryTestData.INCOME_NAME)
						);
			}

			@Test
			@DisplayName("카테고리 코드 오름차순으로 정렬된다.")
			void sortsCategoriesByCodeAscending_whenCategoriesExist() {
				//given: 캐시에서 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				Map<String, Category> categoryMap = new java.util.HashMap<>(CategoryFixture.hierarchyMap());
				categoryMap.put(
						CategoryTestData.OUTLAY_CODE,
						CategoryFixture.builder().code(CategoryTestData.OUTLAY_CODE).name("지출").build()
				);

				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 최상위 카테고리를 조회한다.
				List<CategoryItem> result = target.getRootCategories();
				
				//then: 카테고리 코드가 오름차순으로 정렬되어 반환된다.
				assertThat(result)
						.extracting(CategoryItem::getCode)
						.containsExactly(
								CategoryTestData.INCOME_CODE, CategoryTestData.OUTLAY_CODE
						);
			}

			@Test
			@DisplayName("캐시의 저장된 순서와 관계없이 코드 기준으로 정렬하여 반환된다.")
			void validatesSortDiscrepancy_whenCacheSortIsDifferent() {
				//given: 정렬되지 않은 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				Map<String, Category> categoryMap = new LinkedHashMap<>();

				categoryMap.put(
						CategoryTestData.OUTLAY_CODE,
						CategoryFixture.builder().code(CategoryTestData.OUTLAY_CODE).name("지출").build()
				);
				categoryMap.put(
						CategoryTestData.INCOME_CODE,
						CategoryFixture.top()
				);

				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 최상위 카테고리를 조회한다.
				List<CategoryItem> result = target.getRootCategories();
				
				//then: 캐시에 정렬된 순서와 리스트에 정렬된 순서는 다르다.
				assertThat(result)
						.extracting(CategoryItem::getCode)
						.containsExactly(
								CategoryTestData.INCOME_CODE,
								CategoryTestData.OUTLAY_CODE
						);
			}

			@Test
			@DisplayName("최상위 카테고리가 없으면 빈 리스트가 반환된다.")
			void returnsEmptyList_whenRootCategoryDoesNotExist() {
				//given: 캐시에서 최상위 카테고리 정보가 저장되어 있지 않다.
				Map<String, Category> categoryMap = new java.util.HashMap<>(CategoryFixture.hierarchyMap());
				categoryMap.remove(CategoryTestData.INCOME_CODE);

				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 최상위 카테고리를 조회한다.
				List<CategoryItem> result = target.getRootCategories();
				
				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
			}
			
			@Test
			@DisplayName("캐시가 비어있으면 빈 리스트를 반환된다.")
			void returnsEmptyList_whenCacheIsEmpty() {
				//given: 캐시 조회 시 빈 Map이 반환되도록 동작이 정의되어 있다.
				when(categoryCacheService.getCategoryMap()).thenReturn(Collections.emptyMap());

				//when: 최상위 카테고리를 조회한다.
				List<CategoryItem> result = target.getRootCategories();
				
				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
			}

			@Test
			@DisplayName("캐시 서비스는 한 번만 호출된다.")
			void validatesCacheServiceCallCount_whenDataIsRequested() {
				//given: 캐시에서 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				Map<String, Category> categoryMap = new java.util.HashMap<>(CategoryFixture.hierarchyMap());

				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 최상위 카테고리를 조회한다.
				List<CategoryItem> result = target.getRootCategories();
				
				//then: 캐시 서비스는 한 번만 호출된다.
				verify(categoryCacheService, times(1)).getCategoryMap();
			}

		}

	}


	@Nested
	@DisplayName("중간 카테고리 조회")
	class GetMiddleCategory {

		Map<String, Category> categoryMap;

		@BeforeEach
		void setUp() {
			Category top1 = CategoryFixture.builder().code("010000").name("수입").build();
			Category top2 = CategoryFixture.builder().code("020000").name("지출").build();

			Category middle1 = CategoryFixture.builder().code("010100").name("월급").parentCode(top1.getCode()).build();
			Category middle2 = CategoryFixture.builder().code("020100").name("간식").parentCode(top2.getCode()).build();
			Category middle3 = CategoryFixture.builder().code("020200").name("영화").parentCode(top2.getCode()).build();

			categoryMap = new LinkedHashMap<>();

			categoryMap.put(top1.getCode(), top1);
			categoryMap.put(middle1.getCode(), middle1);
			categoryMap.put(middle2.getCode(), middle2);
			categoryMap.put(top2.getCode(), top2);
			categoryMap.put(middle3.getCode(), middle3);
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("수입 유형이면 수입 카테고리만 반환된다.")
			void returnsOnlyIncomeMiddleCategories_whenTypeIsIncome() {
				//given: 수입 유형과 캐시에서 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				CategoryType type = CategoryType.INCOME;

				when(categoryCacheService.getCategoryMap())
						.thenReturn(categoryMap);

				//when: 중간 카테고리를 조회한다.
				List<CategoryItem> result = target.getMiddleCategories(type);
				
				//then: 수입 유형의 중간 카테고리만 반환된다.
				assertThat(result)
						.hasSize(1)
						.extracting(CategoryItem::getCode, CategoryItem::getName)
						.containsExactly(tuple("010100", "월급"));
			}
			
			@Test
			@DisplayName("지출 유형이면 지출 카테고리만 반환된다.")
			void returnsOnlyOutlayMiddleCategories_whenTypeIsOutlay() {
				//given: 지출 유형과 캐시에서 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				CategoryType type = CategoryType.OUTLAY;

				when(categoryCacheService.getCategoryMap())
						.thenReturn(categoryMap);

				//when: 중간 카테고리를 조회한다.
				List<CategoryItem> result = target.getMiddleCategories(type);

				//then: 지출 유형의 중간 카테고리만 반환된다.
				assertThat(result)
						.hasSize(2)
						.extracting(CategoryItem::getCode, CategoryItem::getName)
						.containsExactly(
								tuple("020100", "간식"),
								tuple("020200", "영화")
						);
			}

			@Test
			@DisplayName("카테고리 코드 오름차순으로 정렬된다.")
			void sortsCategoriesByCodeAscending_whenCategoriesExist() {
				//given: 캐시에서 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 중간 카테고리를 조회한다.
				List<CategoryItem> result = target.getMiddleCategories(CategoryType.OUTLAY);

				//then: 카테고리 코드가 오름차순으로 정렬되어 반환된다.
				assertThat(result)
						.extracting(CategoryItem::getCode)
						.containsExactly(
								"020100", "020200"
						);
			}

			@Test
			@DisplayName("캐시의 저장된 순서와 관계없이 코드 기준으로 정렬하여 반환된다.")
			void validatesSortDiscrepancy_whenCacheSortIsDifferent() {
				//given: 정렬되지 않은 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				Map<String, Category> categoryMap = new LinkedHashMap<>();

				categoryMap.put(
						"010300",
						CategoryFixture.builder().code("010300").name("이름A").parentCode("010000").build()
				);
				categoryMap.put(
						"010100",
						CategoryFixture.builder().code("010100").name("이름C").parentCode("010000").build()
				);
				categoryMap.put(
						"010200",
						CategoryFixture.builder().code("010200").name("이름B").parentCode("010000").build()
				);

				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 중간 카테고리를 조회한다.
				List<CategoryItem> result = target.getMiddleCategories(CategoryType.INCOME);

				//then: 캐시에 정렬된 순서와 리스트에 정렬된 순서는 다르다.
				assertThat(result)
						.extracting(CategoryItem::getCode)
						.containsExactly(
								"010100",
								"010200",
								"010300"
						);
			}

			@Test
			@DisplayName("중간 카테고리가 없으면 빈 리스트가 반환된다.")
			void returnsEmptyList_whenMiddleCategoryDoesNotExist() {
				//given: 캐시에서 중간 카테고리 정보가 저장되어 있지 않다.
				categoryMap.entrySet().removeIf(entry -> {
					String code = entry.getValue().getCode();

					return code.matches("\\d{4}00") && !code.endsWith("0000");
				});

				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 중간 카테고리를 조회한다.
				List<CategoryItem> result = target.getMiddleCategories(CategoryType.INCOME);

				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
			}

			@Test
			@DisplayName("캐시가 비어있으면 빈 리스트를 반환된다.")
			void returnsEmptyList_whenCacheIsEmpty() {
				//given: 캐시 조회 시 빈 Map이 반환되도록 동작이 정의되어 있다.
				when(categoryCacheService.getCategoryMap()).thenReturn(Collections.emptyMap());

				//when: 중간 카테고리를 조회한다.
				List<CategoryItem> result = target.getMiddleCategories(CategoryType.OUTLAY);

				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
			}

			@Test
			@DisplayName("캐시 서비스는 한 번만 호출된다.")
			void validatesCacheServiceCallCount_whenDataIsRequested() {
				//given: 캐시에서 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 중간 카테고리를 조회한다.
				List<CategoryItem> result = target.getMiddleCategories(CategoryType.INCOME);

				//then: 캐시 서비스는 한 번만 호출된다.
				verify(categoryCacheService, times(1)).getCategoryMap();
			}

		}

	}


	@Nested
	@DisplayName("하위 카테고리 조회")
	class GetLowCategory {

		Map<String, Category> categoryMap;

		@BeforeEach
		void setUp() {
			Category top1 = CategoryFixture.builder().code("010000").name("수입").build();
			Category top2 = CategoryFixture.builder().code("020000").name("지출").build();

			Category middle1 = CategoryFixture.builder().code("010100").name("월급").parentCode(top1.getCode()).build();
			Category middle2 = CategoryFixture.builder().code("020100").name("간식").parentCode(top2.getCode()).build();
			Category middle3 = CategoryFixture.builder().code("020200").name("영화").parentCode(top2.getCode()).build();

			Category low1 = CategoryFixture.builder().code("010101").name("월급A").parentCode(middle1.getCode()).build();
			Category low2 = CategoryFixture.builder().code("010102").name("월급B").parentCode(middle1.getCode()).build();
			Category low3 = CategoryFixture.builder().code("020101").name("간식A").parentCode(middle2.getCode()).build();
			Category low4 = CategoryFixture.builder().code("020102").name("간식B").parentCode(middle2.getCode()).build();
			Category low5 = CategoryFixture.builder().code("020103").name("간식C").parentCode(middle2.getCode()).build();
			Category low6 = CategoryFixture.builder().code("020201").name("영화A").parentCode(middle3.getCode()).build();

			categoryMap = new LinkedHashMap<>();

			categoryMap.put(low6.getCode(), low6);
			categoryMap.put(top1.getCode(), top1);
			categoryMap.put(low4.getCode(), low4);
			categoryMap.put(middle1.getCode(), middle1);
			categoryMap.put(low1.getCode(), low1);
			categoryMap.put(low2.getCode(), low2);
			categoryMap.put(middle2.getCode(), middle2);
			categoryMap.put(top2.getCode(), top2);
			categoryMap.put(low5.getCode(), low5);
			categoryMap.put(middle3.getCode(), middle3);
			categoryMap.put(low3.getCode(), low3);
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("수입 유형이면 수입 카테고리만 반환된다.")
			void returnsOnlyIncomeLowCategories_whenTypeIsIncome() {
				//given: 수입 유형과 캐시에서 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				CategoryType type = CategoryType.INCOME;

				when(categoryCacheService.getCategoryMap())
						.thenReturn(categoryMap);

				//when: 하위 카테고리를 조회한다.
				List<CategoryItem> result = target.getLowCategories(type);

				//then: 수입 유형의 하위 카테고리만 반환된다.
				assertThat(result)
						.hasSize(2)
						.extracting(CategoryItem::getCode, CategoryItem::getName)
						.containsExactly(
								tuple("010101", "월급A"),
								tuple("010102", "월급B")
						);
			}

			@Test
			@DisplayName("지출 유형이면 지출 카테고리만 반환된다.")
			void returnsOnlyOutlayMiddleCategories_whenTypeIsOutlay() {
				//given: 지출 유형과 캐시에서 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				CategoryType type = CategoryType.OUTLAY;

				when(categoryCacheService.getCategoryMap())
						.thenReturn(categoryMap);

				//when: 하위 카테고리를 조회한다.
				List<CategoryItem> result = target.getLowCategories(type);

				//then: 지출 유형의 하위 카테고리만 반환된다.
				assertThat(result)
						.hasSize(4)
						.extracting(CategoryItem::getCode, CategoryItem::getName)
						.containsExactly(
								tuple("020101", "간식A"),
								tuple("020102", "간식B"),
								tuple("020103", "간식C"),
								tuple("020201", "영화A")
						);
			}

			@Test
			@DisplayName("카테고리 코드 오름차순으로 정렬된다.")
			void sortsCategoriesByCodeAscending_whenCategoriesExist() {
				//given: 캐시에서 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 하위 카테고리를 조회한다.
				List<CategoryItem> result = target.getLowCategories(CategoryType.INCOME);

				//then: 카테고리 코드가 오름차순으로 정렬되어 반환된다.
				assertThat(result)
						.extracting(CategoryItem::getCode)
						.containsExactly(
								"010101", "010102"
						);
			}

			@Test
			@DisplayName("캐시의 저장된 순서와 관계없이 코드 기준으로 정렬하여 반환된다.")
			void validatesSortDiscrepancy_whenCacheSortIsDifferent() {
				//given: 정렬되지 않은 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				Map<String, Category> categoryMap = new LinkedHashMap<>();

				categoryMap.put(
						"020102",
						CategoryFixture.builder().code("020102").name("이름A").parentCode("020100").build()
				);
				categoryMap.put(
						"020103",
						CategoryFixture.builder().code("020103").name("이름C").parentCode("020100").build()
				);
				categoryMap.put(
						"020101",
						CategoryFixture.builder().code("020101").name("이름B").parentCode("020100").build()
				);

				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 하위 카테고리를 조회한다.
				List<CategoryItem> result = target.getLowCategories(CategoryType.OUTLAY);

				//then: 캐시에 정렬된 순서와 리스트에 정렬된 순서는 다르다.
				assertThat(result)
						.extracting(CategoryItem::getCode)
						.containsExactly(
								"020101",
								"020102",
								"020103"
						);
			}

			@Test
			@DisplayName("하위 카테고리가 없으면 빈 리스트가 반환된다.")
			void returnsEmptyList_whenLowCategoryDoesNotExist() {
				//given: 캐시에서 하위 카테고리 정보가 저장되어 있지 않다.
				categoryMap.entrySet().removeIf(entry -> {
					String code = entry.getValue().getCode();

					return !code.endsWith("0000") && !code.endsWith("00");
				});

				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 하위 카테고리를 조회한다.
				List<CategoryItem> result = target.getLowCategories(CategoryType.INCOME);

				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
			}

			@Test
			@DisplayName("캐시가 비어있으면 빈 리스트를 반환된다.")
			void returnsEmptyList_whenCacheIsEmpty() {
				//given: 캐시 조회 시 빈 Map이 반환되도록 동작이 정의되어 있다.
				when(categoryCacheService.getCategoryMap()).thenReturn(Collections.emptyMap());

				//when: 하위 카테고리를 조회한다.
				List<CategoryItem> result = target.getLowCategories(CategoryType.OUTLAY);

				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
			}

			@Test
			@DisplayName("캐시 서비스는 한 번만 호출된다.")
			void validatesCacheServiceCallCount_whenDataIsRequested() {
				//given: 캐시에서 카테고리 정보가 반환되도록 동작이 정의되어 있다.
				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 하위 카테고리를 조회한다.
				target.getMiddleCategories(CategoryType.INCOME);

				//then: 캐시 서비스는 한 번만 호출된다.
				verify(categoryCacheService, times(1)).getCategoryMap();
			}

		}

	}


	@Nested
	@DisplayName("카테고리 조회")
	class GetCategory {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("코드에 해당하는 카테고리가 있으면 Category가 반환된다.")
			void returnsCategory_whenCategoryExists() {
				//given: 캐시에 저장된 카테고리를 반환하도록 동작이 정의되어 있다.
				when(categoryCacheService.getCategoryMap())
						.thenReturn(CategoryFixture.hierarchyMap());
				
				//when: 카테고리를 조회한다.
				Category result = target.getCategory(CategoryTestData.SALARY_CODE);
				
				//then: 코드에 해당하는 카테고리가 반환된다.
				assertThat(result)
						.extracting(
								Category::getCode,
								Category::getName
						)
						.containsExactly(
								CategoryTestData.SALARY_CODE,
								CategoryTestData.SALARY_NAME
						);
			}
			
		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("코드에 해당하는 카테고리가 없으면 예외가 발생한다.")
			void throwsException_whenCategoryDoesNotExist() {
				//given: 존재하지 않은 카테고리 코드가 주어진다.
				String code = "999999";
				
				//when & then: 카테고리를 조회하면 BusinessException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(
						catchThrowable(() -> target.getCategory(code))
				)
						.hasErrorCode(CategoryErrorCode.NOT_FOUND_DATA)
						.hasWork("카테고리 조회")
						.hasCauseMessage("카테고리 없음")
						.hasField("code")
						.hasValue(code)
						.hasUserMessage("존재하지 않은 카테고리");
			}

			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("코드가 빈 문자열이면 예외가 발생한다.")
			void throwsException_whenCodeIsInvalid(String code) {
				//when & then: 카테고리를 조회하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.getCategory(code))
						.isInstanceOf(BusinessException.class);
			}

			@Test
			@DisplayName("캐시에 저장된 카테고리 정보가 없으면 예외가 발생한다.")
			void throwsException_whenCacheIsEmpty() {
				//given: 캐시에 저장된 카테고리 정보를 조회하면 빈 Map이 반환되도록 동작이 정의되어 있다.
				String code = CategoryTestData.SALARY_CODE;

				when(categoryCacheService.getCategoryMap())
						.thenReturn(Collections.emptyMap());

				//when & then: 카테고리를 조회하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.getCategory(code))
						.isInstanceOf(BusinessException.class);
			}

		}

	}


	@Nested
	@DisplayName("카테고리 계층 조회")
	class GetCategoryHierarchy {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("하위 카테고리 코드면 전체 카테고리 계층이 반환된다.")
			void returnsAllCategories_whenSubCategoryCodeIsGiven() {
				//given: 하위 카테고리 코드가 주어진다.
				String code = CategoryTestData.SALARY_CODE;

				when(categoryCacheService.getCategoryMap()).thenReturn(CategoryFixture.hierarchyMap());
				
				//when: 카테고리 계층을 조회한다.
				List<CategoryItem> result = target.findCategoryHierarchy(code);
				
				//then: 최상위 계층까지 카테고리가 반환된다.
				assertThat(result)
						.hasSize(3)
						.extracting(
								CategoryItem::getCode,
								CategoryItem::getName
						)
						.containsExactly(
								tuple(CategoryTestData.INCOME_CODE, CategoryTestData.INCOME_NAME),
								tuple(CategoryTestData.EARNED_CODE, CategoryTestData.EARNED_NAME),
								tuple(CategoryTestData.SALARY_CODE, CategoryTestData.SALARY_NAME)
						);
			}
			
			@Test
			@DisplayName("중간 카테고리 코드면 일부 카테고리 계층이 반환된다.")
			void returnsPartialCategories_whenMiddleCategoryCodeIsGiven() {
				//given: 중간 카테고리 코드가 주어진다.
				String code = CategoryTestData.EARNED_CODE;

				when(categoryCacheService.getCategoryMap()).thenReturn(CategoryFixture.hierarchyMap());

				//when: 카테고리 계층을 조회한다.
				List<CategoryItem> result = target.findCategoryHierarchy(code);

				//then: 최상위 계층까지 카테고리가 반환된다.
				assertThat(result)
						.hasSize(2)
						.extracting(
								CategoryItem::getCode,
								CategoryItem::getName
						)
						.containsExactly(
								tuple(CategoryTestData.INCOME_CODE, CategoryTestData.INCOME_NAME),
								tuple(CategoryTestData.EARNED_CODE, CategoryTestData.EARNED_NAME)
						);
			}
			
			@Test
			@DisplayName("최상위 카테고리 코드면 자기 자신만 담은 리스트가 반환된다.")
			void returnsOnlySelf_whenRootCategoryCodeIsGiven() {
				//given: 최상위 카테고리 코드가 주어진다.
				String code = CategoryTestData.INCOME_CODE;

				when(categoryCacheService.getCategoryMap()).thenReturn(CategoryFixture.hierarchyMap());

				//when: 카테고리 계층을 조회한다.
				List<CategoryItem> result = target.findCategoryHierarchy(code);

				//then: 최상위 계층까지 카테고리가 반환된다.
				assertThat(result)
						.hasSize(1)
						.extracting(
								CategoryItem::getCode,
								CategoryItem::getName
						)
						.containsExactly(
								tuple(CategoryTestData.INCOME_CODE, CategoryTestData.INCOME_NAME)
						);
			}
			
			@Test
			@DisplayName("존재하지 않은 코드면 예외가 전파된다.")
			void throwsException_whenCategoryDoesNotExist() {
				//given: 저장되지 않은 카테고리 코드가 주어진다.
				String code = "019999";

				when(categoryCacheService.getCategoryMap()).thenReturn(CategoryFixture.hierarchyMap());

				//when & then: 카테고리 계층을 조회하면 BusinessException가 발생한다.
				assertThatThrownBy(() -> target.findCategoryHierarchy(code))
						.isInstanceOf(BusinessException.class);
			}

			@Test
			@DisplayName("최상위 계층부터 현재 계층까지 정렬된 리스트가 반환된다.")
			void sortsCategoriesByHierarchy_whenCategoriesExist() {
				//given: 하위 카테고리 코드가 주어진다.
				String code = CategoryTestData.SALARY_CODE;

				when(categoryCacheService.getCategoryMap()).thenReturn(CategoryFixture.hierarchyMap());

				//when: 카테고리 계층을 조회한다.
				List<CategoryItem> result = target.findCategoryHierarchy(code);

				//then: 최상위 계층부터 하위 계층 순으로 정렬된다.
				assertThat(result)
						.extracting(
								CategoryItem::getCode
						)
						.containsExactly(
								CategoryTestData.INCOME_CODE,
								CategoryTestData.EARNED_CODE,
								CategoryTestData.SALARY_CODE
						);
			}
			
			@Test
			@DisplayName("부모가 없는 카테고리 정보에서도 가능한 범위까지 반환된다.")
			void returnsPartialCategories_whenParentDoesNotExist() {
				//given: 부모가 없는 카테고리 정보가 반환되도록 동작이 정의되어 있따.
				Map<String, Category> categoryMap = new HashMap<>(CategoryFixture.hierarchyMap());
				categoryMap.put(
						CategoryTestData.EARNED_CODE,
						CategoryFixture.builder().code(CategoryTestData.EARNED_CODE).name(CategoryTestData.EARNED_NAME).parentCode(null).build()
				);

				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 카테고리 계층을 조회한다.
				List<CategoryItem> result = target.findCategoryHierarchy("010101");

				//then: 자기 자신의 카테고리만 반환된다.
				assertThat(result)
						.hasSize(2)
						.extracting(
								CategoryItem::getCode,
								CategoryItem::getName
						)
						.containsExactly(
								tuple(CategoryTestData.EARNED_CODE, CategoryTestData.EARNED_NAME),
								tuple(CategoryTestData.SALARY_CODE, CategoryTestData.SALARY_NAME)
						);
			}
			
			@Test
			@DisplayName("중간 부모가 끊어져도 가능한 범위까지 반환된다.")
			void throwsException_whenMiddleParentDoesNotExist() {
				//given: 중간 부모가 없는 카테고리 정보가 반환되도록 동작이 정의되어 있따.
				Map<String, Category> categoryMap = new HashMap<>(CategoryFixture.hierarchyMap());
				categoryMap.put(
						CategoryTestData.EARNED_CODE,
						CategoryFixture.builder().code(CategoryTestData.EARNED_CODE).name(CategoryTestData.EARNED_NAME).parentCode("021000").build()
				);

				when(categoryCacheService.getCategoryMap()).thenReturn(categoryMap);

				//when: 카테고리 계층을 조회하면 BusinessException가 발생한다.
				assertThatThrownBy(() -> target.findCategoryHierarchy("020101"))
						.isInstanceOf(BusinessException.class);
			}
			
		}

	}

}