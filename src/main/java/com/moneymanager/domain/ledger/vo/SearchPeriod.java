package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.global.vo.DateGroupable;
import com.moneymanager.utils.date.DateTimeUtils;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDate;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : SearchPeriod<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 22.<br>
 * 설명              : 가계부 검색을 위한 기간값을 나타내는 클래스
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
 * 		 	  <td>25. 11. 22.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
@Getter
public class SearchPeriod implements DateGroupable {
	String start;
	String end;

	public SearchPeriod(String start, String end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public LocalDate getStartDate() {
		return DateTimeUtils.parseDateFromYyyyMMdd(start);
	}

	@Override
	public LocalDate getEndDate() {
		return DateTimeUtils.parseDateFromYyyyMMdd(end);
	}
}
