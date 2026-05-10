package com.moneymanager.integration.controller.ledger;

import com.moneymanager.controller.web.GlobalWebControllerAdvice;
import com.moneymanager.controller.web.ledger.LedgerController;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.dto.response.*;
import com.moneymanager.domain.ledger.enums.*;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.fixture.LedgerResponseFixture;
import com.moneymanager.fixture.history.HistoryDashboardResponseFixture;
import com.moneymanager.fixture.history.HistoryItemFixture;
import com.moneymanager.fixture.ledger.LedgerEditResponseFixture;
import com.moneymanager.security.jwt.JwtAuthenticationFilter;
import com.moneymanager.security.jwt.JwtTokenProvider;
import com.moneymanager.service.ledger.LedgerCommandService;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.service.validation.LedgerValidator;
import org.assertj.core.api.SoftAssertions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.moneymanager.exception.error.ErrorCode.LEDGER_TARGET_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
	private LedgerReadService ledgerReadService;

	@MockBean
	private LedgerCommandService ledgerCommandService;

	@MockBean
	private LedgerValidator ledgerValidator;

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
	@DisplayName("작성 1단계")
	class Step1ViewTest {

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
	@DisplayName("작성 2단계")
	class Step2ViewTest {

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
			LedgerWriteStep2Response response = LedgerResponseFixture.step2Income();    //기본값은 수입
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
	class CreateTest {

		@ParameterizedTest(name = "[{index}] {0}")
		@MethodSource("com.moneymanager.fixture.LedgerRequestFixture#successWriteRequest")
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
			if (value == null) return;

			String stringValue = String.valueOf(value);
			if (stringValue.isBlank()) return;

			params.add(name, stringValue);
		}

	}


	@Nested
	@DisplayName("가계부 내역 조회")
	class HistoryTest {

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


	@Nested
	@DisplayName("가계부 상세 조회")
	class DetailTest {

		@Test
		@DisplayName("유효한 code면  해당 code의 가계부 상세정보를 조회한다.")
		void returnsDetailView_whenCodeIsValid() throws Exception {
			//given
			LedgerDetailResponse response = LedgerDetailResponse.builder()
					.date("날짜")
					.type(CategoryType.INCOME)
					.category(CategoryItem.builder().code("010101").name("월급").build())
					.memo("메모")
					.images(Arrays.asList("image/image1.png", null, null))
					.amount(10000L)
					.paymentType(AmountType.CARD)
					.build();

			when(ledgerReadService.getDetailData(any())).thenReturn(response);

			//when
			mockMvc.perform(
							get("/ledgers/{code}", "code123")
									.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
					)
					.andExpect(status().isOk())
					.andExpect(view().name("/ledger/ledger_detail"))
					.andExpect(model().attributeExists("ledger"))
					.andDo(print());

			//then
			verify(ledgerReadService).getDetailData(eq("code123"));
		}


		@Disabled("TODO: 기능 구현 후 진행 예정")
		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("유효하지 않은 code면 가계부 내역조회 화면으로 리디렉션한다.")
			void redirectsToDashboard_whenCodeIsInvalid() throws Exception {
				//given
				when(ledgerReadService.getDetailData(any()))
						.thenThrow(BusinessException.of(
								LEDGER_TARGET_NOT_FOUND,
								"가계부 조회 실패 발생"
						));

				//when
				mockMvc.perform(
								get("/ledgers/{code}", "code123")
										.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
						)
						.andExpect(status().is3xxRedirection())
						.andExpect(view().name("/ledger/ledger_history"))
						.andExpect(model().attributeExists("history"))
						.andExpect(model().attribute("type", HistoryType.MONTH))
						.andExpect(model().attributeExists("activeMenu"))
						.andDo(print());

				//then
				verify(ledgerReadService).getDetailData(eq("code123"));
			}

			@Test
			@DisplayName("다른 회원의 code면 가계부 내역조회 화면으로 리디렉션한다.")
			void redirectsToDashboard_whenCodeIsOtherMember() throws Exception {
				//given
				when(ledgerReadService.getDetailData(any()))
						.thenThrow(BusinessException.of(
								LEDGER_TARGET_NOT_FOUND,
								"가계부 조회 실패 발생"
						));

				//when
				mockMvc.perform(
								get("/ledgers/{code}", "otherCode")
										.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
						)
						.andExpect(status().is3xxRedirection())
						.andExpect(view().name("/ledger/ledger_history"))
						.andExpect(model().attributeExists("history"))
						.andExpect(model().attribute("type", HistoryType.MONTH))
						.andExpect(model().attributeExists("activeMenu"))
						.andDo(print());

				//then
				verify(ledgerReadService).getDetailData(eq("otherCode"));
			}

		}

	}


	@Nested
	@DisplayName("수정화면 요청")
	class EditTest {

		@Test
		@DisplayName("정상 요청이면 수정 화면과 필요한 정보를 반환한다.")
		void returnsEditViewWithInfo_whenSuccessfully() throws Exception {
			//given: 수정화면에 필요한 응답 데이터 설정
			when(ledgerReadService.getEditData("code123"))
					.thenReturn(LedgerEditResponseFixture.outlay());
			
			//when: 컨트롤러 model 검증
			MvcResult result = mockMvc.perform(
					get("/ledgers/{code}/edit", "code123")
							.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
			)
					.andExpect(status().isOk())
					.andExpect(view().name("/ledger/ledger_edit"))
					.andExpect(model().attributeExists("ledger"))
					.andExpect(model().attribute("fixes", FixedYN.values()))
					.andExpect(model().attribute("fixCycles", FixCycle.values()))
					.andExpect(model().attribute("paymentTypes", AmountType.values()))
					.andReturn();

			//then: 호출 검증
			verify(ledgerReadService).getEditData("code123");

			//html 요소 검증
			Document doc = Jsoup.parse(result.getResponse().getContentAsString());

			SoftAssertions.assertSoftly(soft -> {
				//form
				soft.assertThat(doc.selectFirst("form")).isNotNull();

				//fix
				Elements fixRadios = doc.select("input[type=radio][name=fix]");
				soft.assertThat(fixRadios).hasSize(2);
				soft.assertThat(doc.selectFirst(".form__input-box--option")).isNull();

				//category
				Elements middleOptions = doc.select("select.form__select--middle option");
				soft.assertThat(middleOptions).hasSize(4);

				Elements lowOptions = doc.select("select.form__select--sub option");
				soft.assertThat(lowOptions).hasSize(7);

				//memo
				Element memo = doc.selectFirst("textarea[name=memo]");
				soft.assertThat(memo).isNotNull();
				soft.assertThat(memo.text()).isBlank();

				//amount
				Element amount = doc.selectFirst("input[name=amount]");
				soft.assertThat(amount).isNotNull();
				soft.assertThat(amount.attr("value")).isEqualTo("10000");

				//paymentType
				soft.assertThat(doc.select("input[name=paymentType]")).hasSize(4);

				//image
				Elements imageLabels = doc.select(".form__input-box--photo .form__label");
				soft.assertThat(imageLabels).hasSize(3);

				long iconCount = imageLabels.stream()
								.filter(e -> e.selectFirst("img.icon") != null)
								.count();

				long previewCount = imageLabels.stream()
							.filter(e -> e.selectFirst("img.photo_preview") != null)
							.count();

				soft.assertThat(iconCount).isEqualTo(3);
				soft.assertThat(previewCount).isEqualTo(0);

				//place
				Element place = doc.selectFirst(".form__input-box--empty");
				soft.assertThat(place).isNotNull();

				//button
				soft.assertThat(doc.selectFirst("button.button--danger")).isNull();
				soft.assertThat(doc.select("button.button--medium")).hasSize(2);
			});
		}

		@Test
		@DisplayName("저장된 값에 따라 선택요소가 선택된다.")
		void selectsElement_whenValueExists() throws Exception {
			//given: 수정화면에 필요한 응답 데이터 설정
			when(ledgerReadService.getEditData("code123"))
					.thenReturn(LedgerEditResponseFixture.withFix(FixCycle.MONTHLY));

			//when: 컨트롤러 model 검증
			MvcResult result = mockMvc.perform(
					get("/ledgers/{code}/edit", "code123")
							.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
			)
					.andReturn();
			
			//then: 선택된 요소 검증
			Document doc = Jsoup.parse(result.getResponse().getContentAsString());

			//고정
			Element checkedFix = doc.selectFirst("input[name=fix][checked]");
			assertThat(checkedFix).isNotNull();
			assertThat(checkedFix.attr("value")).isEqualTo("Y");

			//고정주기
			Element checkedCycle = doc.selectFirst("input[name=cycle][checked]");
			assertThat(checkedCycle).isNotNull();
			assertThat(checkedCycle.attr("value")).isEqualTo("M");

			//카테고리
			Element selectedMiddle = doc.selectFirst("select.form__select--middle option[selected]");
			assertThat(selectedMiddle).isNotNull();
			assertThat(selectedMiddle.attr("value")).isEqualTo("010100");

			Element selectedLow = doc.selectFirst("select.form__select--sub option[selected]");
			assertThat(selectedLow).isNotNull();
			assertThat(selectedLow.attr("value")).isEqualTo("010101");

			//금액유형
			Element checkedPaymentType = doc.selectFirst("input[name=paymentType][checked]");
			assertThat(checkedPaymentType).isNotNull();
			assertThat(checkedPaymentType.attr("value")).isEqualTo("NONE");
		}

		@Test
		@DisplayName("이미지 유뮤와 등록 가능한 개수에 따라 이미지를 표시한다.")
		void displaysImagesWithPolicy_whenImagesExist() throws Exception {
			//given: 이미지가 있는 응답 데이터 설정
			when(ledgerReadService.getEditData("code123"))
					.thenReturn(LedgerEditResponseFixture.withImage());

			//when: 컨트롤러 model 검증
			MvcResult result = mockMvc.perform(
							get("/ledgers/{code}/edit", "code123")
									.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
					)
					.andReturn();

			//then: 이미지 요소 검증
			Document doc = Jsoup.parse(result.getResponse().getContentAsString());

			//개수 검증
			Elements imageLabels = doc.select("div.form__input-box--photo label");
			assertThat(imageLabels).hasSize(3);

			//이미지 있는 슬롯 검증
			Element uploadSlot = imageLabels.get(0);
			assertThat(uploadSlot.selectFirst("input[type=file]")).isNotNull();
			assertThat(uploadSlot.selectFirst("img.photo_preview")).isNotNull();
			assertThat(uploadSlot.selectFirst("button")).isNotNull();
			assertThat(uploadSlot.attr("src")).isEqualTo("/upload/ledger/test.png");
			
			//등록 가능한 슬롯
			Element unlockSlot = imageLabels.get(1);
			assertThat(unlockSlot.selectFirst("input[type=file]")).isNotNull();
			assertThat(unlockSlot.selectFirst("img.icon")).isNotNull();
			assertThat(unlockSlot.selectFirst("button")).isNull();
			assertThat(unlockSlot.attr("src")).isEqualTo("/image/ledger/test.png");

			//등록 불가능한 슬롯
			Element lockSlot = imageLabels.get(2);
			assertThat(lockSlot.selectFirst("input[type=file]")).isNull();
			assertThat(lockSlot.selectFirst("img.icon")).isNotNull();
			assertThat(lockSlot.selectFirst("button")).isNull();
		}

		@Test
		@DisplayName("장소가 저정되어 있으면 저장된 장소가 표시된다.")
		void displaysLocation_whenLocationExists() throws Exception {
			//given: 장소가 있는 응답 데이터 설정
			when(ledgerReadService.getEditData("code123"))
					.thenReturn(LedgerEditResponseFixture.withPlace());

			//when: 컨트롤러 model 검증
			MvcResult result = mockMvc.perform(
							get("/ledgers/{code}/edit", "code123")
									.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
					)
					.andReturn();

			//then: 장소 검증
			Document doc = Jsoup.parse(result.getResponse().getContentAsString());

			Element placeArea = doc.selectFirst(".form__input-container--location");
			assertThat(placeArea).isNotNull();

			//장소 문자 검증
			Element placeInfo = placeArea.selectFirst(".form__input-box--filled");

			String placeName = placeInfo.selectFirst(".location__name").text();
			String roadAddress = placeInfo.selectFirst(".location__road").text();
			String detailAddress = placeInfo.selectFirst(".location__add").text();

			assertThat(placeName).isEqualTo("CGV 강남점");
			assertThat(roadAddress).contains("서울특별시 강남구");
			assertThat(detailAddress).isEqualTo("4층");

			//svg 존재 검증
			Element deleteIcon = placeArea.selectFirst("svg.icon--del");
			assertThat(deleteIcon).isNotNull();
		}

		@Test
		@DisplayName("존재하지 않은 가계부 코드로 요청하면 내역조회 화면으로 리디렉션 한다.")
		void redirectsToHistoryView_whenResourceNotFound() throws Exception {
			//given: 예외 설정
			String code = "noCode";
			when(ledgerReadService.getEditData(code)).thenThrow(BusinessException.class);

			//when: 컨트롤러 model 검증
			mockMvc.perform(
					get("/ledgers/{code}/edit", code)
							.cookie(new Cookie("ACCESS_TOKEN", "test-token"))
			)
					.andExpect(status().is3xxRedirection())
					.andExpect(view().name("/ledger/ledger_history"));
		}

	}

}