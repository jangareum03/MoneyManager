package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.utils.DateTimeUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.YearMonth;

import static com.moneymanager.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : PeriodSearchTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 14.<br>
 * 설명              : PeriodSearch 검증하는 테스트 클래스
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
 * 		 	  <td>25. 11. 14.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class PeriodSearchTest {

	//실패케이스(공통)
	@Test
	@DisplayName("년,월,주 모두 0이면 예외가 발생한다.")
	void shouldThrowExceptionWhenAllIsZero(){
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> PeriodSearch.ofYearMonthWeek(0, 0, 0))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("조회할 년도를 확인해주세요.");
				});
	}

	@Test
	@DisplayName("년은 입력되고 월은 0이면 예외가 발생한다.")
	void shouldThrowExceptionWhenMonthIsZeroButYearIsProvided() {
		//given
		int year = 2025;
		int month = 0;

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> PeriodSearch.ofYearMonth(year, month))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("조회할 월을 확인해주세요.");
				});
	}

	//실패케이스(년)
	@Test
	@DisplayName("년이 0이면 예외가 발생한다.")
	void shouldThrowExceptionWhenYearIsZero() {
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> PeriodSearch.ofYear(0))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("조회할 년도를 확인해주세요.");
				});
	}


	@ParameterizedTest
	@ValueSource(ints = {2026, 2019, 1940})
	@DisplayName("년 범위가 현재년도 - 5년전까지가 아니면 예외가 발생한다.")
	void shouldThrowExceptionWhenYearIsOutOfRange(int year) {
		//given
		int currentYear = LocalDate.now().getYear();
		int range = 5;

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> PeriodSearch.ofYear(year))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("조회할 년도는 " + (currentYear - range) + "~" + currentYear +"까지만 가능합니다." );
				});
	}

	//실패케이스(년·월)
	@Test
	@DisplayName("월이 0이면 예외가 발생한다.")
	void shouldThrowExceptionWhenMonthIsZero() {
		//given
		int year = 2025;

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> PeriodSearch.ofYearMonth(year, 0))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("조회할 월을 확인해주세요.");
				});
	}

	@ParameterizedTest
	@ValueSource(ints = {12})
	@DisplayName("현재 년도이면 월 범위가 현재월보다 크면 예외가 발생한다.")
	void shouldThrowExceptionWhenMonthIsGreaterThanCurrentMonthInCurrentYear(int month) {
		//given
		int year = 2025;

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> PeriodSearch.ofYearMonth(year, month))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo(String.format("조회할 월은 1~%d까지만 가능합니다.", LocalDate.now().getMonthValue()) );
				});
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, 13, 2334})
	@DisplayName("과거 년도이면 월이 1~12내에 벗어나면 예외가 발생한다.")
	void shouldThrowExceptionWhenMonthIsOutOfRangeInPastYear(int month) {
		//given
		int year = 2024;

		//when&then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> PeriodSearch.ofYearMonth(year, month))
				.satisfies(e-> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("조회할 월은 1~12까지만 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(month);
				});
	}


	//실패케이스(주)
	@Test
	@DisplayName("주가 0이면 예외가 발생한다.")
	void shouldThrowExceptionWhenWeekIsZero() {
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> PeriodSearch.ofYearMonthWeek(2025, 1, 0))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("조회할 주를 확인해주세요.");
				});
	}

	@ParameterizedTest
	@ValueSource(ints = {6, 13, 2043})
	@DisplayName("주 범위가 1~N이 아니면 예외가 발생한다.")
	void shouldThrowExceptionWhenWeekIsOutOfRange(int week) {
		//given
		int year = 2025;
		int month = 8;

		//when
		int maxWeek = DateTimeUtils.getTotalWeeksOfMonth(YearMonth.of(year, month));

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> PeriodSearch.ofYearMonthWeek(year, month, week))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("조회할 주는 1~" + maxWeek +"까지만 가능합니다." );
				});
	}


	//성공케이스
	@ParameterizedTest
	@ValueSource(ints = {2020, 2024, 2025})
	@DisplayName("년만 입력하면 예외없이 PeriodSearch 객체가 생성된다.")
	void shouldCreateObjectWhenOnlyYearInput(int year){
		//when
		PeriodSearch vo = PeriodSearch.ofYear(year);

		//then
		assertDoesNotThrow(() ->{
			assertThat(vo)
					.isNotNull()
					.extracting(PeriodSearch::getYear, PeriodSearch::getMonth, PeriodSearch::getWeek)
					.containsExactly(year, null, null);
		});
	}


	@ParameterizedTest
	@CsvSource({
			"2025, 1",
			"2024, 9",
			"2023, 12"
	})
	@DisplayName("년, 월만 입력하면 에외없이 PeriodSearch 객체가 생성된다.")
	void shouldCreateObjectWhenWithoutWeekInput(int year, int month){
		//when
		PeriodSearch vo = PeriodSearch.ofYearMonth(year, month);

		//then
		assertDoesNotThrow(() -> {
			assertThat(vo)
					.isNotNull()
					.extracting(PeriodSearch::getYear, PeriodSearch::getMonth, PeriodSearch::getWeek)
					.containsExactly(year, month, null);
		});
	}

	@ParameterizedTest
	@CsvSource({
			"2025, 1, 1",
			"2024, 07, 2",
			"2022, 12, 4"
	})
	@DisplayName("년, 월, 주 모두 입력하면 에외없이 PeriodSearch 객체가 생성된다.")
	void shouldCreateObjectWhenAllInput(int year, int month, int week) {
		//when
		PeriodSearch vo = PeriodSearch.ofYearMonthWeek(year, month, week);

		//then
		assertDoesNotThrow(() -> {
			assertThat(vo)
					.isNotNull()
					.extracting(PeriodSearch::getYear, PeriodSearch::getMonth, PeriodSearch::getWeek)
					.containsExactly(year, month, week);
		});
	}
}