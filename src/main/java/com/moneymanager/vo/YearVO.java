package com.moneymanager.vo;

import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import lombok.Value;

import java.time.LocalDate;
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
		if( year == null ) throw new ClientException(ErrorCode.COMMON_YEAR_MISSING, "년도를 입력해주세요.");

		try{
			int parsedYear = Integer.parseInt(year);

			if( !isValidYearRange(parsedYear) ) {
				int currentYear = LocalDate.now().getYear();
				throw new ClientException(ErrorCode.COMMON_YEAR_INVALID, String.format("년도는 %d ~ %d까지만 입력가능합니다.", currentYear-MAX_YEAR_RANGE, currentYear));
			}

			this.year = parsedYear;
		}catch ( NumberFormatException e ) {
			throw new ClientException(ErrorCode.COMMON_YEAR_FORMAT, "년도는 4자리 숫자만 입력 가능합니다.");
		}
	}


	private boolean isValidYearRange(int year) {
		int currentYear = Year.now().getValue();

		return (currentYear - MAX_YEAR_RANGE) < year && year <= currentYear;
	}
}
