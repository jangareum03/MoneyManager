package com.moneymanager.service.ledger;

import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.repository.ledger.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.ledger<br>
 * 파일이름       : CategoryCacheService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 14<br>
 * 설명              : 캐시에 카테고리 정보를 저장하는 클래스
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
 * 		 	  <td>26. 4. 14</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class CategoryCacheService {

	private final CategoryRepository categoryRepository;

	/**
	 * 모든 카테고리 정보를 조회합니다.
	 * <p>
	 *     최초 호출 시 데이터베이스에서 카테고리 정보를 모두 조회하고,
	 *     이후 호출부터는 캐시에 저장된 데이터를 반환합니다.
	 *     데이터베이스 조회 결과가 비어있는 경우에는 예외가 발생합니다.
	 * </p>
	 * @return	전체 카테고리 정보를 담은 {@link Category} map
	 */
	@Cacheable(value = "category")
	public Map<String, Category> getCategoryMap() {
		List<Category> categoryList = categoryRepository.findAllCategory();

		if(categoryList == null || categoryList.isEmpty()) {
			return Collections.emptyMap();
		}

		return	categoryList.stream()
				.collect(Collectors.toMap(
						Category::getCode,
						c -> c
				));
	}

}
