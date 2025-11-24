package com.moneymanager.budgetBook.domain;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.domain.budgetBook.vo.BudgetBookDate;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : BudgetBookDateTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 17.<br>
 * 설명              : 가계부 날짜 검증하는 테스트 클래스
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
class BudgetBookDateTest {

	@DisplayName("날짜가 null이거나 빈문자열이면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void 날짜_없으면_예외발생(String date){
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> new BudgetBookDate(date))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_DATE_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("가계부 날짜는 필수입니다.");
				});
	}

	@DisplayName("날짜길이가 8이 아니면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(strings = {"0", "12", "1234567"})
	void 날짜_문자추출_숫자반환(String date){
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> new BudgetBookDate(date))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_DATE_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("가계부 날짜 형식이 올바르지 않습니다. (예: 20250101)");
				});
	}
}
