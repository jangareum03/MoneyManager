package com.moneymanager.ledger.domain;

import com.moneymanager.domain.ledger.enums.FixedPeriod;
import com.moneymanager.domain.ledger.vo.FixedStatus;
import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain<br>
 * 파일이름       : FixStatusTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 24.<br>
 * 설명              : FixStatus 검증하는 테스트 클래스
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
class FixStatusTest {

	@DisplayName("고정여부가 y인데 고정주기가 없으면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@NullSource
	void 고정주기_없으면_예외발생(FixedPeriod period){
		//given
		boolean fixed = true;

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() ->new FixedStatus(fixed, period))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_FIX_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("고정 주기는 필수입니다.");
				});
	}

	@DisplayName("고정여부가 n인데 고정주기가 있으면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@EnumSource(FixedPeriod.class)
	void 고졍여부가_없는데_고정주기_있으면_예외발생(FixedPeriod period){
		//given
		boolean fixed = false;

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() ->new FixedStatus(fixed, period))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_FIX_INVALID);
					assertThat(errorDTO.getMessage()).isEqualTo("고정 여부를 확인해주세요.");
				});
	}

	@DisplayName("고정여부가 y이고 고정주기가 있으면 객체를 반환한다.")
	@ParameterizedTest
	@EnumSource(FixedPeriod.class)
	void 고정여부_y_고정주기_있으면_객체반환(FixedPeriod period){
		//given
		boolean fixed = true;

		//when
		FixedStatus result = new FixedStatus(fixed, period);

		//then
		assertThat(result).isNotNull();
		assertThat(result.isFixed()).isTrue();

		assertThat(result.getPeriod()).isNotNull();
		switch (period) {
			case YEARLY :
				assertThat(result.getPeriod().getLabel()).isEqualTo("일년");
				assertThat(result.getPeriod().getDbValue()).isEqualTo("Y");
				break;
			case MONTHLY:
				assertThat(result.getPeriod().getLabel()).isEqualTo("한달");
				assertThat(result.getPeriod().getDbValue()).isEqualTo("M");
				break;
			case WEEKLY:
				assertThat(result.getPeriod().getLabel()).isEqualTo("일주일");
				assertThat(result.getPeriod().getDbValue()).isEqualTo("W");
		}
	}
}
