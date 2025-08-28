package com.moneymanager.vo;

import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.enums.RegexPattern;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import lombok.Value;

import java.time.LocalDate;
import java.time.Year;
import java.util.Objects;

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
 * 		 	  <td>25. 8. 12.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
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
		this.year = parseYear(year, maxValue);
	}


	/**
	 * 년도 문자열({@code year})을 정수로 변환하고 검증합니다.
	 * <p>
	 *     {@code max}는 정수형으로 현재 년도로부터 과거로 허용되는 범위입니다.<br>
	 *     예를 들어, 현재 년도가 2025이고 max가 5이면 2020 ~ 2025년만 허용됩니다.
	 * </p>
	 * <p>
	 *     아래와 같은 상황에 {@link ClientException} 예외가 발생합니다.
	 *     <ul>
	 *         <li>년도가 null인 경우</li>
	 *         <li>년도가 숫자로 변환이 안되는 경우</li>
	 *         <li>년도가 1 또는 2로 시작하지 않는 4자리 숫자 입력한 경우</li>
	 *         <li>년도가 지정된 범위({@code max})를 벗어난 경우</li>
	 *     </ul>
	 * </p>
	 *
	 * @param year	문자열로 입력한 년도
	 * @param max	현재 년도로부터 허용되는 최대 과거 범위(0이면 범위 제한 없음)
	 * @return 검증된 정수형 년도
	 */
	private int parseYear( String year, int max ) {
		if( year == null ) throwYearException(ErrorCode.COMMON_YEAR_MISSING, "년도를 입력해주세요.");

		int parsedYear = 0;
		try{
			parsedYear = Integer.parseInt(Objects.requireNonNull(year));
		}catch ( NumberFormatException e ) {
			throwYearException(ErrorCode.COMMON_YEAR_FORMAT, "년도는 숫자만 입력 가능합니다.", year);
		}

		if( !isMatchYear(parsedYear) ){
			throwYearException(ErrorCode.COMMON_YEAR_FORMAT, "년도는 1 또는 2로 시작하는 4자리 숫자만 입력 가능합니다.", year);
		}

		if( !isValidYearRange(parsedYear, max) ) {
			int currentYear = LocalDate.now().getYear();
			String message = String.format("년도는 %d ~ %d까지만 입력가능합니다.", currentYear-max, currentYear);

			throwYearException(ErrorCode.COMMON_YEAR_INVALID, message, year);
		}

		return parsedYear;
	}


	/**
	 * {@link ClientException}을 발생시킵니다.
	 *
	 * @param code			오류코드
	 * @param message	사용자 메시지
	 * @throws ClientException	항상 발생
	 */
	private void throwYearException(ErrorCode code, String message) {
		throwYearException(code, message, null);
	}


	/**
	 * {@link ClientException}을 발생시킵니다.
	 *
	 * @param code			오류코드
	 * @param message	사용자 메시지
	 * @param data			요청 데이터
	 * @throws ClientException	항상 발생
	 */
	private void throwYearException(ErrorCode code, String message, String data) {
		ErrorDTO<String> errorDTO = ErrorDTO.<String>builder()
				.errorCode(code)
				.message(message)
				.requestData(data)
				.build();

		throw new ClientException(errorDTO);
	}


	/**
	 * 년도가 RegexPattern 조건에 부합하는지 검증합니다.
	 *
	 * @param year	검증할 정수형 년도
	 * @return	패턴과 일치하면 true, 아니면 false
	 */
	private boolean isMatchYear(int year) {
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
