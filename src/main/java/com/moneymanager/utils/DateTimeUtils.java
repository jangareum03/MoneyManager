package com.moneymanager.utils;


import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.exception.custom.ServerException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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


	/**
	 *	입력된 날짜 문자열을 여러 형식으로 해석하여 {@link LocalDate} 객체러 변환합니다.
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
	 *	Date를 LocalDateTime 객체로 형변환합니다.
	 *
	 * @param date	날짜정보를 담은 객체
	 * @return	형변환된 LocalDateTime
	 */
	public static LocalDateTime getLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}


	/**
	 * 주어진 년과 월의 값에 따라 월의 최대 주를 얻습니다.
	 * 첫 주의 월요일 날짜에서 마지막 주의 일요일까지 사이의 주를 계산합니다.
	 *
	 * @param year			최대 주를 구할 년도
	 * @param month		최대 주를 구할 월
	 * @return	해당 년도와 월의 최대 주
	 */
	public static int getMaxWeekByMonth(int year, int month) {
		LocalDate firstDay = LocalDate.of(year, month, 1);
		LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

		//첫 주의 시작(월의 1일이 포함된 주의 월요일)
		LocalDate startOfFirstWeek = firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		//마지막 주의 끝(월의 마지막 일이 포함된 주의 일요일)
		LocalDate endOfLastWeek = lastDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

		//총 주 수
		return Math.toIntExact(ChronoUnit.WEEKS.between(startOfFirstWeek, endOfLastWeek) + 1);
	}

}
