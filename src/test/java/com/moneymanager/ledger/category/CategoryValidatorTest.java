package com.moneymanager.ledger.category;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.domain.ledger.dto.request.CategoryRequest;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.service.main.validation.CategoryValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.category<br>
 * 파일이름       : CategoryValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 26.<br>
 * 설명              : 가계부 카테고리 검증 관련 테스트 클래스
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
 * 		 	  <td>25. 11. 26.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class CategoryValidatorTest {

//=================================================
// validate() 테스트
//=================================================
	@DisplayName("CategoryRequest 객체가 null이 ClientException 예외가 발생한다.")
	@Test
	void 요청정보_없으면_예외발생(){
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> CategoryValidator.validate(null))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_CATEGORY_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("카테고리를 확인해주세요.");
				});
	}

	@DisplayName("카테고리 코드가 null이거나 빈 문자열이면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void 코드_없으면_예외발생(String code){
		//given
		CategoryRequest request = CategoryRequest.ofLowCategory(code);

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> CategoryValidator.validate(request))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_CATEGORY_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("카테고리 코드를 확인해주세요.");
				});
	}

	@DisplayName("카테고리 코드가 숫자가 아니면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(strings = {"abc", "ab23c", "숫자2", "234!!숫자"})
	void 코드_숫자아니면_예외발생(String code) {
		//given
		CategoryRequest request = CategoryRequest.ofLowCategory(code);

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> CategoryValidator.validate(request))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_CATEGORY_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("카테고리 코드는 6자리 숫자만 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(code);
				});
	}
}
