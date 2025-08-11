package com.moneymanager.utils;


import com.moneymanager.enums.RegexPattern;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * 패키지이름    : com.moneymanager.utils<br>
 * 파일이름       : ValidationUtil<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 1.<br>
 * 설명              : 공통적으로 처리하는 기본검증 클래스
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
 * 		 	  <td>25. 8. 1.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class ValidationUtil {

	/**
	 * 객체의 입력여부를 검사합니다. <br>
	 * 객체가 null, empty 시 입력되지 않은 상태로 인지합니다.<br>
	 * 컬렉션 프레임워크(리스트, 맵 등) 사이즈가 0이면 입력되지 않은 상태로 인지합니다.<br>
	 *
	 * @param o 검사할 객체
	 * @return 입력되지 않은 상태면 true, 그렇지 않으면 false
	 */
	public static boolean isEmptyInput(Object o) {
		if (o == null) return true;

		if (o instanceof String) {
			return ((String) o).trim().isBlank();
		}

		if (o instanceof Collection) {
			return ((Collection<?>) o).isEmpty();
		}

		if (o instanceof Map) {
			return ((Map<?, ?>) o).isEmpty();
		}

		if (o.getClass().isArray()) {
			return Array.getLength(o) == 0;
		}

		return false;
	}


	public static boolean isMatchPattern(Object o, String regex) {
		if(o == null) return false;

		if( o instanceof String ) {
			String value = ((String) o).trim();
			String pattern = RegexPattern.valueOf(regex).getPattern();

			return value.matches(pattern);
		}

		return false;
	}

}
