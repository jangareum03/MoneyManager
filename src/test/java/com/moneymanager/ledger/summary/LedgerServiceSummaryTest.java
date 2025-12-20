package com.moneymanager.ledger.summary;

import com.moneymanager.dao.main.LedgerDao;
import com.moneymanager.domain.ledger.dto.LedgerCategoryDto;
import com.moneymanager.domain.ledger.dto.LedgerGroupResponse;
import com.moneymanager.domain.ledger.dto.LedgerSearchRequest;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.DateType;
import com.moneymanager.domain.ledger.vo.*;
import com.moneymanager.service.main.CategoryService;
import com.moneymanager.service.main.ImageServiceImpl;
import com.moneymanager.service.main.LedgerService;
import com.moneymanager.service.main.validation.DateScopeValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.summary<br>
 * 파일이름       : LedgerServiceSummaryTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 3<br>
 * 설명              : 가계부 요약 기능 관련 테스트 클래스
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
 * 		 	  <td>25. 12. 3.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class LedgerServiceSummaryTest {

	@InjectMocks
	private LedgerService service;

	@Mock
	private CategoryService categoryService;
	@Mock
	private ImageServiceImpl imageService;
	@Mock
	private DateScopeValidator validator;
	@Mock
	private LedgerDao dao;

	//=================================================
	// getLedgersForSummary() 테스트
	//=================================================\
	@DisplayName("날짜 범위가 월이고 연도 값이 있으면 전송된 연도와 월값으로 제목이 생성된다.")
	@Test
	void 월범위_이면_월제목으로_반환(){
		//given
		LedgerSearchRequest search = LedgerSearchRequest.builder().type(DateType.MONTH).year(2025).month(1).mode("all").build();

		//when
		LedgerGroupResponse result = service.getLedgersForSummary("member123", search);

		//then
		assertThat(result.getTitle()).isEqualTo("2025년 01월");
	}

	@DisplayName("날짜 범위가 월이고 연도 값이 없으면 현재 연도와 월값으로 제목이 생성된다.")
	@Test
	void 월범위_이고_연도가_없으면_현재날짜로_설정된_제목으로_반환(){
		//given
		LedgerSearchRequest search = LedgerSearchRequest.builder().type(DateType.MONTH).month(11).mode("all").build();
		LocalDate today = LocalDate.now();

		//when
		LedgerGroupResponse result = service.getLedgersForSummary("member123", search);

		//then
		assertThat(result.getTitle()).isEqualTo(String.format("%s년 %s월", today.getYear(), today.getMonthValue()));
	}

	@DisplayName("날짜 범위가 주면 제목이 생성된다.")
	@ParameterizedTest
	@CsvSource({
			"2025, 11, 1, 2025년 11월 1주",
			"2025, 10, 3, 2025년 10월 3주"
	})
	void 주범위_이면_주제목으로_반환(int year, int month, int week, String expected){
		//given
		LedgerSearchRequest search = LedgerSearchRequest.builder().type(DateType.WEEK).year(year).month(month).week(week).mode("all").build();

		//when
		LedgerGroupResponse result = service.getLedgersForSummary("member123", search);

		//then
		assertThat(result.getTitle()).isEqualTo(expected);
	}

	@DisplayName("날짜 범위가 연이면 제목이 생성된다.")
	@ParameterizedTest
	@ValueSource(ints = {2024, 2025})
	void 연범위_이면_연제목으로_반환(int year){
		//given
		LedgerSearchRequest search = LedgerSearchRequest.builder().type(DateType.YEAR).year(year).mode("all").build();

		//when
		LedgerGroupResponse result = service.getLedgersForSummary("member123", search);

		//then
		assertThat(result.getTitle()).isEqualTo(year+ "년");
	}

	@DisplayName("카테고리 검색하면 검색한 내용에 맞춰 가계부 내역이 조회된다.")
	@Test
	void 카테고리검색이면_선택한_날짜로_내역조회(){
		//given
		LedgerSearchRequest search = LedgerSearchRequest.builder().type(DateType.YEAR).year(2025).mode("inout").keywords(List.of("020000")).build();
		SearchPeriod mockPeriod = new SearchPeriod("20250101", "20251231");

		List<LedgerCategoryDto> mockDao = List.of(
				new LedgerCategoryDto(
						Ledger.builder()
								.id("01F8Z6YQJ3G5Z7K1V2A9B0C1D2")
								.date("20251105")
								.amount(50000L)
								.memo("초밥 먹었어용")
								.build(),
						Category.builder().code("020601").build()
				),
				new LedgerCategoryDto(
						Ledger.builder()
								.id("01F8Z6YQJ3G5Z7K1V2A9B0C1D3")
								.date("20251108")
								.amount(15000L)
								.memo("양초 구매 완료!!!")
								.build(),
						Category.builder().code("020301").build()
				)
		);

		when(dao.findLedgersBySearch("member123", search.getMode(), search.getKeywords(), mockPeriod))
				.thenReturn(mockDao);

		//when
		LedgerGroupResponse result = service.getLedgersForSummary("member123", search);

		//then
		assertThat(result.getTitle()).isEqualTo("2025년");
		//assertThat(result.getSummary()).usingRecursiveComparison().isEqualTo(new LedgerByDate(mockDao));
	}

	@DisplayName("메모 검색하면 검색한 내용에 맞춰 가계부 내역이 조회된다.")
	@Test
	void 메모검색이면_선택한_날짜로_내역조회(){
		//given
		LedgerSearchRequest search = LedgerSearchRequest.builder().type(DateType.MONTH).year(2025).month(11).mode("memo").keywords(List.of("초")).build();
		SearchPeriod mockPeriod = new SearchPeriod("20251101", "20251130");

		List<LedgerCategoryDto> mockDao = List.of(
				new LedgerCategoryDto(
						Ledger.builder()
								.id("01F8Z6YQJ3G5Z7K1V2A9B0C1D2")
								.date("20251105")
								.amount(50000L)
								.memo("초밥 먹었어용")
								.build(),
						Category.builder().code("020601").build()
				),
				new LedgerCategoryDto(
						Ledger.builder()
								.id("01F8Z6YQJ3G5Z7K1V2A9B0C1D3")
								.date("20251108")
								.amount(15000L)
								.memo("양초 구매 완료!!!")
								.build(),
						Category.builder().code("020301").build()
				)
		);

		when(dao.findLedgersBySearch("member123", search.getMode(), search.getKeywords(), mockPeriod))
				.thenReturn(mockDao);

		//when
		LedgerGroupResponse result = service.getLedgersForSummary("member123", search);

		//then
		assertThat(result.getTitle()).isEqualTo("2025년 11월");
		//assertThat(result.getSummary()).usingRecursiveComparison().isEqualTo(new LedgerByDate(mockDao));
	}

	@DisplayName("기간으로 검색하면 시작날짜와 종료날짜 내의 가계부 내역이 조회 및 금액 통계 계산이 된다.")
	@Test
	void 기간검색이면_선택한_날짜로_내역조회(){
		//given
		LedgerSearchRequest search = LedgerSearchRequest.builder().type(DateType.YEAR).year(2025).mode("period").keywords(List.of("20251105", "20251110")).build();
		SearchPeriod mockPeriod = new SearchPeriod(search.getKeywords().get(0), search.getKeywords().get(1));

		List<LedgerCategoryDto> mockDao = List.of(
				new LedgerCategoryDto(
						Ledger.builder()
								.id("01F8Z6YQJ3G5Z7K1V2A9B0C1D2")
								.date("20251105")
								.amount(500000L)
								.build(),
						Category.builder().code("020601").build()
				),
				new LedgerCategoryDto(
						Ledger.builder()
								.id("01H5HZ8X9E7EY2XKZCW2FQX16B")
								.date("20251106")
								.amount(2500000L)
								.memo("월급")
								.build(),
						Category.builder().code("010101").build()
				),
				new LedgerCategoryDto(
						Ledger.builder()
								.id("01F8Z6YQJ3G5Z7K1V2A9B0C1D3")
								.date("20251108")
								.amount(15000L)
								.memo("주토피아2")
								.build(),
						Category.builder().code("020301").build()
				)
		);

		IncomeExpenseSummary mockStats = IncomeExpenseSummary.of(2500000, 515000);

		when(dao.findLedgersBySearch("member123", search.getMode(), search.getKeywords(), mockPeriod ))
				.thenReturn(mockDao);

		//when
		LedgerGroupResponse result = service.getLedgersForSummary("member123", search);

		//then
		assertThat(result.getTitle()).isEqualTo("2025년");
		//assertThat(result.getSummary()).usingRecursiveComparison().isEqualTo(new LedgerByDate(mockDao));
		assertThat(result.getStats()).usingRecursiveComparison().isEqualTo(mockStats);
	}

	@DisplayName("검색 결과가 없으면 가계부 내역이 조회 및 모든 금액은 0으로 설정된다.")
	@Test
	void 검색결과_없으면_빈내역조회(){
		//given
		LedgerSearchRequest search = LedgerSearchRequest.builder().type(DateType.MONTH).year(2025).month(11).mode("category").keywords(List.of("020101")).build();
		List<Ledger> mockDao = Collections.emptyList();

		//when
		LedgerGroupResponse result = service.getLedgersForSummary("member123", search);

		//then
		assertThat(result.getTitle()).isEqualTo("2025년 11월");
		assertThat(result.getSummary()).usingRecursiveComparison().isEqualTo(new LedgerByDate(mockDao));
		assertThat(result.getStats())
				.extracting(IncomeExpenseSummary::getTotal, IncomeExpenseSummary::getIncome, IncomeExpenseSummary::getExpense)
				.containsExactly(0L, 0L, 0L);
	}
}