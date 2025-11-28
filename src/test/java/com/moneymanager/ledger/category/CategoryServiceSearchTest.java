package com.moneymanager.ledger.category;

import com.moneymanager.dao.main.CategoryDao;
import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.domain.ledger.dto.CategoryResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.exception.custom.ServerException;
import com.moneymanager.service.main.CategoryService;
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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.category<br>
 * 파일이름       : CategoryServiceSearchTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 26.<br>
 * 설명              : 가계부 카테고리 코드 조회 관련 테스트 클래스
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
@ExtendWith(MockitoExtension.class)
class CategoryServiceSearchTest {
	@Mock
	private CategoryDao dao;
	
	@InjectMocks
	private CategoryService service;

	//=================================================
	// getMyParentCategories() 테스트
	//=================================================
	@DisplayName("주어진 코드 없이 최상위 카테고리 리스트를 조회한다.")
	@Test
	void 최상위카테고리_있으면_리스트반환(){
		//given
		List<Category> mockDao = List.of(
				Category.builder().code("010000").name("수입").build(),
				Category.builder().code("020000").name("지출").build()
		);

		when(dao.findTopCategories()).thenReturn(mockDao);

		//when
		List<CategoryResponse> result = service.getTopCategories();

		//then
		verify(dao, times(1)).findTopCategories();

		assertThat(result).hasSize(mockDao.size());
		assertThat(result.get(0).getName()).isEqualTo("수입");
		assertThat(result.get(1).getName()).isEqualTo("지출");
	}

	@DisplayName("최상위 카테고리가 DB에서 없으면 ServerException 예외가 발생한다.")
	@Test
	void 최상위카테고리_없으면_예외발생() {
		//given
		when(dao.findTopCategories()).thenReturn(List.of());

		//when & then
		assertThatExceptionOfType(ServerException.class)
				.isThrownBy(() -> service.getTopCategories())
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.DATABASE_QUERY_RESULT);
					assertThat(errorDTO.getMessage()).isEqualTo("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
				});
	}


	//=================================================
	// getAncestorCategoriesByCode() 테스트
	//=================================================
	@DisplayName("내부 메서드 호출 순서가 맞고, 반환값이 일치한다.")
	@Test
	void 메서드순서_검증_반환값_확인() {
		//given
		String code = "010101";

		List<CategoryResponse> mockResult = List.of(CategoryResponse.builder().build());
		when(service.getMyParentCategories(code)).thenReturn(mockResult);

		try( MockedStatic<CategoryValidator> validatorMockedStatic = Mockito.mockStatic(CategoryValidator.class)) {
			//when
			List<CategoryResponse> result = service.getAncestorCategoriesByCode(code);

			//then
			validatorMockedStatic.verify(() -> CategoryValidator.validateCode(CategoryLevel.LOW, code));
			verify(service).getMyParentCategories(code);

			assertThat(result)
					.isNotNull()
					.hasSize(1)
					.containsExactlyElementsOf(mockResult);
		}
	}


	//=================================================
	// getMyParentCategories() 테스트
	//=================================================
	@DisplayName("코드가 없으면 ClientException 예외가 발생한다.")
	@Test
	void 코드_없으면_예외발생() {
		//given
		String code = "010108";

		when(dao.findAncestorCategoriesByCode(code)).thenReturn(List.of());

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> service.getMyParentCategories(code))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_CATEGORY_NONE);
					assertThat(errorDTO.getMessage()).isEqualTo("카테고리를 찾을 수 없습니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(code);
				});
	}

	@DisplayName("코드가 있으면 자신의 상위 카테고리 리스트를 반환한다.")
	@Test
	void 코드_있으면_리스트반환(){
		//given
		String code = "010101";

		when(dao.findAncestorCategoriesByCode(code)).thenReturn(List.of(
				Category.builder().code("010000").name("수입").build(),
				Category.builder().code("010100").name("소득").build(),
				Category.builder().code(code).name("월급").build()
		));

		//when
		List<CategoryResponse> result = service.getMyParentCategories(code);

		//then
		assertThat(result).hasSize(3);

		assertThat(result.get(0).getCode()).isEqualTo("010000");
		assertThat(result.get(1).getCode()).isEqualTo("010100");
		assertThat(result.get(2).getCode()).isEqualTo(code);
		assertThat(result.get(2).getName()).isEqualTo("월급");

	}
}
