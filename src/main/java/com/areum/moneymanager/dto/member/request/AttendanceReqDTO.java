package com.areum.moneymanager.dto.member.request;

import com.areum.moneymanager.dto.common.request.DateReqDTO;
import lombok.*;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.member.request<br>
 * * 파일이름       : AttendanceReqDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              : 회원 출석 등록 요청을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 25.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class AttendanceReqDTO {

	/**
	 * 출석 정보를 표시하기 위한 DTO
	 */
	@Builder
	@Getter
	public static class Calendar {
		private DateReqDTO.MonthRange date;
	}

	/**
	 * 달력 이동하기 위한 DTO
	 */
	@Builder
	@Getter
	public static class Move {
		private DateReqDTO.MonthRange date;
	}

	/**
	 * 회원의 출석체크를 진행하기 위한 DTO
	 */
	@Builder
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class AttendToday {
		private DateReqDTO.DayRange today;
	}

}
