package com.moneymanager.budgetBook.search;

import com.moneymanager.dao.main.BudgetBookDao;
import com.moneymanager.domain.budgetBook.dto.LedgerCategoryResponse;
import com.moneymanager.domain.budgetBook.dto.LedgerListResponse;
import com.moneymanager.domain.budgetBook.dto.LedgerSearchRequest;
import com.moneymanager.domain.budgetBook.enums.DateType;
import com.moneymanager.domain.budgetBook.vo.DateScope;
import com.moneymanager.service.main.BudgetBookService;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.main<br>
 * 파일이름       : BudgetBookServiceSearchTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 20.<br>
 * 설명              : BudgetBookService 기능을 검증하는 테스트 코드를 작성한 클래스
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
 * 		 	  <td>25. 11. 20.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class BudgetBookServiceSearchTest {

	@Mock
	private BudgetBookDao budgetBookDao;

	@InjectMocks
	private BudgetBookService budgetBookService;

	//=================================================
	// getBudgetBooksForSummary() 테스트
	//=================================================
	@DisplayName("월별 가계부에서 메모 검색 시 일자별 카드로 조회되어야 한다.")
	@Test
	void getBudgetBooksForSummary_memoMode_returnResponse() {
		//given
		String memberId = "member";
		LedgerSearchRequest search = LedgerSearchRequest.builder()
				.type(DateType.MONTH)
				.year(2025)
				.month(5)
				.mode("memo")
				.keywords(List.of("점심")).build();

		LedgerListResponse.DayCards mockDayCards = LedgerListResponse.DayCards.builder()
				.date("20250501")
				.cardList(List.of(
						LedgerListResponse.Card.builder()
								.id(1L)
								.category(LedgerCategoryResponse.builder().code("010101").name("식비").build())
								.price(20000L)
								.memo("점심")
								.build()
				))
				.build();

		when(budgetBookDao.findBudgetBooksBySearch(memberId, search.getMode(), search.getKeywords(), DateScope.ofYearMonth(search.getYear(), search.getMonth())))
				.thenReturn(List.of(mockDayCards));

		//when
		LedgerListResponse response = budgetBookService.getBudgetBooksForSummary(memberId, search);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("2025년 05월");

		assertThat(response.getCards())
				.hasSize(1)
				.extracting("date").containsExactly("2025. 05. 01 (목)");

		assertThat(response.getCards().get(0).getCardList())
				.hasSize(1)
				.extracting("id", "category.code","category.name", "price", "memo")
				.containsExactly(Tuple.tuple(1L, "010101", "식비", 20000L, "점심"));

		assertThat(response.getStats()).isNotNull();

	}

}