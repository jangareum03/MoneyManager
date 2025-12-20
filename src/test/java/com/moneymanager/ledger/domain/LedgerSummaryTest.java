package com.moneymanager.ledger.domain;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.domain.ledger.vo.LedgerSummary;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.info<br>
 * 파일이름       : LedgerSummaryTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 1<br>
 * 설명              : LedgerSummary 클래스를 검증하는 테스트 클래스
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
 * 		 	  <td>25. 12. 1.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class LedgerSummaryTest {

	@DisplayName("Ledger 엔티티가 null이면 ClientException 예외가 발생한다.")
	@Test
	void entity_null이면_변환실패(){
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> LedgerSummary.from(null))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO).isNotNull();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_THIS_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("가계부 정보를 확인해주세요.");
					assertThat(errorDTO.getRequestData()).isEqualTo("Ledger");
				});
	}

	@DisplayName("Ledger 엔티티의 id가 null, ''이면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void entity_id필드_null이면_변환실패(String id){
		//given
		Ledger ledger = Ledger.builder()
				.id(id)
				.category("020301")
				.memo("집에서 회사")
				.amount(12000L)
				.build();

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> LedgerSummary.from(ledger))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO).isNotNull();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_ID_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("가계부 번호를 확인해주세요.");
				});
	}


	@DisplayName("Ledger 엔티티를 LedgerSummary VO로 변환이 된다.")
	@Test
	void entity_vo로_변환성공(){
		//given
		Ledger ledger = Ledger.builder()
				.id("01ARZ3NDEKTSV4RRFFQ69G5FAV")
				.category("020301")
				.memo("집에서 회사")
				.amount(12000L)
				.build();

		//when
		LedgerSummary result = LedgerSummary.from(ledger);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo("01ARZ3NDEKTSV4RRFFQ69G5FAV");
		assertThat(result.getType()).isSameAs(LedgerType.OUTLAY);
		assertThat(result.getMemo()).isEqualTo("집에서 회사");
		assertThat(result.getAmount()).isSameAs(ledger.getAmount());

	}

	@Test
	@DisplayName("값이 동일한 객체면 tru를 반환한다.")
	void 두객체_동일하면_true반환(){
		//given
		Ledger ledger = Ledger.builder()
				.id("01ARZ3NDEKTSV4RRFFQ69G5FAV")
				.category("020301")
				.memo("집에서 회사")
				.amount(12000L)
				.build();

		LedgerSummary obj1 = LedgerSummary.from(ledger);
		LedgerSummary obj2 = LedgerSummary.from(ledger);

		//when
		boolean result = obj1.equals(obj2);

		//then
		assertThat(result).isTrue();
	}


}
