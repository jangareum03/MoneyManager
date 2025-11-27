package com.moneymanager.ledger.category;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.domain.ledger.dto.CategorySearchRequest;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.exception.custom.ServerException;
import com.moneymanager.service.main.validation.CategoryValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
	@DisplayName("CategorySearchRequest 객체가 null이 ClientException 예외가 발생한다.")
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

	@DisplayName("가계부 유형이 없으면 ClientException 예외가 발생한다.")
	@Test
	void 가계부_유형_없으면_예외발생(){
		//given
		CategorySearchRequest request = CategorySearchRequest.ofTopCategory(null);

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> CategoryValidator.validate(request))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_CATEGORY_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("가계부 유형을 확인해주세요.");
				});
	}

	@DisplayName("카테고리 코드가 null이거나 빈 문자열이면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void 코드_없으면_예외발생(String code){
		//given
		CategorySearchRequest request = CategorySearchRequest.ofLowCategory(LedgerType.OUTLAY, code);

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
		CategorySearchRequest request = CategorySearchRequest.ofLowCategory(LedgerType.OUTLAY, code);

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> CategoryValidator.validate(request))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_CATEGORY_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("카테고리 코드는 숫자만 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(code);
				});
	}

	@DisplayName("LOW 레벨인데 카테고리 코드가 4가 아니면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(strings = {"1", "12", "123", "12345", "123445555"})
	void LOW레벨_코드길이_4아니면_예외발생(String code) {
		//given
		CategorySearchRequest request = CategorySearchRequest.ofLowCategory(LedgerType.INCOME, code);

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> CategoryValidator.validate(request))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_CATEGORY_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("카테고리 코드는 4자리입니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(code);
				});
	}

	@DisplayName("MIDDLE 레벨인데 카테고리 코드 길이는 무조건 2다.")
	@Test
	void MIDDLE레벨_코드길이2_예외없음() {
		//given
		CategorySearchRequest request = CategorySearchRequest.ofMiddleCategory(LedgerType.INCOME);

		//when & then
		assertDoesNotThrow(() ->
				CategoryValidator.validate(request)
		);
	}


//=================================================
// validateCode() 테스트
//=================================================
	@DisplayName("TOP레벨인데 코드가 있으면 ServerException 예외가 발생한다.")
	@Test
	void TOP레벨_코드있으면_예외발생(){
		//given
		CategoryLevel level = CategoryLevel.TOP;
		String parentCode = "010101";

		//when & then
		assertThatExceptionOfType(ServerException.class)
				.isThrownBy(() -> CategoryValidator.validateCode(level, parentCode))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.SYSTEM_LOGIN_INTERNAL);
					assertThat(errorDTO.getMessage()).isEqualTo("알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
				});
	}

	@DisplayName("TOP레벨인데 코드가 빈문자열이면 ServerException 예외가 발생한다.")
	@Test
	void TOP레벨_빈문자열_코드_예외발생(){
		//given
		CategoryLevel level = CategoryLevel.TOP;
		String parentCode = "";

		//when & then
		assertThatExceptionOfType(ServerException.class)
				.isThrownBy(() -> CategoryValidator.validateCode(level, parentCode))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.SYSTEM_LOGIN_INTERNAL);
					assertThat(errorDTO.getMessage()).isEqualTo("알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
				});
	}

	@DisplayName("MIDDLE 레벨인데 코드가 없으면 ServerException 예외가 발생한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void MIDDLE레벨_코드없으면_예외발생(String code){
		//given
		CategoryLevel level = CategoryLevel.MIDDLE;

		//when & then
		assertThatExceptionOfType(ServerException.class)
				.isThrownBy(() -> CategoryValidator.validateCode(level, code))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.SYSTEM_LOGIN_INTERNAL);
					assertThat(errorDTO.getMessage()).isEqualTo("알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
				});
	}

	@DisplayName("LOW 레벨인데 코드가 없으면 ServerException 예외가 발생한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void LOW레벨_코드없으면_예외발생(String code){
		//given
		CategoryLevel level = CategoryLevel.LOW;

		//when & then
		assertThatExceptionOfType(ServerException.class)
				.isThrownBy(() -> CategoryValidator.validateCode(level, code))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.SYSTEM_LOGIN_INTERNAL);
					assertThat(errorDTO.getMessage()).isEqualTo("알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
				});
	}

	@DisplayName("6글자의 숫자코드가 아니면 ServerException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(strings = {"12345", "12", "CODE34", "코드c233", "co3@"})
	void 코드_형식불일치_예외발생(String code) {
		//given
		CategoryLevel level = CategoryLevel.MIDDLE;

		//when & then
		assertThatExceptionOfType(ServerException.class)
				.isThrownBy(() -> CategoryValidator.validateCode(level, code))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.SYSTEM_LOGIN_INTERNAL);
					assertThat(errorDTO.getMessage()).isEqualTo("알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
					assertThat(errorDTO.getRequestData()).isEqualTo(code);
				});
	}

	@DisplayName("MIDDLE 레벨에 맞는 코드가 아니면 ServerException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(strings = {"000000", "010100", "020001", "011000"})
	void MIDDLE레벨_코드형식_불일치_예외발생(String code){
		//given
		CategoryLevel level = CategoryLevel.MIDDLE;

		//when & then
		assertThatExceptionOfType(ServerException.class)
				.isThrownBy(() -> CategoryValidator.validateCode(level, code))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.SYSTEM_LOGIN_INTERNAL);
					assertThat(errorDTO.getMessage()).isEqualTo("알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
					assertThat(errorDTO.getRequestData()).isEqualTo(code);
				});
	}

	@DisplayName("LOW 레벨에 맞는 코드가 아니면 ServerException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(strings = {"000000", "010000", "020001", "010110"})
	void LOW레벨_코드형식_불일치_예외발생(String code){
		//given
		CategoryLevel level = CategoryLevel.LOW;

		//when & then
		assertThatExceptionOfType(ServerException.class)
				.isThrownBy(() -> CategoryValidator.validateCode(level, code))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.SYSTEM_LOGIN_INTERNAL);
					assertThat(errorDTO.getMessage()).isEqualTo("알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
					assertThat(errorDTO.getRequestData()).isEqualTo(code);
				});
	}

	@DisplayName("TOP레벨인데 코드가 없으면 예외가 발생하지 않는다.")
	@Test
	void TOP레벨_코드없으면_예외없음(){
		//given
		CategoryLevel level = CategoryLevel.TOP;

		//when & then
		assertDoesNotThrow(() ->
				CategoryValidator.validateCode(level, null)
		);
	}
}
