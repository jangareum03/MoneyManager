package com.moneymanager.ledger.domain;

import com.moneymanager.domain.ledger.vo.IncomeExpenseSummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain<br>
 * 파일이름       : IncomeExpenseSummaryTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 2<br>
 * 설명              : IncomeExpenseSummary 클래스를 검증하는 테스트 클래스
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
 * 		 	  <td>25. 12. 2.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class IncomeExpenseSummaryTest {

	@DisplayName("수입 금액이 음수면 0으로 변경된다.")
	@Test
	void 수입_음수면_0변경(){
		//given
		long income = -1L;
		long expense = 1000L;

		//when
		IncomeExpenseSummary result = IncomeExpenseSummary.of(income, expense);

		//then
		assertThat(result)
				.extracting(IncomeExpenseSummary::getTotal, IncomeExpenseSummary::getIncome, IncomeExpenseSummary::getExpense)
				.containsExactly(-1000L, 0L, 1000L);
	}

	@DisplayName("지출 금액이 음수면 0으로 변경된다.")
	@Test
	void 지출_음수면_0변경(){
		//given
		long income =  1000L;
		long expense = -100L;

		//when
		IncomeExpenseSummary result = IncomeExpenseSummary.of(income, expense);

		//then
		assertThat(result)
				.extracting(IncomeExpenseSummary::getTotal, IncomeExpenseSummary::getIncome, IncomeExpenseSummary::getExpense)
				.containsExactly(1000L, income , 0L);
	}

	@DisplayName("총합 금액이 음수면 그대로 반환된다.")
	@ParameterizedTest
	@CsvSource({
			"500, 700, -200",
			"0, 2000, -2000"
	})
	void 총합_음수면_그대로반환(long income, long expense, long total){
		//when
		IncomeExpenseSummary result = IncomeExpenseSummary.of(income, expense);

		//then
		assertThat(result)
				.extracting(IncomeExpenseSummary::getTotal, IncomeExpenseSummary::getIncome, IncomeExpenseSummary::getExpense)
				.containsExactly(total, income , expense);
	}
}