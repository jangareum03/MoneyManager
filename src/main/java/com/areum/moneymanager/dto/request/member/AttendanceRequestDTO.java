package com.areum.moneymanager.dto.request.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.request.member<br>
 *  * 파일이름       : AttendanceRequestDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 출석정보 데이터를 전달하기 위한 클래스
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
public class AttendanceRequestDTO {

	/**
	 * 출석 정보를 가져올 때 필요한 객체입니다.<br>
	 * <b color='white'>year</b>은 년도, <b color='white'>month</b>은 월입니다.
	 */
	@Getter
	@Builder
	public static class Calendar {
		private int year;
		private int month;
	}


	/**
	 * 날짜로 출석을 진행할 때 필요한 객체입니다. <br>
	 * <b color='white'>year</b>는 년도,
	 * <b color='white'>month</b>는 월,
	 * <b color='white'>day</b>는 일입니다.
	 */
	@Getter
	@NoArgsConstructor
	public static class Date {
		private int year;
		private int month;
		private int day;
	}

	/**
	 * 달력 이동할 때 필요한 객체입니다.<p>
	 * <b color='white'>year</b>은 년도, <b color='white'>month</b>은 월입니다.
	 */
	@Builder
	@Getter
	@ToString
	public static class Move {
		private Integer year;
		private Integer month;
	}



}