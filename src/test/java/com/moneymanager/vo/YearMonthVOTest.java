package com.moneymanager.vo;


import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : YearMonthVOTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 28.<br>
 * 설명              : YearMonthVO 검증하는 테스트 클래스
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
 * 		 	  <td>25. 8. 28.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class YearMonthVOTest {

	private static YearVO successYear;

	@BeforeAll
	static void setup() {
		successYear = new YearVO("2025", 5);
	}

	@Test
	@DisplayName("월이 null이면 COMMON_MONTH_MISSING 예외가 발생한다.")
	void shouldThrowExceptionWhenMonthIsNull() {
		assertThatThrownBy(() -> new YearMonthVO(successYear, null))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();


					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_MONTH_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("월을 입력해주세요.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"", "일월", "abc", "12#"})
	@DisplayName("월이 숫자가 아니면 COMMON_MONTH_FORMAT 예외가 발생한다.")
	void shouldThrowExceptionWhenMonthNotNumber(String month) {
		assertThatThrownBy(() -> new YearMonthVO(successYear, month))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_MONTH_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("월은 숫자만 입력 가능합니다.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"13", "0010020","0", "00", "123", "-1", "-0"})
	@DisplayName("월이 범위에 벗어나면 COMMON_MONTH_FORMAT 예외가 발생한다.")
	void shouldThrowExceptionWhenMonthRange(String month) {
		assertThatThrownBy(() -> new YearMonthVO(successYear, month))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_MONTH_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("월은 1~12까지만 가능합니다.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"9", "12", "10"})
	@DisplayName("올해의 미래월이면 isFutureMonth()가 true를 반환한다.")
	void shouldReturnTrueForFutureMonthInCurrentYear(String month){
		assertThat(new YearMonthVO(successYear, month).isFutureMonth()).isTrue();
	}


	@ParameterizedTest
	@ValueSource(strings = {"1", "5", "8"})
	@DisplayName("올해의 과거월이면 isFutureMonth()가 false을 반환한다.")
	void shouldReturnFalseForPastMonthInCurrentYear(String month){
		assertThat(new YearMonthVO(successYear, month).isFutureMonth()).isFalse();
	}


	@ParameterizedTest
	@ValueSource(strings = {"1", "2", "11", "12"})
	@DisplayName("월은 정상이고, 연도가 문제이면 COMMON_YEAR_XXX 예외가 발생한다.")
	void shouldThrowYearExceptionWithoutMonthError(String month) {
		assertThatThrownBy(() -> new YearMonthVO(new YearVO(""), month))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode().name()).startsWith("COMMON_YEAR");
					assertThat(errorDTO.getMessage()).startsWith("년도");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"01", "002", "5", "6", "9", "10", "12"})
	@DisplayName("정상 범위 내 년,월이면 YearMonthVO 생성 성공한다.")
	void shouldCreateYearMonthVO(String month) {
		YearMonthVO vo = new YearMonthVO(successYear, month);

		assertThat(vo.getYear()).isEqualTo(LocalDate.now().getYear());
		assertThat(vo.getMonth()).isEqualTo(Integer.parseInt(month));
	}

}