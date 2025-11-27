package com.moneymanager.service.main;

import com.moneymanager.dao.main.CategoryDao;
import com.moneymanager.domain.ledger.dto.CategoryResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.exception.ErrorCode;
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
 *		</tbody>
 * </table>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryDao categoryDAO;


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
			throw createServerException(ErrorCode.DATABASE_QUERY_RESULT, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
		}

		return CategoryResponse.from( categories );
	}


	/**
	 * 주어진 코드({@code code})에 해당하는 중간 카테고리 리스트를 조회 후 반환합니다.
	 * <p>
	 * 가계부 카테고리 체계 중 중간 단계에 해당합니다.
	 * 주어진 코드는 반드시 상위 단계(TOP)의 카테고리 코드여야 하며, 조건에 맞지 않으면 {@link com.moneymanager.exception.custom.ClientException} 예외가 발생합니다.
	 * </p>
	 *
	 * @param code	조회할 카테고리의 부모 코드
	 * @return 중간 카테고리를 {@link CategoryResponse} 형태로 변환한 리스트
	 */
	public List<CategoryResponse> getMiddleCategories(String code) {
		return CategoryResponse.from( categoryDAO.findCategoryCodesByParentCode(code) );
	}


	/**
	 * 주어진 코드({@code code})에 해당하는 하위 카테고리 리스트를 조회 후 반환합니다.
	 * <p>
	 * 가계부 카테고리 체계 중 최하위 단계에 해당합니다. 주어진 코드는 반드시 중간 단계(MIDDLE)의 카테고리 코드여야 하며, 조건이 맞지 않으면 {@link com.moneymanager.exception.custom.ClientException} 예외가 발생합니다.
	 * </p>
	 *
	 *@param code	조회할 카테고리의 부모 코드
	 * @return 하위 카테고리를 {@link CategoryResponse} 형태로 변환한 리스트
	 */
	public List<CategoryResponse> getLowCategories(String code) {
		return CategoryResponse.from( categoryDAO.findCategoryCodesByParentCode(code) );
	}


	/**
	 * 주어진 코드(code)에 대해 자신의 상위 계층 카테고리(부모, 조부모)를 조회합니다.
	 * <p>
	 *     조회된 카테고리 리스트를 {@link CategoryResponse} 리스트로 매핑 후 반환합니다.
	 *     조회 결과가 없는 경우 {@link com.moneymanager.exception.custom.ClientException} 예외가 발생합니다.
	 * </p>
	 *
	 * @param code		조회할 하위 카테고리 코드
	 * @return	코드의 상위 계층 카테고리 정보를 담은 {@link CategoryResponse} 리스트
	 * @throws com.moneymanager.exception.custom.ClientException 상위 카테고리를 찾을 수 없는 경우
	 */
	public List<CategoryResponse> getMyParentCategories(String code) {
		//데이터베이스 조회 결과
		List<Category> categories = categoryDAO.findCategoryByStep(code);
		if( categories.isEmpty() ) {
			throw createClientException(ErrorCode.LEDGER_CATEGORY_NONE, "카테고리를 찾을 수 없습니다.", code);
		}

		return CategoryResponse.from(categories);
	}


	/**
	 * 카테고리 단계({@link CategoryLevel}), 가계부 유형, 일부 코드를 기반으로 DB에서 저장 및 조회할 부모코드 문자열을 생성한 후 반환합니다.
	 * <p>
	 *     카테고리 단계:
	 *     <ul>
	 *         <li>TOP: 부모 코드이므로 null</li>
	 *         <li>MIDDLE: 상위코드({@link LedgerType}의 DB값)를 사용하여 6자리 코드 생성</li>
	 *         <li>LOW: 상위코드({@code code})를 그대로 반환</li>
	 *         <li>지원하지 않은 단계이면 {@link com.moneymanager.exception.custom.ClientException} 발생</li>
	 *     </ul>
	 * </p>
	 *
	 * <pre>{@code
	 * 	//TOP 단계의 수입 코드
	 * 	String topCode = generateCode(CategoryLevel.TOP, LedgerType.INCOME, null);		// null
	 *
	 * 	//MIDDLE 단계의 지출 코드
	 * 	String middleCode = generateCode(CategoryLevel.MIDDLE, LedgerType.OUTLAY, "02");		//"020000"
	 *
	 * 	//LOW 단계의 수입 코드
	 * 	String lowCode = generateCode(CategoryLevel.LOW, LedgerType.INCOME, "010200");	//"010200"
	 * }</pre>
	 *
	 * @param level		카테고리 단계(TOP, MIDDLE, LOW)
	 * @param code		상위 카테고리(예: 교통코드는 "020200")
	 * @return	단계에 맞는 부모 카테고리 코드 문자열
	 */
	public  String generateParentCode(CategoryLevel level, String code) {
		switch (level) {
			case TOP:
				return null;
			case MIDDLE:
				return code + "0000";
			case LOW:
				return code;
			default:
				throw createClientException(ErrorCode.LEDGER_CATEGORY_INVALID, "지원하지 않은 카테고리 단계입니다.", level);
		}
	}
}
