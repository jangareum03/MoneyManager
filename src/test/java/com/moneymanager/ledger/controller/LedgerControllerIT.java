package com.moneymanager.ledger.controller;

import com.moneymanager.global.config.MutableClock;
import com.moneymanager.delete.domain.member.Member;
import com.moneymanager.delete.repository.member.MemberRepository;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.response.*;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.CategoryType;
import com.moneymanager.ledger.domain.enums.HistoryMenuType;
import com.moneymanager.ledger.domain.enums.HistoryType;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.repository.LedgerRepository;
import com.moneymanager.ledger.service.command.LedgerCommandService;
import com.moneymanager.ledger.service.read.LedgerReadService;
import com.moneymanager.ledger.service.validation.LedgerValidator;
import com.moneymanager.support.IntegrationTestSupport;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import com.moneymanager.support.fixture.entity.MemberFixture;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import com.moneymanager.support.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Named.named;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.controller<br>
 * 파일이름       : LedgerControllerIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 16<br>
 * 설명              : LedgerController 클래스 요청을 검증하는 통합 테스트 클래스
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
@AutoConfigureMockMvc
public class LedgerControllerIT extends IntegrationTestSupport {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LedgerReadService readService;

	@Autowired
	private LedgerCommandService commandService;

	@Autowired
	private LedgerValidator validator;

	@Autowired
	private MutableClock clock;

	@Nested
	@DisplayName("작성 1단계")
	class Step1ViewTest {

		private final String URI = "/ledgers/new/step1";

		@Nested
		@DisplayName("성공 케이스")
		@WithMockCustomUser
		class Success {
		
			@Test
			@DisplayName("현재 날짜 기준 가계부 작성 페이지를 조회한다.")
			void returnsLedgerWritePage_whenCurrentDateIsGiven() throws Exception {
				mockMvc.perform(
						get(URI)
				)
						.andExpect(status().isOk())
						.andExpect(view().name("/ledger/ledger_writeStep1"))
						.andExpect(model().attributeExists("ledger"));

			}

		}

	}


	@Nested
	@DisplayName("작성 2단계")
	@WithMockCustomUser
	class Step2ViewTest {

