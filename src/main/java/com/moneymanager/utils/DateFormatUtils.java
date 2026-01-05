package com.moneymanager.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.utils<br>
 * 파일이름       : DateFormatUtils<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 5<br>
 * 설명              : 공통적으로 사용하는 날짜 포맷 기능 클래스
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
 * 		 	  <td>26. 1. 5.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class DateFormatUtils {

	private static final String KOREAN_FORMAT = "yyyy년 MM월 dd일";

	/**
	 * 날짜 객체를 {@code yyyy년 MM월 dd일} 날짜 형식으로 지정 후 문자열로 반환합니다.
	 *
	 * @param date	문자열로 포맷할 {@link LocalDate} 객체
	 * @return 지정한 날짜 형식({@code yyyy년 MM월 dd일})으로 변환된 문자열
	 */
	public static String formatKorean(LocalDate date) {
		return formatDate( date, KOREAN_FORMAT);
	}

	//날짜를 pattern으로 포맷 후 문자열로 반환
	private static String formatDate(LocalDate date, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

		return date.format(formatter);
	}

}
