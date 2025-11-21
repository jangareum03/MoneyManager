package com.moneymanager.utils;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.utils<br>
 * 파일이름       : DateTimeUtilsTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 19.<br>
 * 설명              : DateTimeUtils 검증하는 테스트 클래스
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
 * 		 	  <td>25. 11. 19.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class DateTimeUtilsTest {

	//=================================================
	// parseDateFlexible() 테스트
	//=================================================
	@ParameterizedTest
	@ValueSource(strings = {"15-02-2025", "오월이십일", "2025/13/01"})
	@DisplayName("지원하지 않은 형식의 날짜가 입력되면 예외가 발생한다.")
	void shouldThrowExceptionWhenUnsupportedFormat(String date) {
		//when & then
		assertThatExceptionOfType(ServerException.class)
				.isThrownBy(() -> DateTimeUtils.parseDateFlexible(date))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.SYSTEM_LOGIC_INTERVAL);
					assertThat(errorDTO.getMessage()).isEqualTo("지원하지 않은 날짜 형식입니다.");
				});
	}

	@Test
	@DisplayName("날짜범위가 벗어나면 예외가 발생한다.")
	void shouldThrowExceptionWhenDateOutOfRange() {
		//given
		String date = "20241301";

		//when&then
		assertThatExceptionOfType(Exception.class)
				.isThrownBy(() -> DateTimeUtils.parseDateFlexible(date));
	}

	//=================================================
	// parseDateFlexible() 테스트
	//=================================================
	@ParameterizedTest
	@CsvSource({
			"1990-01-19, 1990, 1, 19",
			"2024년 07월 20일, 2024, 7, 20",
			"2025/08/09, 2025, 8, 9",
			"20220523, 2022, 5, 23"
	})
	@DisplayName("지원하는 형식의 날짜는 LocalDate 객체가 생성된다.")
	void shouldCreateLocalDateWhenSupportedFormat(String date, int year, int month, int day) {
		//when
		LocalDate localDate = DateTimeUtils.parseDateFlexible(date);

		//then
		assertThat(localDate).isNotNull();
		assertThat(localDate).isEqualTo(LocalDate.of(year, month, day));
	}

	//=================================================
	// getMaxWeekByMonth() 테스트
	//=================================================
	@ParameterizedTest
	@CsvSource({
			"2025, 11, 5",
			"2025, 9, 5",
			"2025, 6, 6"
	})
	@DisplayName("월의 총 주를 반환한다.")
	void shouldReturnTotalWeeksOfMonth(int year, int month, int expected) {
		//given
		YearMonth yearMonth = YearMonth.of(year, month);

		//when
		int result = DateTimeUtils.getTotalWeeksOfMonth(yearMonth);

		//then
		assertThat(result).isEqualTo(expected);
	}
}