package com.moneymanager.domain.ledger.vo;

import com.moneymanager.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

import static com.moneymanager.exception.ErrorUtil.createClientException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : YearMonthVO<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 17.<br>
 * 설명              :
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
 * 		 	  <td>25. 11. 17.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class YearMonthVO {
	int year;
	int month;


	@Builder
	public YearMonthVO( YearVO year, String month ) {
		this.year = year.getYear();
		this.month = parseMonth(month);

		validateMonthRange();
	}


	private void validateMonthRange() {
		if( month < 1 || month > 12) {
			throw createClientException(ErrorCode.COMMON_MONTH_FORMAT, "월은 1~12까지만 가능합니다.", String.valueOf(month));
		}
	}


	private int parseMonth(String month) {
		if( month == null ) throw createClientException(ErrorCode.COMMON_MONTH_MISSING, "월을 입력해주세요.");

		try{
			return Integer.parseInt(month);
		}catch (NumberFormatException e) {
			throw createClientException(ErrorCode.COMMON_MONTH_FORMAT, "월은 숫자만 입력 가능합니다.", month);
		}
	}


	public boolean isFutureMonth() {
		int currentYear = LocalDate.now().getYear();
		int currentMonth = LocalDate.now().getMonthValue();

		return currentYear == year && month > currentMonth;
	}



	public LocalDate getFirstDate() {
		return LocalDate.of(
				year,
				month,
				1
		);
	}


	public LocalDate getLastDate() {
		return LocalDate.of(
				year,
				month,
				getFirstDate().lengthOfMonth()
		);
	}
}
