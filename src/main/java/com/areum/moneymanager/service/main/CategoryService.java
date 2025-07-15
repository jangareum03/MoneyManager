package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.main.CategoryDao;
import com.areum.moneymanager.dto.response.main.CategoryResponseDTO;
import com.areum.moneymanager.entity.Category;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 가계부의 수입/지출 카테고리와 관련된 작업을 처리하는 클래스</br>
 * 수입 카테고리 조회, 지출 카테고리 조회 등의 메서드를 구현
 *
 * @version 1.0
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
	public List<CategoryResponseDTO.Read> getTopCategories() {
		List<CategoryResponseDTO.Read> readList = new ArrayList<>();

		for ( Category category : categoryDAO.findCategory() ) {
			readList.add(CategoryResponseDTO.Read.builder().name(category.getName()).code(category.getCode()).build());
		}

		return readList;
	}



	/**
	 * 코드에 해당하는 하위 카테고리 리스트를 찾는 메서드
	 *
	 * @param code 카테고리 코드
	 * @return 하위 카테고리 리스트
	 */
	public List<CategoryResponseDTO.Read> getMySubCategories( String code) {
		List<CategoryResponseDTO.Read> categoryList = new ArrayList<>();

		for (Category entity : categoryDAO.findCategoryByCode(code)) {
			categoryList.add(CategoryResponseDTO.Read.builder().name(entity.getName()).code(entity.getCode()).build());
		}


		return categoryList;
	}


	/**
	 * 코드에 해당하는 상위 카테고리 리스트를 찾아 반환합니다.
	 *
	 * @param code	하위 카테고리 코
	 * @return	상위 카테고리의 이름과 코드를 담은 리스트
	 */
	public List<CategoryResponseDTO.Read> getMyParentCategories( String code ) {
		List<CategoryResponseDTO.Read> categoryList = new ArrayList<>();

		for( Category entity : categoryDAO.findCategoryByStep(code) ) {
			categoryList.add( CategoryResponseDTO.Read.builder().name(entity.getName()).code(entity.getCode()).build() );
		}

		return categoryList;
	}



}
