package com.moneymanager.common.utils.date;

import com.moneymanager.utils.date.DateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Named.named;


/**
 * <p>
 * 패키지이름    : com.moneymanager.utils.date<br>
 * 파일이름       : DateTimeUtilTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 8<br>
 * 설명              : DateUtil 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 7. 8</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class DateUtilTest {

	@Nested
	@DisplayName("연도 리스트 조회")
	class YearRange {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("시작일과 종료일 모두 정상적인 값이면 리스트가 반환된다.")
			void returnsList_whenYearsAreValid() {
				//given: 시작일과 종료일이 준비되어 있다.
				int start = 2020;
				int end = 2025;
				
				//when: 시작일과 종료일까지 연도 리스트를 반환한다.
				List<Integer> result = DateUtils.getYearsInRange(start, end);
				
				//then: 요청한 값이 연도 리스트에 포함된다.
				assertThat(result)
						.startsWith(start)
						.endsWith(end)
						.hasSize(6);
			}
			
		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest
			@ValueSource(ints = {-1, 0})
			@DisplayName("시작일이 0 이하면 IllegalArgumentException이 발생한다. ")
			void throwsException_whenStartDateIsLessThanOrEqualToZero(int start) {
				//given: 종료일은 양수로 준비되어 있다.
				int end = 2025;
				
				//when: 0 이하인 시작일로 연도 리스트를 반환한다.
				Throwable throwable = catchThrowable(() -> DateUtils.getYearsInRange(start, end));
				
				//then: 음수 검증에 대한 예외가 발생한다.
				assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining("연도", "0보다", start, end);
			}

			@ParameterizedTest
			@ValueSource(ints = {-1, 0})
			@DisplayName("종료일이 0 이하면 IllegalArgumentException이 발생한다. ")
			void throwsException_whenEndDateIsLessThanOrEqualToZero(int end) {
				//given: 시작일은 양수로 준비되어 있다.
				int start = 2020;

				//when: 0이하인 종료일로 연도 리스트를 반환한다.
				Throwable throwable = catchThrowable(() -> DateUtils.getYearsInRange(start, end));

				//then: 음수 검증에 대한 예외가 발생한다.
				assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining("연도", "0보다", start, end);
			}
			
			@Test
			@DisplayName("시작일이 종료일보다 미래면 IllegalArgumentException이 발생한다.")
			void throwsException_whenStartDateIsAfterEndDate() {
				//given: 시작일이 종료일보다 큰 연도로 주어진다.
				int start = 2025;
				int end = 2020;

				//when: 잘못된 시작일과 종료일로 연도 리스트를 반환한다.
				Throwable throwable = catchThrowable(() -> DateUtils.getYearsInRange(start, end));

				//then: 시작일과 종료일 검증에 대한 예외가 발생한다.
				assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining("시작연도", "종료연도", "보다", start, end);
			}
			
		}

	}


	@Nested
	@DisplayName("월 리스트 조회")
	class MonthRange {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@ParameterizedTest(name = "[{index}] {0}월 ~ {1}월 범위면 {2}개의 월을 반환한다.")
			@MethodSource("validMonthRanges")
			@DisplayName("시작일과 종료일 모두 정상적인 값이면 리스트가 반환된다.")
			void returnsList_whenMonthsAreValid(int start, int end, int expected) {
				//when: 정상적인 요청값으로 월 리스트를 반환한다.
				List<Integer> result = DateUtils.getMonthsInRange(start, end);
				
				//then: 리스트에 시작일과 종료일을 포함된다.
				assertThat(result)
						.startsWith(start)
						.endsWith(end)
						.hasSize(expected);
			}

			static Stream<Arguments> validMonthRanges() {
				return Stream.of(
						Arguments.of(1, 12, 12),
						Arguments.of(3, 5, 3),
						Arguments.of(11, 12, 2)
				);
			}
			
		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest
			@MethodSource("invalidMonthValues")
			@DisplayName("시작일이 1~12 사이가 아니면 IllegalArgumentException이 발생한다.")
			void throwsException_whenStartMonthIsOutOfRange(int start) {
				//given: 종료일은 정상적인 값으로 준비되어 있다.
				int end = 12;
				
				//when: 범위에서 벗어난 시작일로 월 리스트를 반환한다.
				Throwable throwable = catchThrowable(() -> DateUtils.getMonthsInRange(start, end));
				
				//then: 시작일 검증 중 예외가 발생한다.
				assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining("1~12", start);
			}

			@ParameterizedTest
			@MethodSource("invalidMonthValues")
			@DisplayName("종료일이 1~12 사이가 아니면 IllegalArgumentException이 발생한다.")
			void throwsException_whenEndMonthIsOutOfRange(int end) {
				//given: 시작일은 정상적인 값으로 준비되어 있다.
				int start = 1;

				//when: 범위에서 벗어난 종료일로 월 리스트를 반환한다.
				Throwable throwable = catchThrowable(() -> DateUtils.getMonthsInRange(start, end));

				//then: 종료일 검증 중 예외가 발생한다.
				assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining("1~12", end);
			}

			static Stream<Arguments> invalidMonthValues() {
				return Stream.of(
						Arguments.of(named("월이 음수인 경우", -1)),
						Arguments.of(named("월이 0인 경우", 0)),
						Arguments.of(named("월이 13인 경우", 13))
				);
			}
			
			@Test
			@DisplayName("시작일이 종료일보다 크면 IllegalArgumentException이 발생한다.")
			void throwsException_whenStartDateIsAfterEndDate() {
				//given: 시작일이 종료일보다 크게 준비되어 있다.
				int start = 12;
				int end = 6;
				
				//when: 시작일이 종료일보다 큰 상태로 월 리스트를 반환한다.
				Throwable throwable = catchThrowable(() -> DateUtils.getMonthsInRange(start, end));
				
				//then: 날짜 검증 중 예외가 발생한다.
				assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining("시작월", "종료월", start, end);
			}
			
		}

	}


	@Nested
	@DisplayName("일 리스트 조회")
	class DayRange {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@ParameterizedTest(name = "[{index}] {0}일부터 {1}일까지 리스트 개수는 {2}개다.")
			@MethodSource("validDayRanges")
			@DisplayName("시작일과 종료일 모두 정상적인 값이면 리스트가 반환된다.")
			void returnsList_whenDaysAreValid(int start, int end, int size) {
				//when: 시작일과 종료일로 일 리스트를 반환한다.
				List<Integer> result = DateUtils.getDaysInRange(start, end);
				
				//then:
				assertThat(result)
						.startsWith(start)
						.endsWith(end)
						.hasSize(size);
			}

			static Stream<Arguments> validDayRanges() {
				return Stream.of(
						Arguments.of(1, 31, 31),
						Arguments.of(1, 5, 5),
						Arguments.of(10, 20, 11),
						Arguments.of(28, 30, 3)
				);
			}
			
		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest
			@MethodSource("invalidDayValues")
			@DisplayName("시작일이 1~31 사이가 아니면 IllegalArgumentException이 발생한다.")
			void throwsException_whenStartDayIsOutOfRange(int start) {
				//given: 종료일은 정상적인 값으로 준비되어 있다.
				int end = 31;

				//when: 범위에서 벗어난 종료일로 일 리스트를 반환한다.
				Throwable throwable = catchThrowable(() -> DateUtils.getDaysInRange(start, end));

				//then: 종료일 검증 중 예외가 발생한다.
				assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining("1~31", end);
			}

			@ParameterizedTest
			@MethodSource("invalidDayValues")
			@DisplayName("종료일이 1~31 사이가 아니면 IllegalArgumentException이 발생한다.")
			void throwsException_whenEndDayIsOutOfRange(int end) {
				//given: 시작일은 정상적인 값으로 준비되어 있다.
				int start = 1;

				//when: 범위에서 벗어난 종료일로 일 리스트를 반환한다.
				Throwable throwable = catchThrowable(() -> DateUtils.getDaysInRange(start, end));

				//then: 종료일 검증 중 예외가 발생한다.
				assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining("1~31", end);
			}

			static Stream<Arguments> invalidDayValues() {
				return Stream.of(
						Arguments.of(named("일이 음수인 경우", -1)),
						Arguments.of(named("일이 0인 경우", 0)),
						Arguments.of(named("일이 32인 경우", 32))
				);
			}
			
			@Test
			@DisplayName("시작일이 종료일보다 크면 IllegalArgumentException이 발생한다.")
			void throwsException_whenStartDateIsAfterEndDate() {
				//given: 시작일이 종료일보다 크게 준비되어 있다.
				int start = 25;
				int end = 15;

				//when: 시작일이 종료일보다 큰 상태로 일 리스트를 반환한다.
				Throwable throwable = catchThrowable(() -> DateUtils.getDaysInRange(start, end));

				//then: 날짜 검증 중 예외가 발생한다.
				assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining("시작일", "종료일", start, end);
			}
			
		}

	}

}
