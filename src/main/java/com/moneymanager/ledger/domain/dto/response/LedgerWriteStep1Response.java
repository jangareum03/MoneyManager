package com.moneymanager.ledger.domain.dto.response;


import com.moneymanager.global.domain.enums.DatePatterns;
import com.moneymanager.global.util.date.DateTimeUtil;
import com.moneymanager.ledger.domain.dto.response.item.LedgerTypeItem;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto<br>
 * 파일이름       : LedgerWriteStep1Response<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 18<br>
 * 설명              : 가계부 초기 작성 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 12. 18</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LedgerWriteStep1Response {
	private final List<LedgerTypeItem> types;								//가계부 유형
	private final List<Integer> years;												//연도 목록
	private final List<Integer> months;											//월 목록
	private final List<Integer> days;												//일 목록

	private final Integer currentYear;											//현재 연도
	private final Integer currentMonth;										//현재 월
	private final Integer currentDay;												//현재 일

	private final String displayDate;												//날짜를 문자열로 표현

	private LedgerWriteStep1Response(List<Integer> years, List<Integer> months, List<Integer> days) {
		this.types = LedgerTypeItem.findAll();

		this.years = years;
		this.months = months;
		this.days = days;

		LocalDate today = LocalDate.now();

		this.currentYear = today.getYear();
		this.currentMonth = today.getMonthValue();
		this.currentDay = today.getDayOfMonth();

		this.displayDate = DateTimeUtil.formatDate(today, DatePatterns.KOREAN_DATE_WITH_DAY.getPattern());
	}

	public static LedgerWriteStep1Response of(List<Integer> years, List<Integer> months, List<Integer> days) {
		return new LedgerWriteStep1Response(years, months, days);
	}

}