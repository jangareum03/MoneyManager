package com.areum.moneymanager.dto.request.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 사용자의 출석 정보를 서버로 전송하는 데이터 객체
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