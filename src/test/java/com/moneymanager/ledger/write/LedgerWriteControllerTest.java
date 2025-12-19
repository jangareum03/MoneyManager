package com.moneymanager.ledger.write;

import com.moneymanager.config.SecurityConfig;
import com.moneymanager.controller.web.GlobalWebControllerAdvice;
import com.moneymanager.controller.web.main.WriteController;
import com.moneymanager.domain.ledger.dto.CategoryResponse;
import com.moneymanager.domain.ledger.dto.LedgerWriteStep1Response;
import com.moneymanager.domain.ledger.dto.LedgerWriteStep2Response;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.member.dto.MemberLoginResponse;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.security.jwt.JwtAuthenticationFilter;
import com.moneymanager.service.main.LedgerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.write<br>
 * 파일이름       : LedgerWriteControllerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 18<br>
 * 설명              : 가계부 작성 요청에 대한 컨트롤러 테스트 클래스
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
 * 		 	  <td>25. 12. 18</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@WebMvcTest(
		controllers = WriteController.class,
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
public class LedgerWriteControllerTest {

	@Autowired	private MockMvc mockMvc;

	@MockBean	private LedgerService	service;

	private static String url;

	@BeforeAll
	static void 설정(){
		url = "/ledgers/write";
	}

	@DisplayName("가계부 작성 1단계 화면을 보여준다.")
	@Test
	void 가계부_초기단계_작성화면_이동() throws Exception {
		//given
		LedgerWriteStep1Response mockResponse = new LedgerWriteStep1Response(LocalDate.now());

		//when
		mockMvc.perform(
				MockMvcRequestBuilders.get(url)
						.requestAttr("member", MemberLoginResponse.Success.builder()
								.nickName("홍길동").profile(null).build())
		)
				.andExpect(status().isOk())
				.andExpect(view().name("/main/ledger_writeStep1"))
				.andExpect(model().attributeExists("data"))
				.andDo(print());
	}

	@DisplayName("파라미터 date값이 없으면 1단계 화면으로 이동한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void 가계부_날짜가_없으면_1단계화면으로_이동(String date) throws Exception {
		//when
		mockMvc.perform(
				post(url+"/{type}", "income")
						.param("date", date)
						.requestAttr("member", MemberLoginResponse.Success.builder()
								.nickName("홍길동").profile(null).build())
		)
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/main/ledger_writeStep1"))
				.andDo(print());
	}

	@DisplayName("가계부 상세 작성 화면 요청에서 ClientException 예외 발생 시 1단계 화면으로 이동한다.")
	@Test
	void 예외_발생하면_1단계화면_이동() throws Exception {
		//given
		when(service.getWriteByData(any(), anyString(), anyString()))
				.thenThrow(ClientException.class);

		//when
		mockMvc.perform(
				post(url+"/{type}", "income")
						.param("date", "20251201")
						.requestAttr("member", MemberLoginResponse.Success.builder()
								.nickName("홍길동").profile(null).build())
		)
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/main/ledger_writeStep1"))
				.andDo(print());
	}

	@DisplayName("1단계에서 정상적으로 값이 넘어오면 2단계화면으로 이동한다.")
	@Test
	void 정상동작_이면_2단계화면_이동() throws Exception {
		//given
		String type = "income";
		String date = "20251201";

		when(service.getWriteByData("member123", type, date))
				.thenReturn(
						LedgerWriteStep2Response.builder()
								.title("2025년 12월 01일 월요일")
								.type(LedgerType.INCOME)
								.fixed(List.of(FixedYN.values()))
								.categories(
										List.of(
												CategoryResponse.builder()
														.name("소득")
														.code("010100")
														.build(),
												CategoryResponse.builder()
														.name("저축")
														.code("010200")
														.build()
										)
								)
								.paymentTypes(List.of(PaymentType.values()))
								.imageSlot(List.of(true, false, false))
								.build()
				);

		//when & then
		mockMvc.perform(
				post(url+"/{type}", type)
						.param("date", date)
						.sessionAttr("mid", "member123")
						.requestAttr("member", MemberLoginResponse.Success.builder()
								.nickName("홍길동").profile(null).build())
		)
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("ledger"))
				.andExpect(model().attributeExists("defaultPaymentType"))
				.andExpect(model().attributeExists("defaultFix"))
				.andExpect(view().name("/main/ledger_writeStep2"))
				.andDo(print());
	}
}
