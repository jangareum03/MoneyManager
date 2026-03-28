package com.moneymanager.utils;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
 * 		 	  <td>26. 1. 5</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 1. 5</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 이름] formatKorean → formatKoreanDate</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class DateFormatUtils {

	private static final String KOREAN_DATE = "yyyy년 MM월 dd일";
	private static final String KOREAN_DATE_WITH_DAY = "yyyy년 MM월 dd일 E요일";
	private static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";

	/**
	 *	주어진 날짜 문자열({@code date})을 {@link LocalDate} 객체로 변환합니다.
	 *
	 * @param date			변환할 날짜 문자열 (yyyyMMdd 형식)
	 * @return 변환한 {@link LocalDate} 객체
	 */
	public static LocalDate parse(String date){
		return LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE);
	}


	/**
	 * 날짜 객체를 {@value KOREAN_DATE} 날짜 형식으로 지정 후 문자열로 반환합니다.
	 *
	 * @param date	문자열로 포맷할 {@link LocalDate} 객체
	 * @return 지정한 날짜 형식으로 변환된 문자열
	 */
	public static String formatKoreanDate(LocalDate date)	 {
		return formatDate(date, KOREAN_DATE);
	}


	/**
	 * 날짜 객체를 {@value KOREAN_DATE_WITH_DAY} 날짜 형식으로 지정 후 문자열로 반환합니다.
	 *
	 * @param date	문자열로 포맷할 {@link LocalDate} 객체
	 * @return	지정한 날짜 형식으로 변환된 문자을
	 */
	public static String formatKoreanDateWithDay(LocalDate date) {
		return formatDate(date, KOREAN_DATE_WITH_DAY);
	}


	/**
	 * 날짜 객체를 {@value DATE_FORMAT_YYYYMMDD} 날짜 형식으로 지정 후 문자열로 반환합니다.
	 *
	 * @param date	문자열로 포맷할 {@link LocalDate} 객체
	 * @return 지정한 날짜 형식으로 변환된 문자열
	 */
	public static String formatYYYYMMDD(LocalDate date) {
		return formatDate(date, DATE_FORMAT_YYYYMMDD);
	}


	//날짜를 pattern으로 포맷 후 문자열로 반환
	private static String formatDate(LocalDate date, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

		return date.format(formatter);
	}

}
