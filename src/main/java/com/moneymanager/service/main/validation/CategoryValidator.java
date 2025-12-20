package com.moneymanager.service.main.validation;

import com.moneymanager.domain.ledger.dto.request.CategoryRequest;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.exception.ErrorCode;

import static com.moneymanager.exception.ErrorUtil.createClientException;
import static com.moneymanager.utils.ValidationUtils.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.main.validation<br>
 * 파일이름       : CategoryValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 26.<br>
 * 설명              : 가계부 카테고리 값을 검증하는 클래스
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
 * 		 	  <td>25. 11. 26</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 11. 29</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 삭제] validateCode, isMiddleCode, fail</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class CategoryValidator {

	/**
	 * {@link CategoryRequest} 객체의 유효성을 검증합니다.
	 * 유효성 검증이 실패하면 {@link com.moneymanager.exception.custom.ClientException}이 발생합니다.
	 * <p>
	 *     검증 조건:
	 *     <ul>
	 *         <li>부모코드(code)가 비어있거나 숫자가 아니면 {@link  ErrorCode#LEDGER_CATEGORY_FORMAT} 발생</li>
	 *         <li>MIDDLE 레벨인데 코드 길이가 2가 아니거나 LOW 레벨인데 코드 길이가 4가 아니면 {@link  ErrorCode#LEDGER_CATEGORY_FORMAT} 발생</li>
	 *     </ul>
	 * </p>
	 *
	 * @param request			검증할 카테고리 검색 요청 객체
	 * @throws com.moneymanager.exception.custom.ClientException	코드 규칙에 맞지 않을 경우
	 */
	public static void validate(CategoryRequest request) {
		CategoryLevel level = request.getLevel();
		if (level == CategoryLevel.TOP) return;        //상위 카테고리는 카테고리 코드가 필요 없어서 종료

		String code = request.getCode();
		if ( isNullOrBlank(code) ) {
			throw createClientException(ErrorCode.LEDGER_CATEGORY_MISSING, "카테고리 코드를 확인해주세요.");
		}

		//코드가 숫자가 아닌 경우
		if( !isMatchedPattern(code, "\\d+$") ) {
			throw createClientException(ErrorCode.LEDGER_CATEGORY_FORMAT, "카테고리 코드는 6자리 숫자만 가능합니다.", code);
		}
	}
}