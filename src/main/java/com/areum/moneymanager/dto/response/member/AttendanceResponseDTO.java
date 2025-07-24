package com.areum.moneymanager.dto.response.member;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.response.member<br>
 *  * 파일이름       : AttendanceResponseDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 사용자에게 출석정보 데이터를 전달하기 위한 클래스
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
public class AttendanceResponseDTO {

	@Builder
	@Getter
	//회원 출석정보 가져올 때
	public static class Complete {
		private List<String> dates;
	}

}
