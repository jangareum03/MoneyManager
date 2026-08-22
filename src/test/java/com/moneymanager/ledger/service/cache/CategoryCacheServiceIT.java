package com.moneymanager.ledger.service.cache;

import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.ledger.repository.CategoryRepository;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
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
@Transactional
public class CategoryCacheServiceIT extends IntegrationTest {

	@Autowired
	private CategoryCacheService target;

	@SpyBean
	private CategoryRepository categoryRepository;

	@Autowired
	private CacheManager manager;


	@BeforeEach
	void setUp() {
		Cache cache = manager.getCache("category");

		if(cache != null) {
			cache.clear();
		}
	}


	@Nested
	@DisplayName("전체 카테고리를 조회할 때")
	class GetCache {

		@Nested
		@DisplayName("성공")
		class Success {

			@Test
			@DisplayName("데이터베이스에 저장된 카테고리가 있다면 캐시에 저장한다.")
			void cachesCategories_whenCategoriesAreFetched() {
				//when
				Map<String, Category> firstResult = target.getCategoryMap();
				Map<String, Category> secondResult = target.getCategoryMap();

				//then
				assertThat(firstResult).isNotEmpty();
				assertThat(secondResult).isEqualTo(firstResult);
			}
			
			@Test
			@DisplayName("첫 호출에는 DB에서 조회하고 두번째 호출에서는 캐시를 사용한다.")
			void returnsUserFromCache_whenCalledTwice() {
				//when
				target.getCategoryMap();
				target.getCategoryMap();

				//then: CategoryRepository는 한 번만 호출된다.
				verify(categoryRepository, times(1)).findAllCategory();
			}
			
			@Test
			@Sql(statements = "DELETE FROM ledger_category", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("카테고리가 없으면 예외가 발생한다.")
			void throwInternalException_whenCategoriesDoNotExist() {
				//when & then
				ApplicationExceptionAssert.assertThatApplicationException(
								catchThrowable(() -> target.getCategoryMap())
						)
						
						.hasErrorCode(DATA_NOT_FOUND)
						.hasWork("전체 카테고리 조회")
						.hasCauseMessage("카테고리 없음");
			}

		}

	}

}