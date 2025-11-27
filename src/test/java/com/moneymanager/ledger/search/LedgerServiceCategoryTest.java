package com.moneymanager.ledger.search;

import com.moneymanager.domain.ledger.dto.CategoryResponse;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.service.main.CategoryService;
import com.moneymanager.service.main.LedgerService;
import com.moneymanager.service.main.validation.CategoryValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.search<br>
 * 파일이름       : LedgerServiceCategoryTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 27.<br>
 * 설명              : 가계부 카테고리 조회 기능 관련 테스트 클래스
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
 * 		 	  <td>25. 11. 27.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class LedgerServiceCategoryTest {

	@Mock
	private CategoryService categoryService;

	@InjectMocks
	private LedgerService ledgerService;


	//=================================================
	// getAncestorCategoriesByCode() 테스트
	//=================================================
	@DisplayName("내부 메서드 호출 순서가 맞고, 반환값이 일치한다.")
	@Test
	void 메서드순서_검증_반환값_확인() {
		//given
		String code = "010101";

		List<CategoryResponse> mockResult = List.of(CategoryResponse.builder().build());
		when(categoryService.getMyParentCategories(code)).thenReturn(mockResult);

		try( MockedStatic<CategoryValidator> validatorMockedStatic = Mockito.mockStatic(CategoryValidator.class)) {
			//when
			List<CategoryResponse> result = ledgerService.getAncestorCategoriesByCode(code);

			//then
			validatorMockedStatic.verify(() -> CategoryValidator.validateCode(CategoryLevel.LOW, code));
			verify(categoryService).getMyParentCategories(code);

			assertThat(result)
					.isNotNull()
					.hasSize(1)
					.containsExactlyElementsOf(mockResult);
		}
	}
}
