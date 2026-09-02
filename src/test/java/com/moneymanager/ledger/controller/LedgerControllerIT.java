package com.moneymanager.ledger.controller;

import com.moneymanager.global.config.TimeConfig;
import com.moneymanager.ledger.domain.dto.response.history.HistoryDashboardResponse;
import com.moneymanager.ledger.domain.dto.response.history.LedgerHistoryDisplay;
import com.moneymanager.ledger.domain.dto.response.history.LedgerStatistics;
import com.moneymanager.ledger.domain.dto.response.history.MenuResponse;
import com.moneymanager.ledger.domain.dto.response.item.MenuItem;
import com.moneymanager.ledger.domain.enums.HistoryMenu;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.file.ImageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
public class LedgerControllerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.findById(MemberTestData.DEFAULT_ID);
    }

    @Nested
    @DisplayName("가계부 작성 1단계 화면 요청할 때")
    class Step1ViewTest {

        private final String URI = "/ledgers/new/step1";

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("현재 날짜 기준 가계부 작성 페이지를 조회한다.")
            void returnsLedgerWritePage_whenCurrentDateIsGiven() throws Exception {
                //when & then
                mockMvc.perform(
                                get(URI)
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("/ledger/ledger_writeStep1"))
                        .andExpect(model().attributeExists("ledger"));

            }

        }

    }


    @Nested
    @DisplayName("가계부 작성 2단계 화면 요청할 때")
    class Step2ViewTest {

        private final String URI = "/ledgers/new/step2";

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("필수인 요청 파라미터가 모두 있으면 모델에 ledger 속성과 Step2 페이지를 반환한다.")
            void returnsStep2Page_whenRequestIsValid() throws Exception {
                //given: 정상적인 가계부 유형과 거래날짜가 주어진다.
                String type = "income";
                String date = "20260101";

                //when: 가계부 작성 2단계 페이지를 요청한다.
                mockMvc.perform(
                                get(URI)
                                        .param("type", type)
                                        .param("date", date)
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isOk())
                        .andExpect(model().attributeExists("ledger"))
                        .andExpect(view().name("/ledger/ledger_writeStep2"))
                        .andDo(print())
                        .andReturn();
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("요청 파라미터에 가계부 유형이 누락되면 200 코드 반환하고 에러 페이지를 반환한다.")
            void returns200AndErrorPage_whenLedgerTypeIsMissing() throws Exception {
                //when
                mockMvc.perform(
                                get(URI)
                                        .param("date", "20260101")
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("error/400"));
            }

            @Test
            @DisplayName("요청 파라미터에 가계부 날짜가 누락되면 200 코드 반환하고 에러 페이지를 반환한다.")
            void returns200AndErrorPage_whenLedgerDateIsMissing() throws Exception {
                //when
                mockMvc.perform(
                                get(URI)
                                        .param("type", "income")
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("error/400"));
            }

            @Test
            @DisplayName("요청 파라미터가 모두 누락되면 400 코드 반환하고 에러 페이지를 반환한다.")
            void returns400AndErrorPage_whenAllParametersAreMissing() throws Exception {
                //when
                mockMvc.perform(
                                get(URI)
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("error/400"));
            }

            @Test
            @DisplayName("POST로 요청하면 200 코드 반환하고 에러 페이지를 반환한다.")
            void returns405AndErrorPage_whenRequestMethodIsPost() throws Exception {
                mockMvc.perform(
                                post(URI)
                                        .param("type", "income")
                                        .param("date", "20260101")
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isMethodNotAllowed());
            }

            @Test
            @Sql(statements = "DELETE FROM ledger_category", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @DisplayName("요청 중 예외가 발생하면 가계부 2단계 작성 화면으로 리디렉션한다.")
            void redirectsToLedgerStep2_whenExceptionOccurs() throws Exception {
                mockMvc.perform(
                                get(URI)
                                        .param("type", "income")
                                        .param("date", "20260101")
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("/ledger/ledger_writeStep2"))
                        .andDo(print());
            }

        }

    }


    @Nested
    @DisplayName("가계부 등록 요청할 때")
    class Create {

        final String URI = "/ledgers";

        @Test
        @DisplayName("등록 요청으로 가계부를 저장한다.")
        void savesLedger_whenRequestIsValid() throws Exception {
            //given
            MockMultipartFile file = ImageFixture.jpg("test");

            //when
            mockMvc.perform(
                            multipart(URI)
                                    .file(file)
                                    .param("date", LedgerTestData.DEFAULT_DATE)
                                    .param("categoryCode", LedgerTestData.DEFAULT_CATEGORY)
                                    .param("fixed", LedgerTestData.DEFAULT_FIX.getValue().toLowerCase())
                                    .param("amount", LedgerTestData.DEFAULT_AMOUNT.toString())
                                    .param("paymentType", LedgerTestData.DEFAULT_PAYMENT_TYPE.name().toLowerCase())
                                    .cookie(accessTokenCookie(member.getUsername()))
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ledgers"));

            //then
            assertThat(ledgerRepository.findAll())
                    .anyMatch(ledger ->
                            ledger.getCategory().equals(LedgerTestData.DEFAULT_CATEGORY)
                    );

            try (Stream<Path> files = Files.walk(tempDir)) {
                boolean exist = files
                        .filter(Files::isRegularFile)
                        .anyMatch(path -> {
                            String fileName = path.getFileName().toString().toLowerCase();

                            return fileName.endsWith(".jpg") || fileName.endsWith(".png");
                        });

                assertThat(exist).isTrue();
            }
        }

        @Test
        @DisplayName("카테고리 코드가 없으면 등록하지 않는다.")
        void rejectsRequest_whenCategoryCodeIsNull() throws Exception {
            //when
            mockMvc.perform(
                            multipart(URI)
                                    .param("date", LedgerTestData.DEFAULT_DATE)
                                    .param("fixed", LedgerTestData.DEFAULT_FIX.getValue().toLowerCase())
                                    .param("amount", LedgerTestData.DEFAULT_AMOUNT.toString())
                                    .param("paymentType", LedgerTestData.DEFAULT_PAYMENT_TYPE.name().toLowerCase())
                                    .cookie(accessTokenCookie(member.getUsername()))
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ledgers/new/step2"));

            //then
            assertThat(ledgerRepository.count()).isEqualTo(0);
            assertThat(Files.exists(tempDir.resolve(member.getId()))).isFalse();
        }

        @Test
        @DisplayName("서버에 파일 저장 실패하면 등록하지 않는다.")
        void doesNotSaveLedger_whenFileUploadFails() throws Exception {
            //when
            MockMultipartFile file = ImageFixture.empty("test");

            //when
            mockMvc.perform(
                            multipart(URI)
                                    .file(file)
                                    .param("date", LedgerTestData.DEFAULT_DATE)
                                    .param("categoryCode", CategoryTestData.SALARY_CODE)
                                    .param("fixed", LedgerTestData.DEFAULT_FIX.getValue().toLowerCase())
                                    .param("amount", LedgerTestData.DEFAULT_AMOUNT.toString())
                                    .param("paymentType", LedgerTestData.DEFAULT_PAYMENT_TYPE.name().toLowerCase())
                                    .cookie(accessTokenCookie(member.getUsername()))
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ledgers/new/step2"));

            //then
            assertThat(ledgerRepository.count()).isEqualTo(0);
            assertThat(Files.exists(tempDir.resolve(member.getId()))).isFalse();
        }
    }


    @Nested
    @Import(TimeConfig.class)
    @Sql(
            scripts = {"/sql/ledger-history-test.sql"},
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
    )
    @Sql(
            scripts = "/sql/clear-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
    )
    @DisplayName("가계부 내역을 조회할 때")
    class GetHistories {

        final String URI = "/ledgers";

        @Test
        @DisplayName("연 범위 요청 시 연간 가계부 내역을 반환한다.")
        void returnsYearlyLedger_whenRangeIsYearly() throws Exception {
            //given
            String type = "year";

            //when
            MvcResult result = mockMvc.perform(
                            get(URI)
                                    .param("type", type)
                                    .cookie(accessTokenCookie("member1"))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            //then
            ModelAndView mav = result.getModelAndView();
            assertThat(mav).isNotNull();

            HistoryDashboardResponse history = (HistoryDashboardResponse) mav.getModel().get("history");
            assertThat(history).isNotNull();

            LedgerStatistics statistics = history.getStatistics();
            assertThat(statistics)
                    .isNotNull()
                    .extracting(LedgerStatistics::getTotal, LedgerStatistics::getIncome, LedgerStatistics::getOutlay)
                    .containsExactly(2994000L, 2522000L, 472000L);

            List<LedgerHistoryDisplay> historyDisplays = history.getHistoryGroups();
            assertThat(historyDisplays)
                    .isNotEmpty()
                    .hasSize(5);

            MenuResponse menu = (MenuResponse) mav.getModel().get("menu");
            assertThat(menu.getMenus())
                    .extracting(MenuItem::getType)
                    .containsExactly(
                            HistoryMenu.ALL,
                            HistoryMenu.CATEGORY,
                            HistoryMenu.SUB_CATEGORY,
                            HistoryMenu.MEMO,
                            HistoryMenu.PERIOD
                    );
        }

        @Test
        @DisplayName("월 범위 요청 시 월간 가계부 내역을 반환한다.")
        void returnsMonthlyLedger_whenRangeIsMonthly() throws Exception {
            //given
            String type = "month";

            //when
            MvcResult result = mockMvc.perform(
                            get(URI)
                                    .param("type", type)
                                    .cookie(accessTokenCookie("member1"))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            //then
            ModelAndView mav = result.getModelAndView();
            assertThat(mav).isNotNull();

            HistoryDashboardResponse history = (HistoryDashboardResponse) mav.getModel().get("history");
            assertThat(history).isNotNull();

            List<LedgerHistoryDisplay> historyDisplays = history.getHistoryGroups();
            assertThat(historyDisplays)
                    .isNotEmpty()
                    .hasSize(4)
                    .extracting(LedgerHistoryDisplay::getDate)
                    .doesNotContain("2026. 02. 05 (목)");

            MenuResponse menu = (MenuResponse) mav.getModel().get("menu");
            assertThat(menu.getMenus())
                    .extracting(MenuItem::getType)
                    .containsExactly(
                            HistoryMenu.ALL,
                            HistoryMenu.CATEGORY,
                            HistoryMenu.SUB_CATEGORY,
                            HistoryMenu.MEMO
                    );
        }

        @Test
        @DisplayName("주 범위 요청 시 주간 가계부 내역을 반환한다.")
        void returnsWeeklyLedger_whenRangeIsWeekly() throws Exception {
            //given
            String type = "week";

            //when
            MvcResult result = mockMvc.perform(
                            get(URI)
                                    .param("type", type)
                                    .cookie(accessTokenCookie("member1"))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            //then
            ModelAndView mav = result.getModelAndView();
            assertThat(mav).isNotNull();

            HistoryDashboardResponse history = (HistoryDashboardResponse) mav.getModel().get("history");
            assertThat(history).isNotNull();

            List<LedgerHistoryDisplay> historyDisplays = history.getHistoryGroups();
            assertThat(historyDisplays)
                    .isNotEmpty()
                    .hasSize(3)
                    .extracting(LedgerHistoryDisplay::getDate)
                    .doesNotContain("2026. 01. 05 (월)");
        }

        @Test
        @DisplayName("조회할 가계부 내역이 없으면 빈 목록을 반환한다.")
        void returnsEmptyList_whenLedgerDoesNotExist() throws Exception {
            //given
            String type = "week";

            //when
            MvcResult result = mockMvc.perform(
                            get(URI)
                                    .param("type", type)
                                    .cookie(accessTokenCookie("member2"))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            //then
            ModelAndView mav = result.getModelAndView();
            assertThat(mav).isNotNull();

            HistoryDashboardResponse history = (HistoryDashboardResponse) mav.getModel().get("history");
            assertThat(history).isNotNull();

            LedgerStatistics statistics = history.getStatistics();
            assertThat(statistics)
                    .isNotNull()
                    .extracting(LedgerStatistics::getTotal, LedgerStatistics::getIncome, LedgerStatistics::getOutlay)
                    .containsExactly(0L, 0L, 0L);

            List<LedgerHistoryDisplay> historyDisplays = history.getHistoryGroups();
            assertThat(historyDisplays).isEmpty();
        }

        @Test
        @DisplayName("잘못된 내역 유형 요청 시 월간 가계부 내역을 반화한다.")
        void returnsMonthlyLedger_whenTypeIsInvalid() throws Exception {
            //given
            String type = "none";

            //when
            MvcResult result = mockMvc.perform(
                            get(URI)
                                    .param("type", type)
                                    .cookie(accessTokenCookie("member1"))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            //then
            ModelAndView mav = result.getModelAndView();
            assertThat(mav).isNotNull();

            HistoryDashboardResponse history = (HistoryDashboardResponse) mav.getModel().get("history");
            assertThat(history).isNotNull();

            List<LedgerHistoryDisplay> historyDisplays = history.getHistoryGroups();
            assertThat(historyDisplays)
                    .isNotEmpty()
                    .hasSize(4);
        }

    }


    @Nested
    @Import(TimeConfig.class)
    @Sql(
            scripts = {"/sql/ledger-get-test.sql"},
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
    )
    @Sql(
            scripts = "/sql/clear-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
    )
    @DisplayName("가계부 상세 정보를 요청할 때")
    class GetDetail {

        String URI = "/ledgers/{code}";
        
        @Test
        @DisplayName("작성된 가계부 요청하면 상세 정보를 반환한다.")
        void returnsLedgerDetail_whenExists() throws Exception {
        	//when
            mockMvc.perform(
                    get(URI, "code1")
                            .cookie(accessTokenCookie("member1"))
            )
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("ledger"))
                    .andExpect(view().name("/ledger/ledger_detail"));
        }
        
        @Test
        @DisplayName("존재하지 않은 가계부로 요청하면 내역 조회 화면으로 이동한다.")
        void redirectsToLedgerHistories_whenLedgerDoesNotExist() throws Exception {
        	//when
            mockMvc.perform(
                            get(URI, "code5")
                                    .cookie(accessTokenCookie("member1"))
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ledgers"));
        }
        
        @Test
        @DisplayName("타인의 가계부로 요청하면 내역 조회 화면으로 이동한다.")
        void redirectsToAccountBookList_whenUserIsNotOwner() throws Exception {
        	//given
            String code = "code3";
        	
        	//when
            mockMvc.perform(
                    get(URI, code)
                            .cookie(accessTokenCookie("member1"))
            )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ledgers"));
        }
        
    }


    @Nested
    @Import(TimeConfig.class)
    @Sql(
            scripts = {"/sql/ledger-get-test.sql"},
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
    )
    @Sql(
            scripts = "/sql/clear-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
    )
    @DisplayName("가계부 수정 정보를 요청할 때")
    class GetEdit {

        String URI = "/ledgers/{code}/edit";

        @Test
        @DisplayName("작성된 가계부 요청하면 수정 정보를 반환한다.")
        void returnsLedgerDetail_whenExists() throws Exception {
            //when
            mockMvc.perform(
                            get(URI, "code1")
                                    .cookie(accessTokenCookie("member1"))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("ledger"))
                    .andExpect(view().name("/ledger/ledger_edit"));
        }

        @Test
        @DisplayName("존재하지 않은 가계부로 요청하면 내역 조회 화면으로 이동한다.")
        void redirectsToLedgerHistories_whenLedgerDoesNotExist() throws Exception {
            //when
            mockMvc.perform(
                            get(URI, "code5")
                                    .cookie(accessTokenCookie("member1"))
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ledgers"));
        }

        @Test
        @DisplayName("타인의 가계부로 요청하면 내역 조회 화면으로 이동한다.")
        void redirectsToAccountBookList_whenUserIsNotOwner() throws Exception {
            //given
            String code = "code3";

            //when
            mockMvc.perform(
                            get(URI, code)
                                    .cookie(accessTokenCookie("member1"))
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ledgers"));
        }

    }

}