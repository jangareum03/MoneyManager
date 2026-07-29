package com.moneymanager.ledger.service;

import com.moneymanager.domain.ledger.dto.response.CategoryItem;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.exception.code.CategoryErrorCode;
import com.moneymanager.exception.exception.BusinessException;
import com.moneymanager.service.ledger.CategoryCacheService;
import com.moneymanager.service.ledger.CategoryReadService;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.IntegrationTestSupport;
import com.moneymanager.support.data.CategoryTestData;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service<br>
 * 파일이름       : CategoryReadServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 20<br>
 * 설명              : CategoryReadService 클래스 로직을 검증하는 통합 테스트 클래스
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
public class CategoryReadServiceIT extends IntegrationTestSupport {

	@Autowired
	private CategoryReadService target;

	@Autowired
	private CategoryCacheService categoryCacheService;
	
	@Nested
	@DisplayName("최상위 카테고리 조회")
	class GetRootCategory {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("캐시에 저장된 카테고리 정보가 조회된다.")
			void fetchesCategories_whenStoredInCache() {
				//when: 최상위 카테고리를 조회한다.
				List<CategoryItem> result = target.getRootCategories();
				
				//then: 최상위 카테고리만 조회된다.
				assertThat(result).hasSize(2);
			}
			
			@Test
			@DisplayName("카테고리 코드 오름차순으로 정렬된다.")
			void sortsCategoriesByCodeAscending_whenCategoriesExist() {
				//when: 최상위 카테고리를 조회한다.
				List<CategoryItem> result = target.getRootCategories();

				//then: 최상위 카테고리만 조회된다.
				assertThat(result)
						.extracting(
								CategoryItem::getCode
						)
						.containsExactly(
								CategoryTestData.INCOME_CODE,
								CategoryTestData.OUTLAY_CODE
						);
			}
			
			@Test
			@Sql(statements = "DELETE FROM ledger_category WHERE parent_code IS NULL", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("최상위 카테고리가 저장되지 않으면 빈 리스트가 반환된다.")
			void returnsEmptyList_whenSavingRootCategoryFails() {
				//when: 최상위 카테고리를 조회한다.
				List<CategoryItem> result = target.getRootCategories();

				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
			}
			
			@Test
			@DisplayName("카테고리 정보가 CategoryItem으로 매핑된다.")
			void validatesDataMapping_whenCategoryItemIsMapped() {
				//when: 최상위 카테고리를 조회한다.
				List<CategoryItem> result = target.getRootCategories();

				//then: CategoryItem에 매핑이 올바르게 된다.
				assertThat(result)
						.extracting(
								CategoryItem::getCode,
								CategoryItem::getName
						)
						.containsExactly(
								Tuple.tuple(CategoryTestData.INCOME_CODE, CategoryTestData.INCOME_NAME),
								Tuple.tuple(CategoryTestData.OUTLAY_CODE, "지출")
						);
			}

		}

	}


	@Nested
	@DisplayName("중간 카테고리 조회")
	class GetMiddleCategory {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("validSize")
			@DisplayName("캐시에 저장된 카테고리 정보가 조회된다.")
			void fetchesCategories_whenStoredInCache(CategoryType type, int size) {
				//when: 중간 카테고리를 조회한다.
				List<CategoryItem> result = target.getMiddleCategories(type);

				//then: 유형별 중간 카테고리만 조회된다.
				assertThat(result).hasSize(size);
			}

			static Stream<Arguments> validSize() {
				return Stream.of(
						Arguments.of(
								named("수입 유형인 경우", CategoryType.INCOME),
								3
						),
						Arguments.of(
								named("지출 유형인 경우", CategoryType.OUTLAY),
								9
						)
				);
			}

			@ParameterizedTest
			@MethodSource("validCategories")
			@DisplayName("카테고리 코드 오름차순으로 정렬된다.")
			void sortsCategoriesByCodeAscending_whenCategoriesExist(CategoryType type, List<String> expected) {
				//when: 중간 카테고리를 조회한다.
				List<CategoryItem> result = target.getMiddleCategories(type);

				//then: 유형별 중간 카테고리만 조회된다.
				assertThat(result)
						.extracting(CategoryItem::getCode)
						.containsExactlyElementsOf(expected);
			}

			static Stream<Arguments> validCategories() {
				return Stream.of(
						Arguments.of(
								named("수입 유형인 경우", CategoryType.INCOME),
								List.of("010100", "010200", "010300")
						),
						Arguments.of(
								named("지출 유형인 경우", CategoryType.OUTLAY),
								List.of("020100", "020200", "020300", "020400", "020500", "020600", "020700", "020800", "020900")
						)
				);
			}

			@Test
			@Sql(statements = "DELETE FROM ledger_category WHERE parent_code IS NOT NULL", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("중간 카테고리가 저장되지 않으면 빈 리스트가 반환된다.")
			void returnsEmptyList_whenSavingRootCategoryFails() {
				//when: 중간 카테고리를 조회한다.
				List<CategoryItem> result = target.getMiddleCategories(CategoryType.INCOME);

				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
			}

		}

	}


	@Nested
	@DisplayName("하위 카테고리 조회")
	class GetLowCategory {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("validSize")
			@DisplayName("캐시에 저장된 카테고리 정보가 조회된다.")
			void fetchesCategories_whenStoredInCache(CategoryType type, int size) {
				//when: 하위 카테고리를 조회한다.
				List<CategoryItem> result = target.getLowCategories(type);

				//then: 유형별 하위 카테고리만 조회된다.
				assertThat(result).hasSize(size);
			}

