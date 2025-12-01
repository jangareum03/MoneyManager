package com.moneymanager.domain.ledger.dto;

import com.moneymanager.domain.ledger.vo.IncomeExpenseSummary;
import com.moneymanager.domain.ledger.vo.LedgerByDate;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto<br>
 * 파일이름       : LedgerGroupForCardResponse<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 29<br>
 * 설명              : 가계부 요약정보를 카드 리스트로 형태로 클라이언트에게 전달하기 위한 클래스
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
 * 		 	  <td>25. 11. 29.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class LedgerGroupForCardResponse {
	private final String title;
	private final List<LedgerByDate> summary;
	private final IncomeExpenseSummary stats;
}
