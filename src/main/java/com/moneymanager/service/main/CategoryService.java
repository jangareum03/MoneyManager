package com.moneymanager.service.main;

import com.moneymanager.dao.main.CategoryDao;
import com.moneymanager.dto.budgetBook.CategoryDTO;
import com.moneymanager.entity.Category;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

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
 *		</tbody>
 * </table>
 */
@Service
public class CategoryService {

	private final CategoryDao categoryDAO;
	private final Logger logger = LogManager.getLogger(this);

	public CategoryService(CategoryDao categoryDAO) {
		this.categoryDAO = categoryDAO;
	}




	/**
	 * 최상위 카테고리 리스트를 찾는 메서드
	 *
	 * @return 최상위 카테고리 리스트
	 */
	public List<CategoryDTO> getTopCategories() {
		List<CategoryDTO> readList = new ArrayList<>();

		for ( Category category : categoryDAO.findCategory() ) {
			readList.add(CategoryDTO.builder().name(category.getName()).code(category.getCode()).build());
		}

		return readList;
	}



	/**
	 * 코드에 해당하는 하위 카테고리 리스트를 찾는 메서드
	 *
	 * @param code 카테고리 코드
	 * @return 하위 카테고리 리스트
	 */
	public List<CategoryDTO> getMySubCategories( String code) {
		List<CategoryDTO> categoryList = new ArrayList<>();

		for (Category entity : categoryDAO.findCategoryByCode(code)) {
			categoryList.add(CategoryDTO.builder().name(entity.getName()).code(entity.getCode()).build());
		}


		return categoryList;
	}


	/**
	 * 코드에 해당하는 상위 카테고리 리스트를 찾아 반환합니다.
	 *
	 * @param code	하위 카테고리 코
	 * @return	상위 카테고리의 이름과 코드를 담은 리스트
	 */
	public List<CategoryDTO> getMyParentCategories(String code ) {
		List<CategoryDTO> categoryList = new ArrayList<>();

		for( Category entity : categoryDAO.findCategoryByStep(code) ) {
			categoryList.add( CategoryDTO.builder().name(entity.getName()).code(entity.getCode()).build() );
		}

		return categoryList;
	}



}
