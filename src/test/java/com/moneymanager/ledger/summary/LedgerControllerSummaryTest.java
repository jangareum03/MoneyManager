package com.moneymanager.ledger.summary;


import com.moneymanager.config.SecurityConfig;
import com.moneymanager.controller.web.GlobalWebControllerAdvice;
import com.moneymanager.controller.web.main.LedgerController;
import com.moneymanager.domain.ledger.dto.LedgerGroupForCardResponse;
import com.moneymanager.domain.ledger.dto.LedgerGroupResponse;
import com.moneymanager.domain.ledger.dto.LedgerSearchRequest;
import com.moneymanager.domain.ledger.dto.LedgerSearchResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.vo.AmountInfo;
import com.moneymanager.domain.ledger.vo.IncomeExpenseSummary;
import com.moneymanager.domain.ledger.vo.LedgerByDate;
import com.moneymanager.domain.ledger.vo.LedgerDate;
import com.moneymanager.domain.member.dto.MemberLoginResponse;
import com.moneymanager.security.jwt.JwtAuthenticationFilter;
import com.moneymanager.service.main.CategoryService;
import com.moneymanager.service.main.ImageServiceImpl;
import com.moneymanager.service.main.LedgerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LedgerController.class,
		excludeFilters = {
		@ComponentScan.Filter(
				type = FilterType.ASSIGNABLE_TYPE,
				classes = {
						SecurityConfig.class,
						JwtAuthenticationFilter.class,
						GlobalWebControllerAdvice.class
				}
		)
})
@AutoConfigureMockMvc(addFilters = false)		//스프링 시큐리티 필터 제거
public class LedgerControllerSummaryTest {

	@Autowired	private MockMvc mockMvc;

	@MockBean	private LedgerService ledgerService;
	@MockBean	private CategoryService	categoryService;
	@MockBean @Qualifier("ledgerImage")	private ImageServiceImpl imageService;


	@DisplayName("잘못된 경로(/ledgers/list)로 접근하면 400에러가 발생한다.")
	@Test
	void type_없으면_400에러() throws Exception {
		//given
		String memberId = "UCa12001";

		//when
		mockMvc.perform(
				MockMvcRequestBuilders.get("/ledgers/list")
						.sessionAttr("mid", memberId)
						.requestAttr("member", MemberLoginResponse.Success.builder()
								.nickName("홍길동").profile(null).build())
		)
				.andExpect(status().isBadRequest());
	}

	@DisplayName("정상 요청 시 데이터와 뷰 이름이 반환된다.")
	@Test
	void getLedgerPage_요청_정상응답() throws Exception {
		//given
		String memberId = "UCa12001";

		//가짜 결과
		List<Ledger> mockDao = List.of(
				Ledger.builder()
						.id("01F8Z6YQJ3G5Z7K1V2A9B0C1D6")
						.category(Category.builder().code("020105").name("외식").build())
						.date(new LedgerDate("20251123"))
						.memo("도시락")
						.amountInfo(AmountInfo.builder().amount(9500).build())
						.build(),
				Ledger.builder()
						.id("01F8Z6YQJ3G5Z7K1V2A9B0C1D7")
						.category(Category.builder().code("010101").name("월급").build())
						.date(new LedgerDate("20251202"))
						.memo("12월 월급")
						.amountInfo(AmountInfo.builder().amount(2500000).build())
						.build(),
				Ledger.builder()
						.id("01ARZ3NDEKTSV4RRFFQ69G5FAV")
						.category(Category.builder().code("010201").name("주식").build())
						.date(new LedgerDate("20251123"))
						.amountInfo(AmountInfo.builder().amount(200000).build())
						.build(),
				Ledger.builder()
						.id("01H5HZ8X9E7EY2XKZCW2FQX16B")
						.category(Category.builder().code("020201").name("OTT").build())
						.date(new LedgerDate("20251123"))
						.amountInfo(AmountInfo.builder().amount(10000).build())
						.build(),
				Ledger.builder()
						.id("01F8Z6YQJ3G5Z7K1V2A9B0C1E0")
						.category(Category.builder().code("020502").name("간식").build())
						.date(new LedgerDate("20251123"))
						.amountInfo(AmountInfo.builder().amount(10000).build())
						.memo("배고파")
						.build()
		);

		LedgerGroupResponse mockGroup = LedgerGroupResponse.builder()
				.title("2025년 11월")
				.summary(new LedgerByDate(mockDao))
				.stats(IncomeExpenseSummary.of(2500000, 150000))
				.build();

		LedgerGroupForCardResponse mockCard = LedgerGroupForCardResponse.from(mockGroup);

		LedgerSearchResponse mockSearch = LedgerSearchResponse.builder().type("month").mode("all").build();

		when(ledgerService.getLedgersForSummary( eq(memberId), any(LedgerSearchRequest.class) )).thenReturn(mockGroup);

		//when
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.get("/ledgers/list/{type}", "month")
						.sessionAttr("mid", memberId)
						.requestAttr("member", MemberLoginResponse.Success.builder()
								.nickName("홍길동").profile(null).build())
		)

		.andExpect(status().isOk())
				.andExpect(xpath("//h1[@class='ledger-date']/@data-type").string(is("month")))
				.andDo(print())
		.andReturn();

		//then
		ModelAndView mav = result.getModelAndView();

		assertThat(mav.getViewName()).isEqualTo("/main/ledger_list");
		assertThat(mav.getModel().get("cards")).usingRecursiveComparison().isEqualTo(mockCard);
		assertThat(mav.getModel().get("search")).usingRecursiveComparison().isEqualTo(mockSearch);
	}
}
