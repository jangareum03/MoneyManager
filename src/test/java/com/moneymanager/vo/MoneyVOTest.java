package com.moneymanager.vo;


import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : MoneyVOTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 31.<br>
 * 설명              : MoneyVO 검증하는 테스트 클래스
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
 * 		 	  <td>25. 8. 31.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class MoneyVOTest {

	@Test
	@DisplayName("금액이 null이면 COMMON_AMOUNT_MISSING 예외가 발생한다.")
	void shouldThrowExceptionWhenAmountIsNull() {
		//given
		String amount = null;

		//when&then
		assertThatThrownBy(() -> new MoneyVO(amount))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_AMOUNT_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("금액을 입력해주세요.");
				});
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " ", "    "})
	@DisplayName("금액이 빈 문자열이면 COMMON_AMOUNT_MISSING 예외가 발생한다.")
	void shouldThrowExceptionWhenAmountIsBlank(String amount) {
		assertThatThrownBy(() -> new MoneyVO(amount))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_AMOUNT_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("금액을 입력해주세요.");
				});
	}

	@ParameterizedTest
	@ValueSource(strings = {"천원", "1000a", "123!", "1,000", "55000\\"})
	@DisplayName("금액이 숫자가 아니면 COMMON_AMOUNT_FORMAT 예외가 발생한다.")
	void shouldThrowExceptionWhenAmountNotNumber(String amount) {
		assertThatThrownBy(() -> new MoneyVO(amount))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_AMOUNT_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("금액은 숫자만 입력 가능합니다.");
				});
	}

	@ParameterizedTest
	@ValueSource(strings = {"100.0", "12.345"})
	@DisplayName("금액이 정수가 아니면 COMMON_AMOUNT_FORMAT 예외가 발생한다.")
	void shouldThrowExceptionWhenAmountNotInteger(String amount) {
		assertThatThrownBy(() -> new MoneyVO(amount))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_AMOUNT_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("금액은 1원 단위로 입력해주세요.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"-123", "-1", "0"})
	@DisplayName("금액은 1 이상이 아니면 COMMON_AMOUNT_INVALID 예외가 발생한다.")
	void shouldThrowExceptionWhenAmountIsLessThanOne(String amount) {
		assertThatThrownBy(() -> new MoneyVO(amount))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_AMOUNT_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("금액은 0보다 큰 숫자를 입력해주세요.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"20", "2000", "1550", "55400000"})
	@DisplayName("금액이 정상범위라면 MoneyVO 생성이 성공한다.")
	void shouldCreateMoneyVO(String amount) {
		MoneyVO vo = new MoneyVO(amount);

		assertThat(vo.getAmount()).isEqualTo(Integer.parseInt(amount));
	}
}