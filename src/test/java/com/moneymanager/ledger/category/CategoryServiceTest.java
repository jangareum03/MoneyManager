package com.moneymanager.ledger.category;

import com.moneymanager.dao.main.CategoryDao;
import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.domain.ledger.dto.request.CategoryRequest;
import com.moneymanager.domain.ledger.dto.response.CategoryResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.exception.custom.ServerException;
import com.moneymanager.service.main.CategoryService;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.category<br>
 * 파일이름       : CategoryServiceTest<br>
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
class CategoryServiceTest {
	@Mock
	private CategoryDao dao;
	
	@InjectMocks
	private CategoryService service;

	//=================================================
	// getAllCategoriesByCode() 테스트
	//=================================================
	@DisplayName("하위코드가 데이터베이스에 있으면 각 계층별 카테고리 목록 조회가 가능하다.")
	@Test
	void 하위코드_있으면_성공(){
		//given
		String code = "020201";

		List<Category> top = List.of(
				Category.builder().code("010000").name("수입").build(),
				Category.builder().code("020000").name("지출").build()
		);
		List<Category> middle = List.of(
				Category.builder().code("020100").name("식비").build(),
				Category.builder().code("020200").name("교통").build(),
				Category.builder().code("020300").name("문화생활").build(),
				Category.builder().code("020400").name("미용·패선").build(),
				Category.builder().code("020500").name("교육").build(),
				Category.builder().code("020600").name("주거").build(),
				Category.builder().code("020700").name("통신").build(),
				Category.builder().code("020800").name("의료").build(),
				Category.builder().code("020900").name("저축").build()
		);
		List<Category> low = List.of(
				Category.builder().code("020201").name("버스비").build(),
				Category.builder().code("020202").name("택시비").build(),
				Category.builder().code("020203").name("지하철").build(),
				Category.builder().code("020204").name("기타").build()
		);

		when(dao.findTopCategories())
				.thenReturn(top);
		when(dao.findAncestorCategoriesByCode(code))
				.thenReturn(List.of(top.get(1), middle.get(1), low.get(1)));
		when(dao.findCategoriesByParentCode("020000"))
				.thenReturn(middle);
		when(dao.findCategoriesByParentCode("020200"))
				.thenReturn(low);


		//when
		Map<String, List<CategoryResponse>> result
				= service.getAllCategoriesByCode(code);

		//then
		assertThat(result)
				.hasSize(3)
						.containsKeys(CategoryLevel.TOP.name(), CategoryLevel.MIDDLE.name(), CategoryLevel.LOW.name());

		assertThat(result.get(CategoryLevel.TOP.name()))
				.hasSize(2)
				.extracting(CategoryResponse::getCode, CategoryResponse::getName)
				.containsExactly(
					Tuple.tuple("010000", "수입"),
					Tuple.tuple("020000", "지출")
				);

		assertThat(result.get(CategoryLevel.MIDDLE.name()))
				.hasSize(9)
				.extracting(CategoryResponse::getCode, CategoryResponse::getName)
				.containsExactly(
						Tuple.tuple("020100", "식비"),
						Tuple.tuple("020200", "교통"),
						Tuple.tuple("020300", "문화생활"),
						Tuple.tuple("020400", "미용·패선"),
						Tuple.tuple("020500", "교육"),
						Tuple.tuple("020600", "주거"),
						Tuple.tuple("020700", "통신"),
						Tuple.tuple("020800", "의료"),
						Tuple.tuple("020900", "저축")
				);

		assertThat(result.get(CategoryLevel.LOW.name()))
				.hasSize(4)
				.extracting(CategoryResponse::getCode, CategoryResponse::getName)
				.containsExactly(
						Tuple.tuple("020201", "버스비"),
						Tuple.tuple("020202", "택시비"),
						Tuple.tuple("020203", "지하철"),
						Tuple.tuple("020204", "기타")
				);
	}

