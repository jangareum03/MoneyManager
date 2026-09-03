package com.moneymanager.ledger.controller;

import com.moneymanager.global.config.TimeConfig;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
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

    private final String BASE_URI = "/ledgers";
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

        final String URI = BASE_URI + "/histories";

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("요청에서 type이 누락되면 month으로 진행한다.")
            void returnsHistoryWithDefaultMonthType_whenTypeIsMissing() throws Exception {
            	//when
                mockMvc.perform(
                        get(URI)
                                .cookie(accessTokenCookie("member1"))
                )
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("type", "month"))
                        .andExpect(model().attribute("history", hasProperty("title", is("2026년 01월"))))
                        .andExpect(view().name("/ledger/ledger_history"));
            }

            @Test
            @DisplayName("요청에서 type만 있어도 현재 날짜 기준으로 내역을 조회한다.")
            void returnsHistoryForCurrentDate_whenOnlyTypeIsGiven() throws Exception {
            	//given
                String type = "month";
            	
            	//when
                mockMvc.perform(
                        get(URI)
                                .param("type", type)
                                .cookie(accessTokenCookie("member1"))
                )
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("history",
                                hasProperty("historyGroups", hasSize(4))));
            }
            
            @Test
            @DisplayName("요청에서 날짜가 있으면 날짜별로 내역을 조회한다.")
            void returnsHistoryByDate_whenDateIsGiven() throws Exception {
            	//given
                String type = "month";
                Integer year = 2026;
                Integer month = 3;
            	
            	//when
                mockMvc.perform(
                        get(URI)
                                .param("type", type)
                                .param("year", String.valueOf(year))
                                .param("month", String.valueOf(month))
                                .cookie(accessTokenCookie("member1"))
                )
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("history", hasProperty("title", is("2026년 03월"))))
                        .andExpect(model().attribute("history", hasProperty("historyGroups", empty())));
            }
            
            @Test
            @DisplayName("type이 YEAR이면 기간 메뉴가 존재한다.")
            void returnsHistoryWithPeriodMenu_whenTypeIsYear() throws Exception {
            	//given
                String type = "year";
            	
            	//when
                mockMvc.perform(
                        get(URI)
                                .param("type", type)
                                .cookie(accessTokenCookie("member1"))
                )
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("menu", hasProperty("menus", hasSize(5))));
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("type과 날짜 값이 다르면 403을 발생시킨다.")
            void returns403Forbidden_whenTypeAndDateMismatch() throws Exception {
            	//given: WEEK 타입에 month와 week날짜 값이 없다.
                String type = "week";
                Integer year = 2026;
            	
            	//when
                mockMvc
                        .perform(
                                get(URI)
                                        .param("type", type)
                                        .param("year", String.valueOf(year))
                                        .cookie(accessTokenCookie("member1"))
                        )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/ledgers/histories?type=month"));
            }

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