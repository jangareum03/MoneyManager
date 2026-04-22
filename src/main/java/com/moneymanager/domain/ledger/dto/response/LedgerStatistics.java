package com.moneymanager.domain.ledger.dto.response;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto.response<br>
 * 파일이름       : LedgerStatistics<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 5<br>
 * 설명              : 조회된 가계부 내역 금액을 계산하여 나온 통계 금액을 담은 객체
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
 * 		 	  <td>26. 4. 5</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LedgerStatistics {
	private final Long total;			//수입+지출 합계
	private final Long income;		//수입 합계
	private final Long outlay;			//지출 합계

	private LedgerStatistics(Long income, Long outlay) {
		this.income = income;
		this.outlay = outlay;
		this.total = income + outlay;
	}

	public static LedgerStatistics of(Long income, Long outlay) {
		return new LedgerStatistics(income, outlay);
	}
}
