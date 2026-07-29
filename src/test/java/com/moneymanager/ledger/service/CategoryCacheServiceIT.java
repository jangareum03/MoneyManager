package com.moneymanager.ledger.service;

import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.repository.ledger.CategoryRepository;
import com.moneymanager.service.ledger.CategoryCacheService;
import com.moneymanager.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service<br>
 * 파일이름       : CategoryCacheServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 19<br>
 * 설명              : CategoryCacheService 클래스 로직을 검증하는 통합 테스트 클래스
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
@EnableCaching
public class CategoryCacheServiceIT extends IntegrationTestSupport {

	@Autowired
	private CategoryCacheService target;

	@SpyBean
	private CategoryRepository categoryRepository;

	@Autowired
	private CacheManager manager;

	@Autowired
	private JdbcTemplate jdbcTemplate;


	@BeforeEach
	void setUp() {
		Cache cache = manager.getCache("category");

		if(cache != null) {
			cache.clear();
		}
	}


	@Nested
	@DisplayName("캐시 조회")
	class GetCache {

		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@Test
			@DisplayName("저장된 카테고리가 있으면 Map이 반환된다.")
			void returnsCategoryMap_whenCategoriesExist() {
				//when: 모든 카테고리 정보를 조회한다.
				Map<String, Category> result = target.getCategoryMap();
				
				//then: 카테고리 코드를 key로 하는 Map이 반환된다.
				assertThat(result).hasSize(64);
				assertThat(result).containsKeys("010000");
				assertThat(result).containsKeys("020000");
			}
			
			@Test
			@Sql(statements = "DELETE FROM ledger_category", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("저장된 카테고리가 없으면 빈 Map이 반환된다.")
			void returnsEmptyMap_whenCategoriesDoNotExist() {
				//when: 모든 카테고리 정보를 조회한다.
				Map<String, Category> result = target.getCategoryMap();
				
				//then: 빈 Map이 반환된다.
				assertThat(result).isEmpty();
			}
			
			@Test
			@DisplayName("같은 요청을 두 번 호출하면 캐시를 사용한다.")
			void returnsCachedData_whenCalledTwiceWithSameRequest() {
				//when: 같은 요청을 두 번 조회한다.
				target.getCategoryMap();
				target.getCategoryMap();

				//then: CategoryRepository는 한 번만 호출된다.
				verify(categoryRepository, times(1)).findAllCategory();
			}
			
			@Test
			@Sql(statements = "DELETE FROM ledger_category", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("빈 Map은 캐시에 저장되지 않는다.")
			void doesNotCache_whenMapIsEmpty() {
				//given: 카테고리 테이블이 비어있다.
				target.getCategoryMap();

				jdbcTemplate.update(
						"INSERT INTO ledger_category(code, name) VALUES (?, ?)",
						"010000",
						"수입"
				);

				//when: DB에 카테고리를 추가한 후 다시 조회한다.
				Map<String, Category> result = target.getCategoryMap();
				
				//then: DB에서 조회한 데이터가 반환된다.
				assertThat(result).isNotEmpty();
				verify(categoryRepository, times(2)).findAllCategory();
			}

		}

	}

}