package com.moneymanager.service.main.validation;

import com.moneymanager.domain.ledger.enums.DateType;
import com.moneymanager.domain.ledger.vo.DateScope;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;

import static com.moneymanager.exception.ErrorUtil.createClientException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.main.validation<br>
 * 파일이름       : DateScopeValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 21.<br>
 * 설명              : 가계부 조회범위 값을 검증하는 클래스
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
 * 		 	  <td>25. 11. 21.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Component
public class DateScopeValidator {

	private final LocalDate today = LocalDate.now();
	private final int YEARS_BACK = 5;		//

	public void validate(DateScope scope, DateType type) {
		switch (type) {
			case YEAR:
				validateYearRange(scope.getYear());
				break;
			case WEEK:
				validateWeekRange(scope.getYear(), scope.getMonth(), scope.getWeek());
				break;
			case MONTH:
			default:
				validateMonthRange(scope.getYear(), scope.getMonth());
		}
	}

	//연도범위가 현재부터 5년전 과거까지인지 확인
	private void validateYearRange(int year) {
		int minYear = today.getYear() - YEARS_BACK;
		int maxYear = today.getYear();

		//연도가 현재연도 ~ 5년전 연도까지가 아닌 경우
		if( year < minYear || year > maxYear ) {
			throw createClientException(ErrorCode.LEDGER_DATE_INVALID, String.format("연도는 %d~%d까지만 가능합니다.", minYear, maxYear), year );
		}
	}

	//월 범위를 확인
	private void validateMonthRange(int year, int month) {
		validateYearRange(year);

		int maxMonth = 12;
		if( year == today.getYear() ) {
			maxMonth = today.getMonthValue();
		}

		if( month > maxMonth ) {
			throw createClientException(ErrorCode.LEDGER_DATE_INVALID, String.format("월은 1~%d까지만 가능합니다.", maxMonth), month);
		}
	}

	//주 범위를 확인
	private void validateWeekRange(int year, int month, int week) {
		validateYearRange(year);
		validateMonthRange(year, month);

		//년과 월에 해당하는 최대 주
		int maxWeek = DateTimeUtils.getTotalWeeksOfMonth(YearMonth.of(year, month));

		if (!(0 < week && week <= maxWeek)  ) {
			throw createClientException(ErrorCode.LEDGER_DATE_INVALID, String.format("주는 1~%d까지만 가능합니다.", maxWeek), week);
		}
	}

}