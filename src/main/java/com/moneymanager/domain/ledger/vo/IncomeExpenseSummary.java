package com.moneymanager.domain.ledger.vo;

import com.moneymanager.exception.ErrorCode;
import lombok.Builder;
import lombok.Value;

import static com.moneymanager.exception.ErrorUtil.createServerException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : IncomeExpenseSummary<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 29<br>
 * 설명              : 가계부 수입과 지출 요약 값을 나타내는 클래스
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
@Value
@Builder
public class IncomeExpenseSummary {
	long total;			//수입과 지출을 합친 금액
	long income;		//수입
	long expense;		//지출

	//수입과 지출금액을 이용하여 IncomeExpenseSummary 객체 생성
	public static IncomeExpenseSummary of(long income, long expense){
		long incomeOrZero = Math.max(income, 0L);
		long expenseOrZero = Math.max(expense, 0L);
		long total;

		try{
			total = Math.subtractExact(incomeOrZero, expenseOrZero);
		} catch (ArithmeticException e) {
			total = 0;
		}

		return IncomeExpenseSummary.builder()
				.total(total)
				.income(incomeOrZero)
				.expense(expenseOrZero)
				.build();
	}
}