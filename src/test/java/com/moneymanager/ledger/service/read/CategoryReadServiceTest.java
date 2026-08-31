package com.moneymanager.ledger.service.read;

import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.ledger.domain.enums.LedgerType;
import com.moneymanager.ledger.service.cache.CategoryCacheService;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.fixture.entity.category.CategoryHierarchyFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.read<br>
 * 파일이름       : CategoryReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 13<br>
 * 설명              : LedgerReadService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 8. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class CategoryReadServiceTest {

    private CategoryReadService target;

    @Mock
    private CategoryCacheService categoryCacheService;

    @BeforeEach
    void setUp() {
        when(categoryCacheService.getCategoryMap())
                .thenReturn(
                        CategoryHierarchyFixture.buildFullCategoryMap()
                );

        target = new CategoryReadService(categoryCacheService);
    }

    @Nested
    @DisplayName("중간 카테고리를 조회할 때")
    class GetMiddleCategories {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("수입 유형이면 수입 중간 카테고리들만 조회된다.")
            void returnsIncomeSubCategories_whenTypeIsIncome() {
                //when
                List<Category> result = target.getMiddleCategories(LedgerType.INCOME);

                //then: 수입 카테고리만 조회된다.
                assertThat(result)
                        .isNotNull()
                        .doesNotHaveDuplicates()
                        .extracting(Category::getCode)
                        .allMatch(code -> code.startsWith("01"))
                        .allMatch(code -> code.endsWith("00"));
            }

            @Test
            @DisplayName("지출 유형이면 지출 중간 카테고리들만 조회된다.")
            void returnsExpenseSubCategories_whenTypeIsExpense() {
                //when
                List<Category> result = target.getMiddleCategories(LedgerType.OUTLAY);

                //then: 지출 카테고리만 조회된다.
                assertThat(result)
                        .isNotNull()
                        .doesNotHaveDuplicates()
                        .extracting(Category::getCode)
                        .allMatch(code -> code.startsWith("02"))
                        .allMatch(code -> code.endsWith("00"));
            }

            @Test
            @DisplayName("카테고리 코드 오름차순으로 정렬되어 조회된다.")
            void returnsCategoriesSortedByCodeAsc_whenCategoriesExist() {
                //when
                List<Category> result = target.getMiddleCategories(LedgerType.OUTLAY);

                //then: 조회된 카테고리 코드가 오름차순으로 조회된다.
                assertThat(result)
                        .extracting(Category::getCode)
                        .isSorted();
            }

        }

    }


    @Nested
    @DisplayName("자식 카테고리들을 조회할 때")
    class GetChildrenCategories {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("최상위 카테고리 코드면 중간 단계 카테고리들을 반환한다.")
            void returnsSubCategories_whenTopCategoryCodeIsGiven() {
                //when
                List<Category> result = target.getChildrenByParentCode(CategoryTestData.INCOME_CODE);

                //then
                assertThat(result)
                        .hasSize(2)
                        .extracting(Category::getCode)
                        .containsExactly("010100", "010200");
            }

            @Test
            @DisplayName("중간 카테고리 코드면 하위 단계 카테고리들을 반환한다.")
            void returnsSubCategories_whenMiddleCategoryCodeIsGiven() {
                //when
                List<Category> result = target.getChildrenByParentCode(CategoryTestData.FOOD_CODE);

                //then
                assertThat(result)
                        .singleElement()
                        .extracting(Category::getCode, Category::getName)
                        .containsExactly("020101", "하위1");
            }

            @Test
            @DisplayName("하위 카테고리 코드면 빈 리스트를 반환한다.")
            void returnsEmptyList_whenLowestCategoryCodeIsGiven() {
                //when
                List<Category> result = target.getChildrenByParentCode(CategoryTestData.SALARY_CODE);

                //then
                assertThat(result).isEmpty();
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("코드가 존재하지 않으면 예외가 발생한다.")
            void throwsValidationException_whenCodeDoesNotExist() {
                //when & then
                assertThatThrownBy(() -> target.getChildrenByParentCode("nonExistentCode"));
            }

        }

    }


    @Nested
    @DisplayName("카테고리 조회할 때")
    class GetCategory {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("코드가 존재하면 카테고리를 반환한다.")
            void returnsCategory_whenCodeExists() {
                //when
                Category result = target.getCategory("010101");

                //then
                assertThat(result)
                        .isNotNull()
                        .extracting(Category::getCode, Category::getName)
                        .containsExactly("010101", "하위1");
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @ParameterizedTest
            @NullAndEmptySource
            @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
            @DisplayName("코드가 null 또는 빈 문자열이면 예외를 발생시킨다.")
            void throwsValidationException_whenCodeIsNullOrEmpty(String code) {
                //when & then
                ApplicationExceptionAssert.assertThatApplicationException(
                                catchThrowable(() -> target.getCategory(code))
                        )
                        
                        .hasErrorCode(DATA_NOT_FOUND)
                        .hasWork("카테고리 조회")
                        .hasTarget(Category.class)
                        .hasValue("code", code);
            }

            @Test
            @DisplayName("코드가 존재하지 않으면 예외를 발생시킨다")
            void throwsValidationException_whenCodeDoesNotExist() {
                assertThatThrownBy(() -> target.getCategory("030000"));
            }

            @Test
            @DisplayName("코드가 유효하지 않으면 예외를 발생시킨다.")
            void throwsValidationException_whenCodeIsInvalid() {
                assertThatThrownBy(() -> target.getCategory("nonExistCode"));
            }

        }

    }


    @Nested
    @DisplayName("카테고리 존재 확인할 때")
    class Exists {

        @Test
        @DisplayName("코드가 존재하면 true를 반환한다.")
        void returnsTrue_whenCodeExists() {
            //when
            boolean result = target.exists(CategoryTestData.SALARY_CODE);

            //then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("코드가 존재하지 않으면 false를 반환한다.")
        void returnsFalse_whenCodeDoesNotExist() {
            //when
            boolean result = target.exists("nonExistCode");

            //then
            assertThat(result).isFalse();
        }

    }

}