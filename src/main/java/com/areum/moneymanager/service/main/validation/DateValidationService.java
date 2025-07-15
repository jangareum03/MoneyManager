package com.areum.moneymanager.service.main.validation;

import static com.areum.moneymanager.enums.RegexPattern.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;


/**
 * 날짜가 적합하게 입력되었는지 검증하는 클래스</br>
 *
 * @version 1.0
 */
@Service
public class DateValidationService {

	private final static Logger logger = LogManager.getLogger(DateValidationService.class);

	private final static int YEAR_RANGE = 5;


	/**
	 * 년도 값이 적합한지 확인합니다.<br>
	 * 년도가 null 이거나 정규식 패턴과 일치하지 않으면 {@link IllegalArgumentException}이 발생합니다.
	 *
	 * @param year		년
	 */
	private static void checkYearAvailability( String year ) {
		if( Objects.isNull(year) ) {
			throw new IllegalArgumentException("년도가 입력되지 않았습니다.");
		}else if( !year.matches(BUDGET_YEAR.getPattern()) ) {
			throw new IllegalArgumentException(year + "년은 잘못된 형식입니다.");
		}else{
			int yearValue = Integer.parseInt(year);
			int maxValue = LocalDate.now().getYear();
			int minValue = maxValue - YEAR_RANGE;


			if( !(minValue <= yearValue && yearValue <= maxValue) ) {
				throw new IllegalArgumentException("년도 값이 유효하지 않습니다. (허용범위: " + minValue + "~" + maxValue + ")");
			}
		}
	}



	/**
	 * 년 값이 적합한지 확인 후 값을 반환합니다. <br>
	 * 년이 null이거나 정규식 패턴과 일치하지 않으면 현재 년도를 반환합니다.
	 *
	 * @param year	년
	 * @return	입력한 년 또는 현재 년
	 */
	public static String getValidYearOrCurrent( String year ) {
		try{
			checkYearAvailability(year);

			return year;
		}catch ( IllegalArgumentException e ) {
			return String.valueOf(LocalDate.now().getYear());
		}
	}



	/**
	 * 월 값이 적합한지 확인합니다. <br>
	 * 월이 null이거나 정규식 패턴과 일치하지 않으면 {@link IllegalArgumentException} 이 발생합니다.
	 *
	 * @param month		월
	 */
	private static void checkMonthAvailability( String month ) {
		if( Objects.isNull(month) ) {
			throw new IllegalArgumentException("월이 입력되지 않았습니다.");
		}else if( !month.matches(BUDGET_MONTH.getPattern()) ) {
			throw new IllegalArgumentException(month + "월은 잘못된 형식입니다.");
		}else {
			int monthValue = Integer.parseInt(month);

			if( !( 0<monthValue && monthValue <=12) ) {
				throw new IllegalArgumentException("월 값이 유효하지 않습니다. (허용범위: 1~12)");
			}
		}
	}



	/**
	 * 월 값이 적합한지 확인 후 값을 반환합니다. <br>
	 * 월이 null이거나 정규식 패턴과 일치하지 않으면 현재 월을 반환합니다.
	 *
	 * @param month	월
	 * @return	입력한 월 또는 현재 월
	 */
	public static String getValidMonthOrCurrent( String month ) {
		try{
			checkMonthAvailability(month);

			return String.format("%02d", Integer.parseInt(month));
		}catch ( IllegalArgumentException e ) {
			return String.format("%02d", LocalDate.now().getMonthValue());
		}
	}



	/**
	 * 주 값이 적합한지 확인합니다.<br>
	 * 주가  null이거나 정규식 패턴과 일치하지 않으면 {@link IllegalArgumentException}이 발생합니다.
	 *
	 * @param week		주
	 */
	public static void checkWeekAvailability( String week ) {
		if( Objects.isNull(week) ) {
			throw new IllegalArgumentException("주가 입력되지 않았습니다.");
		}else if( !week.matches(BUDGET_WEEK.getPattern()) ) {
			throw new IllegalArgumentException("주 값이 유효하지 않습니다. (허용범위: 1~5)");
		}
	}



	/**
	 * 주 값이 적합한지 확인 후 값을 반환합니다. <br>
	 * 주이 null이거나 정규식 패턴과 일치하지 않으면 현재 주를 반환합니다.
	 *
	 * @param week		주
	 * @return	입력한 주 또는 현재 주
	 */
	public static String getValidWeekOrCurrent( String week ) {
		try{
			checkWeekAvailability(week);

			return week;
		}catch ( IllegalArgumentException e ) {
			Calendar cal = Calendar.getInstance();

			return String.valueOf( cal.get(Calendar.WEEK_OF_MONTH) );
		}
	}



	/**
	 * 일 값이 적합한지 확인합니다.<br>
	 * 일이 null이거나 정규식 패턴과 일치하지 않으면 {@link IllegalArgumentException}이 발생합니다.
	 *
	 * @param day		일
	 */
	private static void checkDayAvailability( String day, int maxValue ) {
		if( Objects.isNull(day) ) {
			throw new IllegalArgumentException("일이 입력되지 않았습니다.");
		}else if( !(day.matches(BUDGET_DAY.getPattern())) ) {
			throw new IllegalArgumentException(day + "일은 잘못된 형식입니다.");
		}else {
			int dayValue = Integer.parseInt(day);

			if( !(0 < dayValue && dayValue <= maxValue) ) {
				throw new IllegalArgumentException("일 값이 유효하지 않습니다. (허용범위: 1~" + maxValue + ")");
			}
		}
	}



	public static void checkDateAvailability( String date ) {
		String year = date.substring(0, 4);
		String month = date.substring(4, 6).startsWith("0") ? date.substring(5,6) : date.substring(4, 6);
		String day = date.substring(6, 8);

		DateValidationService.checkYearAvailability(year);
		DateValidationService.checkMonthAvailability(month);
		DateValidationService.checkDayAvailability(day, LocalDate.of( Integer.parseInt(year), Integer.parseInt(month), 1 ).lengthOfMonth());
	}



	/**
	 * 날짜 값이  null 이거나 0이면 true를 반환합니다.
	 *
	 * @param value		검사할 날짜값
	 * @return	null 이거나 0이면 true
	 */
	public static boolean isNullOrZero( Integer value ) {
		return Objects.isNull(value) || value == 0;
	}
}