	@DisplayName("하위코드가 데이터베이스에 없으면 ClientException 예외가 발생한다.")
	@Test
	void 하위코드_없으면_실패(){
		//given
		String code = "030201";
		when(dao.findAncestorCategoriesByCode(code)).thenReturn(Collections.emptyList());

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> service.getAllCategoriesByCode(code))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_CATEGORY_NONE);
					assertThat(errorDTO.getMessage()).isEqualTo("카테고리를 찾을 수 없습니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(code);
				});
	}


	//=================================================
	// getMyParentCategories() 테스트
	//=================================================
	@DisplayName("상위 카테고리 값이 형변환 되어 DTO로 변환됩니다.")
	@Test
	void 상위카테고리_있으면_리스트반환(){
		//given
		when(dao.findTopCategories()).thenReturn(List.of(
				Category.builder().code("010000").name("수입").build(),
				Category.builder().code("020000").name("지출").build()
			));

		//when
		List<CategoryResponse> result = service.getTopCategories();

		//then
		assertThat(result)
				.hasSize(2)
				.extracting(CategoryResponse::getCode, CategoryResponse::getName)
				.containsExactly(
						Tuple.tuple("010000", "수입"),
						Tuple.tuple("020000", "지출")
				);
	}

	@DisplayName("상위 카테고리가 없으면 ServerException 예외가 발생합니다.")
	@Test
	void 상위카테고리_없으면_예외발생(){
		//given
		when(dao.findTopCategories()).thenReturn(Collections.emptyList());

		//when & then
		assertThatExceptionOfType(ServerException.class)
				.isThrownBy(() -> service.getTopCategories())
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.DATABASE_RESULT_INTERNAL);
					assertThat(errorDTO.getMessage()).isEqualTo("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
				});
	}


	//=================================================
	// getSubCategories() 테스트
	//=================================================
	@DisplayName("TOP레벨 아닌 하위 카테고리 값이 형변환 되어 DTO로 변환됩니다.")
	@Test
	void 하위카테고리_TOP레벨_아니면_리스트반환() {
		//given
		CategoryRequest request = CategoryRequest.ofMiddleCategory("010000");

		when(dao.findCategoriesByParentCode("010000")).thenReturn(List.of(
				Category.builder().code("010100").name("소득").build(),
				Category.builder().code("010200").name("저축").build(),
				Category.builder().code("010300").name("차입").build()
		));

		//when
		List<CategoryResponse> result = service.getSubCategories(request);

		//then
		assertThat(result)
				.hasSize(3)
				.extracting(CategoryResponse::getCode, CategoryResponse::getName)
				.containsExactly(
						Tuple.tuple("010100", "소득"),
						Tuple.tuple("010200", "저축"),
						Tuple.tuple("010300", "차입")
				);
	}


	//=================================================
	// getSubCategories() 테스트
	//=================================================
	@DisplayName("하위 카테고리 코드가 없으면 ClientException 예외가 발생한다.")
	@Test
	void DB결과_0이면_예외발생(){
		//given
		String code = "040101";

		when(dao.findAncestorCategoriesByCode(code)).thenReturn(Collections.emptyList());

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> service.getAncestorCategoriesByCode(code))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_CATEGORY_NONE);
					assertThat(errorDTO.getMessage()).isEqualTo("카테고리를 찾을 수 없습니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(code);
				});
	}

	@DisplayName("하위 카테고리로 조회시 결과가 2면 ClientException 예외가 발생한다.")
	@Test
	void DB결과_3미만_예외발생(){
		//given
		String code = "020100";

		when(dao.findAncestorCategoriesByCode(code)).thenReturn(List.of(
				Category.builder().code("020000").name("지출").build(),
				Category.builder().code("020100").name("식비").build()
		));

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> service.getAncestorCategoriesByCode(code))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_CATEGORY_NONE);
					assertThat(errorDTO.getMessage()).isEqualTo("카테고리를 찾을 수 없습니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(code);
				});
	}

	@DisplayName("하위 카테고리 조회 시 결과가 3이면 리스트를 반환된다.")
	@Test
	void DB결과_3이면_리스트반환(){
		//given
		String code = "020101";

		when(dao.findAncestorCategoriesByCode(code)).thenReturn(List.of(
				Category.builder().code("020000").name("지출").build(),
				Category.builder().code("020100").name("식비").build(),
				Category.builder().code("020101").name("식재료").build()
		));

		//when
		List<CategoryResponse> result = service.getAncestorCategoriesByCode(code);

		//then
		assertThat(result)
				.hasSize(3)
				.extracting(CategoryResponse::getCode, CategoryResponse::getName)
				.containsExactly(
						Tuple.tuple("020000", "지출"),
						Tuple.tuple("020100", "식비"),
						Tuple.tuple("020101", "식재료")
				);
	}
}
