package com.moneymanager.utils.date;



import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.moneymanager.utils.validation.ValidationUtils.isNullOrBlank;


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

	private static final List<DateTimeFormatter> DATE_TIME_FORMATTER_LIST = List.of(
			DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT),
			DateTimeFormatter.ofPattern("uuuu/MM/dd").withResolverStyle(ResolverStyle.STRICT),
			DateTimeFormatter.ofPattern("uuuuMMdd").withResolverStyle(ResolverStyle.STRICT),
			DateTimeFormatter.ofPattern("uuuu년 MM월 dd일", Locale.KOREAN).withResolverStyle(ResolverStyle.STRICT),
			DateTimeFormatter.ofPattern("uuuu년 MM월 dd일 E요일", Locale.KOREAN).withResolverStyle(ResolverStyle.STRICT)
	);

	private static final String DEFAULT_DATE_PATTERN = "yyyy";


	/**
	 *	다양한 날짜 형식을 지원하여 문자열을 {@link LocalDate}로 반환합니다.
	 *<p>
	 * 입력된 문자열을 날짜 포맷 목록({@code DATE_TIME_FORMATTER_LIST})으로 하니씩 파싱하며, 반환에 성공한 결과를 반환합니다.
	 * 입력값이 {@code null}, 공백, 모든 포맷으로 반환에 실패하면 null을 반환합니다.
	 *
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	parseDateFlexible("2025-11-07");		//결과: LocalDate(2025-11-07)
	 *     	parseDateFlexible("2025/11/07");		//결과: LocalDate(2025-11-07)
	 *     	parseDateFlexible("2025년 11월 7일");	//결과: LocalDate(2025-11-07)
	 *     	parseDateFlexible("11-07-2025");		//결과: null
	 *     }</pre>
	 * </p>
	 *
	 * @param dateStr		변환할 날짜 문자열
	 * @return	변환된 {@link LocalDate}, 변환 실패 시 {@code null}
	 */
	public static LocalDate parseDateFlexible(String dateStr) {
		if(isNullOrBlank(dateStr)) {
			return null;
		}

		String trimmedDate = dateStr.trim();

		for(DateTimeFormatter formatter : DATE_TIME_FORMATTER_LIST) {
			try {
				return LocalDate.parse(trimmedDate, formatter);
			}catch (Exception e) {
				//다음 패턴 진행
			}
		}

		return null;
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
	 * 주어진 날짜가 시작일과 종료일 사이에 포함되는지 확인합니다.
	 * <p>
	 * 시작일과 종료일 자체도 포함됩니다.
	 * date가 startDate보다 빠르거나 endDate보다 늦으면 false를 반환합니다.
	 *
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	isDateInRange(2026-01-10, 2026-01-01, 2026-01-31);	//true
	 *     	isDateInRange(2026-01-01, 2026-01-01, 2026-01-31);	//true
	 *     	isDateInRange(2026-01-31, 2026-01-01, 2026-01-31);	//true
	 *     	isDateInRange(2025-12-31, 2026-01-01, 2026-01-31);		//false
	 *     }</pre>
	 * </p>
	 *
	 * @param date				확인할 날짜
	 * @param startDate		범위 시작날짜 (시작일 포함)
	 * @param endDate		범위 종료날짜 (종료일 포함)
	 * @return	date가 범위 안에 있으면 true, 아니면 false
	 */
	public static boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
		if(date == null || startDate == null || endDate == null) {
			return false;
		}

		return !date.isBefore(startDate) && !date.isAfter(endDate);
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
	 * {@link LocalDate}를 지정한 패턴의 문자열로 변환됩니다.
	 *
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	LocalDate date = LocalDate.of(2025, 11, 5);
	 *
	 *     	formatDate(date, "yyyy-MM-dd");			//"2025-11-05"
	 *     	formatDate(date, "yyyy년 MM월 dd일");	//"2025년 11월 05일"
	 *     }</pre>
	 * </p>
	 *
	 * @param date			변환할 날짜
	 * @param pattern	변환할 문자열 패턴
	 * @return	지정한 패턴으로 변환된 날짜 문자열
	 */
	public static String formatDate(LocalDate date, String pattern) {
		return date.format(DateTimeFormatter.ofPattern(pattern));
	}

}