			static Stream<Arguments> validSize() {
				return Stream.of(
						Arguments.of(
								named("수입 유형인 경우", CategoryType.INCOME),
								6
						),
						Arguments.of(
								named("지출 유형인 경우", CategoryType.OUTLAY),
								44
						)
				);
			}

			@ParameterizedTest
			@MethodSource("validCategories")
			@DisplayName("카테고리 코드 오름차순으로 정렬된다.")
			void sortsCategoriesByCodeAscending_whenCategoriesExist(CategoryType type, List<String> expected) {
				//when: 하위 카테고리를 조회한다.
				List<CategoryItem> result = target.getLowCategories(type);

				//then: 유형별 하위 카테고리만 조회된다.
				assertThat(result)
						.extracting(CategoryItem::getCode)
						.containsAnyElementsOf(expected);
			}

			static Stream<Arguments> validCategories() {
				return Stream.of(
						Arguments.of(
								named("수입 유형인 경우", CategoryType.INCOME),
								List.of("010101", "010201", "010301")
						),
						Arguments.of(
								named("지출 유형인 경우", CategoryType.OUTLAY),
								List.of("020101", "020201", "020301", "020401", "020501", "020505", "020601", "020701", "020801", "020901")
						)
				);
			}

			@Test
			@Sql(statements = "DELETE FROM ledger_category WHERE parent_code LIKE '__0000'", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("하위 카테고리가 저장되지 않으면 빈 리스트가 반환된다.")
			void returnsEmptyList_whenSavingRootCategoryFails() {
				//when: 중간 카테고리를 조회한다.
				List<CategoryItem> result = target.getMiddleCategories(CategoryType.INCOME);

				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
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
				//when: 카테고리를 조회한다.
				Category result = target.getCategory(CategoryTestData.SALARY_CODE);
				
				//then: 코드에 해당하는 카테고리가 반환된다.
				assertThat(result)
						.extracting(
								Category::getCode,
								Category::getName,
								Category::getParentCode
						)
						.containsExactly(
								CategoryTestData.SALARY_CODE,
								CategoryTestData.SALARY_NAME,
								"010100"
						);
			}
			
		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("코드에 해당하는 카테고리가 없으면 예외가 발생한다.")
			void throwsException_whenCategoryDoesNotExist() {
				//given: 저장하지 않은 카테골 코드가 주어진다.
				String code = "99999";
				
				//when & then: 카테고리를 조회한다.
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
				String code = "010301";

				//when: 카테고리 계층을 조회한다.
				List<CategoryItem> result = target.findCategoryHierarchy(code);
				
				//then: 저장된 카테고리 계층 리스트가 반환된다.
				assertThat(result)
						.hasSize(3)
						.extracting(
								CategoryItem::getCode,
								CategoryItem::getName
						)
						.containsExactly(
								Tuple.tuple("010000", "수입"),
								Tuple.tuple("010300", "차입"),
								Tuple.tuple("010301", "빌린돈")
						);
			}
			
			@Test
			@DisplayName("중간 카테고리 코드면 일부 카테고리 계층이 반환된다.")
			void returnsPartialCategories_whenMiddleCategoryCodeIsGiven() {
				//given: 중간 카테고리 코드가 주어진다.
				String code = "020700";

				//when: 카테고리 계층을 조회한다.
				List<CategoryItem> result = target.findCategoryHierarchy(code);

				//then: 저장된 카테고리 계층 리스트가 반환된다.
				assertThat(result)
						.hasSize(2)
						.extracting(
								CategoryItem::getCode,
								CategoryItem::getName
						)
						.containsExactly(
								Tuple.tuple("020000", "지출"),
								Tuple.tuple("020700", "통신")
						);
			}
			
			@Test
			@DisplayName("최상위 카테고리 코드면 자기 자신만 담은 리스트가 반환된다.")
			void returnsOnlySelf_whenRootCategoryCodeIsGiven() {
				//given: 최상위 카테고리 코드가 주어진다.
				String code = "010000";

				//when: 카테고리 계층을 조회한다.
				List<CategoryItem> result = target.findCategoryHierarchy(code);

				//then: 저장된 카테고리 계층 리스트가 반환된다.
				assertThat(result)
						.hasSize(1)
						.extracting(
								CategoryItem::getCode,
								CategoryItem::getName
						)
						.containsExactly(
								Tuple.tuple(code, "수입")
						);
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("존재하지 않은 코드면 예외가 전파된다.")
			void throwsException_whenCategoryDoesNotExist() {
				//given: 존재하지 않은 카테고리가 주어진다.
				String code = "019999";
				
				//when & then: 카테고리 계층을 조회하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.findCategoryHierarchy(code))
						.isInstanceOf(BusinessException.class);
			}
			
			@Test
			@Sql(statements = "DELETE FROM ledger_category WHERE code = '010000'", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("부모 카테고리 삭제되면 예외가 전파된다.")
			void throwsException_whenParentIsDeleted() {
				//when & then: 카테고리 계층을 조회하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.findCategoryHierarchy("010101"))
						.isInstanceOf(BusinessException.class);
			}
			
			@Test
			@Sql(statements = "DELETE FROM ledger_category WHERE code = '020300'", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("중간 카테고리 삭제되면 예외가 전파된다.")
			void throwsException_whenMiddleCategoryIsDeleted() {
				//when & then: 카테고리 계층을 조회하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.findCategoryHierarchy("020304"))
						.isInstanceOf(BusinessException.class);
			}

			@Test
			@Sql(statements = "DELETE FROM ledger_category", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("모든 카테고리가 삭제되면 예외가 전파된다.")
			void throwsException_whenAllCategoriesAreDeleted() {
				//when & then: 카테고리 계층을 조회하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.findCategoryHierarchy("010101"))
						.isInstanceOf(BusinessException.class);
			}
			
		}

	}

}