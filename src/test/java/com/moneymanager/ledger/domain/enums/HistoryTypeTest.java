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
 * 패키지이름    : com.moneymanager.unit.domain.ledger.enums<br>
 * 파일이름       : HistoryTypeTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 1<br>
 * 설명              : HistoryType 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 6. 1</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class HistoryTypeTest {

	@Nested
	@DisplayName("HistoryType 변환")
	class FromTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@ParameterizedTest
			@MethodSource("validHistoryTypes")
			@DisplayName("대문자 값이면 HistoryType으로 변환된다.")
			void createsHistoryType_whenUpperCaseIsGiven(String value, HistoryType expected) {
				//when: 대문자로 HistoryType을 변환한다.
				HistoryType result = HistoryType.from(value.toUpperCase());

				//then: HistoryType을 반환한다.
				assertThat(result).isSameAs(expected);
			}

			@ParameterizedTest
			@MethodSource("validHistoryTypes")
			@DisplayName("소문자 값이면 HistoryType으로 변환된다.")
			void createsHistoryType_whenLowerCaseIsValid(String value, HistoryType expected) {
				//when: 소문자로 HistoryType을 변환한다.
				HistoryType result = HistoryType.from(value.toLowerCase());

				//then: HistoryType을 반환한다.
				assertThat(result).isSameAs(expected);
			}

			static Stream<Arguments> validHistoryTypes() {
				return Stream.of(
						Arguments.of(
								named("week인 경우", "week"),
								HistoryType.WEEK
						),
						Arguments.of(
								named("month인 경우", "month"),
								HistoryType.MONTH
						),
						Arguments.of(
								named("year인 경우", "year"),
								HistoryType.YEAR
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
			void throwsException_whenValueIsNull(String value) {
				//when: null로 HistoryType을 변환한다.
				Throwable throwable = catchThrowable(() -> HistoryType.from(value));

				//then: 값 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("가계부 내역유형 생성")
						.hasCauseMessage("필수값 누락")
						.hasField("type")
						.hasValue(value);
			}

			@ParameterizedTest
			@EmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("값이 비어있으면 변환에 실패한다.")
			void throwsException_whenValueIsEmpty(String value) {
				//when: 빈 값으로 HistoryType을 변환한다.
				Throwable throwable = catchThrowable(() -> HistoryType.from(value));

				//then: 값 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("가계부 내역유형 생성")
						.hasCauseMessage("필수값 누락")
						.hasField("type")
						.hasValue(value);
			}

			@Test
			@DisplayName("허용되지 않은 값이면 변환에 실패한다.")
			void throwsException_whenValueIsInvalid() {
				//when: 허용되지 않은 값으로 FixCycle을 변환한다.
				Throwable throwable = catchThrowable(() -> HistoryType.from("error"));

				//then: 변환 중 에외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(INVALID_VALUE)
						.hasWork("가계부 내역유형 생성")
						.hasCauseMessage("허용되지 않은 값")
						.hasField("type")
						.hasValue("error")
						.hasOption("allowed", "WEEK, MONTH, YEAR");
			}

		}

	}

}
