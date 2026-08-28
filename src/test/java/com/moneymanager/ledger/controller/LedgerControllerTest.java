package com.moneymanager.ledger.controller;

import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep1Response;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep2Response;
import com.moneymanager.ledger.domain.dto.response.history.HistoryDashboardResponse;
import com.moneymanager.ledger.domain.dto.response.history.LedgerHistoryDisplay;
import com.moneymanager.ledger.domain.dto.response.history.LedgerStatistics;
import com.moneymanager.ledger.domain.dto.response.item.CategoryItem;
import com.moneymanager.ledger.domain.enums.LedgerType;
import com.moneymanager.ledger.service.application.LedgerHistoryService;
import com.moneymanager.ledger.service.application.LedgerService;
import com.moneymanager.support.UnitTest;
import com.moneymanager.support.fixture.entity.category.IncomeCategoryFixture;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import com.moneymanager.support.fixture.response.HistoryItemTestFixture;
import com.moneymanager.support.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
class LedgerControllerTest extends UnitTest {

    private static final String BASE_URI = "/ledgers";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LedgerService ledgerService;

    @MockBean
    private LedgerHistoryService ledgerHistoryService;

    @Nested
    @DisplayName("가계부 작성 1단계 화면 요청할 때")
    @WithMockCustomUser
    class Step1View {

        private static final String URI = BASE_URI + "/new/step1";

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("가계부 작성 1단계 페이지를 반환한다.")
            @WithMockCustomUser
            void returnView_whenGetRequestIsGiven() throws Exception {
                //given: 화면에 필요한 데이터를 조회하는 동작이 정의되어 있다.
                LedgerWriteStep1Response response = LedgerWriteStep1Response.of(
                        List.of(2026, 2025),
                        List.of(1, 2),
                        List.of(1, 2, 3, 4, 5)
                );

                when(ledgerService.getStep1()).thenReturn(response);

                //when: 가계부 작성 1단계 페이지를 요청한다.
                mockMvc.perform(
                                get(URI)
                        )
                        .andExpect(status().isOk());
            }

        }

    }


    @Nested
    @WithMockCustomUser
    @DisplayName("가계부 작성 2단계 화면을 요청할 때")
    class Step2View {

        private static final String URI = BASE_URI + "/new/step2";

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("필수인 요청 파라미터가 모두 있으면 모델에 ledger속성과 페이지를 반환한다.")
            void returnsModelAndPage_whenRequiredParamsExist() throws Exception {
                //given
                String type = "income";
                String date = "20260101";

                LedgerWriteStep2Response response
                        = LedgerWriteStep2Response.of(
                        "제목",
                        LedgerType.INCOME,
                        CategoryItem.from(
                                IncomeCategoryFixture.createMiddleAll()
                        ),
                        List.of(
                                ImageSlot.ofEmptySlot(),
                                ImageSlot.ofEmptySlot()
                        )
                );

                when(ledgerService.getStep2(type, date))
                        .thenReturn(response);

                //when
                mockMvc.perform(
                                get(URI)
                                        .param("type", type)
                                        .param("date", date)
                        )
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("ledger", response))
                        .andExpect(view().name("/ledger/ledger_writeStep2"));

                verify(ledgerService).getStep2(type, date);
            }

        }

    }


    @Nested
    @DisplayName("카카오 지도를 오청할 때")
    class GetKakaoMap {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("장소를 선택할 수 있는 화면을 보여준다.")
            void loadsLocationSelectionView_whenRequested() throws Exception {
                //when
                String URI = BASE_URI + "/map";
                mockMvc.perform(
                                get(URI)
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("/map/kakao_map"));
            }

        }

    }


    @Nested
    @DisplayName("가계부 등록 요청할 때")
    class Create {

        final String URI = BASE_URI;

        @Test
        @DisplayName("등록 요청을 서비스에 전달하고 목록으로 리디렉션한다.")
        void createsLedgerAndRedirectsToList_whenRequestIsValid() throws Exception {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture
                    .builder()
                    .build();

            //when
            mockMvc.perform(
                            post(URI)
                                    .requestAttr("ledger", request)
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ledgers"));
        }

    }


    @Nested
    @DisplayName("가계부 내역을 조회할 때")
    @WithMockCustomUser
    class GetHistories {

        private String URI = BASE_URI;

        @BeforeEach
        void setUp() {
            when(ledgerHistoryService.searchLedgersByDate(anyString()))
                    .thenReturn(
                            HistoryDashboardResponse.of(
                                    "제목",
                                    LedgerStatistics.of(10000L, 5000L),
                                    List.of(
                                            LedgerHistoryDisplay.of(
                                                    "2026. 03. 01 (일)",
                                                    List.of(
                                                            List.of(
                                                                    HistoryItemTestFixture.builder().build(LocalDate.of(2026, 3, 1), "0101010"),
                                                                    HistoryItemTestFixture.builder().build(LocalDate.of(2026, 3, 1), "0101010"),
                                                                    HistoryItemTestFixture.builder().build(LocalDate.of(2026, 3, 1), "0101010")
                                                            ),
                                                            List.of(
                                                                    HistoryItemTestFixture.builder().build(LocalDate.of(2026, 3, 1), "0101010"),
                                                                    HistoryItemTestFixture.builder().build(LocalDate.of(2026, 3, 1), "0101010")
                                                            )
                                                    )
                                            ),
                                            LedgerHistoryDisplay.of(
                                                    "2026. 03. 02(월)",
                                                    List.of(
                                                            List.of(
                                                                    HistoryItemTestFixture.builder().build(LocalDate.of(2026, 3, 2), "0102010")
                                                            )
                                                    )
                                            )
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("정상적인 내역 유형이면 내역 조회 서비스를 호출하고 응답 객체를 반환한다.")
        void returnsHistoryResponse_whenHistoryTypeIsValid() throws Exception {
            //given
            String type = "month";

            //when
            mockMvc.perform(
                            get(URI)
                                    .param("type", type)
                    )
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("history", "type"))
                    .andExpect(view().name("/ledger/ledger_history"));
        }

        @Test
        @DisplayName("내역 유형이 누락되면 MONTH으로 진행한다.")
        void returnsMonthlyList_whenTypeIsMissing() throws Exception {
            //given
            String type = "error";

            //when
            mockMvc.perform(
                            get(URI)
                                    .param("type", type)
                    )
                    .andExpect(status().isOk())
                    .andExpect(view().name("/ledger/ledger_history"));
        }

    }

}