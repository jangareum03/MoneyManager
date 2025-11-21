package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : LedgerDateTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 17.<br>
 * 설명              : Ledger 검증하는 테스트 클래스
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
 * 		 	  <td>25. 11. 17.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class LedgerDateTest {

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("날짜가 미입력되면 예외가 발생한다.")
	void shouldThrowExceptionWhenDateIsNullOrEmpty(String date) {
		//when&then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> new LedgerDate(date))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_DATE_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("가계부 날짜는 필수입니다.");
				});
	}

	@ParameterizedTest
	@ValueSource(strings = {"251106", "2024", "2", "202502", "2025213"})
	@DisplayName("숫자만 추출한 날짜길이가 8이 아니면 예외가 발생한다.")
	void shouldThrowExceptionWhenDigitsLengthIsNotEight(String date) {
		//when&then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> new LedgerDate(date))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_DATE_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("가계부 날짜 형식이 올바르지 않습니다. (예: 20250101)");
				});
	}

	@ParameterizedTest
	@CsvSource({
			"2025-11-15, 20251115",
			"!@#$, ''",
			"123-35a^^, 12335",
			"abc2025/02/11, 20250211"
	})
	@DisplayName("날짜에서 숫자만 추출합니다.")
	void shouldReturnDigitsOnlyWhenDateContainsSpecialAndAlphabet(String date, String result){
		//given
		LedgerDate vo = new LedgerDate("2025-01-10");

		//when
		String digits = vo.extractDigits(date);

		//then
		assertThat(digits).isEqualTo(result);
	}

	@ParameterizedTest
	@ValueSource(strings = {"20250501", "20241130", "18880101", "08910101"})
	@DisplayName("숫자로만 이루어진 날짜는 예외없이 LedgerDate 객체가 생성된다.")
	void shouldCreateObjectWhenDateHasOnlyDigits(String date) {
		//given
		LedgerDate vo = new LedgerDate(date);

		//숫자 문자열을 LocalDate로 변환
		int year = Integer.parseInt(date.substring(0,4));
		int month = Integer.parseInt(date.substring(4,6));
		int day = Integer.parseInt(date.substring(6,8));
		LocalDate expectedDate = LocalDate.of(year, month, day);

		//then
		assertDoesNotThrow(() -> {
			assertThat(vo.getDate())
					.isNotNull()
					.isEqualTo(expectedDate);
		});
	}
}
