package com.moneymanager.global.domain.vo;

import com.moneymanager.global.exception.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * <p>
 * 패키지이름    : com.moneymanager.Ncommon.vo<br>
 * 파일이름       : DateRangeTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 16<br>
 * 설명              : DateRange 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 7. 16</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class DateRangeTest {

	@Nested
	@DisplayName("객체 생성")
	class CreateTest {
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("정상적인 날짜 문자열이면 DateRange 객체를 생성한다.")
			void createsDateRange_whenDateStringsAreValid() {
				//given: 시작일과 종료일이 주어진다.
				String from = "20260101";
				String to = "20261231";

				//when: DateRange를 생성한다.
				DateRange result = new DateRange(from, to);

				//then: 요청된 시작일과 종료일로 설정된다.
				assertThat(result.getFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
				assertThat(result.getTo()).isEqualTo(LocalDate.of(2026, 12, 31));
			}

			@Test
			@DisplayName("정상적인 날짜 객체면 DateRange 객체를 생성한다.")
			void createsDateRange_whenDatesAreValid() {
				//given: 시작일과 종료일 LocalDate가 주어진다.
				LocalDate from = LocalDate.of(2026, 1, 1);
				LocalDate to = LocalDate.of(2026, 1, 31);

				//when: DateRange를 생성한다.
				DateRange result = new DateRange(from, to);

				//then: 요청된 시작일과 종료일로 설정된다.
				assertThat(result.getFrom()).isEqualTo(from);
				assertThat(result.getTo()).isEqualTo(to);
			}

			@Test
			@DisplayName("시작일과 종료일이 모두 동일하면 DateRange 객체를 생성한다.")
			void createsDateRange_whenDatesAreEqual() {
				//given: 시작일과 종료일을 동일하게 주어진다.
				String date = "20260101";
				LocalDate expected = LocalDate.of(2026, 1, 1);

				//when: DateRange를 생성한다.
				DateRange result = new DateRange(date, date);

				//then: 요청된 시작일과 종료일로 설정된다.
				assertThat(result.getFrom()).isEqualTo(expected);
				assertThat(result.getTo()).isEqualTo(expected);
			}

		}


		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("날짜 문자열 검증에 실패하면 생성에 실패한다.")
			void throwsException_whenDateStringsAreInvalid() {
				//given: 시작일 날짜 포맷이 yyyy-MM-dd로 주어진다.
				String from = null;
				String to = "2026-01-31";

				//when & then: DateRange를 생성 중 ValidationException이 발생한다.
				assertThatThrownBy(() -> new DateRange(from, to))
						.isInstanceOf(ValidationException.class);
			}
			
			@Test
			@DisplayName("날짜 객체 검증에 실패하면 생성에 실패한다.")
			void throwsException_whenDatesAreInvalid() {
				//given: 시작일에 null이 주어진다.
				LocalDate from = null;
				LocalDate to = LocalDate.of(2026, 1, 31);

				//when & then: DateRange를 생성 중 ValidationException이 발생한다.
				assertThatThrownBy(() -> new DateRange(from, to))
						.isInstanceOf(ValidationException.class);
			}

		}

	}


	@Nested
	@DisplayName("차이 일자 계산")
	class CalculationTest {
		
		@Test
		@DisplayName("시작일과 종료일의 차이 일수를 반환한다.")
		void returnsDaysDiff_whenDatesAreGiven() {
			//given: 시작일과 종료일 차이가 5일 나는 DateRange가 존재한다.
			DateRange dateRange = new DateRange("20260101", "20260106");
			
			//when: 시작일과 종료일 차이 일수를 계산한다.
			long result = dateRange.daysBetween();
			
			//then: 5일이 반환된다.
			assertThat(result).isEqualTo(5);
		}
		
		@Test
		@DisplayName("시작일과 죵료일이 동일하면 0을 반환한다.")
		void returnsZero_whenDatesAreEqual() {
			//given: 시작일과 종료일이 동일한 DateRange가 존재한다.
			DateRange dateRange = new DateRange("20260101", "20260101");

			//when: 시작일과 종료일 차이 일수를 계산한다.
			long result = dateRange.daysBetween();

			//then: 0일이 반환된다.
			assertThat(result).isEqualTo(0);
		}
		
	}

}