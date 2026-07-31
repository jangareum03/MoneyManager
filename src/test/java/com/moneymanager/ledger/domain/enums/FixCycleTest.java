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
 * 파일이름       : FixCycleTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 1<br>
 * 설명              : FixCycle 클래스 기능을 검증하는 테스트 클래스
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
public class FixCycleTest {

	@Nested
	@DisplayName("FixCycle 변환")
	class FromTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@ParameterizedTest
			@MethodSource("validFixCycles")
			@DisplayName("대문자 값이면 FixCycle으로 변환된다.")
			void createsFixCycle_whenUpperCaseIsGiven(String cycle, FixCycle expected) {
				//when: 대문자로 FixCycle을 변환한다.
				FixCycle result = FixCycle.from(cycle.toUpperCase());
				
				//then: FixCycle이 반환된다.
				assertThat(result).isSameAs(expected);
			}

			@ParameterizedTest
			@MethodSource("validFixCycles")
			@DisplayName("소문자 값이면 FixCycle으로 변환된다.")
			void createsFixCycle_whenLowerCaseIsValid(String cycle, FixCycle expected) {
				//when: 소문자로 FixCycle을 변환한다.
				FixCycle result = FixCycle.from(cycle.toLowerCase());

				//then: FixCycle이 반환된다.
				assertThat(result).isSameAs(expected);
			}

			static Stream<Arguments> validFixCycles() {
			    return Stream.of(
			        Arguments.of(
							named("w인 경우", "w"),
							FixCycle.WEEKLY
					),
					Arguments.of(
							named("m인 경우", "M"),
							FixCycle.MONTHLY
					),
					Arguments.of(
							named("y인 경우", "y"),
							FixCycle.YEARLY
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
			void throwsException_whenValueIsNull(String cycle) {
				//when: null로 FixCycle을 변환한다.
				Throwable throwable = catchThrowable(() -> FixCycle.from(cycle));
				
				//then: 값 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
								.hasErrorCode(REQUIRED_VALUE)
								.hasWork("고정주기 생성")
								.hasCauseMessage("필수값 누락")
								.hasField("fixCycle")
								.hasValue(cycle)
								.hasUserMessage("고정 주기", "선택");
			}
			
			@ParameterizedTest
			@EmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("값이 비어있으면 변환에 실패한다.")
			void throwsException_whenValueIsEmpty(String cycle) {
				//when: 빈 값으로 FixCycle을 변환한다.
				Throwable throwable = catchThrowable(() -> FixCycle.from(cycle));

				//then: 값 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("고정주기 생성")
						.hasCauseMessage("필수값 누락")
						.hasField("fixCycle")
						.hasValue(cycle)
						.hasUserMessage("고정 주기", "선택");
			}
			
			@Test
			@DisplayName("허용되지 않은 값이면 변환에 실패한다.")
			void throwsException_whenValueIsInvalid() {
				//when: 허용되지 않은 값으로 FixCycle을 변환한다.
				Throwable throwable = catchThrowable(() -> FixCycle.from("error"));
				
				//then: 변환 중 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(INVALID_VALUE)
						.hasWork("고정주기 생성")
						.hasCauseMessage("허용되지 않은 값")
						.hasField("fixCycle")
						.hasValue("error")
						.hasUserMessage("허용하지 않은", "고정 주기");
			}

		}

	}

}
