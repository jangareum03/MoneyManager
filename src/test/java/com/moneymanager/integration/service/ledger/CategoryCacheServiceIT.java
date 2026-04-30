package com.moneymanager.integration.service.ledger;

import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.repository.ledger.CategoryRepository;
import com.moneymanager.service.ledger.CategoryCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.integration.service.ledger<br>
 * 파일이름       : CategoryCacheServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 13<br>
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
 * 		 	  <td>26. 4. 13</td>
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
	private CategoryCacheService service;

	@Autowired
	private CacheManager cacheManager;

	@SpyBean
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp() {
		Cache cache = cacheManager.getCache("category");
		if(cache != null) {
			cache.clear();
		}
	}


	@Test
	@DisplayName("같은 메서드를 두 번 호출하면 Repository는 한 번만 호출된다.")
	void returnsCache_whenCalledTwice() {
		//when
		Map<String, Category> first = service.getCategoryMap();
		Map<String, Category> second = service.getCategoryMap();

		//then
		assertThat(first).isEqualTo(second);

		verify(categoryRepository, times(1)).findAllCategory();
	}

	@Test
	@DisplayName("처음 호출 후 category 캐시에 값이 저장된다.")
	void storesResultInCache() {
		//when
		Map<String, Category> result = service.getCategoryMap();

		//then
		Cache cache = cacheManager.getCache("category");
		assertThat(cache).isNotNull();

		Cache.ValueWrapper wrapper = cache.get(SimpleKey.EMPTY);
		assertThat(wrapper).isNotNull();
		assertThat(wrapper.get()).isEqualTo(result);
	}

	@Test
	@DisplayName("캐시 지우면 다음 호출 시 Repository를 다시 호출한다.")
	void callsRepositoryAgain_afterCacheClear() {
		//given
		Cache cache = cacheManager.getCache("category");
		assertThat(cache).isNotNull();

		//when
		service.getCategoryMap();
		cache.clear();
		service.getCategoryMap();

		//then
		verify(categoryRepository, times(2)).findAllCategory();
	}

	@Test
	@DisplayName("Repository 결과가 null이면 캐시에 저장되지 않는다.")
	void doseNotCache_whenResultIsNull() {
		//given
		when(categoryRepository.findAllCategory()).thenReturn(null);

		//when
		Map<String, Category> first = service.getCategoryMap();
		Map<String, Category> second = service.getCategoryMap();

		//then
		assertThat(first).isNull();;
		assertThat(second).isNull();;

		verify(categoryRepository, times(2)).findAllCategory();

		Cache cache = cacheManager.getCache("category");
		assertThat(cache).isNotNull();
		assertThat(cache.get(SimpleKey.EMPTY)).isNull();
	}

}
