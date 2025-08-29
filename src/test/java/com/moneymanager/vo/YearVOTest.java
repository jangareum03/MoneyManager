package com.moneymanager.vo;

import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
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
 * 파일이름       : YearVOTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 28.<br>
 * 설명              : YearVO 검증하는 테스트 클래스
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
class YearVOTest {

	@Test
	@DisplayName("년도가 null이면 COMMON_YEAR_MISSING 예외가 발생한다.")
	void shouldThrowExceptionWhenYearIsNull() {
		assertThatThrownBy(() -> new YearVO(null))
				.isInstanceOf(ClientException.class)
				.satisfies( e -> {
					ClientException ce = (ClientException) e;

					assertThat(ce.getErrorDTO().getErrorCode()).isSameAs(ErrorCode.COMMON_YEAR_MISSING);
					assertThat(ce.getMessage()).isEqualTo("년도를 입력해주세요.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"", "한글", "abc", "2015^"})
	@DisplayName("년도가 숫자가 아니면 COMMON_YEAR_FORMAT 예외가 발생한다.")
	void shouldThrowExceptionWhenYearNotNumber(String year) {
		assertThatThrownBy( () -> new YearVO(year))
				.isInstanceOf(ClientException.class)
				.satisfies( e -> {
					ClientException ce = (ClientException) e;

					assertThat(ce.getErrorDTO().getErrorCode()).isSameAs(ErrorCode.COMMON_YEAR_FORMAT);
					assertThat(ce.getErrorDTO().getMessage()).isEqualTo("년도는 숫자만 입력 가능합니다.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"0212", "3125", "215", "20156"})
	@DisplayName("년도가 RegexPattern과 일치하지 않으면 COMMON_YEAR_FORMAT 예외가 발생한다.")
	void shouldThrowExceptionWhenYearMisMatchRegexPattern(String year) {
		assertThatThrownBy(() -> new YearVO(year))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_YEAR_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("년도는 1 또는 2로 시작하는 4자리 숫자만 입력 가능합니다.");
				});
	}

	@ParameterizedTest
	@ValueSource(strings = {"2015", "2016", "2014", "2026"})
	@DisplayName("년도가 범위에서 벗어나면 COMMON_YEAR_INVALID 예외가 발생한다.")
	void shouldThrowExceptionWhenYearRange(String year) {
		assertThatThrownBy(() -> new YearVO(year, 5))
				.isInstanceOf(ClientException.class)
				.satisfies( e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_YEAR_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo(String.format("년도는 %d ~ %d까지만 입력가능합니다.", LocalDate.now().getYear() - 5, LocalDate.now().getYear()));
				});
	}


	@Test
	@DisplayName("정상 범위 내 년도면 YearVO 생성 성공한다.")
	void shouldCrateYearVOWhenValidYear() {
		int currentYear = LocalDate.now().getYear();
		YearVO vo = new YearVO(String.valueOf(currentYear), 5);

		assertThat(vo.getYear()).isEqualTo(currentYear);
	}
}