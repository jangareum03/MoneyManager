package com.moneymanager.vo;

import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.utils.ValidationUtil;
import lombok.Builder;
import lombok.ToString;
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
@ToString
public class YearMonthVO {
	int year;
	int month;


	@Builder
	public YearMonthVO( YearVO year, String month ) {
		this.year = year.getYear();
		this.month = parseMonth(month);

		validateMonthRange();
	}


	/**
	 * 정수 월({@code month})을 검증합니다.
	 * <p>
	 *     {@code month}가 1보다 작거나 12보다 크면 {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드를 통해 {@link com.moneymanager.exception.custom.ClientException} 예외가 발생합니다.
	 * </p>
	 * @throws com.moneymanager.exception.custom.ClientException {@code month} 값이 1~12 범위를 벗어날 경우 발생
	 */
	private void validateMonthRange() {
		if( month < 1 || month > 12) {
			throw ValidationUtil.createClientException(ErrorCode.COMMON_MONTH_FORMAT, "월은 1~12까지만 가능합니다.", String.valueOf(month));
		}
	}


	/**
	 * 문자열 월({@code month})을 정수로 변환합니다.
	 * <p>
	 *     다음과 같은 경우 {@link ClientException} 예외가 발생합니다.
	 *     <ul>
	 *         <li>월이 {@code null}인 경우 → {@link ValidationUtil#createClientException(ErrorCode, String)} 메서드 호출</li>
	 *         <li>월이 숫자로 변환이 안되는 경우 → {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 *     </ul>
	 * </p>
	 * @param month	문자열로 입력한 월
	 * @return 정수로 변환된 월 값(1~12)
	 * @throws ClientException	월이 {@code null}이거나 숫자로 변환할 수 없는 경우 발생
	 */
	private int parseMonth(String month) {
		if( month == null ) throw ValidationUtil.createClientException(ErrorCode.COMMON_MONTH_MISSING, "월을 입력해주세요.");

		try{
			return Integer.parseInt(month);
		}catch (NumberFormatException e) {
			throw ValidationUtil.createClientException(ErrorCode.COMMON_MONTH_FORMAT, "월은 숫자만 입력 가능합니다.", month);
		}
	}


	/**
	 * 년도({@code year})와 월({@code month})이 현재 날짜 기준으로 미래의 달인지 확인합니다. <br>
	 * 현재 날짜와 저장된 년도와 월을 비교하여, 현재 월보다 클 때만 {@code ture}를 반환합니다.
	 * <p>
	 *     예를 들어, 현재 날짜가 2025년 8월이라면
	 *     <ul>
	 *         <li>{@code year} = 2025, {@code month} = 9 이면 true 반환</li>
	 *         <li>{@code year} = 2025, {@code month} = 7 이면 false 반환</li>
	 *         <li>{@code year} = 2024, {@code month} = 12 이면 false 반환</li>
	 *     </ul>
	 * </p>
	 * @return	현재 년도 기준으로 저장된 월이 미래라면 true, 아니면 false
	 */
	public boolean isFutureMonth() {
		int currentYear = LocalDate.now().getYear();
		int currentMonth = LocalDate.now().getMonthValue();

		return currentYear == year && month > currentMonth;
	}


	/**
	 * 해당 년월의 첫째 날을 반환합니다.
	 *
	 * @return	년도와 월 값의 첫째날로 설정된 {@link LocalDate} 객체
	 */
	public LocalDate getFirstDate() {
		return LocalDate.of(
				year,
				month,
				1
		);
	}


	/**
	 * 해당 년월의 마지막 날을 반환합니다.
	 *
	 * @return	년도와 월 값의 마지막 날로 설정된 {@link LocalDate} 객체
	 */
	public LocalDate getLastDate() {
		return LocalDate.of(
				year,
				month,
				getFirstDate().lengthOfMonth()
		);
	}
}
