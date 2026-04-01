package com.moneymanager.domain.ledger.vo;

import com.moneymanager.service.validation.DateValidator;
import lombok.Value;

import java.time.LocalDate;

import static com.moneymanager.utils.date.DateTimeUtils.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : DateRange<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 30<br>
 * 설명              : 날짜 범위를 나타내는 클래스
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
 * 		 	  <td>26. 3. 30</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
public class DateRange {

	LocalDate from;
	LocalDate to;

	public DateRange(String from, String to) {
		DateValidator.validatePeriod(from, to);

		this.from = parseDateFromYyyyMMdd(from);
		this.to = parseDateFromYyyyMMdd(to);
	}
}