package com.moneymanager.vo;

import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.utils.ValidationUtil;
import lombok.Builder;
import lombok.Value;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : YearMonthWeekVO<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 12.<br>
 * 설명              : 날짜 년도, 월, 주의 값을 나타내는 클래스
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
 * 		 	  <td>25. 8. 12.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
public class YearMonthWeekVO {
	int year;
	int month;
	int week;


	@Builder
	public YearMonthWeekVO( YearMonthVO vo, String week ) {
		this.year = vo.getYear();
		this.month = vo.getMonth();
		this.week = parseWeek(week);

		validateWeek();
	}


	/**
	 * 정수 주({@code week})를 검증합니다.
	 * <p>
	 *     다음과 같은 경우 {@link ClientException}예외가 발생합니다.
	 *     <ul>
	 *         <li>주가 1~6 범위에서 벗어난 경우 → {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 *         <li>년도와 월에 해당하는 전체 주에서 벗어난 경우 → {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 *     </ul>
	 * </p>
	 * @throws ClientException 주가 허용되는 범위에서 벗어난 경우 발생
	 */
	private void validateWeek() {
		if( !(1 <= week && week <= 6) ) {
			throw ValidationUtil.createClientException(ErrorCode.COMMON_WEEK_INVALID, "주는 1 ~ 6까지만 가능합니다.", week);
		}

		if( !isValidWeekRange() ) {
			long max = getMaxWeekByMonth();
			throw new ClientException(ErrorDTO.builder().errorCode(ErrorCode.COMMON_WEEK_INVALID).message(String.format("주의 범위는 1~%d까지 입니다.", max)).requestData(month).build());
		}
	}


	/**
	 * 주({@code week})가 유효한 범위에 있는지 검증합니다. <br>
	 * {@code getMaxWeekByMonth()} 메서드로	최대 주를 구한 뒤, 주({@code week})가 1이상, 최대 주 이하인지 확인합니다.
	 *
	 * @return	주가 유효하면 true, 아니면 false
	 * @see #getMaxWeekByMonth()
	 */
	private boolean isValidWeekRange() {
		long maxWeek = getMaxWeekByMonth();

		return 1 <= week && week <= maxWeek;
	}


	/**
	 * 문자열 주({@code week})를 정수로 변환합니다.
	 * <p>
	 *     다음과 같은 경우 {@link ClientException}예외가 발생합니다.
	 *     <ul>
	 *         <li>주가 {@code null} 또는 "" 경우 → {@link ValidationUtil#createClientException(ErrorCode, String)} 메서드 호출</li>
	 *         <li>주가 숫자로 변환이 안되는 경우 → {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 *     </ul>
	 * </p>
	 * @param week	문자열로 입력된 주
	 * @return	{@link  ClientException} 주가 {@code null}이거나 숫자로 변환할 수 없는 경우 발생
	 */
	private int parseWeek(String week) {
		if(week == null || week.isBlank()) throw ValidationUtil.createClientException(ErrorCode.COMMON_WEEK_MISSING, "주를 입력해주세요.");

		try{
			return Integer.parseInt(week);
		}catch (NumberFormatException e) {
			throw ValidationUtil.createClientException(ErrorCode.COMMON_WEEK_FORMAT, "주는 숫자만 입력 가능합니다.", week);
		}
	}


	/**
	 * 지정한 년({@code year})과 월({@code month})의 최대 주를 반환합니다.
	 * <p>
	 *     월의 1일이 포함한 주의 월요일부터 시작하며, 마지막 날이 속한 주의 일요일이 마지막입니다. <br>
	 *     따라서 결과는 최소 4주에서 최대 6주까지 나올 수 있습니다.
	 * </p>
	 * @return	년과 월의 최대 주
	 * @see TemporalAdjusters
	 * @see ChronoUnit
	 */
	public int getMaxWeekByMonth() {
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
