package com.moneymanager.domain.ledger.vo;

import lombok.Getter;
import lombok.Value;

import java.time.LocalDate;
import static com.moneymanager.utils.DateTimeUtils.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : LedgerDate<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 14.<br>
 * 설명              : 가계부 날짜 값을 나타내는 클래스
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
public class LedgerDate {

	LocalDate transactionDate;

	public LedgerDate(String date) {
		this.transactionDate = parseDateFlexible(date);
	}



	/**
	 *	주어진 문자열에서 숫자(0~9)만 추출하여 반환합니다.
	 *<p>
	 * 문자열 안의 모든 숫자가 아닌 문자열이 제거됩니다.
	 *
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	extractDigits("2025-11-11");			//"20251111" 반환
	 *     	extractDigits("12일 3시");					//"123" 반환
	 *     	extractDigits("AB95BC3#");			//"953" 반환
	 *     	extractDigits("ABCDE");					//"" 반환
	 *     }</pre>
	 * </p>
	 *
	 *
	 * @param date		숫자만 추출할 날짜 문자열
	 * @return	입력된 문자열(<code>date</code>)에서 숫자만 모아 만든 새 문자열
	 */
	private String extractDigits(String date) {
		return date.replaceAll("\\D", "");
	}
}