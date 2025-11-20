package com.moneymanager.utils;


import com.moneymanager.domain.ledger.enums.DateType;
import com.moneymanager.domain.ledger.vo.PeriodSearch;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ServerException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import static com.moneymanager.exception.ErrorUtil.createServerException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.utils<br>
 * 파일이름       : DateTimeUtils<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 5.<br>
 * 설명              : 날짜와 시간을 검증하는 클래스
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
 * 		 	  <td>25. 8. 5.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 11. 12.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 추가] getLocalDateTime - Date에서 LocalDateTime으로 형변환</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class DateTimeUtils {

	private static final DayOfWeek START_DAY = DayOfWeek.MONDAY;
	private static final DayOfWeek END_DAY = DayOfWeek.SUNDAY;


	/**
	 *	입력된 날짜 문자열을 여러 형식으로 해석하여 {@link LocalDate} 객체로 변환합니다.
	 *<p>
	 * 지원하는 형식 중 하나라도 성공하면 해당 날짜를 반환합니다. 모든 형식이 실패할 경우 예외가 발생합니다.
	 *
	 *<ul>
	 *     <li>지원하는 형식:</li>
	 *     <ul>
	 *         <li>"yyyy-MM-dd"</li>
	 *         <li>"yyyy/MM/dd"</li>
	 *         <li>"yyyy년 MM월 dd일"</li>
	 *     </ul>
	 *</ul>
	 *
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	parseDateFlexible("2025-11-07");		//LocalDate(2025-11-07) 반환
	 *     	parseDateFlexible("2025/11/07");		//LocalDate(2025-11-07) 반환
	 *     	parseDateFlexible("2025년 11월 7일");		//LocalDate(2025-11-07) 반환
	 *     	parseDateFlexible("11-07-2025");		//지원하지 않은 형식이므로 예외 발생
	 *     }</pre>
	 * </p>
	 *
	 * @param dateStr		해석할 날짜 문자열
	 * @return	문자열을 분석하여 생성된 LocalDate 객체
	 * @throws ServerException	지원하지 않는 날짜 형식인 경우 발생
	 */
	public static LocalDate parseDateFlexible(String dateStr) {
		String[] patterns = {"yyyy-MM-dd", "yyyy/MM/dd", "yyyy년 MM월 dd일", "yyyyMMdd"};

		for( String pattern : patterns ) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

				return LocalDate.parse(dateStr, formatter);
			}catch (Exception e) {
				//실패로 다음 패턴 진행
			}
		}

		throw createServerException(ErrorCode.SYSTEM_LOGIC_INTERVAL, "지원하지 않은 날짜 형식입니다.", dateStr);
	}


	/**
	 * 연도, 월, 주차 정보별로 시작날짜를 계산하여 반환합니다.
	 * <ul>
	 *     <li>연도만 있는 경우 → 해당 연도의 1월 1일 반환</li>
	 *     <li>연도+월만 있는 경우 → 해당 월의 1일 반환</li>
	 *     <li>연도 + 월 + 주 모두 있는 경우 → 해당 주의 월요일 날짜 반환</li>
	 * </ul>
	 *
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	getStartDate(new PeriodSearch(2025, null, null));		//2025년 전체 시작일 → 2025-01-01
	 *     	getStartDate(new PeriodSearch(2025, 3, null));		//2025년 3월 시작일 → 2025-03-01
	 *     	getStartDate(new PeriodSearch(2025, 5, 2));			//2025년 3월 2주의 월요일 → 2025-05-05
	 *     }</pre>
	 * </p>
	 *
	 * @param period	조회 단위를 나타내는 PeriodSearch 객체
	 * @return	주어진 조건에 만족하는 시작 날짜
	 */
	public static LocalDate getStartDate(PeriodSearch period) {
		//년도만 있을 경우
		if( period.getMonth() == null ) {
			return LocalDate.of(period.getYear(), 1, 1);
		}

		LocalDate firstDayOfMonth = LocalDate.of(period.getYear(), period.getMonth(), 1);		//년,월에 해당하는 첫째일

		//년+월만 있는 경우
		if(period.getWeek() == null ) {
			return firstDayOfMonth;
		}

		//년+월+주 모두 있는 경우
		return getWeekStartDate(firstDayOfMonth, period.getWeek());
	}


	//해당 월의 주차에 맞는 월요일 날짜 계산
	private static LocalDate getWeekStartDate(LocalDate firstDayOfMonth, int week) {
		if( week == 1 ) {
			return firstDayOfMonth;
		}

		//첫째주에 해당하는 주차의 월요일 날짜
		LocalDate firstMondayOfMonth = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(START_DAY));

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
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	getEndDate(new PeriodSearch(2025, null, null));	//2025년 전체 마지막일 → 2025-12-31
	 *     	getEndDate(new PeriodSearch(2025, 3, null));		//2025년 3월 마지막일 → 2025-03-31
	 *     	getEndDate(new PeriodSearch(2025, 5, 2));		//2025년 5월 2주의 일요일 → 2025-05-11
	 *     }</pre>
	 * </p>
	 * @param period	조회 단위를 나타내는 PeriodSearch 객체
	 * @return	주어진 조건에 만족하는 마지막 날짜
	 */
	public static LocalDate getEndDate(PeriodSearch period) {
		//년도만 있을 경우
		if( period.getMonth() == null ) {
			return LocalDate.of(period.getYear(), 12, 31);
		}

		LocalDate firstDayOfMonth = LocalDate.of(period.getYear(), period.getMonth(), 1);

		//년 + 월만 있는 경우
		if( period.getWeek() == null ) {
			return firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
		}

		//년 + 월 + 주 모두 있는 경우
		return getWeekEndDate(firstDayOfMonth, period.getWeek());
	}


	//해당 월의 주차에 맞는 일요일 날짜 계산
	private static LocalDate getWeekEndDate(LocalDate firstOfMonth, int week) {
		//첫째주에 해당하는 주차의 일요일 날짜
		LocalDate firstSundayOfMonth = firstOfMonth.with(TemporalAdjusters.nextOrSame(END_DAY));

		if( week == 1 ) {
			return firstSundayOfMonth;
		}

		LocalDate lastDayOfMonth = firstOfMonth.with(TemporalAdjusters.lastDayOfMonth());			//월의 마지막날짜
		LocalDate sundayOfWeek = firstSundayOfMonth.plusDays( (week-1) * 7L );

		return sundayOfWeek.isAfter(lastDayOfMonth) ? lastDayOfMonth : sundayOfWeek;
	}


	/**
	 *	Date를 LocalDateTime 객체로 변환합니다.
	 *
	 * @param date	날짜정보를 담은 객체({@link Date})
	 * @return	형변환된 {@link LocalDateTime} 객체
	 */
	public static LocalDateTime getLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}


	/**
	 * 특정 날짜가 시작일과 종료일 사이에 포함되는지 확인합니다.
	 * <p>
	 * 시작일과 종료일 자체도 포함됩니다. date가 startDate보다 빠르거나 endDate보다 늦으면 false를 반환합니다.
	 *
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	isDateInRange(	LocalDate.of(2025, 5, 10),	LocalDate.of(2025, 5, 1),	LocalDate.of(2025, 5, 31));			//true
	 *     	isDateInRange(	LocalDate.of(2025, 5, 10),	LocalDate.of(2025, 5, 1),	LocalDate.of(2025, 5, 31));			//true
	 *     }</pre>
	 * </p>
	 *
	 * @param date				확인할 날짜
	 * @param startDate		범위 시작날짜
	 * @param endDate		범위 종료날짜
	 * @return	date가 범위 안에 있으면 true, 아니면 false
	 */
	public static boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
		return (date.isEqual(startDate) || date.isAfter(startDate)) && (date.isEqual(endDate) || date.isBefore(endDate));
	}


	/**
	 * 기준이 되는 날짜(dateTime)로부터 오늘까지 경과한 일 수가  지정한 일 수(days)보다 큰 지 확인합니다.
	 *
	 * @param dateTime		기준이 되는 날짜 및 시간
	 * @param days				비교일 수
	 * @return	기준날짜로부터 오늘까지 경과일이 비교일 수보다 크면 true, 작으면 false
	 */
	public static boolean isPastDays(LocalDateTime dateTime, int days) {
		long betweenDays = ChronoUnit.DAYS.between(dateTime.toLocalDate(), LocalDate.now());

		return betweenDays > days;
	}


	/**
	 * 지정된 {@link YearMonth}가 포함되는 총 주차를 계산하여 반환합니다.
	 * 주의 시작 기준 요일인 <code>월요일</code>부터 마지막 요일인 <code>일요일</code>까지를 한 주라고 계산합니다.
	 * <p>
	 * 예시로 2025년 3월은 1일이 토요일로 시작하여, 총 6주로 계산됩니다.
	 *
	 * @param yearMonth	연도와 월의 정보를 담은 객체({@link YearMonth})
	 * @return	해당 월이 포함되는 전체 주차 수
	 */
	public static int getTotalWeeksOfMonth(YearMonth yearMonth) {
		LocalDate firstDay = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
		LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

		//첫 주의 시작(월의 1일이 포함된 주의 월요일)
		LocalDate startOfFirstWeek = firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		//마지막 주의 끝(월의 마지막 일이 포함된 주의 일요일)
		LocalDate endOfLastWeek = lastDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

		//총 주 수
		return Math.toIntExact(ChronoUnit.WEEKS.between(startOfFirstWeek, endOfLastWeek) + 1);
	}


	/**
	 * {@link LocalDate}를 {@link DateType}에 맞는 문자열 형식으로 변환합니다.
	 * <ul>
	 *     <li>{@link DateType#YEAR} : "yyyy년" 형식으로 변환</li>
	 *     <li>{@link DateType#MONTH} : "yyyy년 MM월" 형식으로 변환</li>
	 *     <li>{@link DateType#WEEK} : "yyyy년 MM월 n주" 형식으로 변환</li>
	 * </ul>
	 *
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	LocalDate date  = LocalDate.of(2025, 11, 5);
	 *
	 *     	formatDateAsString(date, DateType.YEAR);	//"2025년"
	 *     	formatDateAsString(date, DateType.MONTH);	//"2025년 11월"
	 *     	formatDateAsString(date, DateType.WEEK);	//"2025년 11월 2주"
	 *     }</pre>
	 * </p>
	 *
	 * @param date		변환할 날짜
	 * @param type		변환할 날짜 타입
	 * @return				지정한 형식으로 변환된 날짜 문자열
	 */
	public static String formatDateAsString(LocalDate date, DateType type) {
		switch (type) {
			case YEAR:
				return formatDateAsString(date, "yyyy년");
			case WEEK:
				return String.format("%s %s주", formatDateAsString(date, "yyyy년 MM월"), getWeekByMonth(date));
			case MONTH:
			default:
				return formatDateAsString(date, "yyyy년 MM월");
		}
	}


	/**
	 * {@link LocalDate}를 지정한 패턴의 문자열로 변환됩니다.
	 *
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	LocalDate date = LocalDate.of(2025, 11, 5);
	 *
	 *     	formatDateAsString(date, "yyyy-MM-dd");			//"2025-11-05"
	 *     	formatDateAsString(date, "yyyy년 MM월 dd일");	//"2025년 11월 05일"
	 *     }</pre>
	 * </p>
	 *
	 * @param date			변환할 날짜
	 * @param pattern	변환할 문자열 패턴
	 * @return	지정한 패턴으로 변환된 날짜 문자열
	 */
	public static String formatDateAsString(LocalDate date, String pattern) {
		return date.format(DateTimeFormatter.ofPattern(pattern));
	}


	//날짜가 월의 몇주째인지 확인
	static int getWeekByMonth(LocalDate date) {
		int week = 1;

		LocalDate start = date.withDayOfMonth(1);
		LocalDate end = start.with(TemporalAdjusters.nextOrSame(END_DAY));

		//1일 ~ 첫 일요일까지 1주차
		if( !date.isAfter(end) ) return week;

		while (!isDateInRange(date, start, end)) {	//시작일 ~ 종료일 사이에 있을때까지 다음주로 넘어감
			week++;

			start = end.plusDays(1);
			end = end.plusDays(7);
		}

		return week;
	}

}
