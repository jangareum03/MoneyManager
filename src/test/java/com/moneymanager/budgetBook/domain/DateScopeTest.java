package com.moneymanager.budgetBook.domain;


import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.domain.budgetBook.vo.DateScope;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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

	//=================================================
	// getStartDate() 테스트
	//=================================================
	@DisplayName("연도만 있으면 해당 연도의 1월 1일을 반환한다.")
	@ParameterizedTest
	@ValueSource(ints = {2026, 2025, 2024, 2015, 2000})
	void 연도_시작날짜_반환(int year) {
		//given
		DateScope scope = DateScope.ofYear(year);

		//when
		LocalDate startDate = scope.getStartDate();

		//then
		assertThat(startDate).isEqualTo(LocalDate.of(year, 1, 1));
	}

	@DisplayName("연도+월만 있으면 해당 연도의 월의 1일을 반환한다.")
	@ParameterizedTest
	@CsvSource({
			"2026, 7",
			"2025, 10",
			"2024, 12",
			"2015, 1",
			"2000, 3"
	})
	void 연도월_시작날짜_반환(int year, int month){
		//given
		DateScope scope = DateScope.ofYearMonth(year, month);

		//when
		LocalDate startDate = scope.getStartDate();

		//then
		assertThat(startDate).isEqualTo(LocalDate.of(year, month, 1));
	}

	@DisplayName("연도+월+주 모두 있고 시작요일이 기본값이면 해당 주의 시작요일을 반환한다.")
	@ParameterizedTest
	@CsvSource({
			"2026, 7, 3, 2026-07-13",
			"2025, 10, 5, 2025-10-27",
			"2024, 12, 1, 2024-12-01",
			"2015, 1, 2, 2015-01-05",
			"2000, 3, 4, 2000-03-20"
	})
	void 연도월주_시작요일기본값_날짜반환(int year, int month, int week, String expected){
		//given
		DateScope scope = DateScope.ofYearMonthWeek(year, month, week);

		//when
		LocalDate startDate = scope.getStartDate();

		//then
		LocalDate expectedDate = LocalDate.parse(expected);
		assertThat(startDate).isEqualTo(expectedDate);
	}


	//=================================================
	// getEndDate() 테스트
	//=================================================
	@DisplayName("연도만 있으면 해당 연도의 12월 31일을 반환한다.")
	@ParameterizedTest
	@ValueSource(ints = {2026, 2025, 2024, 2015, 2000})
	void 연도_종료날짜_반환(int year) {
		//given
		DateScope scope = DateScope.ofYear(year);

		//when
		LocalDate endDate = scope.getEndDate();

		//then
		assertThat(endDate).isEqualTo(LocalDate.of(year, 12, 31));
	}

	@DisplayName("연도+월만 있으면 해당 연도의 월의 마지막일을 반환한다.")
	@ParameterizedTest
	@CsvSource({
			"2026, 7, 31",
			"2025, 9, 30",
			"2024, 2, 29",
			"2023, 2, 28"
	})
	void 연도월_종료날짜_반환(int year, int month, int expected){
		//given
		DateScope scope = DateScope.ofYearMonth(year, month);

		//when
		LocalDate endDate = scope.getEndDate();

		//then
		assertThat(endDate).isEqualTo(LocalDate.of(year, month, expected));
	}

	@DisplayName("연도+월+주 모두 있고 시작요일이 기본값이면 해당 주의 마지막요일을 반환한다.")
	@ParameterizedTest
	@CsvSource({
			"2026, 7, 3, 2026-07-19",
			"2025, 10, 5, 2025-10-31",
			"2024, 12, 1, 2024-12-01",
			"2024, 2, 5, 2024-02-29",
			"2023, 2, 5, 2023-02-28",
			"2015, 1, 2, 2015-01-11",
			"2000, 3, 4, 2000-03-26"
	})
	void 연도월주_종료요일기본값_날짜반환(int year, int month, int week, String expected){
		//given
		DateScope scope = DateScope.ofYearMonthWeek(year, month, week);

		//when
		LocalDate endDate = scope.getEndDate();

		//then
		LocalDate expectedDate = LocalDate.parse(expected);
		assertThat(endDate).isEqualTo(expectedDate);
	}


	//=================================================
	// ofYear() 테스트
	//=================================================
	@DisplayName("연도가 음수면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(ints = {-1, -100})
	void 연도_음수_예외발생(int year){
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> DateScope.ofYear(year))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("연도는 1970년부터 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(year);
				});
	}

	@DisplayName("연도가 범위(1970년 이상)에 없으면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(ints = {1969, 1960, 1900, 1000, 0})
	void 연도_범위밖_예외발생(int year){
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> DateScope.ofYear(year))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("연도는 1970년부터 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(year);
				});
	}

	@DisplayName("연도가 범위(1970년 이상)면 DateScope 객체가 생성된다.")
	@ParameterizedTest
	@ValueSource(ints = {1970, 1990, 2000, 2010, 2025, 2030})
	void 연도_범위안_DateScope_생성(int year){
		//when
		DateScope scope = DateScope.ofYear(year);

		//then
		assertThat(scope).isNotNull();
		assertThat(scope.getMonth()).isNull();
		assertThat(scope.getWeek()).isNull();

		assertThat(scope.getYear()).isEqualTo(year);
	}


	//=================================================
	// ofYearMonth() 테스트
	//=================================================
	@DisplayName("월이 음수면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(ints = {-1, -200})
	void 연도월_월음수_예외발생(int month) {
		//given
		int year = LocalDate.now().getYear();

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> DateScope.ofYearMonth(year, month))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("월은 1~12까지만 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(month);
				});
	}

	@DisplayName("월이가 범위(1~12)에 없으면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(ints = {0, 14, 17, 20, 100})
	void 연도월_월범위밖_예외발생(int month){
		//given
		int year = LocalDate.now().getYear();

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> DateScope.ofYearMonth(year, month))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("월은 1~12까지만 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(month);
				});
	}

	@DisplayName("월이 범위(1~12)면 DateScope 객체가 생성된다.")
	@ParameterizedTest
	@ValueSource(ints = {1, 3, 8, 10, 12})
	void 연도월_월범위안_DateScope_생성(int month){
		//given
		int year = LocalDate.now().getYear();

		//when
		DateScope scope = DateScope.ofYearMonth(year, month);

		//then
		assertThat(scope).isNotNull();
		assertThat(scope.getWeek()).isNull();

		assertThat(scope.getYear()).isEqualTo(year);
		assertThat(scope.getMonth()).isEqualTo(month);

	}


	//=================================================
	// ofYearMonthWeek() 테스트
	//=================================================
	@DisplayName("주가 음수면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(ints = {-1, -200})
	void 연도월주_주음수_예외발생(int week){
		//given
		int year = LocalDate.now().getYear();
		int month = LocalDate.now().getMonthValue();

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> DateScope.ofYearMonthWeek(year, month, week))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("주는 1~6까지만 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(week);
				});
	}

	@DisplayName("주가 범위(1~6)에 없으면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(ints = {0, 7, 10, 16, 100})
	void 연도월주_주범위밖_예외발생(int week){
		//given
		int year = LocalDate.now().getYear();
		int month = LocalDate.now().getMonthValue();

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> DateScope.ofYearMonthWeek(year, month, week))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_DATE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("주는 1~6까지만 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(week);
				});
	}

	@DisplayName("주가 범위(1~6)면 DateScope 객체가 생성된다.")
	@ParameterizedTest
	@ValueSource(ints = {1, 2, 5, 6})
	void 연도월주_주범위안_DateScope_생성(int week){
		//given
		int year = LocalDate.now().getYear();
		int month = LocalDate.now().getMonthValue();

		//when
		DateScope scope = DateScope.ofYearMonthWeek(year, month, week);

		//then
		assertThat(scope).isNotNull();

		assertThat(scope.getYear()).isEqualTo(year);
		assertThat(scope.getMonth()).isEqualTo(month);
		assertThat(scope.getWeek()).isEqualTo(week);
	}
}