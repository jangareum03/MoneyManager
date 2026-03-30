package com.moneymanager.unit.controller.ledger;

import com.moneymanager.controller.web.ledger.LedgerController;
import com.moneymanager.domain.ledger.dto.response.LedgerTypeResponse;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep1Response;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep2Response;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.security.jwt.JwtAuthenticationFilter;
import com.moneymanager.security.jwt.JwtTokenProvider;
import com.moneymanager.service.ledger.LedgerReadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceView;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
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

	@MockBean	private LedgerReadService	ledgerReadService;

	@MockBean	private JwtTokenProvider jwtTokenProvider;
	@MockBean	private JwtAuthenticationFilter jwtAuthenticationFilter;

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

		when(ledgerReadService.getWriteStep1Data())
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


	//==================[ 📌writeStep2View  ]==================
	@ParameterizedTest(name = "[{index}] type={0}")
	@ValueSource(strings = {"income", "outlay"})
	@DisplayName("가계부 유형과 날짜로 정상적인 데이터와 뷰 반환한다.")
	void 정상_흐름이면_데이터와_뷰_반환(String type) throws Exception {
		//given
		LedgerWriteStep2Response response = mock(LedgerWriteStep2Response.class);

		when(ledgerReadService.getWriteStep2Data(
				eq(LedgerType.fromUrlCode(type)),
				eq(LocalDate.of(2025, 7, 7)))
		).thenReturn(response);

		//when & then
		mockMvc.
				perform(
						get("/ledgers/new/step2")
						.param("type", type)
						.param("date", "20250707"))
				.andExpect(status().isOk())
				.andExpect(view().name("/main/ledger_writeStep2"))
				.andExpect(model().attributeExists("ledger"))
				.andExpect(model().attribute("ledger", response));
	}

	@ParameterizedTest(name = "[{index}] type={0}")
	@ValueSource(strings = {"", "expense", "expe23"})
	@DisplayName("가계부 유형 type이 지원하지 않으면 INCOME으로 대체된다.")
	void 가계부_유형이_문제면_기본값_대체(String type) throws Exception {
		//given
		LedgerWriteStep2Response response = mock(LedgerWriteStep2Response.class);

		when(ledgerReadService.getWriteStep2Data(
				any(LedgerType.class),
				eq(LocalDate.of(2025, 7, 7)))
		).thenReturn(response);

		//when & then
		mockMvc.
				perform(
						get("/ledgers/new/step2")
								.param("type", type)
								.param("date", "20250707"))
				.andExpect(status().isOk())
				.andExpect(view().name("/main/ledger_writeStep2"))
				.andExpect(model().attributeExists("ledger"))
				.andExpect(model().attribute("ledger", response));

		verify(ledgerReadService).getWriteStep2Data(
				eq(LedgerType.INCOME),
				eq(LocalDate.of(2025, 7, 7))
		);
	}

	@ParameterizedTest(name = "[{index}] date={0}")
	@MethodSource("validDate")
	@DisplayName("가계부 거래날짜 date가 지원하지 않으면 오늘날짜로 대체된다.")
	void 날짜_형식이_문제면_기본값_대체(String date) throws Exception {
		//given
		String type = "income";
		LedgerWriteStep2Response response = mock(LedgerWriteStep2Response.class);

		when(ledgerReadService.getWriteStep2Data(
				eq(LedgerType.INCOME),
				any(LocalDate.class))
		).thenReturn(response);

		//when & then
		mockMvc.
				perform(
						get("/ledgers/new/step2")
								.param("type", type)
								.param("date", date))
				.andExpect(status().isOk())
				.andExpect(view().name("/main/ledger_writeStep2"))
				.andExpect(model().attributeExists("ledger"))
				.andExpect(model().attribute("ledger", response));

		ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
		verify(ledgerReadService).getWriteStep2Data(
				eq(LedgerType.INCOME),
				dateCaptor.capture()
		);

		assertThat(dateCaptor.getValue()).isEqualTo(LocalDate.now());
	}

	static Stream<Arguments> validDate() {
		return Stream.of(
				Arguments.of("2025010"),	//날짜 길이 다름
				Arguments.of("2025-01-01"), //날짜 형식 다름
				Arguments.of("") //빈 문자열
		);
	}

	@ParameterizedTest(name = "[{index}] type={0}, date={1}")
	@MethodSource("validTypeAndDate")
	@DisplayName("가계부 유형과 날짜 모두 지원하지 않으면 기본값으로 설정된다.")
	void 유형과_날짜_모두_잘못이면_기본값_대체(String type, String date) throws Exception {
		//given
		LedgerWriteStep2Response response = mock(LedgerWriteStep2Response.class);

		when(ledgerReadService.getWriteStep2Data(
				eq(LedgerType.INCOME),
				any(LocalDate.class))
		).thenReturn(response);

		//when & then
		mockMvc.
				perform(
						get("/ledgers/new/step2")
								.param("type", type)
								.param("date", date))
				.andExpect(status().isOk())
				.andExpect(view().name("/main/ledger_writeStep2"))
				.andExpect(model().attributeExists("ledger"))
				.andExpect(model().attribute("ledger", response));

		ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
		verify(ledgerReadService).getWriteStep2Data(
				eq(LedgerType.INCOME),
				dateCaptor.capture()
		);

		assertThat(dateCaptor.getValue()).isEqualTo(LocalDate.now());
	}

	static Stream<Arguments> validTypeAndDate() {
		return Stream.of(
				Arguments.of("", ""), //빈 문자열
				Arguments.of("incomee", "2025010"), //길이 다름
				Arguments.of("out lay", "2025 01 12"),	//띄어쓰기 포함
				Arguments.of("income12", "2025-01-01"), //형식 다름
				Arguments.of("incom#e", "20250101#") //특수문자 포함

		);
	}
}