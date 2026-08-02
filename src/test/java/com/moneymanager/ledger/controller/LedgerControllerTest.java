package com.moneymanager.ledger.controller;

import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.response.HistoryDashboardResponse;
import com.moneymanager.ledger.domain.dto.response.LedgerDetailResponse;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep1Response;
import com.moneymanager.ledger.domain.enums.CategoryType;
import com.moneymanager.ledger.domain.enums.HistoryMenuType;
import com.moneymanager.ledger.domain.enums.HistoryType;
import com.moneymanager.ledger.service.command.LedgerCommandService;
import com.moneymanager.ledger.service.read.LedgerReadService;
import com.moneymanager.ledger.service.validator.LedgerValidator;
import com.moneymanager.support.ControllerTestSupport;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import com.moneymanager.support.fixture.response.HistoryDashboardResponseFixture;
import com.moneymanager.support.fixture.response.LedgerDetailResponseFixture;
import com.moneymanager.support.fixture.response.LedgerWriteStep1ResponseFixture;
import com.moneymanager.support.fixture.response.LedgerWriteStep2ResponseFixture;
import com.moneymanager.support.security.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.controller<br>
 * 파일이름       : LedgerControllerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 16<br>
 * 설명              : LedgerController 클래스 요청을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 7. 16</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@WebMvcTest(LedgerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LedgerControllerTest extends ControllerTestSupport {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LedgerReadService readService;

	@MockBean
	private LedgerCommandService commandService;

	@MockBean
	private LedgerValidator validator;

	private static final String BASE_URI = "/ledgers";


	@Nested
	@DisplayName("작성 1단계")
	@WithMockCustomUser
	class Step1ViewTest {

		private static final String URI = BASE_URI + "/new/step1";

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("가계부 작성 1단계 페이지를 반환한다.")
			@WithMockCustomUser
			void returnView_whenGetRequestIsGiven() throws Exception {
				//given: 화면에 필요한 데이터를 조회하는 동작이 정의되어 있다.
				LedgerWriteStep1Response response = LedgerWriteStep1ResponseFixture.create();

				when(readService.getWriteStep1Data()).thenReturn(response);

				//when: 가계부 작성 1단계 페이지를 요청한다.
				mockMvc.perform(
						get(URI)
				)
						.andExpect(status().isOk())
						.andExpect(model().attribute("ledger", response))
						.andExpect(view().name("/ledger/ledger_writeStep1"));

				//then: 서비스를 호출한다.
				verify(readService).getWriteStep1Data();
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest
			@ValueSource(strings = {"POST", "DELETE", "PUT"})
			@DisplayName("지원하지 않은 HTTP Method 요청 시 405를 반환한다.")
			void rejectsRequestWithStatusMethodNotAllowed_whenInvalidHttpMethodIsGiven(String method) throws Exception {
				mockMvc.perform(
					MockMvcRequestBuilders.request(HttpMethod.valueOf(method), URI)
				)
						.andExpect(status().isMethodNotAllowed());
			}

		}

	}


	@Nested
	@DisplayName("작성 2단계")
	class Step2ViewTest {

		private final String URI = BASE_URI + "/new/step2";

		@Nested
		@DisplayName("성공 케이스")
		@WithMockCustomUser
		class Success {

			@ParameterizedTest
			@MethodSource("validLedgerTypes")
			@DisplayName("유효한 요청 파라이터가 전달되면 Step2 페이지를 반환한다.")
			void returnsStep2Page_whenRequestIsValid(String paramType, CategoryType type) throws Exception {
				//given: 정상적인 가계부 유형과 거래날짜가 주어진다.
				String date = "20260101";

				when(readService.getWriteStep2Data(any(), any()))
						.thenReturn(LedgerWriteStep2ResponseFixture.create());

				//when: 가계부 작성 2단계 페이지를 요청한다.
				mockMvc.perform(
						get(URI)
								.param("type", paramType)
								.param("date", date)
				)
						.andExpect(status().isOk())
						.andExpect(model().attributeExists("ledger"))
						.andExpect(view().name("/ledger/ledger_writeStep2"));

				//then: 서비스에 요청 파라미터가 전송된다.
				verify(readService).getWriteStep2Data(eq(type), eq(LocalDate.of(2026, 1, 1)));
			}

			static Stream<Arguments> validLedgerTypes() {
				return Stream.of(
						Arguments.of(
								named("수입 유형인 경우", "income"),
								CategoryType.INCOME
						),
						Arguments.of(
								named("지출 유형인 경우", "outlay"),
								CategoryType.OUTLAY
						)
				);
			}

			@Test
			@DisplayName("유효하지 않은 가계부 유형이 전달되면 수입 유형으로 변환되어 요청된다.")
			void fetchesAsIncomeType_whenLedgerTypeIsInvalid() throws Exception {
				//given: 유효하지 않은 가계부 유형이 주어진다.
				String type = "error";
				String date = "20260101";

				when(readService.getWriteStep2Data(any(), any()))
						.thenReturn(LedgerWriteStep2ResponseFixture.create());

				//when: 가계부 작성 2단계 페이지를 요청한다.
				mockMvc.perform(
						get(URI)
								.param("type", type)
								.param("date", date)
				)
						.andExpect(status().isOk());

				//then: 서비스에 수입 유형이 전송된다.
				verify(readService).getWriteStep2Data(eq(CategoryType.INCOME), any());
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("가계부 유형이 빈 문자열이면 수입 유형으로 변환되어 요청된다.")
			void fetchesAsIncomeType_whenLedgerTypeIsEmpty(String type) throws Exception {
				//given: 정상적인 가계부 날짜가 주어진다.
				String date = "20260101";

				when(readService.getWriteStep2Data(any(), any()))
						.thenReturn(LedgerWriteStep2ResponseFixture.create());

				//when: 가계부 작성 2단계 페이지를 요청한다.
				mockMvc.perform(
								get(URI)
										.param("type", type)
										.param("date", date)
						)
						.andExpect(status().isOk());

				//then: 서비스에 수입 유형이 전송된다.
				verify(readService).getWriteStep2Data(eq(CategoryType.INCOME), any());
			}

		}

	}


	@Nested
	@DisplayName("가계부 등록")
	@WithMockCustomUser
	class Create {

		private final String URI = BASE_URI;
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@Test
			@DisplayName("유효한 가계부 등록 요청이면 가계부 목록 페이지로 리다이렉트 한다.")
			void returnsRedirectToLedgerList_whenRequestIsValid() throws Exception {
				//given: 가계부 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.create();

				doNothing().when(validator).register(any());
				doNothing().when(commandService).register(any());

				//when: 가계부 등록을 요청한다.
				mockMvc.perform(
						post(URI)
								.flashAttr("ledger", request)
				)
						.andExpect(status().is3xxRedirection())
						.andExpect(redirectedUrl("/ledgers"));
			}
			
			@Test
			@DisplayName("가계부 등록 검증이 완료되면 가계부 등록 서비스를 호출한다.")
			void createsLedger_whenValidationSucceeds() throws Exception {
				//given: 가계부 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.create();

				doNothing().when(validator).register(any());
				doNothing().when(commandService).register(any());

				//when: 가계부 등록을 요청한다.
				mockMvc.perform(
								post(URI)
										.flashAttr("ledger", request)
						)
						.andExpect(status().is3xxRedirection());

				//then: 검증 및 서비스가 호출된다.
				verify(validator).register(any(LedgerWriteRequest.class));
				verify(commandService).register(any(LedgerWriteRequest.class));
			}
			
			@Test
			@DisplayName("가계부 등록 검증을 수행한 후 가계부 등록 서비스를 호출한다.")
			void createsLedger_whenValidatesDataRange() throws Exception {
				//given: 가계부 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.create();

				doNothing().when(validator).register(any());
				doNothing().when(commandService).register(any());

				//when: 가계부 등록을 요청한다.
				mockMvc.perform(
								post(URI)
										.flashAttr("ledger", request)
						)
						.andExpect(status().is3xxRedirection());

				//then: 검증 및 서비스가 순서대로 호출된다.
				InOrder order = inOrder(validator, commandService);

				order.verify(validator).register(any(LedgerWriteRequest.class));
				order.verify(commandService).register(any(LedgerWriteRequest.class));
			}
		
		}

	}


	@Nested
	@DisplayName("가계부 내역 조회")
	@WithMockCustomUser
	class GetHistories {

		private final String URI = BASE_URI;

		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@ParameterizedTest
			@ValueSource(strings = {"week", "month", "Year"})
			@DisplayName("정상적인 viewType이면 해당 타입으로 서비스가 호출된다.")
			void fetchesData_whenViewTypeIsValid(String type) throws Exception {
				//given: 정상적인 응답 객체가 반환되도록 Service의 동작이 정의되어 있다.
				when(readService.getHistoryDashboard(any(HistoryType.class)))
						.thenReturn(HistoryDashboardResponseFixture.create());
				
				//when: 가계부 내역 조회를 요청한다.
				mockMvc.perform(
						get(URI)
								.param("viewType", type)
				)
						.andExpect(status().isOk());

				//then: LedgerReadService에서 내역조회 메서드가 호출된다.
				switch (type) {
					case "week" -> verify(readService).getHistoryDashboard(HistoryType.WEEK);
					case "month" -> verify(readService).getHistoryDashboard(HistoryType.MONTH);
					case "year" -> verify(readService).getHistoryDashboard(HistoryType.YEAR);
				}
			}

			@Test
			@DisplayName("잘못된 viewType이면 기본 타입으로  변환된다.")
			void returnsDefaultType_whenViewTypeIsInvalid() throws Exception {
				//given: 잘못된 viewType과 정상적인 응답 객체가 반환되도록 동작이 되어 있다.
				String viewType = "error";

				when(readService.getHistoryDashboard(any(HistoryType.class)))
						.thenReturn(HistoryDashboardResponseFixture.create());

				//when: 가계부 내역 조회를 요청한다.
				mockMvc.perform(
						get(URI)
								.param("viewType", viewType)
				)
						.andExpect(status().isOk());
				
				//then: HistoryType은 MONTH로 변환된다.
				verify(readService).getHistoryDashboard(HistoryType.MONTH);
			}
			
			@ParameterizedTest
			@NullSource
			@DisplayName("viewType이 null이면 기본 타입으로 변환된다.")
			void returnsDefaultType_whenViewTypeIsNull(String viewType) throws Exception {
				//given: 응답 객체가 반환되도록 동작이 되어 있다.
				when(readService.getHistoryDashboard(any(HistoryType.class)))
						.thenReturn(HistoryDashboardResponseFixture.create());

				//when: 가계부 내역 조회를 요청한다.
				mockMvc.perform(
						get(URI)
								.param("viewType", viewType)
				)
						.andExpect(status().isOk());

				//then: HistoryType은 MONTH로 변환된다.
				verify(readService).getHistoryDashboard(HistoryType.MONTH);
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("viewType이 빈 문자열이면 기본 타입으로 변환된다.")
			void returnsDefaultType_whenViewTypeIsEmpty(String viewType) throws Exception {
				//given: 응답 객체가 반환되도록 동작이 되어 있다.
				when(readService.getHistoryDashboard(any(HistoryType.class)))
						.thenReturn(HistoryDashboardResponseFixture.create());

				//when: 가계부 내역 조회를 요청한다.
				mockMvc.perform(
								get(URI)
										.param("viewType", viewType)
						)
						.andExpect(status().isOk());

				//then: HistoryType은 MONTH로 변환된다.
				verify(readService).getHistoryDashboard(HistoryType.MONTH);
			}
			
			@Test
			@DisplayName("페이지에 필요한 Model 속성을 저장한다.")
			void savesModelAttributes_whenPageIsRequested() throws Exception {
				//given: 응답 객체가 반환되도록 동작이 되어 있다.
				HistoryDashboardResponse response = HistoryDashboardResponseFixture.create();

				when(readService.getHistoryDashboard(HistoryType.MONTH))
						.thenReturn(response);

				//when: 가계부 내역 조회를 요청한다.
				mockMvc.perform(
								get(URI)
										.param("viewType", "month")
						)
						.andExpect(status().isOk())
						.andExpect(model().attribute("history", response))
						.andExpect(model().attribute("type", HistoryType.MONTH))
						.andExpect(model().attribute("activeMenu", HistoryMenuType.ALL.name()));
			}
			
			@Test
			@DisplayName("가계부 목록 페이지를 반환한다.")
			void returnsLedgerListPage_whenRequestIsValid() throws Exception {
				//given: 응답 객체가 반환되도록 동작이 되어 있다.
				HistoryDashboardResponse response = HistoryDashboardResponseFixture.create();

				when(readService.getHistoryDashboard(HistoryType.YEAR))
						.thenReturn(response);

				//when: 가계부 내역 조회를 요청한다.
				mockMvc.perform(
								get(URI)
										.param("viewType", "year")
						)
						.andExpect(status().isOk())
						.andExpect(view().name("/ledger/ledger_history"));
			}

		}

	}


	@Nested
	@DisplayName("가계부 상세 조회")
	@WithMockCustomUser
	class GetDetail {

		private final String URI = BASE_URI + "/{code}";

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("가계부 상세 조회를 요청하면 상세 페이지를 반환한다.")
			void returnsLedgerDetailPage_whenLedgerDetailIsRequested() throws Exception {
				//given: 정상적인 응답 객체가 반환되도록 Service의 동작이 정의되어 있다.
				String code = LedgerTestData.CODE;
				LedgerDetailResponse response = LedgerDetailResponseFixture.create();

				when(readService.getDetailData(code))
						.thenReturn(response);
				
				//when: 가계부 상세 조회를 요청한다.
				mockMvc.perform(
						get(URI, code)
				)
					.andExpect(status().isOk())
					.andExpect(model().attribute("ledger", response))
					.andExpect(view().name("/ledger/ledger_detail"));
				
				//then:
				verify(readService).getDetailData(eq(code));
			}

		}

	}

}