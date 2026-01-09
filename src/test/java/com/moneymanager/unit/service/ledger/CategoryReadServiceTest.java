package com.moneymanager.unit.service.ledger;

import com.moneymanager.repository.ledger.CategoryRepository;
import com.moneymanager.domain.ledger.dto.response.CategoryResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.exception.custom.ServerException;
import com.moneymanager.service.ledger.CategoryReadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.ledger<br>
 * 파일이름       : CategoryReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 7<br>
 * 설명              : CategoryReadService 클래스 로직을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 1. 7.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class CategoryReadServiceTest {

	@InjectMocks	private CategoryReadService service;

	@Mock				private CategoryRepository categoryRepository;

	//==================[ 📌getAllCategories  ]==================
	@Test
	@DisplayName("카테고리의 모든 정보를 조회 가능하다.")
	void  모든_카테고리_조회_가능(){
		//given
		List<Category> categories = List.of(
				Category.builder().code("010000").name("수입").parentCode(null).build()
		);
		when(categoryRepository.findAllCategory()).thenReturn(categories);

		//when
		service.getAllCategories();

		//then
		verify(categoryRepository, times(1)).findAllCategory();
	}

	@Test
	@DisplayName("모든 카테고리 조회가 안되면 ServerException이 발생한다.")
	void 모든_카테고리_없으면_예외발생() {
		//given
		when(categoryRepository.findAllCategory()).thenReturn(Collections.emptyList());

		//when & then
		assertThatExceptionOfType(ServerException.class)
				.isThrownBy(() -> service.getAllCategories())
				.withMessageContainingAll("데이터", "문제");
	}


	//==================[ 📌getCategoriesByTypeAndLevel  ]==================
	@ParameterizedTest(name = "[{index}] type={0}")
	@EnumSource(LedgerType.class)
	@DisplayName("TOP 레벨 카테고리 정보가 리스트로 조회 가능하다.")
	void 최상위_레벨_카테고리_목록_조회(LedgerType type){
		//given
		CategoryLevel level = CategoryLevel.TOP;

		when(categoryRepository.findAllCategory())
				.thenReturn(List.of(
						Category.builder().code("010000").name("수입").parentCode(null).build(),
						Category.builder().code("020000").name("지출").parentCode(null).build(),
						Category.builder().code("010100").name("소득").parentCode("010000").build(),
						Category.builder().code("020100").name("식비").parentCode("020000").build(),
						Category.builder().code("010101").name("월급").parentCode("010100").build()
				));

		//when
		List<CategoryResponse> result = service.getCategoriesByTypeAndLevel(type, level);

		//then
		assertThat(result).hasSize(2)
				.allSatisfy( c -> {
					assertThat(c.getCode())
							.hasSize(6)
							.endsWith("0000");

					assertThat(c.getName()).isNotNull();
				});
	}

	@ParameterizedTest(name = "[{index}] type={0}")
	@EnumSource(LedgerType.class)
	@DisplayName("MIDDLE 레벨 카테고리 정보가 리스트로 조회 가능하다.")
	void 중간_레벨_카테고리_목록_조회(LedgerType type){
		//given
		CategoryLevel level = CategoryLevel.MIDDLE;
		int expected = (type == LedgerType.INCOME) ? 2 : 3;

		when(categoryRepository.findAllCategory())
				.thenReturn(List.of(
						Category.builder().code("010000").name("수입").parentCode(null).build(),
						Category.builder().code("020000").name("지출").parentCode(null).build(),
						Category.builder().code("010100").name("소득").parentCode("010000").build(),
						Category.builder().code("010200").name("저축").parentCode("010000").build(),
						Category.builder().code("020100").name("식비").parentCode("020000").build(),
						Category.builder().code("020200").name("교통").parentCode("020000").build(),
						Category.builder().code("020300").name("문화생활").parentCode("020000").build(),
						Category.builder().code("010101").name("월급").parentCode("010100").build(),
						Category.builder().code("020101").name("식재료").parentCode("020100").build()
				));

		//when
		List<CategoryResponse> result = service.getCategoriesByTypeAndLevel(type, level);

		//then
		assertThat(result).hasSize(expected)
				.allSatisfy( c -> {
					assertThat(c.getCode())
							.hasSize(6)
							.endsWith("00");

					assertThat(c.getName()).isNotNull();
				});
	}

	@ParameterizedTest(name = "[{index}] type={0}")
	@EnumSource(LedgerType.class)
	@DisplayName("LOW 레벨 카테고리 정보가 리스트로 조회 가능하다.")
	void 하위_레벨_카테고리_목록_조회(LedgerType type){
		//given
		CategoryLevel level = CategoryLevel.LOW;
		int expected = (type == LedgerType.INCOME) ? 3 : 4;

		when(categoryRepository.findAllCategory())
				.thenReturn(List.of(
						Category.builder().code("010000").name("수입").parentCode(null).build(),
						Category.builder().code("020000").name("지출").parentCode(null).build(),
						Category.builder().code("010100").name("소득").parentCode("010000").build(),
						Category.builder().code("020200").name("교통").parentCode("020000").build(),
						Category.builder().code("010101").name("월급").parentCode("010100").build(),
						Category.builder().code("010201").name("예금만기").parentCode("010200").build(),
						Category.builder().code("010301").name("빌린돈").parentCode("010300").build(),
						Category.builder().code("020101").name("식재료").parentCode("020100").build(),
						Category.builder().code("020102").name("외식").parentCode("020100").build(),
						Category.builder().code("020301").name("영화").parentCode("020300").build(),
						Category.builder().code("020601").name("월세·전세").parentCode("020600").build()
				));

		//when
		List<CategoryResponse> result = service.getCategoriesByTypeAndLevel(type, level);

		//then
		assertThat(result).hasSize(expected)
				.allSatisfy( c -> {
					assertThat(c.getCode())
							.hasSize(6)
							.doesNotEndWith("00");

					assertThat(c.getName()).isNotNull();
				});
	}
}
