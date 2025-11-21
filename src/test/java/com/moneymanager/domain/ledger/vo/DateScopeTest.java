package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.moneymanager.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : DateScopeTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 14.<br>
 * 설명              : DateScope 검증하는 테스트 클래스
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
class DateScopeTest {

	@Test
	@DisplayName("년,월,주 모두 0이면 예외가 발생한다.")
	void shouldThrowExceptionWhenAllIsZero(){
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> DateScope.ofYearMonthWeek(0, 0, 0))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("연도는 1970년부터 가능합니다.");
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
				.isThrownBy(() -> DateScope.ofYearMonth(year, month))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("월은 1~12까지만 가능합니다.");
				});
	}

	@Test
	@DisplayName("년이 0이면 예외가 발생한다.")
	void shouldThrowExceptionWhenYearIsZero() {
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> DateScope.ofYear(0))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("연도는 1970년부터 가능합니다.");
				});
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, 0, 13, 200})
	@DisplayName("월의 범위가 벗어나면 예외가 발생한다.")
	void shouldThrowExceptionWhenMonthOutOfRange(int month) {
		//when&then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> DateScope.ofYearMonth(2024, month))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("월은 1~12까지만 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(month);
				});
	}

	@Test
	@DisplayName("주가 0이면 예외가 발생한다.")
	void shouldThrowExceptionWhenWeekIsZero() {
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> DateScope.ofYearMonthWeek(2025, 1, 0))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("주는 1~6까지만 가능합니다.");
				});
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, 7, 8, 100})
	@DisplayName("주의 범위가 벗어나면 예외가 발생한다.")
	void shouldThrowExceptionWhenWeekOutOfRange(int week) {
		//when&then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> DateScope.ofYearMonthWeek(2024, 1, week))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("주는 1~6까지만 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(week);
				});
	}

	@ParameterizedTest
	@ValueSource(ints = {2020, 2024, 2025})
	@DisplayName("년만 입력하면 예외없이 DateScope 객체가 생성된다.")
	void shouldCreateObjectWhenOnlyYearInput(int year){
		//when
		DateScope vo = DateScope.ofYear(year);

		//then
		assertDoesNotThrow(() ->{
			assertThat(vo)
					.isNotNull()
					.extracting(DateScope::getYear, DateScope::getMonth, DateScope::getWeek)
					.containsExactly(year, null, null);
		});
	}


	@ParameterizedTest
	@CsvSource({
			"2025, 1",
			"2024, 9",
			"2023, 12"
	})
	@DisplayName("년, 월만 입력하면 에외없이 DateScope 객체가 생성된다.")
	void shouldCreateObjectWhenWithoutWeekInput(int year, int month){
		//when
		DateScope vo = DateScope.ofYearMonth(year, month);

		//then
		assertDoesNotThrow(() -> {
			assertThat(vo)
					.isNotNull()
					.extracting(DateScope::getYear, DateScope::getMonth, DateScope::getWeek)
					.containsExactly(year, month, null);
		});
	}

	@ParameterizedTest
	@CsvSource({
			"2025, 1, 1",
			"2024, 07, 2",
			"2022, 12, 4"
	})
	@DisplayName("년, 월, 주 모두 입력하면 에외없이 DateScope 객체가 생성된다.")
	void shouldCreateObjectWhenAllInput(int year, int month, int week) {
		//when
		DateScope vo = DateScope.ofYearMonthWeek(year, month, week);

		//then
		assertDoesNotThrow(() -> {
			assertThat(vo)
					.isNotNull()
					.extracting(DateScope::getYear, DateScope::getMonth, DateScope::getWeek)
					.containsExactly(year, month, week);
		});
	}
}