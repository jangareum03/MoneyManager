package com.moneymanager.global.util.string;

/**
 * <p>
 * 패키지이름    : com.moneymanager.utils.string<br>
 * 파일이름       : StringUtil<br>
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
public class StringUtil {

	private StringUtil() {}

	/**
	 * 매개변수로 전달받은 문자열(<code>value</code>)이 null 이거나 빈 문자열인지 확인합니다.
	 *
	 * @param value		확인할 문자열
	 * @return	null이거나 문자열이면 <code>true</code>, 아니면 <code>false</code>
	 */
	public static boolean isNullOrBlank(String value) {
		return value == null || value.trim().isBlank();
	}

	public static String masking(String text, int startIndex, int maskLength) {
		if(isNullOrBlank(text)) {
			throw new IllegalArgumentException("마스킹할 문자 누락");
		}

		if(startIndex <0) {
			throw new IllegalArgumentException("시작 위치 음수");
		}

		if(maskLength <= 0) {
			throw new IllegalArgumentException("마스킹할 길이 0 이하");
		}

		if(startIndex > text.length()) {
			throw new IllegalArgumentException("시작 위치가 문자길이 초과");
		}

		if(text.length() < startIndex + maskLength) {
			throw new IllegalArgumentException("마스킹할 길이가 문자길이 초과");
		}

		int endIndex = startIndex + maskLength;

		return text.substring(0, startIndex)
				+ "*".repeat(maskLength)
				+ text.substring(endIndex);
	}

}