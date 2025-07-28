package com.areum.moneymanager.dto.common.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.common.request<br>
 * * 파일이름       : DateReqDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              :
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
public class DateReqDTO {

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
