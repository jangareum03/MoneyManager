package com.moneymanager.utils.string;

import static com.moneymanager.utils.validation.ValidationUtils.isNullOrBlank;

/**
 * <p>
 * 패키지이름    : com.moneymanager.utils.string<br>
 * 파일이름       : StringUtils<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 23<br>
 * 설명              : 공통적으로 사용하는 문자 기능 클래스
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
 * 		 	  <td>26. 3. 23</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class StringUtils {

	private StringUtils() {}

	/**
	 * 문자열의 앞(prefix)과 뒤(suffix)에 포함된 특정 문자열을 제거합니다.
	 * <p>
	 *     주어진 문자열이 prefix로 시작하면 제거하고, suffix로 끝나면 제거합니다.
	 *     prefix 또는 suffix가 null이거나 공백인 경우에는 원본 문자열을 반환합니다.
	 *
	 *     <p>
	 *         예제 사용법:
	 *         <pre>{@code
	 *         	unwrap("[a, b, c]", "[", "]");		//결과: "a, b, c"
	 *         	unwrap("hello", "he", "x");		//결과: "llo"
	 *         	unwrap("hello", null, "ee");	//결과: "hello"
	 *         }</pre>
	 *     </p>
	 * </p>
	 *
	 * @param text		대상 문자열
	 * @param prefix	제거할 접두사
	 * @param suffix	제거할 접미사
	 * @return	{@code prefix}와 {@code suffix}가 제거된 문자열
	 */
	public static String unwrap(String text, String prefix, String suffix) {
		if(isNullOrBlank(text)) {
			return text;
		}

		if(!isNullOrBlank(prefix) && text.startsWith(prefix)) {
			text = text.substring(prefix.length());
		}

		if(!isNullOrBlank(suffix) && text.endsWith(suffix)) {
			int end = text.length() - suffix.length();

			text = text.substring(0, end);
		}

		return text;
	}

}
