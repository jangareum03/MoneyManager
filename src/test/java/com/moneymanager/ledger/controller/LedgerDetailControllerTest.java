package com.moneymanager.ledger.controller;

import com.moneymanager.config.SecurityConfig;
import com.moneymanager.controller.web.GlobalWebControllerAdvice;
import com.moneymanager.controller.web.main.LedgerController;
import com.moneymanager.domain.ledger.dto.request.LedgerSearchRequest;
import com.moneymanager.domain.ledger.dto.response.*;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.*;
import com.moneymanager.domain.ledger.vo.IncomeExpenseSummary;
import com.moneymanager.domain.ledger.vo.LedgerByDate;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.info<br>
 * 파일이름       : LedgerDetailControllerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 10<br>
 * 설명              : 가계부 정보 요청에 대한 컨트롤러 테스트 클래스
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
 * 		 	  <td>25. 12. 10</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@WebMvcTest(
		controllers = LedgerController.class,
		excludeFilters = {
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						classes = {
								SecurityConfig.class,
								JwtAuthenticationFilter.class,
								GlobalWebControllerAdvice.class
						}
				)
		}
)
@AutoConfigureMockMvc(addFilters = false)
public class LedgerDetailControllerTest {

	@Autowired	private MockMvc	mockMvc;

	@MockBean	private LedgerService ledgerService;
	@MockBean	private CategoryService categoryService;
	@MockBean	@Qualifier("ledgerImage") private ImageServiceImpl imageService;


	@DisplayName("정상 요청 시 데이터와 뷰 이름이 반환된다.")
	@Test
	void getLedgerPage_요청_정상응답() throws Exception {
		//given
		String memberId = "UCa12001";

		//가짜 결과
		List<Ledger> mockDao = List.of(
				Ledger.builder()
						.code("01F8Z6YQJ3G5Z7K1V2A9B0C1D6")
						.category("020105")
						.date("20251123")
						.memo("도시락")
						.amount(9500L)
						.build(),
				Ledger.builder()
						.code("01F8Z6YQJ3G5Z7K1V2A9B0C1D7")
						.category("010101")
						.date("20251202")
						.memo("12월 월급")
						.amount(2500000L)
						.build(),
				Ledger.builder()
						.code("01ARZ3NDEKTSV4RRFFQ69G5FAV")
						.category("010201")
						.date("20251123")
						.amount(200000L)
						.build(),
				Ledger.builder()
						.code("01H5HZ8X9E7EY2XKZCW2FQX16B")
						.category("020201")
						.date("20251123")
						.amount(10000L)
						.build(),
				Ledger.builder()
						.code("01F8Z6YQJ3G5Z7K1V2A9B0C1E0")
						.category("020502")
						.date("20251123")
						.memo("배고파")
						.amount(10000L)
						.build()
		);

		LedgerGroupResponse mockGroup = LedgerGroupResponse.builder()
				.title("2025년 11월")
				.summary(new LedgerByDate(mockDao))
				.stats(IncomeExpenseSummary.of(2500000, 150000))
				.build();

		LedgerGroupForCardResponse mockCard = LedgerGroupForCardResponse.from(mockGroup);

		LedgerSearchResponse mockSearch = LedgerSearchResponse.builder().type("week").mode("all").build();

		when(ledgerService.getLedgersForSummary( eq(memberId), any(LedgerSearchRequest.class) )).thenReturn(mockGroup);

		//when
		MvcResult result = mockMvc.perform(
						MockMvcRequestBuilders.get("/ledgers/list/{type}", "week")
								.sessionAttr("mid", memberId)
								.requestAttr("member", MemberLoginResponse.Success.builder()
										.nickName("홍길동").profile(null).build())
				)

				.andExpect(status().isOk())
				.andExpect(xpath("//h1[@class='ledger-date']/@data-type").string(is("week")))
				.andDo(print())
				.andReturn();

		//then
		ModelAndView mav = result.getModelAndView();

		assertThat(mav.getViewName()).isEqualTo("/main/ledger_list");
		assertThat(mav.getModel().get("cards")).usingRecursiveComparison().isEqualTo(mockCard);
		assertThat(mav.getModel().get("search")).usingRecursiveComparison().isEqualTo(mockSearch);
	}

	@DisplayName("모드가 view면 가계부 상세내역 화면을 보여줘야 한다.")
	@Test
	void 상세모드_상세화면_이등() throws Exception {
		//given
		List<String> images = new ArrayList<>();
		images.add("/2025/11/01/3c2a8f0e-7d6b-4e1c-9f5a-0d4b3c2e1f0d.png");
		images.add(null);
		images.add(null);

		LedgerDetailResponse mockResponse = LedgerDetailResponse.builder()
				.date("2025. 11. 01 (토)")
				.type(LedgerType.INCOME)
				.category(CategoryResponse.builder().code("010101").name("월급").build())
				.memo("얏호~~")
				.amount(25000L)
				.paymentType(PaymentType.CASH)
				.placeName("성수 빵집").roadAddress("서울특별시 성수동").detailAddress("상세주소")
				.images(images)
				.build();

		when(ledgerService.getLedgerDetail("member123", "123"))
				.thenReturn(mockResponse);

		//when
		mockMvc.perform(
				MockMvcRequestBuilders.get("/ledgers/123")
						.param("mode", "view")
						.sessionAttr("mid", "member123")
						.requestAttr("member", MemberLoginResponse.Success.builder()
								.nickName("홍길동").profile(null).build())
		)
				.andExpect(status().isOk())
				.andExpect(view().name("/main/ledger_detail"))
				.andExpect(model().attributeExists("ledger"))
				.andDo(print());
	}
}
