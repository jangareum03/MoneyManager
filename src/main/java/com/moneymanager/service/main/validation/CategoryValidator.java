package com.moneymanager.service.main.validation;

import com.moneymanager.domain.ledger.dto.CategorySearchRequest;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.exception.ErrorCode;

import static com.moneymanager.exception.ErrorUtil.createClientException;
import static com.moneymanager.exception.ErrorUtil.createServerException;
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
 * 		 	  <td>25. 11. 26.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class CategoryValidator {

	/**
	 * {@link CategorySearchRequest} 객체의 유효성을 검증합니다.
	 * 유효성 검증이 실패하면 {@link com.moneymanager.exception.custom.ClientException}이 발생합니다.
	 * <p>
	 *     검증 조건:
	 *     <ul>
	 *         <li>요청 객체가 {@code null}이면 {@link  ErrorCode#LEDGER_CATEGORY_MISSING} 발생</li>
	 *         <li>가계부 유형이 {@code null}이면 {@link  ErrorCode#LEDGER_CATEGORY_MISSING} 발생</li>
	 *         <li>부모코드(code)가 비어있거나 숫자가 아니면 {@link  ErrorCode#LEDGER_CATEGORY_FORMAT} 발생</li>
	 *         <li>MIDDLE 레벨인데 코드 길이가 2가 아니거나 LOW 레벨인데 코드 길이가 4가 아니면 {@link  ErrorCode#LEDGER_CATEGORY_FORMAT} 발생</li>
	 *     </ul>
	 * </p>
	 *
	 * @param request			검증할 카테고리 검색 요청 객체
	 * @throws com.moneymanager.exception.custom.ClientException	코드 규칙에 맞지 않을 경우
	 */
	public static void validate(CategorySearchRequest request) {
		//요청 정보가 없는 경우
		if (request == null) {
			throw createClientException(ErrorCode.LEDGER_CATEGORY_MISSING, "카테고리를 확인해주세요.");
		}

		//가계부 유형이 없는 경우
		LedgerType type = request.getLedgerType();
		if (type == null) {
			throw createClientException(ErrorCode.LEDGER_CATEGORY_MISSING, "가계부 유형을 확인해주세요.");
		}

		CategoryLevel level = request.getLevel();
		if (level == CategoryLevel.TOP) return;        //상위 카테고리는 카테고리 코드가 필요 없어서 종료

		String code = request.getParentCode();
		if ( isNullOrBlank(code) ) {
			throw createClientException(ErrorCode.LEDGER_CATEGORY_MISSING, "카테고리 코드를 확인해주세요.");
		}

		//코드가 숫자가 아닌 경우
		if( !isMatchedPattern(code, "\\d+$") ) {
			throw createClientException(ErrorCode.LEDGER_CATEGORY_FORMAT, "카테고리 코드는 숫자만 가능합니다.", code);
		}

		switch (level) {
			case MIDDLE:
				if (code.length() != 2) {
					throw createClientException(ErrorCode.LEDGER_CATEGORY_FORMAT, "카테고리 코드는 2자리입니다.", code);
				}
				break;
			case LOW:
				if (code.length() != 4) {
					throw createClientException(ErrorCode.LEDGER_CATEGORY_FORMAT, "카테고리 코드는 4자리입니다.", code);
				}
		}
	}


	/**
	 * 카테고리 레빌에 따라 코드의 형식이 올바른지 검증합니다.
	 * <p>
	 *     검증 규칙:
	 *     <ul>
	 *         <li>TOP 레벨에서 코드는 반드시 {@code null}이여야 합니다.</li>
	 *         <li>MIDDLE 레벨에서 코드는 6자리로 이루어진 숫자 형식이며, 상위 2자리만 의미를 가진 숫자여야 합니다.</li>
	 *         <li>LOW 레벨에서 코드는 6자리로 이루어진 숫자 형식이며, 상위 4자리까지 의미를 가진 숫자여야 합니다.</li>
	 *     </ul>
	 * </p>
	 *
	 * @param level		검증할 카테고리 레벨(TOP, MIDDLE, LOW)
	 * @param code		검증 대상 카테고리 코드
	 * @throws com.moneymanager.exception.custom.ServerException	규칙에 맞지 않을 경우 발생
	 */
	public static void validateCode(CategoryLevel level, String code) {
		//TOP레빌인 경우
		if( level == CategoryLevel.TOP) {
			if( code != null ) {
				fail();
			}

			return;
		}

		//카테고리 코드 형식 검증
		if( isNullOrBlank(code) || !isMatchedPattern(code, "\\d{6}") || code.equals("000000") ) {
			fail(code);
		}

		//레벨별 검증
		switch (level) {
			case MIDDLE :
				if( !isMiddleCode(code) ) fail(code);
				break;
			case LOW:
				if( !isLowCode(code) ) fail(code);
		}
	}

	//카테고리 코드가 MIDDLE 레벨의 맞는 코드 형식이 맞는지 검사
	private static boolean isMiddleCode(String code) {
		if( code.equals("000000") ) {
			return false;
		}

		return isMatchedPattern(code, "^(?!00)....$");
	}

	//카테고리 코드가 LOW 레벨의 맞는 코드 형식이 맞는지 검사
	private static boolean isLowCode(String code) {
		if( code.equals("000000") ) {
			return false;
		}

		return isMatchedPattern( code, "^(?!00)..(?!00)..$" );
	}

	//검증 실패 시 서비스 에러를 발생
	private static void fail() {
		throw createServerException(ErrorCode.SYSTEM_LOGIN_INTERNAL, "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
	}

	//검증 실패 시 서비스 에러를 발생
	private static void fail(String code) {
		throw createServerException(ErrorCode.SYSTEM_LOGIN_INTERNAL, "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", code);
	}


}
