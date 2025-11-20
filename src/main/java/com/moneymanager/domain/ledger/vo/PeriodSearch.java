package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.global.vo.DateGroupable;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.utils.DateTimeUtils;
import lombok.Getter;
import lombok.Value;
import java.time.LocalDate;
import java.time.YearMonth;

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
public class PeriodSearch implements DateGroupable {

	//연도범위
	static int YEAR_RANGE = 5;

	Integer year;
	Integer month;
	Integer week;


	private PeriodSearch(Integer year, Integer month, Integer week) {
		this.year = year;
		this.month = month;
		this.week = week;
	}


	@Override
	public LocalDate getStartDate() {
		return DateTimeUtils.getStartDate(this);
	}


	@Override
	public LocalDate getEndDate() {
		return DateTimeUtils.getEndDate(this);
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


	//주어진 연도가 허용 범위 내에 있는지 검증
	private static void validateYearRange(int year) {
		if( year == 0 ) {
			throw createClientException(ErrorCode.BUDGET_DATE_INVALID, "조회할 년도를 확인해주세요.");
		}

		int currentYear = LocalDate.now().getYear();
		if( !(currentYear - YEAR_RANGE <= year && year <= currentYear) ) {
			throw createClientException(ErrorCode.BUDGET_DATE_INVALID, String.format("조회할 년도는 %d~%d까지만 가능합니다.", currentYear-YEAR_RANGE, currentYear), year );
		}
	}


	//주어진 연도, 월이 허용 범위 내에 있는지 검증
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


	//주어진 연도, 월, 주이 허용 범위 내에 있는지 검증
	private static void validateWeekRange(int year, int month, int week) {
		if( week == 0 ) {
			throw createClientException(ErrorCode.BUDGET_DATE_INVALID, "조회할 주를 확인해주세요.");
		}

		//년과 월에 해당하는 최대 주
		int maxWeekByMonth = DateTimeUtils.getTotalWeeksOfMonth(YearMonth.of(year, month));

		if (!(0 < week && week <= maxWeekByMonth)  ) {
			throw createClientException(ErrorCode.BUDGET_DATE_INVALID, String.format("조회할 주는 1~%d까지만 가능합니다.", maxWeekByMonth));
		}
	}

}
