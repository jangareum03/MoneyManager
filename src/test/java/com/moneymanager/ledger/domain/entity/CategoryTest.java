package com.moneymanager.ledger.domain.entity;

import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.fixture.entity.category.CategoryFixture;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.domain.ledger.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import static com.moneymanager.exception.code.CommonErrorCode.REQUIRED_VALUE;
import static com.moneymanager.exception.code.LedgerErrorCode.DATA_INTEGRITY_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.entity<br>
 * 파일이름       : CategoryTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 24<br>
 * 설명              : Category 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 6. 24</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class CategoryTest {

	@Nested
	@DisplayName("최상위 카테고리 생성")
	class CreateTopCategory {
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("최상위 카테고리를 생성한다.")
			void createsRootCategory_whenRequestIsValid() {
				//when: 최상위 카테고리를 생성한다.
				Category result = Category.topCategory("code", "name");

				//then: 요청한 값 그대로 카테고리가 설정된다.
				assertThat(result.getCode()).isEqualTo("code");
				assertThat(result.getName()).isEqualTo("name");

				//then: 최상위 카테고리는 부모가 없다.
				assertThat(result.getParentCode()).isNull();
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("카테고리 코드가 유효하지 않으면 생성에 실패한다.")
			void throwsException_whenCategoryCodeIsInvalid(String code) {
				//given: 유효한 카테고리 이름이 주어진다.
				String name = "이름";
				
				//when: 유효하지 않은 카테고리 코드로 최상위 카테고리를 생성한다.
				Throwable throwable = catchThrowable(() -> Category.topCategory(code, name));
				
				//then: 카테고리 코드 검증 예외가 발생된다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("카테고리 검증")
						.hasCauseMessage("카테고리 코드 없음")
						.hasField("code")
						.hasValue(code);
			}

			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("카테고리 이름이 유효하지 않으면 생성에 실패한다.")
			void throwsException_whenCategoryNameIsInvalid(String name) {
				//given: 유효한 카테고리 코드가 주어진다.
				String code = "코드";

				//when: 유효하지 않은 카테고리 이름으로 최상위 카테고리를 생성한다.
				 Throwable throwable = catchThrowable(() -> Category.topCategory(code, name));

				//then: 카테고리 이름 검증 예외가 발생된다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("카테고리 검증")
						.hasCauseMessage("카테고리 이름 없음")
						.hasField("name")
						.hasValue(name);
			}

		}

	}


	@Nested
	@DisplayName("자식 카테고리 생성")
	class CreateChildCategory {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("자식 카테고리를 생성한다.")
			void createsSubCategory_whenRequestIsValid() {
				//given: 유효한 카테고리 코드와 이름이 주어진다.
				Category parent = CategoryFixture.income();
				String code = CategoryTestData.SALARY_CODE;
				String name = CategoryTestData.SALARY_NAME;

				//when: 자식 카테고리를 생성한다.
				Category result = Category.childCategory(code, name, parent);
				
				//then: 요청한 코드와 이름이 저장된다.
				assertThat(result.getCode()).isEqualTo(code);
				assertThat(result.getName()).isEqualTo(name);

				//then: 부모 카테고리 코드가 parentCode로 저장된다.
				assertThat(result.getParentCode()).isEqualTo(parent.getCode());
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {
			
			@ParameterizedTest
			@NullSource
			@DisplayName("부모 카테고리가 null이면 생성에 실패한다.")
			void throwsException_whenParentCategoryIsNull(Category parent) {
				//given: 유효한 카테고리 코드와 이름을 주어진다.
				String code = CategoryTestData.SNACK_CODE;
				String name = CategoryTestData.SNACK_NAME;

				//when: 부모 카테고리가 null인 자식 카테고리를 생성한다.
				Throwable throwable = catchThrowable(() -> Category.childCategory(code, name, parent));
				
				//then: 부모 카테고리 검증 예외가 발생된다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(DATA_INTEGRITY_ERROR)
						.hasWork("객체 생성")
						.hasCauseMessage("부모 카테고리 없음")
						.hasField("parent")
						.hasValue(null);
			}

			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("카테고리 코드가 유효하지 않으면 생성에 실패한다.")
			void throwsException_whenCategoryCodeIsInvalid(String code) {
				//given: 유효한 카테고리 이름이 주어진다.
				String name = "이름";

				//when: 유효하지 않은 카테고리 코드로 자식 카테고리를 생성한다.
				Throwable throwable = catchThrowable(() -> Category.topCategory(code, name));

				//then: 카테고리 코드 검증 예외가 발생된다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("카테고리 검증")
						.hasCauseMessage("카테고리 코드 없음")
						.hasField("code")
						.hasValue(code);
			}

			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("카테고리 이름이 유효하지 않으면 생성에 실패한다.")
			void throwsException_whenCategoryNameIsInvalid(String name) {
				//given: 유효한 카테고리 코드가 주어진다.
				String code = CategoryTestData.SALARY_CODE;

				//when: 유효하지 않은 카테고리 이름으로 최상위 카테고리를 생성한다.
				Throwable throwable = catchThrowable(() -> Category.topCategory(code, name));

				//then: 카테고리 이름 검증 중 예외가 발생된다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("카테고리 검증")
						.hasCauseMessage("카테고리 이름 없음")
						.hasField("name")
						.hasValue(name);
			}

		}

	}

}
