package com.moneymanager.ledger.service.application;

import com.moneymanager.global.config.MutableClock;
import com.moneymanager.global.config.TimeConfig;
import com.moneymanager.ledger.domain.dto.request.LedgerSearchRequest;
import com.moneymanager.ledger.domain.dto.response.history.LedgerHistoryDisplay;
import com.moneymanager.ledger.domain.dto.response.history.MenuResponse;
import com.moneymanager.ledger.domain.dto.response.item.CategoryItem;
import com.moneymanager.ledger.domain.dto.response.item.ChartBarItem;
import com.moneymanager.ledger.domain.dto.response.item.MenuItem;
import com.moneymanager.ledger.domain.enums.HistoryMenu;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.security.WithMockCustomUser;
import org.assertj.core.groups.Tuple;
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

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("내역을 검색 조건으로 조회할 때")
    class SearchHistories {

        @Nested
        @DisplayName("성공")
        class Success {

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


    @Nested
    @Import(TimeConfig.class)
    @WithMockCustomUser(memberId = "member1")
    @Sql(
            scripts = {"/sql/ledger-stat-test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
    )
    @Sql(
            scripts = "/sql/clear-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
    )
    @DisplayName("유형별 차트 데이터를 조회할 때")
    class GetChart {
        
        @Test
        @DisplayName("연간 내역을 조회하면 월별 수입과 지출 통계를 반환한다.")
        void returnsMonthlyStatistics_whenYearIsGiven() {
        	//when
        	List<ChartBarItem> result = target.fetchChartDataByType("year");

        	//then
        	assertThat(result)
                    .isNotEmpty()
                    .hasSize(12);


            assertThat(result.get(0))
                    .extracting(
                            ChartBarItem::getLabel, ChartBarItem::getIncome, ChartBarItem::getOutlay
                    )
                    .containsExactly(
                            "1월", 10000L, 0L
                    );

            assertThat(result.get(11))
                    .extracting(
                            ChartBarItem::getLabel, ChartBarItem::getIncome, ChartBarItem::getOutlay
                    )
                    .containsExactly(
                            "12월", 0L, 0L
                    );
        }
        
        @Test
        @DisplayName("월간 내역을 조회하면 카테고리별 지출 통계를 반환한다.")
        void returnsCategoryStatistics_whenMonthIsGiven() {
            //given
            clock.set(LocalDate.of(2026, 8, 1));

            //when
            List<ChartBarItem> result = target.fetchChartDataByType("month");

            //then
            assertThat(result.get(0))
                    .extracting(
                            ChartBarItem::getLabel, ChartBarItem::getOutlay
                    )
                    .containsExactly(
                            "식비", 3000L
                    );
            assertThat(result.get(8))
                    .extracting(
                            ChartBarItem::getLabel, ChartBarItem::getOutlay
                    )
                    .containsExactly(
                            "저축", 0L
                    );
        }
        
        @Test
        @DisplayName("주간 내역을 조회하면 주별 수입과 지출 통계를 반환한다.")
        void returnsWeeklyStatistics_whenWeekIsGiven() {
            //given
            clock.set(LocalDate.of(2026, 8, 1));

            //when
            List<ChartBarItem> result = target.fetchChartDataByType("week");

            //then
            assertThat(result.get(0))
                    .extracting(
                            ChartBarItem::getLabel, ChartBarItem::getIncome, ChartBarItem::getOutlay
                    )
                    .containsExactly(
                            "1주", 500L, 0L
                    );

            assertThat(result.get(5))
                    .extracting(
                            ChartBarItem::getLabel, ChartBarItem::getIncome, ChartBarItem::getOutlay
                    )
                    .containsExactly(
                            "6주", 0L, 0L
                    );
        }
        
        @Test
        @DisplayName("내역이 없으면 빈 목록을 반화한다.")
        void returnsEmptyList_whenDataDoesNotExist() {
            //given
            clock.set(LocalDate.of(2026, 9, 1));

            //when
            List<ChartBarItem> result = target.fetchChartDataByType("week");
        	
        	//then
            assertThat(result).hasSize(5);

            assertThat(result)
                    .extracting(ChartBarItem::getIncome, ChartBarItem::getOutlay)
                    .containsOnly(
                            Tuple.tuple(0L, 0L)
                    );
        }

    }

}