package com.moneymanager.domain.ledger.vo;

import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.utils.DateTimeUtils;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDate;

import static com.moneymanager.exception.ErrorUtil.createClientException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : PeriodSearch<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 14.<br>
 * 설명              : 가계부 조회날짜 값을 나타내는 클래스
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
public class PeriodSearch {

	Integer year;
	Integer month;
	Integer week;
	static int YEAR_RANGE = 5;

	private PeriodSearch(Integer year, Integer month, Integer week) {
		this.year = year;
		this.month = month;
		this.week = week;
	}

	/**
	 * 특정 연도로만 조회할 PeriodSearch 객체를 생성합니다.
	 *
	 * @param year		조회할 연도
	 * @return	지정한 연도로 구성된 PeriodSearch 객체
	 */
	public static PeriodSearch ofYear(int year) {
		validateYearRange(year);

		return new PeriodSearch(year, null, null);
	}


	/**
	 * 특정 연도와 월로 조회할 PeriodSearch 객체를 생성합니다.
	 *
	 * @param year			조회할 연도
	 * @param month		조회할 월
	 * @return	지정한 연도와 월로 구성된 PeriodSearch 객체
	 */
	public static PeriodSearch ofYearMonth(int year, int month) {
		validateYearRange(year);
		validateMonthRange(year, month);

		return new PeriodSearch(year, month, null);
	}


	/**
	 * 특정 연도, 월, 주차로 조회할 PeriodSearch 객체를 생성합니다.
	 *
	 * @param year		조회할 연도
	 * @param month	조회할 월
	 * @param week		조회할 주
	 * @return	지정한 연도, 월, 주차로 구성된 PeriodSearch 객체
	 */
	public static PeriodSearch ofYearMonthWeek(int year, int month, int week) {
		validateYearRange(year);
		validateMonthRange(year, month);
		validateWeekRange(year, month, week);

		return new PeriodSearch(year, month, week);
	}


	/**
	 * 주어진 연도가 허용 범위 내에 있는지 검증합니다. <br>
	 * <ul>
	 *     <li>현재 연도가 0이면 오류 → {@link ErrorCode#BUDGET_DATE_INVALID}</li>
	 *     <li>현재 연도 기준 허용 범위({@code YEAR_RANGE})를 벗어나면 오류 → {@link ErrorCode#BUDGET_DATE_INVALID}</li>
	 * </ul>
	*
	 *
	 *<p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	validateYear("2025");		//현재 연도 기준 범위 내이면 통과
	 *     	validateYear("1990");			//범위를 벗어나므로 예외 발생
	 *     }</pre>
	 *</p>
	 *
	 * @param year	검증할 년도
	 * @throws ClientException	   년도 값이 0이거나 허용 범위를 벗어난 경우 발생
	 */
	private static void validateYearRange(int year) {
		if( year == 0 ) {
			throw createClientException(ErrorCode.BUDGET_DATE_INVALID, "조회할 년도를 확인해주세요.");
		}

		int currentYear = LocalDate.now().getYear();
		if( !(currentYear - YEAR_RANGE <= year && year <= currentYear) ) {
			throw createClientException(ErrorCode.BUDGET_DATE_INVALID, String.format("조회할 년도는 %d~%d까지만 가능합니다.", currentYear-YEAR_RANGE, currentYear), year );
		}
	}


	/**
	 *	주어진 연도와 월이 허용 범위 내에 있는지 검증합니다.
	 * <ul>
	 *     <li>월이 0이면 오류 → {@link ErrorCode#BUDGET_DATE_INVALID}</li>
	 *     <li>현재 연도인 경우, 현재 월을 초과하면 오류 → {@link ErrorCode#BUDGET_DATE_INVALID}</li>
	 *     <li>현재 연도가 아닌 경우, 입력된 월이 1~12 범위를 벗어나면 오류 → {@link ErrorCode#BUDGET_DATE_INVALID}</li>
	 * </ul>
	 *
	 *	<p>
	 *	   예제 사용법:
	 *	   <pre>{@code
	 *	   		validateMonthRange(2025, 5);			//현재 연도가 2025년이고 5월이 범위 내면 통과
	 *	   		validateMonthRange(2026, 5);			//현재 연도가 2025년일 경우 2026년은 2월은 통과
	 *
	 *	   		validateMonthRange(2025, 13);			//현재 연도가 2025년이나 월이 범위에 초과되어 예외 발생
	 *	   }</pre>
	 *	</p>
	 *
	 * @param year			검증할 연도
	 * @param month		검증할 월
	 * @throws ClientException		월이 0이거나 유효 범위를 벗어난 경우 발생
	 */
	private static void validateMonthRange(int year, int month) {
		if( month == 0 ) {
			throw createClientException(ErrorCode.BUDGET_DATE_INVALID, "조회할 월을 확인해주세요.");
		}

		int currentYear = LocalDate.now().getYear();
		if( currentYear == year ) {
			int currentMonth = LocalDate.now().getMonthValue();

			if( month > currentMonth ) {
				throw createClientException(ErrorCode.BUDGET_DATE_INVALID, String.format("조회할 월은 1~%d까지만 가능합니다.", currentMonth));
			}
		}else {
			//월의 범위가 벗어난 경우
			if( !( 0 < month && month < 13) ) {
				throw createClientException(ErrorCode.BUDGET_DATE_INVALID, "조회할 월은 1~12까지만 가능합니다.", month);
			}
		}
	}


	/**
	 * 주어진 연도와 월에 대해 입력된 주(week)가 유요한 범위내에 있는지 검증합니다. <br>
	 * 해당 연·월의 최대 주차를 계산하여, 입력된 주가 1 이상이며 최대 주차 이하인지 확인합니다.
	 *	<ul>
	 *	   <li>값이 0인 경우 오류 → {@link ErrorCode#BUDGET_DATE_INVALID}</li>
	 *	   <li>범위를 벗어나는 경우 오류 → {@link ErrorCode#BUDGET_DATE_INVALID}</li>
	 *	</ul>
	 *
	 *<p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	validateWeek(2025, 1, 3);		//1월의 3주가 유효하므로 통과
	 *     	validateWeek(2025, 2, 0);		//0주는 존재하지 않아 예외 발생
	 *     	validateWeek(2025, 4, 6);		//4월은 최대주가 5주라서 예외 발생
	 *     }</pre>
	 *</p>
	 *
	 * @param year	검증할 년도
	 * @throws ClientException	   주 값이 0이거나 허용 범위를 벗어난 경우 발생
	 */
	private static void validateWeekRange(int year, int month, int week) {
		if( week == 0 ) {
			throw createClientException(ErrorCode.BUDGET_DATE_INVALID, "조회할 주를 확인해주세요.");
		}

		//년과 월에 해당하는 최대 주
		int maxWeekByMonth = DateTimeUtils.getMaxWeekByMonth(year, month);

		if (!(0 < week && week <= maxWeekByMonth)  ) {
			throw createClientException(ErrorCode.BUDGET_DATE_INVALID, String.format("조회할 주는 1~%d까지만 가능합니다.", maxWeekByMonth));
		}
	}
}