		private final String URL = "/ledgers/new/step2";

		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@Test
			@DisplayName("유효한 요청 파라미터가 전달되면 Step2 페이지를 반환한다.")
			void returnsStep2Page_whenRequestIsValid() throws Exception {
				//given: 정상적인 가계부 유형과 거래날짜가 주어진다.
				String type = "income";
				String date = "20260101";

				//when: 가계부 작성 2단계 페이지를 요청한다.
				MvcResult result = mockMvc.perform(
						get(URL)
								.param("type", type)
								.param("date", date)
				)
						.andExpect(status().isOk())
						.andExpect(model().attributeExists("ledger"))
						.andExpect(view().name("/ledger/ledger_writeStep2"))
						.andReturn();

				//then: model에 저장된 데이터가 저장된다.
				ModelAndView mav = result.getModelAndView();

				assertThat(mav).isNotNull();

				LedgerWriteStep2Response ledger = (LedgerWriteStep2Response) mav.getModel().get("ledger");

				assertThat(ledger).isNotNull();
				assertThat(ledger.getTitle()).isEqualTo("2026년 01월 01일 목요일");
				assertThat(ledger.getType()).isEqualTo(CategoryType.INCOME);
				assertThat(ledger)
						.extracting(
								LedgerWriteStep2Response::getFixed,
								LedgerWriteStep2Response::getCategories,
								LedgerWriteStep2Response::getPaymentTypes,
								LedgerWriteStep2Response::getImageSlot
						).isNotNull();
			}
		
		}

	}


	@Nested
	@DisplayName("가계부 등록")
	@WithMockCustomUser
	class CreateTest {

		@Autowired
		private LedgerRepository ledgerRepository;

		@Autowired
		private MemberRepository memberRepository;

		private final String URI = "/ledgers";

		@BeforeEach
		void setUp() {
			memberRepository.save(MemberFixture.builder(MemberTestData.MEMBER_ID).build());
		}
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@Test
			@DisplayName("유효한 가계부 등록 요청히면 302 리다이렉트 응답을 반환한다.")
			void returns302Status_whenRequestIsValid() throws Exception {
				//given: 등록 가능한 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.create();
				
				//when: 가계부 등록을 요청한다.
				mockMvc.perform(
						post(URI)
								.flashAttr("ledger", request)
				)
						.andExpect(status().is3xxRedirection())
						.andExpect(redirectedUrl("/ledgers"));
				
				//then: DB에 신규 가계부가 저장된다.
				List<Ledger> ledgers = ledgerRepository.findAll();

				assertThat(ledgers).hasSize(1);

				Ledger ledger = ledgers.get(0);
				assertThat(ledger.getDate()).isEqualTo(LedgerTestData.LOCAL_DATE);
				assertThat(ledger.getCategory()).isEqualTo(request.getCategoryCode());
				assertThat(ledger.getMoney()).isEqualTo(Money.of(request.getAmount(), PaymentType.valueOf(request.getPaymentType())));
			}
		
		}
		
	}


	@Nested
	@DisplayName("가계부 내역 조회")
	@WithMockCustomUser
	class GetHistoriesTest {

		private Member member;

		private final String URI = "/ledgers";

		@BeforeEach
		void setUp() {
			member = memberRepository.save(MemberFixture.builder(MemberTestData.MEMBER_ID).build());
			clock.set(LocalDate.of(2026, 1, 10));

			//수입 내역
			savedIncomeLedger(member.getId(), "code1", LocalDate.of(2026, 1, 5), 20000);
			savedIncomeLedger(member.getId(), "code2", LocalDate.of(2026, 1, 12), 35000);

			//지출 내역
			savedOutlayLedger(member.getId(), "code3", LocalDate.of(2026, 1, 3), 5000);
			savedOutlayLedger(member.getId(), "code4", LocalDate.of(2026, 1, 12), 15000);
			savedOutlayLedger(member.getId(), "code5", LocalDate.of(2026, 1, 20), 20000);
		}

		private void savedIncomeLedger(String memberId, String code, LocalDate date, int amount) {
			ledgerRepository.insert(
					LedgerFixture.newLedger()
							.memberId(memberId)
							.code(code)
							.date(date)
							.money(Money.of((long) amount, PaymentType.NONE))
							.build()
			);
		}

		private void savedOutlayLedger(String memberId, String code, LocalDate date, int amount) {
			ledgerRepository.insert(
					LedgerFixture.newLedger()
							.memberId(memberId)
							.code(code)
							.date(date)
							.money(Money.of((long) amount, PaymentType.NONE))
							.category(CategoryTestData.SNACK_CODE)
							.build()
			);
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("저장된 가계부가 있으면 목록 페이지가 정상적으로 렌더링된다.")
			void returnsLedgerListPage_whenLedgerExists() throws Exception {
				//given: 2월에 등록한 가계부 내역이 1건 저장되어 있다.
				savedOutlayLedger(member.getId(), "code6", LocalDate.of(2026, 2, 1), 10000);

				//when: 가계부 내역 조회를 요청한다.
				MvcResult result = mockMvc.perform(
						get(URI)
								.param("viewType", "month")
				)
						.andExpect(status().isOk())
						.andExpect(view().name("/ledger/ledger_history"))
						.andExpect(model().attribute("type", HistoryType.MONTH))
						.andExpect(model().attribute("activeMenu", HistoryMenuType.ALL.name()))
						.andReturn();

				//then: 월별 가계부 내역 정보가 모델에 정상적으로 저장된다.
				assertThat(result.getModelAndView()).isNotNull();

				HistoryDashboardResponse history = (HistoryDashboardResponse) result.getModelAndView()
																						.getModel()
																						.get("history");

				assertThat(history).isNotNull();

				assertThat(history.getTitle()).isEqualTo("2026년 01월");

				assertThat(history.getMenu())
						.extracting(MenuItem::getLabel)
						.containsExactly("전체", "수입/지출", "카테고리", "메모", "기간");

				assertThat(history.getStatistics())
						.satisfies(stat -> {
							assertThat(stat.getTotal()).isEqualTo(95000L);
							assertThat(stat.getIncome()).isEqualTo(55000L);
							assertThat(stat.getOutlay()).isEqualTo(40000L);
						});

				assertThat(history.getHistoryGroups()).containsKeys("2026. 01. 03 (토)", "2026. 01. 05 (월)", "2026. 01. 12 (월)", "2026. 01. 20 (화)");

				assertThat(history.getHistoryGroups().get("2026. 01. 12 (월)"))
						.hasSize(2)
						.extracting(
								HistoryItem::getCode,
								HistoryItem::getCategoryType
						)
						.containsExactly(
								tuple("code4", CategoryType.OUTLAY),
								tuple("code2", CategoryType.INCOME)
						);
			}
			
			@Test
			@Sql(statements = "DELETE FROM ledger")
			@DisplayName("저장된 가계부가 없으면 빈 목록 페이지가 랜더링된다.")
			void returnsEmptyLedgerListPage_whenLedgerDoesNotExist() throws Exception {
				//given: 날짜가 2월 1일로 설정된 상태이다.
				clock.set(LocalDate.of(2026, 2, 1));

				//when: 가계부 내역 조회를 요쳥한다.
				mockMvc.perform(
						get(URI)
								.param("viewType", "month")
				)
						.andExpect(status().isOk())
						.andExpect(model().attribute(
								"history",
								hasProperty("historyGroups", is(Collections.emptyMap()))
						));
			}
			
			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("validHistoryTypes")
			@DisplayName("정상 viewType으로 요청하면 해당 타입의 데이터가 조회된다.")
			void returnsData_whenViewTypeIsValid(String viewType, int size, String key) throws Exception {
				//given: 2월에 등록한 가계부 내역이 1건 저장되어 있다.
				savedOutlayLedger(member.getId(), "code6", LocalDate.of(2026, 2, 1), 10000);
				
				//when: 가계부 내역 조회를 요청한다.
				mockMvc.perform(
						get(URI)
								.param("viewType", viewType)
				)
						.andExpect(status().isOk())
						.andExpect(model().attributeExists("history"))
						.andExpect(model().attribute(
								"history",
								hasProperty("historyGroups", allOf(
										aMapWithSize(size),
										hasKey(key)
								))
						));
			}

			static Stream<Arguments> validHistoryTypes() {
				return Stream.of(
						Arguments.of(
								named("YEAR인 경우", "year"),
								5,
								"2026. 02. 01 (일)"
						),
						Arguments.of(
								named("MONTH인 경우", "month"),
								4,
								"2026. 01. 20 (화)"
						),
						Arguments.of(
								named("WEEK 경우", "week"),
								1,
								"2026. 01. 05 (월)"
						)
				);
			}
			
			@Test
			@DisplayName("잘못된 viewType이면 기본 타입으로 조회된다.")
			void returnsDataWithDefaultType_whenViewTypeIsInvalid() throws Exception {
				//given: 잘못된 vieType이 주어진다.
				String viewType = "error";
				
				//when: 가계부 내역 조회를 요청한다.
				mockMvc.perform(
						get(URI)
								.param("viewType", viewType)
				)
						.andExpect(status().isOk())
						.andExpect(model().attribute("type", HistoryType.MONTH));
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("viewType이 없으면 기본 타입으로 조회된다.")
			void returnsDataWithDefaultType_whenViewTypeDoesNotExist(String viewType) throws Exception {
				//when: 가계부 내역 조회를 요청한다.
				mockMvc.perform(
								get(URI)
										.param("viewType", viewType)
						)
						.andExpect(status().isOk())
						.andExpect(model().attribute("type", HistoryType.MONTH));
			}

		}

	}


	@Nested
	@DisplayName("가계부 상세 조회")
	@WithMockCustomUser
	class GetDetailTest {

		private Ledger ledger;

		private final String URI = "/ledgers/{code}";

		@BeforeEach
		void setUp() {
			Member member = memberRepository.save(MemberFixture.builder(MemberTestData.MEMBER_ID).build());

			Long id = ledgerRepository.insert(LedgerFixture.newLedger().memberId(member.getId()).build());
			ledger = ledgerRepository.findById(id);
		}
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {
		
			@Test
			@DisplayName("가계부 상세 조회를 요청하면 상세 페이지와 가계부 정보를 반환한다.")
			void returnsLedgerDetailAndPage_whenLedgerDetailIsRequested() throws Exception {
				//given: 가계부가 저장된다.
				String code = ledger.getCode();
				
				//when: 가계부 상세 정보를 조회한다.
				MvcResult result = mockMvc.perform(
						get(URI, code)
				)
						.andExpect(status().isOk())
						.andExpect(model().attributeExists("ledger"))
						.andExpect(view().name("/ledger/ledger_detail"))
						.andReturn();

				//then: model에 전달된 객체 값을 확인한다.
				LedgerDetailResponse response = (LedgerDetailResponse) result.getModelAndView().getModel().get("ledger");

				assertThat(response.getCategory().getCode()).isEqualTo(ledger.getCategory());
				assertThat(response.getAmount()).isEqualTo(ledger.getMoney().getAmount());
				assertThat(response.getImages()).containsOnlyOnce("/image/ledger/slot-unlock.svg");
			}

		}
		
	}


	@Nested
	@DisplayName("수정 화면 요청")
	@WithMockCustomUser
	class EditViewTest {

		private final String URI = "/ledgers/{code}/edit";

		@Nested
		@DisplayName("성공 케이스")
		class Success {
		
			@Test
			@DisplayName("존재하는 가계부 코드로 요청하면 가계부 수정 페이지를 반환한다.")
			void returnsLedgerModifyPage_whenLedgerCodeExists() throws Exception {
				//given: 가계부가 저장되어 있다.
				Member member = memberRepository.save(MemberFixture.builder(MemberTestData.MEMBER_ID).build());

				Long id = ledgerRepository.insert(LedgerFixture.newLedger().memberId(member.getId()).build());
				Ledger ledger = ledgerRepository.findById(id);

				//when: 가계부 조회를 요청한다.
				mockMvc.perform(
						get(URI, ledger.getCode())
				)
						.andExpect(status().isOk())
						.andExpect(model().attributeExists("ledger"))
						.andExpect(model().attributeExists("fixes"))
						.andExpect(model().attributeExists("fixCycles"))
						.andExpect(model().attributeExists("paymentTypes"))
						.andExpect(view().name("/ledger/ledger_edit"));
			}	
			
		}
		
	}

}