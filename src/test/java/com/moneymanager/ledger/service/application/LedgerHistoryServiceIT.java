package com.moneymanager.ledger.service.application;

import com.moneymanager.global.config.MutableClock;
import com.moneymanager.global.config.TimeConfig;
import com.moneymanager.ledger.domain.dto.request.LedgerSearchRequest;
import com.moneymanager.ledger.domain.dto.response.history.HistoryDashboardResponse;
import com.moneymanager.ledger.domain.dto.response.history.LedgerHistoryDisplay;
import com.moneymanager.ledger.domain.dto.response.history.LedgerStatistics;
import com.moneymanager.ledger.domain.dto.response.history.MenuResponse;
import com.moneymanager.ledger.domain.dto.response.item.CategoryItem;
import com.moneymanager.ledger.domain.dto.response.item.ChartBarItem;
import com.moneymanager.ledger.domain.dto.response.item.MenuItem;
import com.moneymanager.ledger.domain.enums.HistoryMenu;
import com.moneymanager.ledger.domain.enums.HistoryType;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.moneymanager.global.exception.code.ErrorCode.POLICY_VIOLATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.application<br>
 * 파일이름       : LedgerHistoryServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 29<br>
 * 설명              : LedgerHistoryService 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 8. 29</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class LedgerHistoryServiceIT extends IntegrationTest {

    @Autowired
    LedgerHistoryService target;

    @Autowired
    private MutableClock clock;


    @Nested
    @DisplayName("메뉴를 생성할 때")
    class CreateMenu {

        @Test
        @DisplayName("YEAR이면 전체 메뉴와 하위 메뉴를 생성한다.")
        void createsAllMenusAndSubMenus_whenPeriodTypeIsYear() {
            //given
            String type = "year";

            //when
            MenuResponse result = target.buildSubMenu(type);

            //then
            assertThat(result.getMenus())
                    .hasSize(HistoryMenu.values().length);

            assertThat(result.getMenus())
                    .extracting(MenuItem::getType)
                    .containsExactly(
                            HistoryMenu.ALL,
                            HistoryMenu.CATEGORY,
                            HistoryMenu.SUB_CATEGORY,
                            HistoryMenu.MEMO,
                            HistoryMenu.PERIOD
                    );

            assertThat(result.getMenus())
                    .filteredOn(menu ->
                            menu.getType() == HistoryMenu.ALL
                            || menu.getType() == HistoryMenu.MEMO
                            || menu.getType() == HistoryMenu.PERIOD
                    )
                    .allSatisfy(menu -> {
                        assertThat(menu.getSubMenus()).isNotNull();
                        assertThat(menu.getSubMenus().getSubItems()).isEmpty();
                    });
        }

        @ParameterizedTest
        @ValueSource(strings = {"month", "week"})
        @DisplayName("MONTH 및 WEEK면 DATE 메뉴가 제외되고 생성한다.")
        void createsMenusExceptDate_whenPeriodTypeIsMonthAndWeek(String type) {
        	//when
            MenuResponse result = target.buildSubMenu(type);
        	
        	//then
            assertThat(result.getMenus())
                    .extracting(MenuItem::getType)
                    .containsExactly(
                            HistoryMenu.ALL,
                            HistoryMenu.CATEGORY,
                            HistoryMenu.SUB_CATEGORY,
                            HistoryMenu.MEMO
                    );
        }
        
        @Test
        @DisplayName("수입/지출 메뉴면 하위 메뉴를 생성한다.")
        void createsSubMenus_whenMenuTypeIsCategory() {
        	//given
            String type = "year";
        	
        	//when
            MenuResponse result = target.buildSubMenu(type);
        	
        	//then
            MenuItem category = result.getMenus()
                    .stream()
                    .filter(menu -> menu.getType() == HistoryMenu.CATEGORY)
                    .findFirst()
                    .orElseThrow();

            assertThat(category.getSubMenus()).isNotNull();
            assertThat(category.getSubMenus().getSubItems())
                    .containsKeys("수입", "지출");

            assertThat(category.getSubMenus().getSubItems().get("수입"))
                    .hasSize(1);

            assertThat(category.getSubMenus().getSubItems().get("지출"))
                    .hasSize(1);
        }

        @Test
        @DisplayName("카테고리 메뉴면 하위 메뉴를 생성한다.")
        void createsSubMenus_whenMenuTypeIsSubCategory() {
            //given
            String type = "week";

            //when
            MenuResponse result = target.buildSubMenu(type);
        	
        	//then
        	MenuItem subCategoryMenu = result.getMenus()
                    .stream()
                    .filter(menu -> menu.getType() == HistoryMenu.SUB_CATEGORY)
                    .findFirst()
                    .orElseThrow();

            Map<String, List<CategoryItem >> subItem = subCategoryMenu.getSubMenus().getSubItems();

            assertThat(subItem)
                    .containsKeys("소득", "차입", "식비", "교육", "저축");

            assertThat(subItem.get("소득")).hasSize(3);
            assertThat(subItem.get("차입")).hasSize(1);
            assertThat(subItem.get("식비")).hasSize(6);
            assertThat(subItem.get("교육")).hasSize(5);
            assertThat(subItem.get("저축")).hasSize(4);
        }

    }


    @Nested
    @Import(TimeConfig.class)
    @WithMockCustomUser(memberId = "member1")
    @Sql(
            scripts = {"/sql/ledger-history-test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
    )
    @Sql(
            scripts = "/sql/clear-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
    )
    @DisplayName("내역 유형별 가계부 조회할 때")
    class FindHistories {

        @Nested
        @DisplayName("성공")
        class Success {
            
            @Test
            @DisplayName("YEAR이면서 선택한 날짜가 없으면 현재 날짜의 연초와 연말 기간의 내역만 조회한다.")
            void fetchesHistoryByCurrentYear_whenTypeIsYearAndSelectedDateIsNull() {
            	//when
                HistoryDashboardResponse result = target.findHistories("year", null, null, null);
            	
            	//then
                assertThat(result).isNotNull();

                assertThat(result.getTitle()).isEqualTo("2026년");
                assertThat(result.getStatistics())
                        .extracting(
                                LedgerStatistics::getTotal, LedgerStatistics::getIncome, LedgerStatistics::getOutlay
                        )
                        .containsExactly(2994000L, 2522000L, 472000L);

                assertThat(result.getHistoryGroups()).hasSize(5);

                //then: 차트 데이터 검증
                List<ChartBarItem> chart = result.getChartBarItems();

                assertThat(chart).hasSize(12);

                assertThat(chart).element(0)
                        .extracting(
                                ChartBarItem::getLabel, ChartBarItem::getIncome, ChartBarItem::getOutlay
                        )
                        .containsExactly(
                                "1월", 2512000L, 442000L
                        );

                assertThat(chart).element(11)
                        .extracting(
                                ChartBarItem::getLabel, ChartBarItem::getIncome, ChartBarItem::getOutlay
                        )
                        .containsExactly(
                                "12월", 0L, 0L
                        );
            }
            
            @Test
            @DisplayName("MONTH이면서 선택한 날짜가 없으면 현재 날짜의 월초와 월말 기간의 내역만 조회한다.")
            void fetchesHistoryByCurrentMonth_whenTypeIsMonthAndSelectedDateIsNull() {
            	//given
                clock.set(LocalDate.of(2026, 2, 10));

                //when
                HistoryDashboardResponse result = target.findHistories("month", null, null, null);

                //then
                assertThat(result).isNotNull();

                assertThat(result.getTitle()).isEqualTo("2026년 02월");
                assertThat(result.getStatistics())
                        .extracting(
                                LedgerStatistics::getTotal, LedgerStatistics::getIncome, LedgerStatistics::getOutlay
                        )
                        .containsExactly(40000L, 10000L, 30000L);

                assertThat(result.getHistoryGroups()).hasSize(1);

                //then: 차트 데이터 검증
                List<ChartBarItem> chart = result.getChartBarItems();

                assertThat(chart).hasSize(9);

                assertThat(chart).element(0)
                        .extracting(
                                ChartBarItem::getLabel, ChartBarItem::getOutlay
                        )
                        .containsExactly(
                                "식비", 10000L
                        );
                assertThat(chart).element(8)
                        .extracting(
                                ChartBarItem::getLabel, ChartBarItem::getOutlay
                        )
                        .containsExactly(
                                "저축", 0L
                        );
            }
            
            @Test
            @DisplayName("WEEK이면서 선택한 날짜가 없으면 현재 날짜의 주 시작일과 종료일 기간의 내역만 조회한다.")
            void fetchesHistoryByCurrentWeek_whenTypeIsWeekAndSelectedDateIsNull() {
                //given
                clock.set(LocalDate.of(2026, 1, 1));

                //when
                HistoryDashboardResponse result = target.findHistories("week", null, null, null);

                //then
                assertThat(result).isNotNull();

                assertThat(result.getTitle()).isEqualTo("2026년 01월 1주");
                assertThat(result.getStatistics())
                        .extracting(
                                LedgerStatistics::getTotal, LedgerStatistics::getIncome, LedgerStatistics::getOutlay
                        )
                        .containsExactly(2654000L, 2512000L, 142000L);

                assertThat(result.getHistoryGroups()).hasSize(3);

                //then: 차트 데이터 검증
                List<ChartBarItem> chart = result.getChartBarItems();

                assertThat(chart).hasSize(5);

                assertThat(chart).element(0)
                        .extracting(
                                ChartBarItem::getLabel, ChartBarItem::getIncome, ChartBarItem::getOutlay
                        )
                        .containsExactly(
                                "1주", 2512000L, 142000L
                        );

                assertThat(chart).element(4)
                        .extracting(
                                ChartBarItem::getLabel, ChartBarItem::getIncome, ChartBarItem::getOutlay
                        )
                        .containsExactly(
                                "5주", 0L, 0L
                        );
            }
            
            @Test
            @DisplayName("선택한 날짜가 있으면 해당 날짜의 type별 기간의 내역만 조회한다.")
            void fetchesHistoryBySelectedDateAndType_whenSelectedDateIsGiven() {
            	//given
                String type = "month";
                Integer year = 2025;
                Integer month = 12;
            	
            	//when
                HistoryDashboardResponse result = target.findHistories(type, year, month, null);
            	
            	//then
                assertThat(result).isNotNull();

                assertThat(result.getTitle()).isEqualTo("2025년 12월");
                assertThat(result.getStatistics())
                        .extracting(
                                LedgerStatistics::getTotal, LedgerStatistics::getIncome, LedgerStatistics::getOutlay
                        )
                        .containsExactly(65000L, 50000L, 15000L);

                assertThat(result.getHistoryGroups()).hasSize(1);

                //then: 차트 데이터 검증
                List<ChartBarItem> chart = result.getChartBarItems();

                assertThat(chart).hasSize(9);

                assertThat(chart).element(0)
                        .extracting(
                                ChartBarItem::getLabel, ChartBarItem::getOutlay
                        )
                        .containsExactly(
                                "식비", 15000L
                        );
                assertThat(chart).element(8)
                        .extracting(
                                ChartBarItem::getLabel, ChartBarItem::getOutlay
                        )
                        .containsExactly(
                                "저축", 0L
                        );
            }

            @Test
            @DisplayName("기간 내 내역이 없어도 정상적으로 조회된다.")
            void returnsEmptyList_whenNoDataInPeriod() {
            	//given
                String type = "week";
                Integer year = 2024;
                Integer month = 6;
                Integer week = 3;

                //when
                HistoryDashboardResponse result = target.findHistories(type,  year, month, week);

                //then
                assertThat(result).isNotNull();

                assertThat(result.getTitle()).isEqualTo("2024년 06월 3주");
                assertThat(result.getStatistics())
                        .extracting(
                                LedgerStatistics::getTotal, LedgerStatistics::getIncome, LedgerStatistics::getOutlay
                        )
                        .containsOnly(0L);

                assertThat(result.getHistoryGroups()).isEmpty();
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("유효하지 않은 type이면 예외를 발생시킨다.")
            void defaultToMonthType_whenTypeIsInvalid() {
                //given
                String type = "none";

                //when
                Throwable throwable = catchThrowable(() -> target.findHistories(type, null, null, null));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(POLICY_VIOLATION)
                        .hasWork("HistoryType 변환")
                        .hasCauseMessage("지원하지 않은 HistoryType")
                        .hasTarget(HistoryType.class)
                        .hasValue(type);
            }

            @Test
            @DisplayName("연이 없고 다른 날짜가 있으면 예외를 발생시킨다.")
            void throwsException_whenYearIsMissingAndOtherDateExists() {
            	//given
                int month = 2;
            	
            	//when
                Throwable throwable = catchThrowable(() -> target.findHistories(HistoryType.YEAR.name(), null, month, null));
            	
            	//then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(POLICY_VIOLATION)
                        .hasWork("연도 검증")
                        .hasValue("year", null)
                        .hasCauseMessage("내역 조회에 필요한 연도 누락");
            }

            @Test
            @DisplayName("월이 없고 다른 날짜가 있으면 예외를 발생시킨다")
            void throwsException_whenMonthIsMissingAndOtherDateExists() {
                //when
                Throwable throwable = catchThrowable(() -> target.findHistories(HistoryType.MONTH.name(), 2026, null, 2));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(POLICY_VIOLATION)
                        .hasWork("월 검증")
                        .hasValue("month", null)
                        .hasCauseMessage("내역 조회에 필요한 월 누락");
            }

            @Test
            @DisplayName("주가 없고 다른 날짜가 있으면 예외를 발생시킨다")
            void throwsException_whenWeekIsMissingAndOtherDateExists() {
                //when
                Throwable throwable = catchThrowable(() -> target.findHistories(HistoryType.WEEK.name(), 2026, 2, null));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(POLICY_VIOLATION)
                        .hasWork("주 검증")
                        .hasValue("week", null)
                        .hasCauseMessage("내역 조회에 필요한 주 누락");
            }

        }

    }


    @Nested
    @Import(TimeConfig.class)
    @WithMockCustomUser(memberId = "member1")
    @Sql(
            scripts = {"/sql/ledger-history-test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
    )
    @Sql(
            scripts = "/sql/clear-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
    )
    @DisplayName("내역을 검색 조건으로 조회할 때")
    class SearchHistories {

        @Nested
        @DisplayName("성공")
        class Success {

            @BeforeEach
            void setUp() {
                clock.set(LocalDate.of(2026, 1, 1));
            }

            @Test
            @DisplayName("전체 검색이면 기간 내에 작성된 내역을 조회한다.")
            void returnsTransactions_whenSearchTypeIsAll() {
            	//given
                LedgerSearchRequest request = LedgerSearchRequest.builder()
                        .type("year")
                        .menu("all")
                        .build();

            	//when
                List<LedgerHistoryDisplay> result = target.searchLedgersByCondition(request);
            	
            	//then
            	assertThat(result)
                        .extracting(LedgerHistoryDisplay::getRows)
                        .hasSize(5);
            }
            
            @Test
            @DisplayName("수입 검색이면 기간 내에 작성된 수입 내역을 조회한다.")
            void returnsIncomeTransactions_whenSearchTypeIsIncome() {
                //given
                LedgerSearchRequest request = LedgerSearchRequest.builder()
                        .type("month")
                        .menu("type")
                        .categories(List.of("010000"))
                        .build();

                //when
                List<LedgerHistoryDisplay> result = target.searchLedgersByCondition(request);

                //then
                assertThat(result)
                        .extracting(LedgerHistoryDisplay::getRows)
                        .hasSize(2);
            }
            
            @Test
            @DisplayName("지출 검색이면 기간 내에 작성된 지출 내역을 조회한다.")
            void returnsExpenseTransactions_whenSearchTypeIsExpense() {
                //given
                LedgerSearchRequest request = LedgerSearchRequest.builder()
                        .type("month")
                        .menu("type")
                        .categories(List.of("020000"))
                        .build();

                //when
                List<LedgerHistoryDisplay> result = target.searchLedgersByCondition(request);

                //then
                assertThat(result)
                        .extracting(LedgerHistoryDisplay::getRows)
                        .hasSize(3);
            }
            
            @Test
            @DisplayName("카테고리 검색이면 기간 내에 작성된 카테고리 내역을 조회한다.")
            void returnsCategoryTransactions_whenSearchTypeIsCategory() {
                //given
                LedgerSearchRequest request = LedgerSearchRequest.builder()
                        .type("month")
                        .menu("category")
                        .categories(List.of("020301", "020402"))
                        .build();

                //when
                List<LedgerHistoryDisplay> result = target.searchLedgersByCondition(request);

                //then
                assertThat(result)
                        .extracting(LedgerHistoryDisplay::getRows)
                        .hasSize(2);
            }
            
            @Test
            @DisplayName("메모 검색이면 기간 내에 작성된 메모 내역을 조회한다.")
            void returnsMemoTransactions_whenSearchTypeIsMemo() {
                //given
                LedgerSearchRequest request = LedgerSearchRequest.builder()
                        .type("month")
                        .menu("memo")
                        .memo("월급")
                        .build();

                //when
                List<LedgerHistoryDisplay> result = target.searchLedgersByCondition(request);

                //then
                assertThat(result)
                        .extracting(LedgerHistoryDisplay::getRows)
                        .hasSize(1);
            }
            
            @Test
            @DisplayName("기간 검색이면 기간 내에 작성된 내역을 조회한다.")
            void returnsPeriodTransactions_whenSearchTypeIsPeriod() {
                //given
                LedgerSearchRequest request = LedgerSearchRequest.builder()
                        .type("year")
                        .menu("period")
                        .fromDate("20260101")
                        .toDate("20260103")
                        .build();

                //when
                List<LedgerHistoryDisplay> result = target.searchLedgersByCondition(request);

                //then
                assertThat(result)
                        .extracting(LedgerHistoryDisplay::getRows)
                        .hasSize(2);
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("검색 키워드가 없으면 예외를 전파한다.")
            void throwsException_whenSearchKeywordIsNull() {
            	//given: 
            	
            	//when:
            	
            	//then:
            	
            }
            
            @Test
            @DisplayName("기간 검색인데 시작일이 종료일 이후면 예외르 전파한다.")
            void throwsException_whenStartDateIsAfterEndDate() {
            	//given: 
            	
            	//when:
            	
            	//then:
            	
            }

            
        }
        
    }

}