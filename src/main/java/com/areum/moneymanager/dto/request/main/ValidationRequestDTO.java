package com.areum.moneymanager.dto.request.main;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.request.main<br>
 *  * 파일이름       : ValidationRequestDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 데이터 검증 데이터 전달하기 위한 클래스
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
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
public class ValidationRequestDTO {


	/**
	 * 날짜의 마지막 일자를 얻을 때 필요한 객체입니다.<br>
	 * <b color='white'>year</b>는 년, <b color='white'>month</b>은 월입니다.
	 */
	@Getter
	@ToString
	@NoArgsConstructor
	public static class JsonLastDay {
		private String year;
		private String month;
	}

}
