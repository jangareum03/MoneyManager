package com.moneymanager.vo;

import com.moneymanager.enums.RegexPattern;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.utils.ValidationUtil;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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


	/**
	 * 다양한 형식의 문자열인 날짜({@code dateStr})를 {@link YearMonthDayVO} 객체로 변환합니다.
	 * <p>
	 *     날짜({@code dateStr})에서 년도, 월, 일을 숫자로 추출하여 "yyyyMMdd" 형식으로 변환 후 파싱합니다.<br>
	 *     단, 존재하지 않은 날짜(예: "2025년 2월 31일")는 파싱 시 예외가 발생합니다.
	 * </p>
	 * <p>
	 *     예시:
	 *     <ul>
	 *         <li>{@code dateStr} = "2025년 2월 28일"이면 YearMonthDayVO(YearMonthVO(YearVO("2025"), "2"), "28")</li>
	 *         <li>{@code dateStr} = "20250228"이면 YearMonthDayVO(YearMonthVO(YearVO("2025"), "2"), "28")</li>
	 *     </ul>
	 * </p>
	 * @param dateStr	변환할 날짜(년도, 월, 일을 포함) 문자열
	 * @return 변환된 {@link YearMonthDayVO} 객체
	 */
	public static YearMonthDayVO fromString(String dateStr) {
		if( dateStr == null || dateStr.isBlank() ) throw ValidationUtil.createClientException(ErrorCode.COMMON_DATE_MISSING, "날짜를 입력해주세요.");

		try{
			LocalDate date = parseDate(dateStr);

			YearVO yearVO = new YearVO(String.valueOf(date.getYear()));
			YearMonthVO yearMonthVO = new YearMonthVO(yearVO, String.valueOf(date.getMonthValue()));

			return new YearMonthDayVO(yearMonthVO, String.valueOf(date.getDayOfMonth()));
		}catch (DateTimeException e) {
			throw ValidationUtil.createClientException(ErrorCode.COMMON_DATE_FORMAT, e.getMessage(), dateStr);
		}
	}


	/**
	 * 문자열 날짜({@code dateStr})에서 숫자만 추출 후 "yyyyMMdd" 형식으로 파싱 후 LocalDate로 반환합니다.
	 * <p>
	 *    	예시 :
	 *    <ul>
	 *        <li>{@code dateStr} = "2025년 2월 28일" 이면 LocalDate(2025-02-28)</li>
	 *        <li>{@code dateStr} = "20250228" 이면 LocalDate(2025-02-28)</li>
	 *    </ul>
	 *    존재하지 않은 날짜(예: "2025-02-31")는 파싱 시 {@link DateTimeException} 발생합니다.
	 * </p>
	 *
	 * @param dateStr		파싱할 날짜 문자열
	 * @return	파싱된 {@link LocalDate} 객체
	 * @throws DateTimeException	형식이 올바르지 않은 경우 발생
	 */
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


	/**
	 * 정수 일({@code day})을 검증합니다.
	 * <p>
	 *     다음과 같은 경우 {@link ClientException}예외가 발생합니다.
	 *     <ul>
	 *         <li>일이 정규식 패턴과 일치하지 않은 경우 → {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 *         <li>일이 월의 마지막일에 벗어난 경우 → {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 *     </ul>
	 * </p>
	 * @throws ClientException	일이 허용되는 범위에 벗어난 경우 발생
	 */
	private void validateDay() {
		if( !isMatchDay() ) {
			throw ValidationUtil.createClientException(ErrorCode.COMMON_DAY_FORMAT, "일은 1 또는 2 또는 3으로 시작하는 최대 2자리 숫자만 입력 가능합니다.", day);
		}

		if( !isValidDayRange() ) {
			LocalDate now = LocalDate.of(year, month, day);
			String message = String.format("일은 %d ~ %d까지만 입력 가능합니다.", 1, now.getDayOfMonth());

			throw ValidationUtil.createClientException(ErrorCode.COMMON_DAY_INVALID, message, day);
		}
	}


	/**
	 * 문자열 일({@code day})을 정수로 변환합니다.
	 * <p>
	 *     다음과 같은 경우 {@link ClientException}예외가 발생합니다.
	 *     <ul>
	 *         <li>일이 {@code null} 또는 "" 경우 → {@link ValidationUtil#createClientException(ErrorCode, String)} 메서드 호출</li>
	 *    		<li>일이 숫자로 변환이 안되는 경우 → {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 *    </ul>
	 * </p>
	 * @param day	문자열로 입력된 일
	 * @return	정수로 변환된 일 값(1~31)
	 * @throws ClientException	일이 {@code null}이거나 숫자로 변환할수 없는 경우 발생
	 */
	private int parseDay(String day) {
		if( day == null || day.isBlank() ) throw ValidationUtil.createClientException(ErrorCode.COMMON_DAY_MISSING, "일을 입력해주세요.");

		try{
			return Integer.parseInt(day);
		}catch ( NumberFormatException e ) {
			throw ValidationUtil.createClientException(ErrorCode.COMMON_DAY_FORMAT, "일은 숫자만 입력 가능합니다.", day);
		}
	}


	/**
	 * 일({@code day})이 해당 년과 월 내에서 유효한 날짜인지 확인합니다.
	 * <p>
	 *     유효하지 않은 날짜면 {@link DateTimeException} 예외가 발생하여 false을 반환됩니다.
	 * </p>
	 *
	 * @return	유효한 값이면 true, 아니면 false
	 */
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


	/**
	 * 일({@code day})이 정규식 패턴과 비교합니다.
	 * <p>
	 *     예를 들어
	 *     <ul>
	 *         <li>{@code day} = 15 이면 true 반환</li>
	 *         <li>{@code day} = 0 이면 false 반환</li>
	 *         <li>{@code day} = 32 이면 false 반환</li>
	 *     </ul>
	 * </p>
	 * @return	정규식 패턴과 일치하면 true, 아니면 false
	 */
	private boolean isMatchDay() {
		return String.valueOf(day).matches(RegexPattern.DATE_DAY.getPattern());
	}


}
