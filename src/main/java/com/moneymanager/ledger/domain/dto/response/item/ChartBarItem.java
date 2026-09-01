package com.moneymanager.ledger.domain.dto.response.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response.item<br>
 * 파일이름       : ChartBarItem<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 31<br>
 * 설명              : 가계 부 내역 차트 바 한 개의 정보를 담은 클래스
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
 * 		 	  <td>26. 8. 31</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class ChartBarItem {

    private final String label;
    private final Long income;
    private final Long outlay;

    private ChartBarItem(String label, Long income, Long outlay) {
        this.label = label;
        this.income = income;
        this.outlay = outlay;
    }

    public static ChartBarItem ofYear(String month, Long income, Long outlay) {
        return new ChartBarItem(month, income, outlay);
    }

    public static ChartBarItem ofMonth(String category, Long amount) {
        return new ChartBarItem(category, null,  amount);
    }

    public static ChartBarItem ofWeek(String week, Long income, Long outlay) {
        return new ChartBarItem(week, income, outlay);
    }

}