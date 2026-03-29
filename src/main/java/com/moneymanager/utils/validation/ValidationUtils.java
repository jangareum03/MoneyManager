package com.moneymanager.utils.validation;

/**
 * <p>
 * 패키지이름    : com.moneymanager.utils<br>
 * 파일이름       : ValidationUtils<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 14.<br>
 * 설명              : 검증 기능에 필요한 공통 기능을 정의한 클래스
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
 * 		 	  <td>25. 11. 14.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class ValidationUtils {


	/**
	 *	주어진 문자열이 정규식 패턴과 일치하는지 확인합니다. <br>
	 *	예를 들어, 숫자만 가능하다면:
	 *	<pre>{@code
	 *		boolean result = ValidationUtils.matchesPattern("111", "\\d");
	 *  }</pre>
	 *
	 * @param value			검사할 문자열
	 * @param pattern		비교할 정규식 패턴
	 * @return	문자열이 유효한 경우{@code ture}, 아니면 {@code false}
	 */
	public static boolean matchesPattern(String value, String pattern) {
		if( isNullOrBlank(value) || isNullOrBlank(pattern) ) {
			return true;
		}

		return value.matches(pattern);
	}

	/**
	 * 매개변수로 전달받은 문자열(<code>value</code>)이 null 이거나 빈 문자열인지 확인합니다.
	 *
	 * @param value		확인할 문자열
	 * @return	null이거나 문자열이면 <code>true</code>, 아니면 <code>false</code>
	 */
	public static boolean isNullOrBlank(String value) {
		return value == null || value.isBlank();
	}


}
