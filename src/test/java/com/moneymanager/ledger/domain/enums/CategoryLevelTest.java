package com.moneymanager.ledger.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import com.moneymanager.support.ApplicationExceptionAssert;

import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.CommonErrorCode.INVALID_VALUE;
import static com.moneymanager.global.exception.code.CommonErrorCode.REQUIRED_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
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
	@DisplayName("객체 생성")
	class FromTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@ParameterizedTest
			@MethodSource("validLevels")
			@DisplayName("유효한 대문자 값이면 카테고리 단계로 변환된다.")
			void createsCategoryLevel_whenUpperCaseIsValid(String level, CategoryLevel expected) {
				//when: 대문자로 카테고리 단계를 변환한다.
				CategoryLevel result = CategoryLevel.from(level.toUpperCase());
				
				//then: 카테고리 단계가 반환된다.
				assertThat(result).isSameAs(expected);
			}

			@ParameterizedTest
			@MethodSource("validLevels")
			@DisplayName("유효한 소문자 값이면 카테고리 단계로 변환된다.")
			void createsCategoryLevel_whenLowerCaseIsValid(String level, CategoryLevel expected) {
				//when: 소문자로 카테고리 단계를 변환한다.
				CategoryLevel result = CategoryLevel.from(level.toLowerCase());

				//then: 카테고리 단계가 반환된다.
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
		@DisplayName("실패 케이스")
		class Failure {
		
			@ParameterizedTest
			@NullSource
			@DisplayName("값이 null이면 변환에 실패한다.")
			void throwsException_whenLevelIsNull(String level) {
				//when: null로 카테고리 단계를 변환한다.
				Throwable throwable = catchThrowable(() -> CategoryLevel.from(level));
				
				//then: 레벨 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
								.hasErrorCode(REQUIRED_VALUE)
								.hasWork("카테고리 단계 변환")
								.hasCauseMessage("필수값 누락")
								.hasField("level")
								.hasValue(level);
			}
			
			@ParameterizedTest
			@EmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("값이 비어있으면 변환에 실패한다.")
			void throwsException_whenLevelIsEmpty(String level) {
				//when: 빈 문자열로 카테고리 단계를 변환한다.
				Throwable throwable = catchThrowable(() -> CategoryLevel.from(level));

				//then: 레벨 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("카테고리 단계 변환")
						.hasCauseMessage("필수값 누락")
						.hasField("level")
						.hasValue(level);
			}
			
			@Test
			@DisplayName("유효하지 않은 값이면 변환에 실패한다.")
			void throwsException_whenLevelIsInvalid() {
				//given: 유효하지 않은 값이 준비되어 있다.
				String level = "error";

				//when: 유효하지 앟은 값으로 카테고리 단계를 변환한다.
				Throwable throwable = catchThrowable(() -> CategoryLevel.from(level));

				//then: 변환 중 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(INVALID_VALUE)
						.hasWork("카테고리 단계 변환")
						.hasCauseMessage("허용되지 않은 값")
						.hasField("level")
						.hasValue(level)
						.hasOption("allowed", "TOP, MIDDLE, LOW");
			}
			
		}

	}

}
