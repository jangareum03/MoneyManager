package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.global.vo.DateGroupable;
import com.moneymanager.exception.ErrorCode;
import lombok.Getter;
import lombok.Value;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static com.moneymanager.exception.ErrorUtil.createClientException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : DateScope<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 14.<br>
 * 설명              : 가계부 조회범위 값을 나타내는 클래스
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
 * 		 	  <td>25. 11. 14.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
@Getter
public class DateScope implements DateGroupable {

	DayOfWeek startDay;
	DayOfWeek endDay;

	Integer year;
	Integer month;
	Integer week;

	private DateScope(Integer year, Integer month, Integer week) {
		this(year, month, week, null, null);
	}

	private DateScope(Integer year, Integer month, Integer week, DayOfWeek start, DayOfWeek end) {
		this.year = year;
		this.month = month;
		this.week = week;

		this.startDay = start;
		this.endDay = end;
	}


	/**
	 * 연도, 월, 주차 정보별로 시작날짜를 계산하여 반환합니다.
	 * <ul>
	 *     <li>연도만 있는 경우 → 해당 연도의 1월 1일 반환</li>
	 *     <li>연도+월만 있는 경우 → 해당 월의 1일 반환</li>
	 *     <li>연도 + 월 + 주 모두 있는 경우 → 해당 주의 월요일 날짜 반환</li>
	 * </ul>
	 *
	 * @return	주어진 조건에 만족하는 시작 날짜를 담은 {@link LocalDate}
	 */
	@Override
	public LocalDate getStartDate() {
		//년도만 있을 경우
		if( month == null ) {
			return LocalDate.of(year, 1, 1);
		}

		LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);		//년,월에 해당하는 첫째일

		//년+월만 있는 경우
		if(week == null ) {
			return firstDayOfMonth;
		}

		//년+월+주 모두 있는 경우
		return getWeekStartDate(firstDayOfMonth, week);
	}


	//해당 월의 주차에 맞는 월요일 날짜 계산
	private LocalDate getWeekStartDate(LocalDate firstDayOfMonth, int week) {
		if( week == 1 ) {
			return firstDayOfMonth;
		}

		//첫째주에 해당하는 주차의 월요일 날짜
		LocalDate firstMondayOfMonth = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(startDay));

		return firstMondayOfMonth.plusDays( (week - 1) * 7L );
	}


	/**
	 * 연도, 월, 주차 정보별로 종료날짜를 계산하여 반환합니다.
	 * <ul>
	 *     <li>연도만 있는 경우 → 해당 연도의 12월 31일 반환</li>
	 *     <li>연도 + 월만 있는 경우 → 해당 월의 마지막날 반환</li>
	 *     <li>연도 + 월 + 주 모두 있는 경우 → 해당 주차의  일요일 날짜 반환(단, 해당 주차의 일요일이 월을 넘어가면 월의 마지막날로 조정)</li>
	 * </ul>
	 *
	 * @return	주어진 조건에 만족하는 마지막 날짜 담은 {@link LocalDate}
	 */
	@Override
	public LocalDate getEndDate() {
		//년도만 있을 경우
		if( month == null ) {
			return LocalDate.of(year, 12, 31);
		}

		LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);

		//년 + 월만 있는 경우
		if( week == null ) {
			return firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
		}

		//년 + 월 + 주 모두 있는 경우
		return getWeekEndDate(firstDayOfMonth, week);
	}


	//해당 월의 주차에 맞는 일요일 날짜 계산
	private LocalDate getWeekEndDate(LocalDate firstOfMonth, int week) {
		//첫째주에 해당하는 주차의 일요일 날짜
		LocalDate firstSundayOfMonth = firstOfMonth.with(TemporalAdjusters.nextOrSame(endDay));

		if( week == 1 ) {
			return firstSundayOfMonth;
		}

		LocalDate lastDayOfMonth = firstOfMonth.with(TemporalAdjusters.lastDayOfMonth());			//월의 마지막날짜
		LocalDate sundayOfWeek = firstSundayOfMonth.plusDays( (week-1) * 7L );

		return sundayOfWeek.isAfter(lastDayOfMonth) ? lastDayOfMonth : sundayOfWeek;
	}


	/**
	 * 특정 연도로만 조회할 DateScope 객체를 생성합니다.
	 *
	 * @param year		조회할 연도
	 * @return	지정한 연도로 구성된 DateScope 객체
	 */
	public static DateScope ofYear(int year) {
		validateYear(year);

		return new DateScope(year, null, null);
	}


	/**
	 * 특정 연도와 월로 조회할 DateScope 객체를 생성합니다.
	 *
	 * @param year			조회할 연도
	 * @param month		조회할 월
	 * @return	지정한 연도와 월로 구성된 DateScope 객체
	 */
	public static DateScope ofYearMonth(int year, int month) {
		validateYear(year);
		validateMonth(month);

		return new DateScope(year, month, null);
	}


	/**
	 * 특정 연도, 월, 주차로 조회할 {@link DateScope} 객체를 생성합니다.
	 *
	 * <p>
	 * year, month, week 모두 필수로 값을 지정해야 합니다. 기본적으로 주의 시작일은 {@code 월요일}, 종료일은 {@code 일요일}로 설정됩니다.
	 *
	 * @param year		조회할 연도
	 * @param month	조회할 월
	 * @param week		조회할 주
	 * @return	지정한 연도, 월, 주차로 구성된 DateScope 객체
	 */
	public static DateScope ofYearMonthWeek(int year, int month, int week) {
		return ofYearMonthWeek(year, month, week, DayOfWeek.MONDAY, DayOfWeek.SUNDAY);
	}


	/**
	 * 특정 연도, 월, 주자로 조회할 {@link DateScope} 객체를 생성합니다.
	 * <p>
	 *     year, month, week 모두 값을 지정해야 합니다.
	 *     주의 시작요일({@link DayOfWeek})과 종료일({@link DayOfWeek})을 지정하여 주차의 범위를 변경할 수 있습니다.
	 * </p>
	 *
	 * @param year			조회할 연도
	 * @param month		조회할 월
	 * @param week			조회할 주
	 * @param startDay	주의 시작요일 객체({@link DayOfWeek})
	 * @param endDay		주의 종료요일 객체({@link DayOfWeek})
	 * @return	주의 시작과 종료 요일을 지정하고 연도, 월, 주차로 구성된 {@link DateScope} 객체
	 */
	public static DateScope ofYearMonthWeek(int year, int month, int week, DayOfWeek startDay, DayOfWeek endDay) {
		validateYear(year);
		validateMonth(month);
		validateWeek(week);

		return new DateScope(year, month, week,startDay, endDay);
	}


	//주어진 연도가 1970 이상인지 확인
	private static void validateYear(int year) {
		if( year < 1970 ) {
			throw createClientException(ErrorCode.LEDGER_DATE_INVALID, "연도는 1970년부터 가능합니다.", year);
		}
	}


	//주어진 월이 0 < n <13 범위에 있는지 확인
	private static void validateMonth(int month) {
		if( !( 0 < month && month < 13) ) {
			throw createClientException(ErrorCode.LEDGER_DATE_INVALID, "월은 1~12까지만 가능합니다.", month);
		}
	}


	//주어진 주가 0 < n <7 범위에 있는지 확인
	private static void validateWeek(int week) {
		if( !(0 < week && week < 7) ) {
			throw createClientException(ErrorCode.LEDGER_DATE_INVALID, "주는 1~6까지만 가능합니다.", week);
		}
	}

}
