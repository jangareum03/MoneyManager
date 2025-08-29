package com.moneymanager.vo;

import com.moneymanager.enums.RegexPattern;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.utils.ValidationUtil;
import lombok.Value;

import java.time.LocalDate;
import java.time.Year;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : YearVO<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 12.<br>
 * 설명              : 날짜 년도 값을 나타내는 클래스
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
 * 		 	  <td>25. 8. 29</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>
 * 		 	      [메서드 수정] parseYear<br>
 * 		 	      <span color='#007FFF' >전)</span> 정수형 파싱과 값 검증을 함께 진행 → <span color='#007FFF' >후)</span> parseYear, validYear로 분리
 * 		 	  </td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
public class YearVO {
	int year;


	public YearVO(String  year) {
		this(year, 0);
	}


	public YearVO( String  year, int maxValue ) {
		this.year = parseYear(year);

		validYear(maxValue);
	}


	/**
	 * 정수 년도({@code year})를 검증합니다.
	 * <p>
	 *     {@code max}는 정수형으로 현재 년도로부터 과거로 허용되는 범위입니다.<br>
	 *     예를 들어, 현재 년도가 2025이고 max가 5이면 2020 ~ 2025년만 허용됩니다.
	 * </p>
	 * <p>
	 *     다음과 같은 경우 {@link ClientException} 예외가 발생합니다.
	 *     <ul>
	 *         <li>년도가 1 또는 2로 시작하지 않는 4자리 숫자 입력한 경우 → {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 *         <li>년도가 지정된 범위({@code max})를 벗어난 경우 → {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 *     </ul>
	 * </p>
	 * @param max	현재 년도로부터 허용되는 최대 과거 범위(0이면 범위 제한 없음)
	 * @throws ClientException	년도가 허용되는 범위에 벗어난 경우 발생
	 */
	private void validYear(int max) {
		if( !isMatchYear() ){
			throw ValidationUtil.createClientException(ErrorCode.COMMON_YEAR_FORMAT, "년도는 1 또는 2로 시작하는 4자리 숫자만 입력 가능합니다.", String.valueOf(year));
		}

		if( !isValidYearRange(year, max) ) {
			int currentYear = LocalDate.now().getYear();
			String message = String.format("년도는 %d ~ %d까지만 입력가능합니다.", currentYear-max, currentYear);

			throw ValidationUtil.createClientException(ErrorCode.COMMON_YEAR_INVALID, message, String.valueOf(year));
		}
	}


	/**
	 * 년도 문자열({@code year})을 정수로 변환합니다.
	 * <p>
	 *     다음과 같은 경우 {@link ClientException} 예외가 발생합니다.
	 *     <ul>
	 *         <li>년도가 {@code null}인 경우 → {@link ValidationUtil#createClientException(ErrorCode, String)} 메서드 호출</li>
	 *         <li>년도가 숫자로 변환이 안되는 경우 → {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 *     </ul>
	 * </p>
	 * @param year	문자열로 입력한 년도
	 * @return 정수로 변환된 년도 값
	 * @throws ClientException	년도가 {@code null}이거나 숫자로 변환할 수 없는 경우 발생
	 */
	private int parseYear(String year) {
		if( year == null ) throw ValidationUtil.createClientException(ErrorCode.COMMON_YEAR_MISSING, "년도를 입력해주세요.");

		try{
			return Integer.parseInt(year);
		}catch (NumberFormatException e) {
			throw ValidationUtil.createClientException(ErrorCode.COMMON_YEAR_FORMAT, "년도는 숫자만 입력 가능합니다.", year);
		}
	}


	/**
	 * 년도({@code year})가 정규식 패턴과 비교합니다.
	 * <p>
	 *     예를 들어
	 *     <ul>
	 *         <li>{@code year} = 2020 이면 true 반환</li>
	 *         <li>{@code year} = 123 이면 false 반환</li>
	 *         <li>{@code year} = 3021 이면 false 반환</li>
	 *     </ul>
	 * </p>
	 * @return	정규식 패턴과 일치하면 true, 아니면 false
	 */
	private boolean isMatchYear() {
		return String.valueOf(year).matches(RegexPattern.DATE_YEAR.getPattern());
	}


	/**
	 * 년도가 현재 년도로부터 허용되는 과거 범위 내({@code range})에 있는지 검증합니다.
	 *
	 * @param year		검증할 정수형 년도
	 * @param range	허용되는 최대 과거 범위(0이면 true 반환)
	 * @return	범위 내이면 true, 아니면 false
	 */
	private boolean isValidYearRange(int year, int range) {
		if( range == 0 ) return true;

		int currentYear = Year.now().getValue();

		return (currentYear - range) < year && year <= currentYear;
	}
}
