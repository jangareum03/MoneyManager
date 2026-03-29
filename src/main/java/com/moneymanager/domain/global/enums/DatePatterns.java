package com.moneymanager.domain.global.enums;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.global.enums<br>
 * 파일이름       : DatePatterns<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 30<br>
 * 설명              : 날짜 패턴을 정의한 클래스
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
 * 		 	  <td>26. 3. 30</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public enum DatePatterns {
	DATE("yyyyMMdd"),
	DATE_DOT_WITH_DAY("yyyy. MM. dd (E)"),
	KOREAN_YEAR("yyyy년"),
	KOREAN_YEAR_MONTH("yyyy년 MM월"),
	KOREAN_YEAR_MONTH_WEEK("yyyy년 MM월 W주"),
	KOREAN_DATE("yyyy년 MM월 dd일"),
	KOREAN_DATE_WITH_DAY("yyyy년 MM월 dd일 E요일");

	private final String pattern;

	DatePatterns(String  pattern) {
		this.pattern = pattern;
	}
}