package com.moneymanager.vo;

import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : YearMonthVO<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 12.<br>
 * 설명              : 날짜 년도, 월의 값을 나타내는 클래스
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
 * 		 	  <td>25. 8. 12</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 8. 22</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>
 * 		 	      [메서드 추가] getFirstDate - 년도와 월의 첫째 날 반환<br>
 * 		 	      [메서드 추가] getLastDate - 년도와 월의 마지막 날 반환
 * 		 	  </td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
public class YearMonthVO {
	YearVO yearVO;
	int month;

	@Builder
	public YearMonthVO( String year, String month ) {
		this.yearVO = new YearVO(year);

		if( month == null ) throw new ClientException(ErrorCode.COMMON_MONTH_MISSING, "월을 입력해주세요.");
		try{
			int parsedMonth = Integer.parseInt(month);

			if(!isValidMonthRange(parsedMonth)) {
				throw new ClientException(ErrorCode.COMMON_MONTH_INVALID, "월은 1~12까지만 입력 가능합니다.");
			}

			this.month = parsedMonth;
		}catch ( NumberFormatException e ) {
			throw new ClientException(ErrorCode.COMMON_MONTH_FORMAT, "월은 숫자만 입력 가능합니다.");
		}
	}

	private boolean isValidMonthRange(int month) {
		return 1 <= month && month <= 12;
	}


	/**
	 * 해당 년월의 첫째 날을 반환합니다.
	 *
	 * @return	년도와 월 값의 첫째날로 설정된 LocalDate 객체
	 */
	public LocalDate getFirstDate() {
		return LocalDate.of(
				yearVO.getYear(),
				month,
				1
		);
	}


	/**
	 * 해당 년월의 마지막 날을 반환합니다.
	 *
	 * @return	년도와 월 값의 마지막 날로 설정된 LocalDate 객체
	 */
	public LocalDate getLastDate() {
		return LocalDate.of(
				yearVO.getYear(),
				month,
				1
		);
	}
}
