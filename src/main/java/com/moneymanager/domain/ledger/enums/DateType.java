package com.moneymanager.domain.ledger.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * <p>
 *  * 패키지이름    : com.moneymanager.domain.ledger.enums<br>
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
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Getter
public enum DateType {

	YEAR("year", "년"), MONTH("month", "월"), WEEK("week", "주"), DAY("day", "일");

	private final String type;
	private final String text;

	DateType( String type, String text ) {
		this.type = type;
		this.text = text;
	}


	/**
	 * 날짜 유형을 비교하여 동일한 DateType을 반환합니다.
	 *
	 * @param type		날짜 유형
	 * @return	DateType
	 */
	public static DateType from( String type ) {
		return Arrays.stream(DateType.values())
				.filter( t -> t.getType().equalsIgnoreCase(type) )
				.findFirst()
				.orElse( MONTH );
	}
}
