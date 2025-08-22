package com.moneymanager.vo;

import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
 * 		 	  <td>25. 8. 12</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 8. 21</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 추가] formatDate - 날짜를 포맷으로 변경 후 반환</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Value
public class YearMonthDayVO {
	LocalDate date;

	YearMonthVO yearMonthVO;
	int day;

	@Builder
	public YearMonthDayVO( String year, String month, String day ) {
		this.yearMonthVO = new YearMonthVO( year, month );
		this.day = parseDayOrDefault(day);
		this.date = LocalDate.of(
				yearMonthVO.getYearVO().getYear(),
				yearMonthVO.getMonth(),
				this.day
		);
	}


	/**
	 *	주어진 일(day)을 정수로 변환하여 반환합니다.
	 *
	 * @param day					사용자가 입력한 일
	 * @return	유효한 일 값
	 */
	private int parseDayOrDefault(String day) {
		if( day == null ) throw new ClientException(ErrorCode.COMMON_DAY_MISSING, "일을 입력해주세요.");

		try {
			int parsedDay = Integer.parseInt(day);
			if( !isValidDayRange(parsedDay) ) {
				LocalDate now = LocalDate.of(yearMonthVO.getYearVO().getYear(), yearMonthVO.getMonth(), 1);
				throw new ClientException(ErrorCode.COMMON_DAY_INVALID, String.format("일은 %d ~ %d까지만 입력 가능합니다.", 1, now.getDayOfMonth()));
			}

			return parsedDay;
		}catch ( NumberFormatException e ) {
			throw new ClientException(ErrorCode.COMMON_DAY_FORMAT, "일은 2자리 숫자만 입력 가능합니다.");
		}
	}


	/**
	 * 주어진 일(day)이 해당 연과월 내에서 유효한 날짜인지 확인합니다.
	 * <p>
	 *     유효하지 않은 날짜면 {@link DateTimeException} 예외가 발생하여 false을 반환됩니다.
	 * </p>
	 *
	 * @param day		유효 검사할 일
	 * @return	유효한 값이면 true, 아니면 false
	 */
	private boolean isValidDayRange( int day ) {
		try{
			LocalDate.of(
					yearMonthVO.getYearVO().getYear(),
					yearMonthVO.getMonth(),
					day
			);
			return true;
		}catch ( DateTimeException e ) {
			return false;
		}
	}


	public static YearMonthDayVO fromStringDate(String date) {
		if( date == null ) throw new ClientException(ErrorCode.COMMON_DATE_MISSING, "날짜를 입력해주세요.");
		if( date.length() != 8 ) throw new ClientException(ErrorCode.COMMON_DATE_FORMAT, "날짜를 YYYYMMDD 형식으로 입력해주세요.");

		String year = date.substring(0, 4);
		String month = date.substring(4, 6);
		String day = date.substring(6);

		return new YearMonthDayVO(year, month, day);
	}


	/**
	 * 연과 월의 첫째 일(=1일)을 반환합니다.
	 * <p>
	 *     예를 들어, 2025년 8월이면 2025-08-01이 반환됩니다.
	 * </p>
	 *
	 * @return	연과 월의 첫째 날을 나타내는 {@link LocalDate} 객체
	 */
	public LocalDate firstDayOfMonth() {
		return date.withDayOfMonth(1);
	}


	/**
	 * 연과 월의 마지막 일을 반환합니다.
	 * <p>
	 *     예를 들어, 2025년 8월이면 2025-08-31이 반환됩니다.
	 * </p>
	 *
	 * @return	연과 월의 마지막 날을 나타내는 {@link LocalDate} 객체
	 */
	public LocalDate lastDayOfMonth() {
		return date.withDayOfMonth(date.lengthOfMonth());
	}


	/**
	 * 날짜를 지정된 형식{@code pattern}으로 문자열로 반환합니다.
	 *
	 * @param pattern	날짜 포맷 패턴
	 * @return	형변환한 날짜 문자열
	 */
	public String formatDate(String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

		return date.format(formatter);
	}
}
