package com.moneymanager.ledger.controller;

import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep2Response;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.Money;
import com.moneymanager.domain.member.Member;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.repository.member.MemberRepository;
import com.moneymanager.service.ledger.LedgerCommandService;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.service.validation.LedgerValidator;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberFixture;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import com.moneymanager.support.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import java.time.Clock;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class LedgerControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LedgerReadService readService;

	@Autowired
	private LedgerCommandService commandService;

	@Autowired
	private LedgerValidator validator;

	@Autowired
	private Clock clock;

	@Nested
	@DisplayName("작성 1단계")
	class Step1ViewTest {

		private final String URL = "/ledgers/new/step1";

		@Nested
		@DisplayName("성공 케이스")
		@WithMockCustomUser
		class Success {
		
			@Test
			@DisplayName("현재 날짜 기준 가계부 작성 페이지를 조회한다.")
			void returnsLedgerWritePage_whenCurrentDateIsGiven() throws Exception {
				mockMvc.perform(
						get(URL)
				)
						.andExpect(status().isOk())
						.andExpect(view().name("/ledger/ledger_writeStep1"))
						.andExpect(model().attributeExists("ledger"));

			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("인증되지 않은 사용자는 로그인 페이지로 이동한다.")
			void rejectsRequestToLoginPage_whenUserIsUnauthenticated() throws Exception {
				mockMvc.perform(
						get(URL)
				)
						.andExpect(status().is3xxRedirection())
						.andExpect(redirectedUrlPattern("**/"));
			}
			
			@Test
			@WithMockCustomUser(role = "ADMIN")
			@DisplayName("권한이 없는 사용자는 접근할 수 없다.")
			void rejectsRequest_whenUserHasNoPermission() throws Exception {
				mockMvc.perform(
						get(URL)
				)
						.andExpect(status().isForbidden())
						.andExpect(redirectedUrl("/403"));
			}
			
			@Test
			@DisplayName("세션 만료된 사용자는 로그인 페이지로 이동한다.")
			void rejectsRequestToLoginPage_whenSessionIsExpired() throws Exception {
				MockHttpSession session = new MockHttpSession();
				session.invalidate();

				mockMvc.perform(
						get(URL)
								.session(session)
				)
						.andExpect(status().is3xxRedirection())
						.andExpect(redirectedUrlPattern("**/"));
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
		
		@Nested
		@DisplayName("실패 케이스")
		class Failure {
		
			@Test
			@DisplayName("서비스에서 예외가 발생하면 Step1리다이렉션 한다.")
			void returnsStep1Redirect_whenServiceExceptionOccurs() throws Exception {
				//given: 가계부 날짜에 빈 문자열로 주어진다.
				String date = "";

				//when: 가계부 작성 2단계 페이지를 요청한다.
				mockMvc.perform(
						get(URL)
								.param("type", "income")
								.param("date", date)
				)
						.andExpect(status().is3xxRedirection())
						.andExpect(redirectedUrlPattern("**/ledgers/new/step1"));
			}
			
		}

	}


	@Nested
	@DisplayName("가계부 등록")
	@WithMockCustomUser
	class Create {

		@Autowired
		private LedgerRepository ledgerRepository;

		@Autowired
		private MemberRepository memberRepository;

		private final String URL = "/ledgers";
		private Member member;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(MemberFixture.builder(MemberTestData.MEMBER_ID).build());
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
						post(URL)
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
		
		@Nested
		@DisplayName("실패 케이스")
		class Failure {
		
			@Test
			@DisplayName("필수 입력값이 누락되면 400 응답을 반환한다.")
			void returns400Status_whenRequiredFieldIsNull() throws Exception {
				//given: 필수 입력값이 없는 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequest.builder()
						.categoryCode(null)
						.build();

				//when: 가계부 등록을 요청한다.
				mockMvc.perform(
						post(URL)
								.flashAttr("ledger", request)
				)
						.andExpect(status().is3xxRedirection())
						.andExpect(view().name("/ledger/ledger_writeStep2"));
				
			}
			
			@Test
			@DisplayName("가계부 등록 서비스에서 예외가 발생하면 Step2 페이지로 리다이렉트 한다.")
			void returnsRedirectToStep2_whenServiceExceptionOccurs() throws Exception {
				//given: 유효하지 않은 값이 포함된 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequest.builder()
						.categoryCode("010109")
						.build();

				//when: 가계부 등록을 요청한다.
				mockMvc.perform(
								post(URL)
										.flashAttr("ledger", request)
						)
						.andExpect(status().is3xxRedirection())
						.andExpect(view().name("/ledger/ledger_writeStep2"));
				
			}
			
		}
		
	}

}