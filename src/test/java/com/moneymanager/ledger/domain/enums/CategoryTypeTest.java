package com.moneymanager.ledger.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
	@DisplayName("CategoryType 변환할 때")
	class FromTest {

		@Nested
		@DisplayName("성공")
		class Success {

			@ParameterizedTest
			@MethodSource("validCategoryTypes")
			@DisplayName("유효한 대문자 값이면 변환한다.")
			void createsCategoryType_whenUpperCaseIsGiven(String type, CategoryType expected) {
				//when
				CategoryType result = CategoryType.from(type.toUpperCase());

				//then
				assertThat(result).isSameAs(expected);
			}

			@ParameterizedTest
			@MethodSource("validCategoryTypes")
			@DisplayName("유효한 소문자 값이면 변환한다.")
			void createsCategoryType_whenLowerCaseIsValid(String type, CategoryType expected) {
				//when
				CategoryType result = CategoryType.from(type.toLowerCase());

				//then
				assertThat(result).isSameAs(expected);
			}

			@ParameterizedTest
			@MethodSource("validCategoryTypes")
			@DisplayName("유효한 혼합 대소문자 값이면 변환한다.")
			void createsCategoryType_whenMixedCaseIsValid(String type, CategoryType expected) {
				//when
				CategoryType result = CategoryType.from(type);

				//then
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
		@DisplayName("실패")
		class Failure {

			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
			void throwsNoSuchElementException_whenValueIsBlank(String type) {
				assertThatThrownBy(() -> CategoryType.from(type))
						;
			}

			@Test
			@DisplayName("허용되지 않은 값이면 예외를 발생시킨다.")
			void throwsNoSuchElementException_whenValueIsInvalid() {
				assertThatThrownBy(() -> CategoryType.from("error"))
						;
			}

		}

	}


	@Nested
	@DisplayName("카테고리 코드 앞 2자리로 변환할 때")
	class FromCodeTest {

		@Nested
		@DisplayName("성공")
		class Success {

			@ParameterizedTest
			@ValueSource(strings = {"01", "010000", "011234"})
			@DisplayName("01이면 INCOME으로 변환한다.")
			void returnsIncome_whenValueStartingWith01IsGiven(String code) {
				//when
				CategoryType result = CategoryType.fromCode(code);

				//then
				assertThat(result).isSameAs(CategoryType.INCOME);
			}

			@ParameterizedTest
			@ValueSource(strings = {"02", "020000", "021234"})
			@DisplayName("02로이면 OUTLAY로 변환한다.")
			void returnsOutlay_whenValueStartingWith02IsGiven(String code) {
				//when
				CategoryType result = CategoryType.fromCode(code);

				//then
				assertThat(result).isSameAs(CategoryType.OUTLAY);
			}
			
		}
		
		@Nested
		@DisplayName("실패")
		class Failure {
		
			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
			void throwsException_whenValueIsBlank(String code) {
				assertThatThrownBy(() -> CategoryType.from(code))
						;
			}

			@Test
			@DisplayName("허용되지 않은 값이면 예외를 발생시킨다.")
			void throwsNoSuchElementException_whenValueIsInvalid() {
				assertThatThrownBy(() -> CategoryType.from("nonExistent"))
						;
			}
			
		}

	}
	
}