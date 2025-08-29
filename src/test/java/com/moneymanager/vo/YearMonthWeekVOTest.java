package com.moneymanager.vo;


import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : YearMonthWeekVOTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 29.<br>
 * 설명              : YearMonthWeekVO 검증한느 테스트 클래스
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
 * 		 	  <td>25. 8. 29.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class YearMonthWeekVOTest {
	private static YearMonthVO monthVO;

	@BeforeAll
	static void setup() {
		monthVO = new YearMonthVO( new YearVO(String.valueOf(LocalDate.now().getYear())), String.valueOf(LocalDate.now().getMonthValue()) );
	}


	//실패 케이스
	@Test
	@DisplayName("주가 null이면 예외가 발생한다.")
	void shouldThrowExceptionWhenWeekIsNull(){
		assertThatThrownBy(() -> new YearMonthWeekVO(monthVO, null))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_WEEK_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("주를 입력해주세요.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"", " ", "   "})
	@DisplayName("주가 빈 문자열이면 예외가 발생한다.")
	void shouldThrowExceptionWhenWeekIsBlank(String week){
		assertThatThrownBy(() -> new YearMonthWeekVO(monthVO, week))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_WEEK_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("주를 입력해주세요.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"1주", "week1", "12!!", "이주"})
	@DisplayName("주가 숫자가 아니면 예외가 발생한다.")
	void shouldThrowExceptionWhenWeekNotNumber(String week) {
		assertThatThrownBy(() -> new YearMonthWeekVO(monthVO, week))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_WEEK_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("주는 숫자만 입력 가능합니다.");
				});
	}

	@ParameterizedTest
	@ValueSource(strings = {"0", "7", "10", "234"})
	@DisplayName("주가 숫자이나 1~6 범위가 아니면 예외가 발생한다.")
	void shouldThrowExceptionWhenWeekRange(String week) {
		assertThatThrownBy(() -> new YearMonthWeekVO(monthVO, week))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_WEEK_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("주는 1 ~ 6까지만 가능합니다.");
				});
	}


	@Test
	@DisplayName("주가 월의 최대 주 범위가 아니면 예외가 발생한다.")
	void shouldThrowExceptionWhenWeekMaxRange(){
		assertThatThrownBy(() -> new YearMonthWeekVO(new YearMonthVO(new YearVO("2023"), "2"), "6"))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_WEEK_INVALID);
					assertThat(errorDTO.getMessage()).startsWith("주의 범위는");
				});

	}


	//정상 케이스
	@ParameterizedTest
	@ValueSource(strings = {"1", "2", "3", "4", "5"})
	@DisplayName("주가 정상 범위라면 YearMonthWeekVO 생성 성공한다.")
	void shouldCreateYearMonthWeek(String week){
		YearMonthWeekVO yearMonthWeekVO = new YearMonthWeekVO(monthVO, week);

		assertThat(yearMonthWeekVO.getYear()).isEqualTo(monthVO.getYear());
		assertThat(yearMonthWeekVO.getMonth()).isEqualTo(monthVO.getMonth());
		assertThat(yearMonthWeekVO.getWeek()).isEqualTo(Integer.parseInt(week));
	}


	@ParameterizedTest
	@CsvSource({
			"1, 5",
			"2, 5",
			"3, 6",
			"4, 5",
			"5, 5",
			"6, 6",
			"7, 5",
			"8, 5",
			"9, 5",
			"10, 5",
			"11, 5",
			"12, 5"
	})
	@DisplayName("주의 최소값과 최대값 모두 정상 처리된다.")
	void shouldPassWeekMinAndMax(String month, String week){
		//given & when
		YearMonthWeekVO yearMonthWeekVO = new YearMonthWeekVO(new YearMonthVO(new YearVO("2025"), month), week);

		//then
		assertThat(yearMonthWeekVO).isNotNull();
	}


	@ParameterizedTest
	@CsvSource({
			"1, 5",
			"2, 5",
			"3, 6",
			"4, 5",
			"5, 5",
			"6, 6",
			"7, 5",
			"8, 5",
			"9, 5",
			"10, 5",
			"11, 5",
			"12, 5"
	})
	@DisplayName("월별로 최대 주를 구할 수 있다.")
	void shouldReturnCorrectMaxWeekForEachMonth(String month, int expected){
		//given
		YearMonthWeekVO yearMonthWeekVO = new YearMonthWeekVO(new YearMonthVO(new YearVO("2025"), month), "1");

		//when
		int actual = yearMonthWeekVO.getMaxWeekByMonth();

		//then
		assertThat(actual).isEqualTo(expected);

	}
}