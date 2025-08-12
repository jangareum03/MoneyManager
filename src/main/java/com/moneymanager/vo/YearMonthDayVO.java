package com.moneymanager.vo;

import lombok.Builder;
import lombok.Value;

import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : YearMonthDayVO<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 12.<br>
 * 설명              : 날짜 년도, 월, 일의 값을 나타내는 클래스
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
public class YearMonthDayVO {
	YearMonthVO yearMonthVO;
	int day;

	@Builder
	public YearMonthDayVO( String year, String month, String day ) {
		this.yearMonthVO = new YearMonthVO( year, month );

		int parsedDay;
		try {
			if( day == null ) {
				throw new IllegalArgumentException("일은 null이 될 수 없습니다.");
			}

			parsedDay = Integer.parseInt(day);
			if( !isValidDayRange(parsedDay) ) {
				throw new IllegalArgumentException("일은 1~N일까지만 가능합니다.");
			}

		}catch ( IllegalArgumentException e ) {
			parsedDay = 1;
		}

		this.day = parsedDay;
	}


	private boolean isValidDayRange( int day ) {
		int year = yearMonthVO.getYearVO().getYear();
		int month = yearMonthVO.getMonth();

		try{
			LocalDate date = LocalDate.of(year, month, day);
			return true;
		}catch ( DateTimeException e ) {
			return false;
		}
	}


	public LocalDate toLocalDate() {
		YearMonthVO monthVO = getYearMonthVO();
		YearVO yearVO = monthVO.getYearVO();

		return LocalDate.of( yearVO.getYear(), monthVO.getMonth(), day );
	}
}
