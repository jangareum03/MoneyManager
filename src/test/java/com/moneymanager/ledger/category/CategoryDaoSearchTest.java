package com.moneymanager.ledger.category;

import com.moneymanager.dao.main.CategoryDao;
import com.moneymanager.domain.ledger.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.category<br>
 * 파일이름       : CategoryDaoSearchTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 27.<br>
 * 설명              : 데이터베이스에서 카테고리 조회 관련 테스트 클래스
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
@SpringBootTest
@ActiveProfiles("test")
class CategoryDaoSearchTest {

	@Autowired
	private CategoryDao categoryDao;

	//=================================================
	// findTopCategories() 테스트
	//=================================================
	@DisplayName("부모 카테고리가 없는 최상위 카테고리 목록을 조회한다.")
	@Test
	void 상위_카테고리_리스트반환() {
		//when
		List<Category> result = categoryDao.findTopCategories();

		//then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);

		assertThat(result.get(0).getName()).isEqualTo("수입");
		assertThat(result.get(0).getCode()).isEqualTo("010000");
		assertThat(result.get(1).getName()).isEqualTo("지출");
		assertThat(result.get(1).getCode()).isEqualTo("020000");
	}


	//=================================================
	// findCategoryCodesByParentCode() 테스트
	//=================================================
	@DisplayName("parent_code가 상위카테고리 코드이면 중간 카테고리 리스트를 반환한다.")
	@Test
	void 부모코드_상위카테고리_리스트_반환(){
		//given
		String parentCode = "010000";

		//when
		List<Category> result = categoryDao.findCategoryCodesByParentCode(parentCode);

		//then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(3);

		assertThat(result.get(0).getName()).isEqualTo("소득");
		assertThat(result.get(0).getCode()).isEqualTo("010100");
		assertThat(result.get(2).getName()).isEqualTo("차입");
		assertThat(result.get(2).getCode()).isEqualTo("010300");
	}

	@DisplayName("parent_code가 중간 카테고리 코드이면 하위 카테고리 리스트를 반환한다.")
	@Test
	void 부모코드_중간카테고리_리스트_반환(){
		//given
		String parentCode = "020200";

		//when
		List<Category> result = categoryDao.findCategoryCodesByParentCode(parentCode);

		//then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(4);

		assertThat(result.get(0).getName()).isEqualTo("버스비");
		assertThat(result.get(0).getCode()).isEqualTo("020201");
		assertThat(result.get(3).getName()).isEqualTo("기타");
		assertThat(result.get(3).getCode()).isEqualTo("020204");
	}

	@DisplayName("parent_code가 하위 카테고리 코드이면 빈 리스트를 반환한다.")
	@Test
	void 부모코드_하위카테고리_빈리스트_반환(){
		//given
		String parentCode = "020204";

		//when
		List<Category> result = categoryDao.findCategoryCodesByParentCode(parentCode);

		//then
		assertThat(result).isEmpty();
	}

	@DisplayName("parent_code가 DB에 없으면 빈 리스트를 반환한다.")
	@ParameterizedTest
	@ValueSource(strings = {"040102", "010", "030000"})
	void 부모코드_없으면_빈리스트_반환(String parentCode){
		//when
		List<Category> result = categoryDao.findCategoryCodesByParentCode(parentCode);

		//then
		assertThat(result).isEmpty();
	}



	//=================================================
	// findCategoryByStep() 테스트
	//=================================================
	@DisplayName("코드가 있으면 카테고리에 해당하는 모든 상위 카테고리를 조회한다.")
	@Test
	void 하위코드_있으면_리스트반환() {
		//given
		String code = "020203";

		//when
		List<Category> result = categoryDao.findAncestorCategoriesByCode(code);

		//then
		assertThat(result).hasSize(3);
		assertThat(result.get(0).getCode()).isEqualTo("020000");
		assertThat(result.get(0).getName()).isEqualTo("지출");

		assertThat(result.get(1).getCode()).isEqualTo("020200");
		assertThat(result.get(1).getName()).isEqualTo("교통");

		assertThat(result.get(2).getCode()).isEqualTo(code);
		assertThat(result.get(2).getName()).isEqualTo("지하철");
	}

	@DisplayName("코드가 없으면 빈 리스트를 반환한다.")
	@Test
	void 하위코드_없으면_빈리스트반환(){
		//given
		String code = "020109";

		//when
		List<Category> result = categoryDao.findAncestorCategoriesByCode(code);

		//then
		assertThat(result).isEmpty();
	}
}
