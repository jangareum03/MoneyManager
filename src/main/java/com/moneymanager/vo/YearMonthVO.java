package com.moneymanager.vo;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : YearMonthVO<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 12.<br>
 * 설명              : 날짜 년도, 월의 값을 나타내는 클래스
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
public class YearMonthVO {
	YearVO yearVO;
	int month;

	@Builder
	public YearMonthVO( String year, String month ) {
		this.yearVO = new YearVO(year);

		int parsedMonth;
		try{
			parsedMonth = (month == null) ? LocalDate.now().getMonthValue() : Integer.parseInt(month);
		}catch ( NumberFormatException e ) {
			throw new IllegalArgumentException("MONTH_FORMAT");
		}

		if(!isValidMonthRange(parsedMonth)) {
			throw new IllegalArgumentException("MONTH_INVALID");
		}

		this.month = parsedMonth;
	}

	private boolean isValidMonthRange(int month) {
		return 1 <= month && month <= 12;
	}
}
