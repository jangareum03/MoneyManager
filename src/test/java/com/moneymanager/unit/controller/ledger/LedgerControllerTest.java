package com.moneymanager.unit.controller.ledger;

import com.moneymanager.controller.web.user.ledger.LedgerController;
import com.moneymanager.domain.ledger.dto.response.LedgerTypeResponse;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep1Response;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.domain.member.dto.MemberLoginResponse;
import com.moneymanager.security.jwt.JwtAuthenticationFilter;
import com.moneymanager.security.jwt.JwtTokenProvider;
import com.moneymanager.service.ledger.LedgerReadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceView;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.controller.ledger<br>
 * 파일이름       : LedgerControllerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 6<br>
 * 설명              : LedgerController 클래스 요청을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 1. 6.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@WebMvcTest(LedgerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LedgerControllerTest {

	@Autowired	private MockMvc mockMvc;

	@MockBean	private JwtTokenProvider jwtTokenProvider;
	@MockBean	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockBean	private LedgerReadService	ledgerReadService;

	@BeforeEach
	void 랜더링제거(@Autowired LedgerController controller){
		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setViewResolvers(((viewName, locale) -> new InternalResourceView(viewName)))
				.build();
	}


	//==================[ 📌writeStep1View  ]==================
	@Test
	void 가계부_1단계_작성화면() throws Exception {
		//given
		LedgerWriteStep1Response response =
				LedgerWriteStep1Response
						.builder()
						.types(LedgerTypeResponse.fromEnum())
						.years(List.of(2020, 2021, 2023))
						.months(List.of(1,2,3))
						.days(List.of(1,2,3,4,5))
						.currentYear(2023)
						.currentMonth(3)
						.currentDay(1)
						.displayDate("2026년 01월 01일")
						.build();

		when(ledgerReadService.getInitialData())
				.thenReturn(response);

		//when
		mockMvc.perform(
						get("/ledgers/new/step1")
		)
				.andExpect(status().isOk())
				.andExpect(view().name("/main/ledger_writeStep1"))
				.andExpect(model().attributeExists("ledger"))
				.andExpect(model().attribute("ledger", response));
	}
}