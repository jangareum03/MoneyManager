package com.moneymanager.ledger.repository;

import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.repository<br>
 * 파일이름       : CategoryRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 19<br>
 * 설명              : LedgerImageRepository 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 7. 19</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class CategoryRepositoryIT extends IntegrationTest {

	@Autowired
	private CategoryRepository target;

	@Nested
	@DisplayName("카테고리 전체 조회할 때")
	class SelectAllCategory {

		@Test
		@DisplayName("카테고리 정보가 있으면 모튼 카테고리를 조회한다.")
		void returnsAllCategories_whenCategoriesExist() {
			//when: 카테고리를 조회한다.
			List<Category> result = target.findAllCategory();
			
			//then: 저장된 카테고리가 모두 조회된다.
			assertThat(result).isNotNull();
			assertThat(result).hasSize(64);
		}
		
		@Test
		@Sql(statements = "DELETE FROM ledger_category")
		@DisplayName("카테고리 정보가 없으면 빈 List를 반환한다.")
		void returnsEmptyList_whenCategoriesDoesNotExist() {
			//when: 카테고리를 조회한다.
			List<Category> result = target.findAllCategory();

			//then: 빈 리스트로 조회된다.
			assertThat(result).isEmpty();
		}

	}

}