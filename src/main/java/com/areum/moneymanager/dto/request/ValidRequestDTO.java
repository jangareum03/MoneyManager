package com.areum.moneymanager.dto.request;


import lombok.Builder;
import lombok.Getter;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.request<br>
 *  * 파일이름       : AttendanceRequestDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 22<br>
 *  * 설명              : 검증 및 로그 기록 데이터를 전달하기 위한 클래스
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
 *		 	  <td>25. 7. 22</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>최초 생성 (버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Builder
@Getter
public class ValidRequestDTO<T> {

	Object key;
	String errorPrefix;
	T requestData;

}
