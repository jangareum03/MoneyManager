package com.moneymanager.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;

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
 * 		</tbody>
 * </table>
 */
public class DateTimeUtils {

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

}
