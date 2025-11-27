package com.moneymanager.ledger.category;

import com.moneymanager.domain.ledger.dto.CategorySearchRequest;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.service.main.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.category<br>
 * 파일이름       : CategoryGenerationTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 26.<br>
 * 설명              : 가계부 카테고리 코드 생성 관련 테스트 클래스
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
class CategoryGenerationTest {

	private CategoryService service;

	@BeforeEach
	void 초기화(){
		service = new CategoryService(null);
	}

	//=================================================
	// generateParentCode() 테스트
	//=================================================
	@DisplayName("TOP 단계에서 수입이면 null을 반환한다.")
	@Test
	void 상위_수입_null반환(){
		//given
		CategorySearchRequest request = CategorySearchRequest.ofTopCategory(LedgerType.INCOME);

		//when
		String result = service.generateParentCode( request.getLevel(), request.getParentCode());

		//then
		assertThat(result).isNull();
	}

	@DisplayName("TOP 단계에서 지출이면 null을 반환한다.")
	@Test
	void 상위_지출_null반환(){
		//given
		CategorySearchRequest request = CategorySearchRequest.ofTopCategory(LedgerType.OUTLAY);

		//when
		String result = service.generateParentCode( request.getLevel(), request.getParentCode());

		//then
		assertThat(result).isNull();
	}

	@DisplayName("MIDDLE 단계에서 수입이면 '0X0000' 반환한다.")
	@Test
	void 중간_수입_상위카테고리_반환(){
		//given
		CategorySearchRequest request = CategorySearchRequest.ofMiddleCategory(LedgerType.INCOME);

		//when
		String result = service.generateParentCode( request.getLevel(), request.getParentCode());

		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo("010000");
	}

	@DisplayName("MIDDLE 단계에서 지출이면 '0X0000' 반환한다.")
	@Test
	void 중간_지출_상위카테고리_반환(){
		//given
		CategorySearchRequest request = CategorySearchRequest.ofMiddleCategory(LedgerType.OUTLAY);

		//when
		String result = service.generateParentCode( request.getLevel(), request.getParentCode());

		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo("020000");
	}

	@DisplayName("LOW 단계에서 수입이면 '0X0X00' 반환한다.")
	@ParameterizedTest
	@ValueSource(strings = {"0101", "0103"})
	void 하위_수입_중간카테고리_반환(String code){
		//given
		CategorySearchRequest request = CategorySearchRequest.ofLowCategory(LedgerType.INCOME, code);

		//when
		String result = service.generateParentCode( request.getLevel(), request.getParentCode());

		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(code+"00");
	}

	@DisplayName("LOW 단계에서 지출이면 '0X0X00' 반환한다.")
	@ParameterizedTest
	@ValueSource(strings = {"0201", "0205"})
	void 하위_지출_중간카테고리_반환(String code){
		//given
		CategorySearchRequest request = CategorySearchRequest.ofLowCategory(LedgerType.OUTLAY, code);

		//when
		String result = service.generateParentCode( request.getLevel(), request.getParentCode());

		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(code+"00");
	}
}
