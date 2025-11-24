package com.moneymanager.domain.budgetBook.vo;

import com.moneymanager.domain.global.vo.DateGroupable;
import static com.moneymanager.utils.DateTimeUtils.formatDateAsString;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : SearchPeriodAdapter<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 22.<br>
 * 설명              : 다른 클래스를 SearchPeriod 클래스에 맞게 변환해주는 클래스
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
public class SearchPeriodAdapter {

	private final DateGroupable dateScope;

	public SearchPeriodAdapter(DateScope dateScope) {
		this.dateScope = dateScope;
	}

	public SearchPeriod toSearchPeriod() {
		String start = formatDateAsString(dateScope.getStartDate(), "yyyyMMdd");
		String end = formatDateAsString(dateScope.getEndDate(), "yyyyMMdd");

		return new SearchPeriod(start, end);
	}

}