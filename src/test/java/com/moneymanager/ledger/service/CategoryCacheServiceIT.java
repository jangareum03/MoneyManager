package com.moneymanager.ledger.service;

import com.moneymanager.repository.ledger.CategoryRepository;
import com.moneymanager.service.ledger.CategoryCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
@SpringBootTest
@EnableCaching
@ActiveProfiles("test")
public class CategoryCacheServiceIT {

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
	@DisplayName("캐시 조회")
	class GetCache {

		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@Test
			@DisplayName("첫번째 호출은 Repository를 조회한다.")
			void fetchesFromRepository_whenFirstCallIsMade() {
				//when: 캐시에서 카테고리 정보를 조회한다.
				target.getCategoryMap();
				
				//then: Repository에서 카테고리 정보를 한 번 조회가 요청된다.
				verify(categoryRepository, times(1)).findAllCategory();
			}
			
			@Test
			@DisplayName("두번째 호출은 캐시를 사용한다.")
			void fetchesFromCache_whenSecondCallIsMade() {
				//when: 카테고리 정보를 두 번 호출한다.
				target.getCategoryMap();
				target.getCategoryMap();

				//then: Repository에서 카테고리 정보를 한 번 조회가 요청된다.
				verify(categoryRepository, times(1)).findAllCategory();
			}
			
			@Test
			@DisplayName("빈 Map은 캐시에 저장되지 않는다.")
			void doesNothingToCache_whenMapIsEmpty() {
				//given: 조회 결과가 빈 List가 반환되도록 정의되어 있다.
				when(categoryRepository.findAllCategory()).thenReturn(Collections.emptyList());

				//when: 캐시에서 카테고리 정보를 조회한다.
				target.getCategoryMap();
				target.getCategoryMap();

				//then: 캐시에 카테고리가 저장되지 않는다.
				verify(categoryRepository, times(2)).findAllCategory();
			}

			
			@Test
			@DisplayName("CacheManager에 실제 캐시가 생성된다.")
			void createsCacheSuccessfully_whenCacheManagerLoads() {
				//given: 캐시가 저장된 상태이다.
				target.getCategoryMap();

				//when: 캐시가 저장된다.
				Cache cache = manager.getCache("category");
				
				//then: 캐시에 카테고리가 저장된다.
				assertThat(cache).isNotNull();
			}
			
			@Test
			@DisplayName("캐시가 삭제되면 다시 Repository를 조회한다.")
			void fetchesFromRepository_whenCacheIsEvicted() {
				//given: 저장된 캐시를 삭제된 상태이다.
				target.getCategoryMap();

				Cache cache = manager.getCache("category");
				cache.clear();

				//when: 캐시에서 카테고리 정보를 조회한다.
				target.getCategoryMap();
				
				//then: Repository가 한 번 호출된다.
				verify(categoryRepository, times(2)).findAllCategory();
			}
			
			@Test
			@DisplayName("캐시 키가 올바르게 저장된다.")
			void validatesCacheKey_whenRequestIsValid() {
				//given: 캐시가 저장된 상태이다.
				target.getCategoryMap();

				//when: 캐시가 저장된다.
				Cache cache = manager.getCache("category");

				//then: 캐시에 카테고리가 저장된다.
				assertThat(cache).isNotNull();
				assertThat(cache.get(SimpleKey.EMPTY)).isNotNull();
			}

		}

	}

}