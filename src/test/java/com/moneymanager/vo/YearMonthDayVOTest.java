package com.moneymanager.vo;

import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static com.moneymanager.vo.YearMonthDayVO.parseDate;
import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : YearMonthDayVOTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 29.<br>
 * 설명              : YearMonthDayVO 검증하는 테스트 클래스
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
class YearMonthDayVOTest {

	private static YearMonthVO vo;

	@BeforeAll
	static void setup() {
		vo = new YearMonthVO(new YearVO("2025", 5), "5");
	}


	@Test
	@DisplayName("일이 null이면 COMMON_DAY_MISSING 예외가 발생한다.")
	void shouldThrowExceptionWhenDayIsNull() {
		assertThatThrownBy(() -> new YearMonthDayVO(vo, null))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_DAY_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("일을 입력해주세요.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"", "1일", "abc", "17!"})
	@DisplayName("일이 숫자가 아니면 COMMON_DAY_FORMAT 예외가 발생한다.")
	void shouldThrowExceptionWhenDayNotNumber(String day) {
		assertThatThrownBy(() -> new YearMonthDayVO(vo, day))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_DAY_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("일은 숫자만 입력 가능합니다.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"-1", "0", "-0", "40", "100", "203495"})
	@DisplayName("일이 정규식패턴과 일치하지 않으면 COMMON_DAY_FORMAT 예외가발생한다.")
	void shouldThrowExceptionWhenDayMisMatchRegexPattern(String day) {
		assertThatThrownBy(() -> new YearMonthDayVO(vo, day))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_DAY_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("일은 1 또는 2 또는 3으로 시작하는 최대 2자리 숫자만 입력 가능합니다.");
				});
	}


	@Disabled
	@ParameterizedTest
	@CsvSource({
			"28, true",
			"29, true",
			"30, false",
			"31, false"
	})
	@DisplayName("윤년의 2월에서 일(day)이 유효 범위 안에 있으면 true, 아니면 false를 반환한다")
	void shouldValidateDayRange_whenLeapYearFebruary(String day, boolean expected) {
		// given
		YearMonthDayVO feb28 = new YearMonthDayVO(new YearMonthVO(new YearVO("2024"), "2"), day); // 2024년은 윤년 → 2월 29일까지 가능

		//when
		boolean actual = feb28.isValidDayRange();

		//then
		assertThat(actual).isEqualTo(expected);
	}


	@Disabled
	@ParameterizedTest
	@CsvSource({
			"28, true",
			"29, false",
			"30, false",
			"31, false"
	})
	@DisplayName("평년의 2월에서 일(day)이 유효 범위 안에 있으면 true, 아니면 false를 반환한다")
	void shouldValidateDayRange_whenCommonYearFebruary(String day, boolean expected) {
		// given
		YearMonthDayVO feb29 = new YearMonthDayVO(new YearMonthVO(new YearVO("2025"), "2"), day);

		//when
		boolean actual = feb29.isValidDayRange();

		//then
		assertThat(actual).isEqualTo(expected);
	}


	@Disabled
	@ParameterizedTest
	@CsvSource({
			"28, true",
			"29, true",
			"30, true",
			"31, false"
	})
	@DisplayName("일이 30일 최대인 월에 해당하는 일의 범위가 isValidDayRange()가 true/false을 반환한다.")
	void shouldValidateDayRange_whenMonthHas30Days(String day, boolean expected) {
		// given
		YearMonthDayVO april = new YearMonthDayVO(new YearMonthVO(new YearVO("2025"), "4"), day);

		//when
		boolean actual = april.isValidDayRange();

		//then
		assertThat(actual).isEqualTo(expected);
	}


	@Disabled
	@ParameterizedTest
	@CsvSource({
			"28, true",
			"29, true",
			"30, true",
			"31, true"
	})
	@DisplayName("일이 31일 최대인 월에 해당하는 일의 범위가 isValidDayRange()가 true/false을 반환한다.")
	void shouldValidateDayRange_whenMonthHas31Days(String day, boolean expected) {
		// given
		YearMonthDayVO august = new YearMonthDayVO(new YearMonthVO(new YearVO("2025"), "8"), day);

		//when
		boolean actual = august.isValidDayRange();

		//then
		assertThat(actual).isEqualTo(expected);
	}


	@ParameterizedTest
	@ValueSource(strings = {"1", "31"})
	@DisplayName("일이 최소값과 최대값일 때 정상 동작한다.")
	void shouldReturnTrueForDayMinAndMax(String day) {
		YearMonthDayVO may = new YearMonthDayVO(vo, day);
		assertThat(may.isValidDayRange()).isTrue();
	}


	@Test
	@DisplayName("12월 31일은 유효한 날짜이다.")
	void shouldReturnTrueForDecember31() {
		YearMonthDayVO dec31 = new YearMonthDayVO(new YearMonthVO(new YearVO("2025"), "12"), "31");

		assertThat(dec31.isValidDayRange()).isTrue();
	}


	@ParameterizedTest
	@ValueSource(strings = {"20250228", "2025년 05월 09일", "2025년12월 25일"})
	@DisplayName("정상적인 날짜 문자열로 YearMonthDayVO 생성 성공한다.")
	void shouldCreateYearMonthDayVOByDate(String date) {
		YearMonthDayVO yearMonthDayVO = YearMonthDayVO.fromString(date);

		LocalDate expected = parseDate(date);

		assertThat(yearMonthDayVO.getYear()).isEqualTo(expected.getYear());
		assertThat(yearMonthDayVO.getMonth()).isEqualTo(expected.getMonthValue());
		assertThat(yearMonthDayVO.getDay()).isEqualTo(expected.getDayOfMonth());
	}


	@Test
	@DisplayName("문자열 날짜가 NULL이면 COMMON_DATE_MISSING 예외가 발생한다.")
	void shouldThrowExceptionWhenDateIsNull() {
		assertThatThrownBy(() -> YearMonthDayVO.fromString(null))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;

					assertThat(ce.getErrorDTO().getErrorCode()).isSameAs(ErrorCode.COMMON_DATE_MISSING);
					assertThat(ce.getErrorDTO().getMessage()).startsWith("날짜를 입력해주세요.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"20250231", "2025년 02월 31일", "2025년 04월 31일"})
	@DisplayName("존재하지 않은 날짜 문자열이면 COMMON_DATE_FORMAT 예외가 발생한다.")
	void shouldThrowExceptionForNonexistentDate(String date){
		assertThatThrownBy(() -> YearMonthDayVO.fromString(date))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;

					assertThat(ce.getErrorDTO().getErrorCode()).isSameAs(ErrorCode.COMMON_DATE_FORMAT);
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"abce-3f-ge", "2025년 3월 12일", "2023뇬1웜1일"})
	@DisplayName("잘못된 형식의 날짜 문자열이면 COMMON_DATE_FORMAT 예외가 발생한다.")
	void shouldThrowExceptionForInvalidFormat(String date) {
		assertThatThrownBy(() -> YearMonthDayVO.fromString(date))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;

					assertThat(ce.getErrorDTO().getErrorCode()).isSameAs(ErrorCode.COMMON_DATE_FORMAT);
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"01", "021", "17", "30"})
	@DisplayName("정상 범위 내 년,월,일이면 YearMonthDayVO 생성 성공한다.")
	void shouldCreateYearMonthDayVO(String day) {
		YearMonthDayVO yearMonthDayVO = new YearMonthDayVO(vo, day);

		assertThat(yearMonthDayVO.getYear()).isEqualTo(vo.getYear());
		assertThat(yearMonthDayVO.getMonth()).isEqualTo(vo.getMonth());
		assertThat(yearMonthDayVO.getDay()).isEqualTo(Integer.parseInt(day));
	}
}