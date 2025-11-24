package com.moneymanager.domain.global.dto;

import com.moneymanager.domain.budgetBook.enums.DateType;
import lombok.*;

import java.time.LocalDate;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.common.request<br>
 * * 파일이름       : DateRequest<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              : 날짜 요청을 위한 데이터 클래스
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
@Getter
@ToString
public class DateRequest {
	//날짜 유형
	private final DateType type;
	//년
	private final Integer year;
	//월
	private final Integer month;
	//주
	private final Integer week;
	//일
	private final Integer day;


	public DateRequest() {
		LocalDate today = LocalDate.now();

		this.type = DateType.MONTH;
		this.year = today.getYear();
		this.month = today.getMonthValue();
		this.week = null;
		this.day = null;
	}


	public DateRequest(YearRange yearRange ) {
		this.type = DateType.YEAR;
		this.year = Integer.parseInt(yearRange.getYear());
		this.month = null;
		this.week = null;
		this.day = null;
	}

	public DateRequest(MonthRange monthRange ) {
		this.type = DateType.MONTH;
		this.year = Integer.parseInt(monthRange.getYear());
		this.month =  Integer.parseInt(monthRange.getMonth());
		this.week = null;
		this.day = null;
	}

	public DateRequest(WeekRange weekRange ) {
		this.type = DateType.WEEK;
		this.year = Integer.parseInt(weekRange.getYear());
		this.month =  Integer.parseInt(weekRange.getMonth());
		this.week = Integer.parseInt(weekRange.getWeek());
		this.day = null;
	}

	public DateRequest(DayRange dayRange ) {
		this.type = DateType.DAY;
		this.year = Integer.parseInt(dayRange.getYear());
		this.month =  Integer.parseInt(dayRange.getMonth());
		this.week = null;
		this.day =  Integer.parseInt(dayRange.getDay());
	}

	/**
	 * 특정 연도에 대한 날짜 정보를 얻기 위한 DTO
	 */
	@Builder
	@Getter
	public static class YearRange {
		//년
		private String year;
	}

	/**
	 * 특정 월에 대한 날짜 정보를 얻기 위한 DTO
	 */
	@Builder
	@Getter
	public static class MonthRange {
		//년
		private String year;
		//월
		private String month;
	}

	/**
	 * 특정 주에 대한 날짜 정보를 얻기 위한 DTO
	 */
	@Builder
	@Getter
	public static class WeekRange {
		//년
		private String year;
		//월
		private String month;
		//주
		private String week;
	}

	/**
	 * 특정 일에 대한 날짜 정보를 얻기 위한 DTO
	 */
	@Builder
	@Getter
	public static class DayRange {
		//년
		private String year;
		//월
		private String month;
		//일
		private String day;
	}

}
