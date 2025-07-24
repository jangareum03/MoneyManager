package com.areum.moneymanager.enums.type;

import com.areum.moneymanager.exception.code.ErrorCode;
import com.areum.moneymanager.exception.ErrorException;
import lombok.Getter;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.enums.type<br>
 *  * 파일이름       : DateRangeType<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 7. 15<br>
 *  * 설명              : 가계부 날짜 범위를 정의한 클래스
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
 *		 	  <td>22. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Getter
public enum DateRangeType {

	YEAR("Y", "년"), MONTH("M", "월"), WEEK("W", "주");

	private final String type;
	private final String text;

	DateRangeType(String type, String text ) {
		this.type = type;
		this.text = text;
	}


	/**
	 * 문자열 type을 Enum 객체로 변환
	 *
	 * @param type	날짜 유형
	 * @return	type에 맞는 Enum 객체
	 */
	public static DateRangeType toEnum( String type ) {
		for( DateRangeType dateType : DateRangeType.values() ) {
			if( dateType.getType().equalsIgnoreCase(type) ) {
				return dateType;
			}
		}

		throw new ErrorException(ErrorCode.BUDGET_TYPE_NONE);
	}
}
