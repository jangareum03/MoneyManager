package com.moneymanager.ledger.domain.enums;

import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.support.ApplicationExceptionAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.CommonErrorCode.INVALID_VALUE;
import static com.moneymanager.global.exception.code.CommonErrorCode.REQUIRED_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.enums<br>
 * 파일이름       : CategoryTypeTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 25<br>
 * 설명              : CategoryType 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 6. 25</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class CategoryTypeTest {

	@Nested
	@DisplayName("Enum 변환")
	class FromTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@ParameterizedTest
			@MethodSource("validCategoryTypes")
			@DisplayName("대문자 값이면 CategoryType으로 변환된다.")
			void createsCategoryType_whenUpperCaseIsGiven(String type, CategoryType expected) {
				//when: 대문자 값으로 CategoryType으로 변환한다.
				CategoryType result = CategoryType.from(type.toUpperCase());

				//then: 일치하는 CategoryType이 반환된다.
				assertThat(result).isSameAs(expected);
			}

			@ParameterizedTest
			@MethodSource("validCategoryTypes")
			@DisplayName("소문자 값이면 CategoryType으로 변환된다.")
			void createsCategoryType_whenLowerCaseIsValid(String type, CategoryType expected) {
				//when: 소문자 값으로 CategoryType으로 변환한다.
				CategoryType result = CategoryType.from(type.toLowerCase());

				//then: 일치하는 CategoryType이 반환된다.
				assertThat(result).isSameAs(expected);
			}

			@ParameterizedTest
			@MethodSource("validCategoryTypes")
			@DisplayName("혼합 대소문자 값이면 CategoryType으로 변환된다.")
			void createsCategoryType_whenMixedCaseIsValid(String type, CategoryType expected) {
				//when: 혼합 대소문자 값으로 CategoryType으로 변환한다.
				CategoryType result = CategoryType.from(type);

				//then: 일치하는 CategoryType이 반환된다.
				assertThat(result).isSameAs(expected);
			}

			static Stream<Arguments> validCategoryTypes() {
				return Stream.of(
					Arguments.of(
							named("income인 경우", "Income"),
							CategoryType.INCOME
					),
					Arguments.of(
							named("outlay인 경우", "outLaY"),
							CategoryType.OUTLAY
					)
				);
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest
			@NullSource
			@DisplayName("값이 null이면 변환에 실패한다.")
			void throwsException_whenValueIsNull(String type) {
				//when: null로 CategoryType으로 변환한다.
				Throwable throwable = catchThrowable(() -> CategoryType.from(type));

				//then: 타입 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
								.hasErrorCode(REQUIRED_VALUE)
								.hasWork("CategoryType 변환")
								.hasCauseMessage("필수값 누락")
								.hasTarget(CategoryType.class)
								.hasField("name")
								.hasValue(null);
			}

			@ParameterizedTest
			@EmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("값이 비어있으면 변환에 실패한다.")
			void throwsException_whenValueIsEmpty(String type) {
				//when: null로 CategoryType으로 변환한다.
				Throwable throwable = catchThrowable(() -> CategoryType.from(type));

				//then: 타입 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("CategoryType 변환")
						.hasCauseMessage("필수값 누락")
						.hasTarget(CategoryType.class)
						.hasField("name")
						.hasValue(type);
			}

			@Test
			@DisplayName("허용되지 않은 값이면 변환에 실패한다.")
			void throwsException_whenValueIsInvalid() {
				//when: 허용되지 않은 값으로 CategoryType으로 변환한다.
				Throwable throwable = catchThrowable(() -> CategoryType.from("error"));

				//then: 변환 중 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(INVALID_VALUE)
						.hasWork("CategoryType 변환")
						.hasCauseMessage("허용되지 않은 값")
						.hasOption("allowed", "INCOME, OUTLAY")
						.hasTarget(CategoryType.class)
						.hasField("name")
						.hasValue("error");
			}

		}

	}


	@Nested
	@DisplayName("코드로 Enum 변환")
	class FromCodeTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@ParameterizedTest
			@ValueSource(strings = {"01", "010000", "011234"})
			@DisplayName("01로 시작하는 값이면 INCOME이 반환된다.")
			void returnsIncome_whenValueStartingWith01IsGiven(String code) {
				//when: 01로 시작하는 값으로 CategoryType을 변환한다.
				CategoryType result = CategoryType.fromCode(code);

				//then: INCOME 반환된다.
				assertThat(result).isSameAs(CategoryType.INCOME);
			}

			@ParameterizedTest
			@ValueSource(strings = {"02", "020000", "021234"})
			@DisplayName("02로 시작하는 값이면 OUTLAY이 반환된다.")
			void returnsOutlay_whenValueStartingWith02IsGiven(String code) {
				//when: 02로 시작하는 값으로 CategoryType을 변환한다.
				CategoryType result = CategoryType.fromCode(code);

				//then: OUTLAY 반환된다.
				assertThat(result).isSameAs(CategoryType.OUTLAY);
			}
			
		}
		
		@Nested
		@DisplayName("실패 케이스")
		class Failure {
		
			@ParameterizedTest
			@NullSource
			@DisplayName("값이 null이면 변환에 실패한다.")
			void throwsException_whenValueIsNull(String code) {
				//when: null로 CategoryType을 변환한다.
				Throwable throwable = catchThrowable(() -> CategoryType.fromCode(code));
				
				//then: 카테고리 코드 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("CategoryType 변환")
						.hasCauseMessage("필수값 누락")
						.hasTarget(Category.class)
						.hasField("code")
						.hasValue(code);
			}
			
			@ParameterizedTest
			@EmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("값이 비어있으면 변환에 실패한다.")
			void throwsException_whenValueIsEmpty(String code) {
				//when: 비어있는 값으로 CategoryType을 변환한다.
				Throwable throwable = catchThrowable(() -> CategoryType.fromCode(code));

				//then: 카테고리 코드 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("CategoryType 변환")
						.hasCauseMessage("필수값 누락")
						.hasTarget(Category.class)
						.hasField("code")
						.hasValue(code);
			}
			
			@Test
			@DisplayName("허용되지 않은 값이면 변환에 실패한다.")
			void throwsException_whenValueIsInvalid() {
				//when: 허용되지 않는 값으로 CategoryType을 변환한다.
				Throwable throwable = catchThrowable(() -> CategoryType.fromCode("error"));

				//then: 변환 중 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(INVALID_VALUE)
						.hasWork("CategoryType 변환")
						.hasCauseMessage("허용되지 않은 값")
						.hasTarget(Category.class)
						.hasField("code")
						.hasValue("error");
			}
			
		}

	}
	
}