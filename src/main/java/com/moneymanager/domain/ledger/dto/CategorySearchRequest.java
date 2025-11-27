package com.moneymanager.domain.ledger.dto;

import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.domain.ledger.enums.LedgerType;
import lombok.Builder;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto<br>
 * 파일이름       : CategorySearchRequest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 26.<br>
 * 설명              : 카테고리 목록 조회 요청을 전달하기 위한 클래스
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
@Getter
@Builder
public class CategorySearchRequest {
	private final CategoryLevel level;				//카테고리 단계
	private final LedgerType ledgerType;			//가계부 유형
	private final String parentCode;					//상위 카테고리 코드 일부


	private CategorySearchRequest(CategoryLevel level, LedgerType type, String code) {
		this.level = level;
		this.ledgerType = type;
		this.parentCode = code;
	}


	/**
	 * 가계부 유형에 따라 조회할 CategorySearchRequest 객체를 생성합니다.
	 *
	 * @param type		가계부 유형(예: INCOME, OUTLAY)
	 * @return	지정한 가계부 유형으로 구성된 최상위 레벨의 CategorySearchRequest 객체
	 */
	public static CategorySearchRequest ofTopCategory(LedgerType type) {
		return new CategorySearchRequest(CategoryLevel.TOP, type, null);
	}


	/**
	 * 가계부 유형과 상위 카테고리 코드로 조회할 CategorySearchRequest 객체를 생성합니다.
	 *
	 * @param type					가계부 유형(예: INCOME, OUTLAY)
	 * @return	지정한 가계부 유형과 상위 카테고리 코드로 구성된 중간 레벨의 CategorySearchRequest 객체
	 */
	public static CategorySearchRequest ofMiddleCategory(LedgerType type) {
		return new CategorySearchRequest(CategoryLevel.MIDDLE, type, type.getDbValue());
	}


	/**
	 * 가계부 유형과 상위 카테고리 코드로 조회할 CategorySearchRequest 객체를 생성합니다.
	 *
	 * @param type					가계부 유형(예: INCOME, OUTLAY)
	 * @param middleCode		중간 카테고리 코드
	 * @return 지정한 가계부 유형과 중간 카테고리 코드로 구성된 하위 레벨의 CategorySearchRequest 객체
	 */
	public static CategorySearchRequest ofLowCategory(LedgerType type, String middleCode) {
		return new CategorySearchRequest(CategoryLevel.LOW, type, middleCode);
	}
}