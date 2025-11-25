package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.global.enums.RegexPattern;
import com.moneymanager.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.moneymanager.exception.ErrorUtil.createClientException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : YearMonthDayVO<br>
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
public class YearMonthDayVO {
	LocalDate date;
	int year;
	int month;
	int day;


	@Builder
	public YearMonthDayVO( YearMonthVO vo, String day ) {
		this.year = vo.getYear();
		this.month = vo.getMonth();
		this.day = parseDay(day);
		validateDay();

		this.date = LocalDate.now();
	}


	public static YearMonthDayVO fromString(String dateStr) {
		if( dateStr == null || dateStr.isBlank() ) throw createClientException(ErrorCode.COMMON_DATE_MISSING, "날짜를 입력해주세요.");

		try{
			LocalDate date = parseDate(dateStr);

			YearVO yearVO = new YearVO(String.valueOf(date.getYear()));
			YearMonthVO yearMonthVO = new YearMonthVO(yearVO, String.valueOf(date.getMonthValue()));

			return new YearMonthDayVO(yearMonthVO, String.valueOf(date.getDayOfMonth()));
		}catch (DateTimeException e) {
			throw createClientException(ErrorCode.COMMON_DATE_FORMAT, e.getMessage(), dateStr);
		}
	}


	public static LocalDate parseDate(String dateStr) {
		//문자열에서 숫자만 추출
		String digits = dateStr.replaceAll("[^0-9]", "");

		if( digits.length() != 8 ) {
			throw new DateTimeParseException("날짜 형식이 올바르지 않습니다. '20250101' 형식으로 입력해주세요.", dateStr, 0);
		}

		int year = Integer.parseInt(digits.substring(0,4));
		int month = Integer.parseInt(digits.substring(4,6));
		int day = Integer.parseInt(digits.substring(6, 8));

		return LocalDate.of(year, month ,day);
	}


	private void validateDay() {
		if( !isMatchDay() ) {
			throw createClientException(ErrorCode.COMMON_DAY_FORMAT, "일은 1 또는 2 또는 3으로 시작하는 최대 2자리 숫자만 입력 가능합니다.", day);
		}

		if( !isValidDayRange() ) {
			LocalDate now = LocalDate.of(year, month, day);
			String message = String.format("일은 %d ~ %d까지만 입력 가능합니다.", 1, now.getDayOfMonth());

			throw createClientException(ErrorCode.COMMON_DAY_INVALID, message, day);
		}
	}


	private int parseDay(String day) {
		if( day == null || day.isBlank() ) throw createClientException(ErrorCode.COMMON_DAY_MISSING, "일을 입력해주세요.");

		try{
			return Integer.parseInt(day);
		}catch ( NumberFormatException e ) {
			throw createClientException(ErrorCode.COMMON_DAY_FORMAT, "일은 숫자만 입력 가능합니다.", day);
		}
	}


	public boolean isValidDayRange() {
		try{
			LocalDate.of(
					year,
					month,
					day
			);
			return true;
		}catch ( DateTimeException e ) {
			return false;
		}
	}

	public String formatDate(String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

		return date.format(formatter);
	}


	private boolean isMatchDay() {
		return String.valueOf(day).matches(RegexPattern.DATE_DAY.getPattern());
	}
}
