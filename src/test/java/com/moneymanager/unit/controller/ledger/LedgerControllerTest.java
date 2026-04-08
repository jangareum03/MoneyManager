package com.moneymanager.unit.controller.ledger;

import com.moneymanager.controller.web.ledger.LedgerController;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep1Response;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep2Response;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.security.jwt.JwtAuthenticationFilter;
import com.moneymanager.security.jwt.JwtTokenProvider;
import com.moneymanager.service.ledger.LedgerCommandService;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.service.validation.LedgerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
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
	@MockBean	private LedgerCommandService	ledgerCommandService;
	@MockBean	private LedgerValidator	ledgerValidator;

	@MockBean	private JwtTokenProvider jwtTokenProvider;
	@MockBean	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@BeforeEach
	void setUp(@Autowired LedgerController controller){
		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.build();
	}

	//==================[ step1 ]==================
	@Test
	@DisplayName("정상 요청이면 가계부 작성 1단계 화면을 반환한다.")
	void step1_validRequest_returnStep1View() throws Exception {
		//given
		LedgerWriteStep1Response mockResponse = mock(LedgerWriteStep1Response.class);
		when(ledgerReadService.getWriteStep1Data()).thenReturn(mockResponse);

		//when & then
		mockMvc.perform(get("/ledgers/new/step1"))
				.andExpect(status().isOk())
				.andExpect(view().name("/ledger/ledger_writeStep1"))
				.andExpect(model().attributeExists("ledger"))
				.andExpect(model().attribute("ledger", mockResponse));
	}


	//==================[ step2 ]==================
	@ParameterizedTest(name = "[{index}] type={0}, date={1}")
	@MethodSource("provideValidTypeAndDate")
	@DisplayName("정상 요청이면 가계부 2단계 작성 화면을 반환한다.")
	void step2_validRequest_returnStep2View(String type, String date, CategoryType categoryType, LocalDate localDate) throws Exception {
		//given
		LedgerWriteStep2Response mockResponse = mock(LedgerWriteStep2Response.class);
		when(ledgerReadService.getWriteStep2Data(any(CategoryType.class), any(LocalDate.class)))
				.thenReturn(mockResponse);

		//when
		mockMvc.perform(
				get("/ledgers/new/step2")
						.param("type", type)
						.param("date", date))
				.andExpect(status().isOk())
				.andExpect(view().name("/ledger/ledger_writeStep2"));

		//then
		verify(ledgerReadService).getWriteStep2Data(categoryType, localDate);
	}

	static Stream<Arguments> provideValidTypeAndDate() {
		return Stream.of(
				Arguments.of("income", "20260101", CategoryType.INCOME, LocalDate.of(2026, 1, 1)),
				Arguments.of("outlay", "20260101", CategoryType.OUTLAY, LocalDate.of(2026, 1, 1))
		);
	}


	@Test
	@DisplayName("가계부 유형과 거래날짜가 비정상이면 기본값으로 변환되어 서비스를 호출한다.")
	void step2_invalidRequest_callsServiceWithDefaultValues() throws Exception {
		//given
		LedgerWriteStep2Response mockResponse = mock(LedgerWriteStep2Response.class);
		when(ledgerReadService.getWriteStep2Data(any(), any()))
				.thenReturn(mockResponse);

		//when
		mockMvc.perform(get("/ledgers/new/step2")
				.param("type", "invalid")
				.param("date", "wrong-date"));

		//then
		verify(ledgerReadService).getWriteStep2Data(CategoryType.INCOME, LocalDate.now());
	}


	//==================[ createLedger ]==================
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("com.moneymanager.unit.fixture.LedgerRequestFixture#successWriteRequest")
	@DisplayName("정상 요청이면 가계부를 저장하고 가계부 목록 화면으로 리다이렉트한다.")
	void createLedger_validRequest_saveLedgerAndRedirectLedgerList(String description, LedgerWriteRequest request) throws Exception {
		//when
		mockMvc.perform(post("/ledgers")
				.params(toParams(request)))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/ledgers"));

		//then
		verify(ledgerValidator).register(any(LedgerWriteRequest.class));
		verify(ledgerCommandService).registerLedger(any(LedgerWriteRequest.class));
	}

	private MultiValueMap<String, String> toParams(LedgerWriteRequest request) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		//필수값
		addParam(params, "date", request.getDate());
		addParam(params, "categoryCode", request.getCategoryCode());
		addParam(params, "fixed", request.isFixed());
		addParam(params, "amount", request.getAmount());
		addParam(params, "amountType", request.getAmountType());

		//선택값
		addParam(params, "fixCycle", request.getFixCycle());
		addParam(params, "memo", request.getMemo());
		addParam(params, "placeName", request.getPlaceName());
		addParam(params, "roadAddress", request.getRoadAddress());
		addParam(params, "detailAddress", request.getDetailAddress());

		return params;
	}

	private void addParam(MultiValueMap<String, String> params, String name, Object value) {
		if(value == null) return;

		String stringValue = String.valueOf(value);
		if(stringValue.isBlank()) return;

		params.add(name, stringValue);
	}


	@Disabled
	@Test
	@DisplayName("가계부 검증이 실패하면 입력값을 유지한 채 가계부 2단계 작성 화면을 반환한다.")
	void createLedger_validatorFails_throwsException() throws Exception {
		//given
		doThrow(BusinessException.class)
				.when(ledgerValidator)
				.register(any(LedgerWriteRequest.class));

		//when
		mockMvc.perform(post("/ledgers")
								.param("date", "")
								.param("amount", "10000"))
				.andExpect(status().isOk())
				.andExpect(view().name("/ledger/ledger_writeStep2"))
						.andExpect(model().attributeExists("ledger"));

		//then
		verify(ledgerCommandService, never()).registerLedger(any(LedgerWriteRequest.class));
	}


	@Disabled
	@Test
	@DisplayName("가계부 등록 중 문제가 발생하면 입력값을 유지한 채 가계부 2단계 작성 화면으로 이동한다.")
	void createLedger_serviceError_throwException() throws Exception {
		//given
		doThrow(BusinessException.class)
				.when(ledgerCommandService)
				.registerLedger(any(LedgerWriteRequest.class));

		//when
		mockMvc.perform(post("/ledgers")
						.param("date", "20260101")
						.param("amount", "10000"))
				.andExpect(status().isOk())
				.andExpect(view().name("/ledger/ledger_writeStep2"))
				.andExpect(model().attributeExists("ledger"));

		//then
		verify(ledgerValidator).register(any());
		verify(ledgerCommandService).registerLedger(any());
	}
}