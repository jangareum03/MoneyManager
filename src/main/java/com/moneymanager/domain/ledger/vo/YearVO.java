package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.global.enums.RegexPattern;
import com.moneymanager.exception.ErrorCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Year;

import static com.moneymanager.exception.ErrorUtil.createClientException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : YearVO<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 17.<br>
 * 설명              :
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
 * 		 	  <td>25. 11. 17.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class YearVO {
	int year;


	public YearVO(String  year) {
		this(year, 0);
	}


	public YearVO( String  year, int maxValue ) {
		this.year = parseYear(year);

		validYear(maxValue);
	}


	private void validYear(int max) {
		if( !isMatchYear() ){
			throw createClientException(ErrorCode.COMMON_YEAR_FORMAT, "년도는 1 또는 2로 시작하는 4자리 숫자만 입력 가능합니다.", String.valueOf(year));
		}

		if( !isValidYearRange(year, max) ) {
			int currentYear = LocalDate.now().getYear();
			String message = String.format("년도는 %d ~ %d까지만 입력가능합니다.", currentYear-max, currentYear);

			throw createClientException(ErrorCode.COMMON_YEAR_INVALID, message, String.valueOf(year));
		}
	}


	private int parseYear(String year) {
		if( year == null ) throw createClientException(ErrorCode.COMMON_YEAR_MISSING, "년도를 입력해주세요.");

		try{
			return Integer.parseInt(year);
		}catch (NumberFormatException e) {
			throw createClientException(ErrorCode.COMMON_YEAR_FORMAT, "년도는 숫자만 입력 가능합니다.", year);
		}
	}


	private boolean isMatchYear() {
		return String.valueOf(year).matches(RegexPattern.DATE_YEAR.getPattern());
	}


	private boolean isValidYearRange(int year, int range) {
		if( range == 0 ) return true;

		int currentYear = Year.now().getValue();

		return (currentYear - range) < year && year <= currentYear;
	}
}
