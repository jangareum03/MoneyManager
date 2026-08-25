package com.moneymanager.ledger.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.enums<br>
 * 파일이름       : CategoryLevelTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 25<br>
 * 설명              : CategoryLevel 클래스 기능을 검증하는 테스트 클래스
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
public class CategoryLevelTest {

	@Nested
	@DisplayName("CategoryLevel 생성할 때")
	class From {

		@Nested
		@DisplayName("성공")
		class Success {

			@ParameterizedTest
			@MethodSource("validLevels")
			@DisplayName("유효한 대문자 값이면 변환한다.")
			void createsCategoryLevel_whenUpperCaseIsValid(String level, CategoryLevel expected) {
				//when
				CategoryLevel result = CategoryLevel.from(level.toUpperCase());
				
				//then
				assertThat(result).isSameAs(expected);
			}

			@ParameterizedTest
			@MethodSource("validLevels")
			@DisplayName("유효한 소문자 값이면 변환한다.")
			void createsCategoryLevel_whenLowerCaseIsValid(String level, CategoryLevel expected) {
				//when
				CategoryLevel result = CategoryLevel.from(level.toLowerCase());

				//then
				assertThat(result).isSameAs(expected);
			}

			static Stream<Arguments> validLevels() {
				return Stream.of(
						Arguments.of(
								named("Top인 경우", "top"),
								CategoryLevel.TOP
						),
						Arguments.of(
								named("Middle인 경우", "middle"),
								CategoryLevel.MIDDLE
						),
						Arguments.of(
								named("Low인 경우", "low"),
								CategoryLevel.LOW
						)
				);
			}

		}
		
		@Nested
		@DisplayName("실패")
		class Failure {
		
			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
			@DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
			void throwsNoSuchElementException_whenLevelIsNullOrBlank(String level) {
				assertThatThrownBy(() -> CategoryLevel.from(level))
						;
			}

			@Test
			@DisplayName("유효하지 않은 값이면 예외를 발생시킨다.")
			void throwsNoSuchElementException_whenLevelIsInvalid() {
				assertThatThrownBy(() -> CategoryLevel.from("noExist"))
						;
			}
			
		}

	}

}
