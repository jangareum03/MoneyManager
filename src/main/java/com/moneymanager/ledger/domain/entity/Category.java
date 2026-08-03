package com.moneymanager.ledger.domain.entity;

import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.log.DeveloperLogInfo;
import lombok.Builder;
import lombok.Getter;

import static com.moneymanager.global.exception.code.CommonErrorCode.REQUIRED_VALUE;
import static com.moneymanager.global.exception.code.LedgerErrorCode.DATA_INTEGRITY_ERROR;
import static com.moneymanager.global.util.string.StringUtil.isNullOrBlank;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.domain.ledger.entity<br>
 *  * 파일이름       : Category<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 11. 17<br>
 *  * 설명              : LEDGER_CATEGORY 테이블과 매칭되는 클래스
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
 *		 	  <td>22. 11. 17</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>최초 생성(버전 1.0)</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>22. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Getter
@Builder
public class Category {
    private final String code;						//카테고리 번호(식별자)
	private final String name;						//카테코리 이름
    private final String parentCode;			//부모 카테고리 코드

	private Category(String code, String name, String parentCode) {
		validate(code, name);

		this.code = code;
		this.name = name;
		this.parentCode = parentCode;
	}

	public static Category topCategory(String code, String name) {
		return new Category(code, name, null);
	}

	public static Category childCategory(String code, String name, Category parent) {
		//부모 카테고리 검증
		if(parent == null) {
			throw BusinessException.of(
					DATA_INTEGRITY_ERROR,
					DeveloperLogInfo.of("객체 생성", "부모 카테고리 없음", "parent", null)
			);
		}

		return new Category(code, name, parent.code);
	}

	private static void validate(String code, String name) {
		if(isNullOrBlank(code)) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					DeveloperLogInfo.of("카테고리 검증", "카테고리 코드 없음", "code", code)
			);
		}

		if(isNullOrBlank(name)) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					DeveloperLogInfo.of("카테고리 검증", "카테고리 이름 없음", "name", name)
			);
		}
	}

}
