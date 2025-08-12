package com.moneymanager.vo;

import lombok.Builder;
import lombok.Value;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : YearMonthWeekVO<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 12.<br>
 * 설명              : 날짜 년도, 월, 주의 값을 나타내는 클래스
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
 * 		 	  <td>25. 8. 12.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
public class YearMonthWeekVO {
	YearMonthVO yearMonthVO;
	int week;

	@Builder
	public YearMonthWeekVO( String year, String month, String week ) {
		yearMonthVO = YearMonthVO.builder().year(year).month(month).build();

		int parsedWeek;
		try{
			if( week == null ) {
				throw new IllegalArgumentException("주는 nulㅣ이 될 수 없습니다.");
			}

			parsedWeek = Integer.parseInt(week);
			if( !isValidWeekRange(parsedWeek) ) {
				throw new IllegalArgumentException("주의 범위는 1~N까지 입니다.");
			}

		}catch ( IllegalArgumentException e ) {
			parsedWeek = 1;
		}

		this.week = parsedWeek;
	}


	private boolean isValidWeekRange( int week ) {
		int year = yearMonthVO.getYearVO().getYear();
		int month = yearMonthVO.getMonth();

		long maxWeek = getMaxWeekByMonth(year, month);

		return 1 <= week && week <= maxWeek;
	}


	private long getMaxWeekByMonth( int year, int month ) {
		LocalDate firstDay = LocalDate.of(year, month, 1);
		LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

		//첫 주의 시작(월의 1일이 포함된 주의 월요일)
		LocalDate startOfFirstWeek = firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		//마지막 주의 끝(월의 마지막 일이 포함된 주의 일요일)
		LocalDate endOfLastWeek = lastDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

		//총 주 수
		return ChronoUnit.WEEKS.between(startOfFirstWeek, endOfLastWeek) + 1;
	}
}
