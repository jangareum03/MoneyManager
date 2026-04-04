package com.moneymanager.service.ledger;

import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.repository.ledger.CategoryRepository;
import com.moneymanager.domain.ledger.dto.response.CategoryResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.ledger<br>
 * 파일이름       : CategoryReadService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 7<br>
 * 설명              : 카테고리 정보를 조회하는 클래스
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
@Service
@RequiredArgsConstructor
public class CategoryReadService {

	private final CategoryRepository categoryRepository;


	/**
	 * 모든 카테고리 정보를 조회합니다.
	 * <p>
	 *     최초 호출 시 데이터베이스에서 카테고리 정보를 모두 조회하고,
	 *     이후 호출부터는 캐시에 저장된 데이터를 반환합니다.
	 *     데이터베이스 조회 결과가 비어있는 경우에는 예외가 발생합니다.
	 * </p>
	 * @return	전체 카테고리 정보를 담은 {@link Category} 리스트
	 */
	@Cacheable(
			value = "category", key = "'ALL'",
			unless = "#result == null"
	)
	public List<Category> getAllCategories() {

		return categoryRepository.findAllCategory();
	}


	/**
	 * 가계부 유형({@link CategoryType})과 카테고리 단계({@link CategoryLevel})에 따라 카테고리 목록을 조회합니다.
	 * <p>
	 *     주어진 단계에 따라 상위, 중위, 하위 카테고리 정보를 조회하며, {@link CategoryResponse} 객체로 변환됩니다.
	 * </p>
	 *
	 * @param type		가계부 유형(수입/지출)
	 * @param level		조회할 카테고리 단계
	 * @return	요청 조건에 맞는 카테고리 정보를 담은 {@link CategoryResponse} 객체 리스트
	 * @throws IllegalArgumentException	지원하지 않은 카테고리 단계인 경우
	 */
	public List<CategoryResponse> getCategoriesByTypeAndLevel(CategoryType type, CategoryLevel level){
		List<Category> categories;

		switch (level) {
			case TOP:
				categories = getRootCategories();
				break;
			case MIDDLE:
				categories = getMiddleCategories(type);
				break;
			case LOW:
				categories = getLowCategories(type);
				break;
			default:
				throw new IllegalArgumentException(
						String.format("잘못된 카테고리 단계 (level=%s)", level)
				);
		}

		return categories.stream()
				.map(CategoryResponse::from)
				.collect(Collectors.toList());
	}

	//최상위 카테고리 목록 조회
	private List<Category> getRootCategories() {
		return getAllCategories().stream()
				.filter( c -> c.getParentCode() == null )
				.collect(Collectors.toList());
	}

	//중간 단계 카테고리 목록 조회
	private List<Category> getMiddleCategories(CategoryType type) {
		return getAllCategories().stream()
				.filter(c -> c.getParentCode() != null)
				.filter(c -> c.getParentCode().endsWith("0000"))
				.filter( c -> type == CategoryType.INCOME
							? c.getCode().startsWith("01")
							: c.getCode().startsWith("02"))
				.collect(Collectors.toList());
	}

	//하위 단계 카테고리 목록 조회
	private List<Category> getLowCategories(CategoryType type) {
		return getAllCategories().stream()
				.filter(c -> c.getParentCode() != null)
				.filter(c -> !c.getCode().endsWith("00"))
				.filter( c -> type == CategoryType.INCOME
						? c.getCode().startsWith("01")
						: c.getCode().startsWith("02"))
				.collect(Collectors.toList());
	}

}
