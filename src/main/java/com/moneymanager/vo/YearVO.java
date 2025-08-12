package com.moneymanager.vo;

import lombok.Value;
import java.time.Year;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : YearVO<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 12.<br>
 * 설명              : 날짜 년도 값을 나타내는 클래스
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
public class YearVO {
	int MAX_YEAR_RANGE = 5;

	int year;

	public YearVO( String  year ) {
		int parsedYear;

		try{
			if( year == null ) {
				throw new IllegalArgumentException("년도는 null이 될 수 없습니다.");
			}

			parsedYear = Integer.parseInt(year);	//문자에서 숫자로 변환

			//범위 검증
			if( !isValidYearRange(parsedYear) ) {
				throw new IllegalArgumentException("년은 현재년도부터 " + MAX_YEAR_RANGE + "년 전까지여야 합니다.");
			}
		}catch ( IllegalArgumentException e ) {
			parsedYear = Year.now().getValue();
		}

		this.year = parsedYear;
	}


	private boolean isValidYearRange(int year) {
		int currentYear = Year.now().getValue();

		return (currentYear - MAX_YEAR_RANGE) < year && year <= currentYear;
	}
}
