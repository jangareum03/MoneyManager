package com.moneymanager.ledger.domain.dto.response.history;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response.history<br>
 * 파일이름       : HistoryDateFilter<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 3<br>
 * 설명              : 가계부 내역을 조회할 연, 월, 주 데이터를 담은 객체
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
 * 		 	  <td>26. 9. 3</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class HistoryDateFilter {

    Integer year;
    Integer month;
    Integer week;

    public HistoryDateFilter(Integer year, Integer month, Integer week) {
        this.year = year;
        this.month = month;
        this.week = week;
    }

    public boolean hasYear() {
        return year != null;
    }

    public boolean hasMonth() {
        return month != null;
    }

    public boolean hasWeek() {
        return week != null;
    }

    public boolean hasDate() {
        return hasYear() || hasMonth() || hasWeek();
    }

}