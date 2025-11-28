package com.moneymanager.domain.ledger.dto;

import com.moneymanager.domain.ledger.enums.CategoryLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto<br>
 * 파일이름       : CategoryRequest<br>
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
public class CategoryRequest {
	private final CategoryLevel level;				//카테고리 단계
	private final String code;								//선택한 카테고리 코드


	@Builder
	private CategoryRequest(String level, String code) {
		this.level = CategoryLevel.from(level);
		this.code = code;
	}


	/**
	 * 상위 단계(TOP) 카테고리 조회할 CategoryRequest 객체를 생성합니다.
	 *
	 * @return	최상위 레벨의 CategoryRequest 객체
	 */
	public static CategoryRequest ofTopCategory() {
		return CategoryRequest.builder()
				.level("top")
				.code(null)
				.build();
	}


	/**
	 * 중간 단계(MIDDLE) 카테고리 조회할 CategoryRequest 객체를 생성합니다.
	 *
	 * @return	상위 카테고리 코드로 구성된 중간 레벨의 CategoryRequest 객체
	 */
	public static CategoryRequest ofMiddleCategory(String code) {
		return CategoryRequest.builder()
				.level("middle")
				.code(code)
				.build();
	}


	/**
	 * 하위 단계(LOW) 카테고리 조회할 CategoryRequest 객체를 생성합니다.
	 *
	 * @param code		중간 카테고리 코드
	 * @return 지정한 가계부 유형과 중간 카테고리 코드로 구성된 하위 레벨의 CategoryRequest 객체
	 */
	public static CategoryRequest ofLowCategory(String code) {
		return CategoryRequest.builder()
				.level("low")
				.code(code)
				.build();
	}
}