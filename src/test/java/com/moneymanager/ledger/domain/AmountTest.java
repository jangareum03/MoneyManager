package com.moneymanager.ledger.domain;

import com.moneymanager.domain.ledger.vo.Amount;
import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain<br>
 * 파일이름       : AmountTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 24.<br>
 * 설명 			   : Amount 검증하는 테스트 클래스
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
 * 		 	  <td>25. 11. 24.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class AmountTest {

	@DisplayName("금액이 없다면 ClientException 예외가 발생한다.")
	@Test
	void 금액_없으면_예외발생(){
		//given
		long amount = 0;

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> new Amount(amount))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_PRICE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("금액은 0보다 커야합니다.");
				});
	}

	@DisplayName("금액이 0이하면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(longs = {0, -1, -10, -100, -10000})
	void 금액_0이하_예외발생(long amount){
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> new Amount(amount))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_PRICE_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("금액은 0보다 커야합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(amount);
				});
	}

	@DisplayName("금액이 있으면 객체가 생성된다.")
	@ParameterizedTest
	@ValueSource(longs = {1000, 200, 3000000})
	void 금액_있으면_객체반환(long amount){
		//when
		Amount result = new Amount(amount);

		//when & then
		assertThat(result).isNotNull();
		assertThat(result.getAmount()).isEqualTo(amount);
	}

}
