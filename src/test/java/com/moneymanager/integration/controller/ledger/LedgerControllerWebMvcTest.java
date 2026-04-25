package com.moneymanager.integration.controller.ledger;

import com.moneymanager.controller.web.GlobalWebControllerAdvice;
import com.moneymanager.controller.web.ledger.LedgerController;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.dto.response.*;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.domain.ledger.enums.HistoryType;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.unit.fixture.ledger.HistoryDashboardResponseFixture;
import com.moneymanager.security.jwt.JwtAuthenticationFilter;
import com.moneymanager.security.jwt.JwtTokenProvider;
import com.moneymanager.service.ledger.LedgerCommandService;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.service.validation.LedgerValidator;
import com.moneymanager.unit.fixture.LedgerResponseFixture;
import com.moneymanager.unit.fixture.ledger.HistoryItemFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.controller.ledger<br>
 * 파일이름       : LedgerControllerWebMvcTest<br>
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
@Import(GlobalWebControllerAdvice.class)
public class LedgerControllerWebMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LedgerReadService	ledgerReadService;

	@MockBean
	private LedgerCommandService	ledgerCommandService;

	@MockBean
	private LedgerValidator	ledgerValidator;

	@MockBean
	private JwtTokenProvider jwtTokenProvider;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@BeforeEach
	void setUp() {
		when(jwtTokenProvider.validateToken("test-token")).thenReturn(true);
		when(jwtTokenProvider.getNickName("test-token")).thenReturn("테스트 닉네임");
		when(jwtTokenProvider.getProfile("test-token")).thenReturn(null);
	}

	@Nested
	@DisplayName("step1 화면 조회")
	class Step1Test {

		@Test
		@DisplayName("정상 요청이면 가계부 작성 1단계 화면을 반환한다.")
		void returnsStep1View_whenRequestIsValid() throws Exception {
			//given
			LedgerWriteStep1Response response = LedgerResponseFixture.step1();

			when(ledgerReadService.getWriteStep1Data()).thenReturn(response);

			//when & then
			mockMvc.perform(
						get("/ledgers/new/step1")
								.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
					)
					.andExpect(status().isOk())
					.andExpect(view().name("/ledger/ledger_writeStep1"))
					.andExpect(model().attributeExists("ledger"))
					.andExpect(model().attributeExists("member"))
					.andExpect(model().attribute("ledger", response));
		}
	}


	@Nested
	@DisplayName("step2 화면 조회")
	class Step2Test {

		@ParameterizedTest(name = "[{index}] type={0}, date={1}")
		@MethodSource("provideValidTypeAndDate")
		@DisplayName("정상 요청이면 가계부 2단계 작성 화면을 반환한다.")
		void returnsStep2View_whenRequestIsValid(String type, String date, CategoryType categoryType, LocalDate localDate) throws Exception {
			//given
			LedgerWriteStep2Response response = LedgerResponseFixture.step2(type);
			when(ledgerReadService.getWriteStep2Data(any(CategoryType.class), any(LocalDate.class)))
					.thenReturn(response);

			//when
			mockMvc.perform(
							get("/ledgers/new/step2")
									.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
									.param("type", type)
									.param("date", date))
					.andExpect(status().isOk())
					.andExpect(model().attributeExists("member"))
					.andExpect(model().attributeExists("ledger"))
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
		@DisplayName("잘못된 요청이면 기본값으로 변환되어 서비스를 호출한다.")
		void returnsStep2View_whenTypeAndDateAreInvalid() throws Exception {
			//given
			LedgerWriteStep2Response response = LedgerResponseFixture.step2Income();	//기본값은 수입
			when(ledgerReadService.getWriteStep2Data(any(CategoryType.class), any(LocalDate.class)))
					.thenReturn(response);

			//when & then
			mockMvc.perform(get("/ledgers/new/step2")
							.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
							.param("type", "invalid")
							.param("date", "wrong-date"))
					.andExpect(status().isOk())
					.andExpect(model().attributeExists("member"))
					.andExpect(model().attributeExists("ledger"))
					.andExpect(view().name("/ledger/ledger_writeStep2"));

			verify(ledgerReadService).getWriteStep2Data(CategoryType.INCOME, LocalDate.now());
		}

	}


	@Nested
	@DisplayName("가계부 등록")
	class CreateLedgerTest {

		@ParameterizedTest(name = "[{index}] {0}")
		@MethodSource("com.moneymanager.unit.fixture.LedgerRequestFixture#successWriteRequest")
		@DisplayName("정상 요청이면 가계부를 저장하고 가계부 목록 화면으로 리다이렉트한다.")
		void redirectsToLedgerHistories_whenRequestIsValid(String description, LedgerWriteRequest request) throws Exception {
			//when
			mockMvc.perform(post("/ledgers")
							.params(toParams(request)))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/ledgers"));

			//then
			verify(ledgerValidator).register(any(LedgerWriteRequest.class));
			verify(ledgerCommandService).registerLedger(any(LedgerWriteRequest.class));
		}

		@Disabled("TODO: 로직 변경 후 진행 예정")
		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("검증 실패하면 입력값을 유지한 채 가계부 2단계 작성 화면을 반환한다.")
			void returnsStep2ViewWithInput_whenValidationFails() throws Exception {
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

			@Test
			@DisplayName("등록 중 문제가 발생하면 입력값을 유지한 채 가계부 2단계 작성 화면으로 이동한다.")
			void returnsStep2ViewWithInput_whenRegisterFails() throws Exception {
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

	}


	@Nested
	@DisplayName("가계부 내역 조회")
	class LedgerHistoryTest {

		private final String viewName = "/ledger/ledger_history";

		@Test
		@DisplayName("viewType 없으면 기본값 MONTH로 내역을 조회한다.")
		void returnsHistoryView_whenViewTypeIsMissing() throws Exception {
			//given
			HistoryDashboardResponse response = HistoryDashboardResponseFixture.defaultFixture().toResponse();

			when(ledgerReadService.getHistoryDashboard(HistoryType.MONTH))
					.thenReturn(response);

			//when
			mockMvc.perform(
					get("/ledgers")
							.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
			)
					.andExpect(status().isOk())
					.andExpect(view().name(viewName))
					.andExpect(model().attributeExists("history"))
					.andDo(print());

			//then
			verify(ledgerReadService).getHistoryDashboard(HistoryType.MONTH);
		}

		@ParameterizedTest(name = "[{index}] type={0}")
		@EnumSource(HistoryType.class)
		@DisplayName("유효한 viewType이 있으면 해당 타입으로 내역을 조회한다.")
		void returnsHistoryView_whenViewTypeIsValid(HistoryType type) throws Exception {
			//given
			HistoryDashboardResponse response = HistoryDashboardResponseFixture.defaultFixture().toResponse();

			when(ledgerReadService.getHistoryDashboard(type)).thenReturn(response);

			//when
			mockMvc.perform(
					get("/ledgers")
							.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
							.param("viewType", type.name())
			)
					.andExpect(status().isOk())
					.andExpect(view().name(viewName))
					.andExpect(model().attribute("history", response))
					.andExpect(model().attributeExists("type"))
					.andExpect(model().attributeExists("activeMenu")) 
					.andDo(print());

			verify(ledgerReadService).getHistoryDashboard(type);
		}

		@ParameterizedTest(name = "[{index}] type={0}")
		@ValueSource(strings = {"m", "aa"})
		@DisplayName("유효하지 않은 viewType이면 MONTH로 내역을 조회한다.")
		void returnsHistoryView_whenViewTypeIsInvalid(String type) throws Exception {
			//given
			HistoryDashboardResponse response = HistoryDashboardResponseFixture.defaultFixture().toResponse();

			when(ledgerReadService.getHistoryDashboard(HistoryType.MONTH)).thenReturn(response);

			//when
			mockMvc.perform(
							get("/ledgers")
									.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
									.param("viewType", type)
					)
					.andExpect(status().isOk())
					.andExpect(view().name(viewName))
					.andExpect(model().attribute("history", response))
					.andExpect(model().attributeExists("type"))
					.andExpect(model().attributeExists("activeMenu"))
					.andDo(print());

			verify(ledgerReadService).getHistoryDashboard(HistoryType.MONTH);
		}

		@Test
		@DisplayName("내역 결과를 모델의 history 속성에 담는다.")
		void returnsModel_whenRequestIsValid() throws Exception {
			//given
			HistoryType type = HistoryType.YEAR;

			LocalDate key1 = LocalDate.of(2026, 1, 10);
			LocalDate key2 = LocalDate.of(2026, 2, 21);

			Map<LocalDate, List<HistoryItem>> data =
					Map.of(
							key1,
							List.of(
									HistoryItemFixture.defaultFixture().toHistoryItem(),
									HistoryItemFixture.defaultFixture().toBuilder().categoryCode("020101").categoryName("식비").amount("25000").build().toHistoryItem()
							)
					);

			HistoryDashboardResponse response = HistoryDashboardResponseFixture.defaultFixture()
					.toBuilder()
					.title("2026년")
					.statistics(LedgerStatistics.of(10000L, 500L))
					.data(data)
					.build().toResponse();

			when(ledgerReadService.getHistoryDashboard(type)).thenReturn(response);

			//when
			mockMvc.perform(
					get("/ledgers")
							.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
							.param("viewType", type.name())
			)
					.andExpect(status().isOk())
					.andExpect(view().name("/ledger/ledger_history"))
					.andExpect(model().attribute("history", response))
					.andExpect(model().attributeExists("type"))
					.andExpect(model().attributeExists("activeMenu"))
					.andDo(print());

			verify(ledgerReadService).getHistoryDashboard(HistoryType.YEAR);
		}


	}

}