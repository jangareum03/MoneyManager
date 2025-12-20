package com.moneymanager.service.main;

import com.moneymanager.dao.main.CategoryDao;
import com.moneymanager.domain.ledger.dto.request.CategoryRequest;
import com.moneymanager.domain.ledger.dto.response.CategoryResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.service.main.validation.CategoryValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.moneymanager.exception.ErrorUtil.createClientException;
import static com.moneymanager.exception.ErrorUtil.createServerException;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.service.main<br>
 *  * 파일이름       : CategoryService<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 가계부 카테고리 관련 비즈니스 로직을 처리하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 11. 29</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[메서드 삭제] getMyParentCategories, generateParentCode</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryDao categoryDAO;

	/**
	 * 하위 카테고리({@code code})를 기준으로 전체 계층(상위, 중간, 하위) 카테고리를 조회 후 {@link Map} 객체를 반환합니다.
	 * <p>
	 *     반환되는 맵은 {@link CategoryLevel}를 키로 하여 각 계층에 해당하는 {@link CategoryResponse} 리스트로 값을 가집니다.
	 *     <ul>
	 *         <li>{@link CategoryLevel#TOP}: 상위 카테고리 전체</li>
	 *         <li>{@link CategoryLevel#MIDDLE}: 주어진 코드(상위 카테고리) 기준 중간 카테고리</li>
	 *         <li>{@link CategoryLevel#LOW}: 주어진 코드(중간 카테고리) 기준 하위 카테고리</li>
	 *     </ul>
	 * </p>
	 * 조회된 카테고리가 없는 경우 {@link com.moneymanager.exception.custom.ClientException} 예외가 발생합니다.
	 *
	 * @param code		조회할 하위 카테고리 코드
	 * @return	각 계층별 {@link CategoryResponse} 리스트를 담은 맵
	 */
	public Map<String, List<CategoryResponse>> getAllCategoriesByCode(String code) {
		List<Category> categories = categoryDAO.findAncestorCategoriesByCode(code);

		if( categories.size() < 2 ) {
			throw createClientException(ErrorCode.LEDGER_CATEGORY_NONE, "카테고리를 찾을 수 없습니다.", code);
		}

		String topCode = categories.get(0).getCode();
		String middleCode = categories.get(1).getCode();

		Map<String, List<CategoryResponse>> map = new HashMap<>();
		map.put(CategoryLevel.TOP.name(), getTopCategories());
		map.put(CategoryLevel.MIDDLE.name(), CategoryResponse.from( categoryDAO.findCategoriesByParentCode(topCode)) );
		map.put(CategoryLevel.LOW.name(), CategoryResponse.from( categoryDAO.findCategoriesByParentCode(middleCode)) );

		return map;
	}


	/**
	 * 최상위 카테고리 리스트를 조회 후 반환합니다.
	 * <p>
	 * 가계부 카테고리 체계 중 가장 상위 단계에 해당합니다. 조회가 되지 않으면 {@link com.moneymanager.exception.custom.ServerException} 예외가 발생합니다.
	 * </p>
	 *
	 * @return 최상위 카테고리를 {@link CategoryResponse} 형태로 변환한 리스트
	 */
	public List<CategoryResponse> getTopCategories() {
		List<Category> categories = categoryDAO.findTopCategories();
		if( categories.isEmpty() ) {
			throw createServerException(ErrorCode.DATABASE_RESULT_INTERNAL, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
		}

		return CategoryResponse.from( categories );
	}


	/**
	 * 카테고리 레벨, 선택한 코드를 기반으로 하위 카테고리 목록을 조회 후 반환합니다.
	 * <p>
	 *     카테고리 레벨, 선택한 카테고리 코드를 하위 카테고리 목록을 조회합니다.
	 * </p>
	 *
	 * @param request 		카테고리 레벨, 선택한 카테고리 코드 정보를 포함한 {@link CategoryRequest} 객체
	 * @return 요청한 레벨에 해당하는 하위 카테고리 리스트
	 * @throws com.moneymanager.exception.custom.ClientException 선택한 카테고리 코드가 올바르지 않은 경우
	 */
	public List<CategoryResponse> getSubCategories(CategoryRequest request) {
		CategoryValidator.validate(request);

		if( request.getLevel() == CategoryLevel.TOP ) {
			return getTopCategories();
		}

		return CategoryResponse.from( categoryDAO.findCategoriesByParentCode(request.getCode()) );
	}


	/**
	 * 지정한 하위 카테고리 코드(code)의 상위 계층(부모, 조부모) 카테고리를 조회합니다.
	 * <p>
	 *     코드가 {@link CategoryLevel#LOW} 형식에 맞으면 상위 계층 카테고리 리스트를 반환합니다.
	 *     형식이 맞지 않으면 {@link com.moneymanager.exception.custom.ClientException} 예외가 발생합니다.
	 * </p>
	 *
	 * @param lowCode 	조회할 하위 카테고리 코드
	 * @return 상위 계층 카테고리 정보를 담은 {@link CategoryResponse} 리스트
	 * @throws com.moneymanager.exception.custom.ClientException	잘못된 코드 형식이거나 상위 카테고리를 찾을 수 없는 경우
	 */
	public List<CategoryResponse> getAncestorCategoriesByCode(String lowCode) {
		CategoryValidator.validate(CategoryRequest.ofLowCategory(lowCode));

		List<Category> categories = categoryDAO.findAncestorCategoriesByCode(lowCode);
		if (categories.size() != 3) {
			throw createClientException(ErrorCode.LEDGER_CATEGORY_NONE, "카테고리를 찾을 수 없습니다.", lowCode);
		}

		return CategoryResponse.from(categories);
	}
}