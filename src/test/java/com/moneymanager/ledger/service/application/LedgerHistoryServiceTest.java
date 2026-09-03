package com.moneymanager.ledger.service.application;

import com.moneymanager.global.config.MutableClock;
import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.security.CurrentUser;
import com.moneymanager.ledger.domain.dto.request.LedgerSearchRequest;
import com.moneymanager.ledger.domain.dto.response.history.HistoryDateFilter;
import com.moneymanager.ledger.domain.dto.response.history.LedgerHistoryDisplay;
import com.moneymanager.ledger.domain.dto.response.history.LedgerSearchCondition;
import com.moneymanager.ledger.domain.dto.response.history.MenuResponse;
import com.moneymanager.ledger.domain.dto.response.item.ChartBarItem;
import com.moneymanager.ledger.domain.dto.response.item.MenuItem;
import com.moneymanager.ledger.domain.dto.vo.LedgerPeriod;
import com.moneymanager.ledger.domain.enums.HistoryMenu;
import com.moneymanager.ledger.domain.enums.HistoryType;
import com.moneymanager.ledger.domain.query.LedgerHistoryQuery;
import com.moneymanager.ledger.service.policy.LedgerPolicy;
import com.moneymanager.ledger.service.read.CategoryReadService;
import com.moneymanager.ledger.service.read.LedgerReadService;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.response.LedgerHistoryQueryTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Named.named;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.application<br>
 * 파일이름       : LedgerHistoryServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 28<br>
 * 설명              : LedgerHistoryService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 8. 28</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class LedgerHistoryServiceTest {

    LedgerHistoryService target;

    @Mock
    CurrentUser currentUser;

    @Mock
    LedgerPolicy ledgerPolicy;

    @Mock
    LedgerReadService ledgerReadService;

    @Mock
    CategoryReadService categoryReadService;

    MutableClock clock = new MutableClock();

    @BeforeEach
    void setUp() {
        target = new LedgerHistoryService(
                clock,
                currentUser,
                ledgerPolicy,
                ledgerReadService,
                categoryReadService
        );
    }

    @Nested
    @DisplayName("내역 유형별 가계부 조회할 때")
    class GetHistories {

        LedgerPeriod period = LedgerPeriod.of(
                LocalDate.now(),
                LocalDate.now()
        );

        List<LedgerHistoryQuery> historyQueries = List.of();
        List<ChartBarItem> charts = List.of();
        String title = "제목";

        @Nested
        @DisplayName("성공")
        class Success {

            @BeforeEach
            void setUp() {
                when(currentUser.getMemberId())
                        .thenReturn(MemberTestData.DEFAULT_ID);

                when(ledgerPolicy.resolveHistoryPeriod(any(), any()))
                        .thenReturn(period);

                when(ledgerReadService.findLedgerByDate(any(), any(), any()))
                        .thenReturn(historyQueries);

                when(ledgerPolicy.getTitleByHistoryType(any(), any()))
                        .thenReturn(title);

                when(ledgerPolicy.resolveChartPeriod(any(), any()))
                        .thenReturn(period);

                when(ledgerReadService.generateChartDataByType(any(), any(), any(), any()))
                        .thenReturn(charts);
            }

            @Test
            @DisplayName("지정된 날짜가 없다면 현재 날짜로 진행한다.")
            void defaultsToCurrentDate_whenDateIsNull() {
                //given
                String type = "month";
                LocalDate date = LocalDate.of(2026, 3, 10);

                clock.set(date);

                when(ledgerPolicy.resolveHistoryPeriod(eq(HistoryType.MONTH), any(LocalDate.class)))
                        .thenReturn(
                                LedgerPeriod.of(
                                        date.withDayOfMonth(1),
                                        date.withDayOfMonth(31)
                                )
                        );

                //when
                target.findHistories(type, null, null, null);

                //then
                verify(ledgerPolicy).resolveHistoryPeriod(eq(HistoryType.MONTH), eq(date));
                verify(ledgerPolicy, never()).validateHistoryDate(any(HistoryType.class), any(HistoryDateFilter.class));
            }

            @ParameterizedTest
            @MethodSource("validHistoryDateFilters")
            @DisplayName("지정된 날짜가 하나라도 있다면 지정된 날짜로 진행한다.")
            void usesGivenDates_whenAtLeastOneDateIsGiven(LocalDate expected, Integer year, Integer month, Integer week) {
                //given
                LocalDate fromDate = expected.withDayOfMonth(1);
                LocalDate toDate = expected.withDayOfMonth(expected.lengthOfMonth());

                when(ledgerPolicy.resolveHistoryPeriod(eq(HistoryType.WEEK), eq(expected)))
                        .thenReturn(
                                LedgerPeriod.of(
                                        fromDate,
                                        toDate
                                )
                        );

                when(ledgerReadService.findLedgerByDate(
                        MemberTestData.DEFAULT_ID,
                        fromDate,
                        toDate
                ))
                        .thenReturn(List.of());

                //when
                target.findHistories("week", year, month, week);

                //then
                verify(ledgerPolicy).resolveHistoryPeriod(eq(HistoryType.WEEK), eq(expected));
                verify(ledgerPolicy, times(1)).validateHistoryDate(eq(HistoryType.WEEK), any(HistoryDateFilter.class));
            }

            static Stream<Arguments> validHistoryDateFilters() {
                return Stream.of(
                        Arguments.of(
                                named(
                                        "연도만 있는 경우 (year: 2026)", LocalDate.of(2026, 1, 1)
                                ),
                                2026, null, null
                        ),
                        Arguments.of(
                                named(
                                        "연도와 월이 있는 경우 (year: 2026, month: 5)", LocalDate.of(2026, 5, 1)
                                ),
                                2026, 5, null
                        ),
                        Arguments.of(
                                named(
                                        "연도,월, 주 모두 있는 경우 (year: 2026, month: 8: week: 2)", LocalDate.of(2026, 8, 8)
                                ),
                                2026, 8, 2
                        )
                );
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @ParameterizedTest
            @MethodSource("invalidDateByType")
            @DisplayName("TYPE별 날짜값이 주어지지 않으면 예외를 전파한다.")
            void throwsException_whenDateIsIsNullByTypes(String type, int year, Integer month, Integer week) {
                //given
                clock.set(LocalDate.of(2026, 8, 5));

                when(currentUser.getMemberId())
                        .thenReturn(MemberTestData.DEFAULT_ID);

                doThrow(ApplicationException.class)
                        .when(ledgerPolicy)
                                .validateHistoryDate(any(HistoryType.class), any(HistoryDateFilter.class));

                //when
                assertThatThrownBy(() -> target.findHistories(type, year, month, week))
                        .isInstanceOf(ApplicationException.class);
            }

            static Stream<Arguments> invalidDateByType() {
                return Stream.of(
                        Arguments.of(
                                named("MONTH인데 월이 없는 경우", "month"),
                                2026, null, null
                        ),
                        Arguments.of(
                                named("WEEK인데 주가 없는 경우", "week"),
                                2026, 5, null
                        ),
                        Arguments.of(
                                named("WEEK인데 월이 없는 경우", "week"),
                                2026, null, 2
                        )
                );
            }

        }

    }


    @Nested
    @DisplayName("내역을 검색 조건으로 조회할 때")
    class SearchHistories {

        @BeforeEach
        void setUp() {
            when(currentUser.getMemberId())
                    .thenReturn(MemberTestData.DEFAULT_ID);
        }

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("유효하지 않은 내역 유형과 메뉴는 기본값으로 설정한다.")
            void setsDefault_whenTransactionTypeAndMenuAreInvalid() {
                //given
                LedgerSearchRequest request = LedgerSearchRequest.builder()
                        .type("error")
                        .menu(null)
                        .build();

                when(ledgerPolicy.resolveHistoryPeriod(any(HistoryType.class), any(LocalDate.class)))
                        .thenReturn(LedgerPeriod.of(
                                LocalDate.of(2026, 3, 1),
                                LocalDate.of(2026, 3, 31)
                        ));

                when(ledgerReadService.findLedgerByCondiction(
                        eq(MemberTestData.DEFAULT_ID),
                        eq(LocalDate.of(2026, 3, 1)),
                        eq(LocalDate.of(2026, 3, 31)),
                        any(LedgerSearchCondition.class)
                ))
                        .thenReturn(List.of(
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 1)).amount(5000L).build(),
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 1)).amount(5000L).build(),
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 17)).categoryCode("020101").amount(20000L).build(),
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 17)).amount(30000L).build(),
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 17)).categoryCode("020201").amount(10000L).build(),
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 17)).categoryCode("020301").amount(500L).build(),
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 31)).categoryCode("020202").amount(50000L).build()
                        ));

                //when
                List<LedgerHistoryDisplay> result = target.searchLedgersByCondition(request);

                //then
                InOrder inOrder = Mockito.inOrder(currentUser, ledgerPolicy, ledgerReadService);

                inOrder.verify(currentUser).getMemberId();
                inOrder.verify(ledgerPolicy).validateSearchCondition(eq(HistoryMenu.ALL), any(LedgerSearchRequest.class));
                inOrder.verify(ledgerPolicy).resolveHistoryPeriod(eq(HistoryType.MONTH), any(LocalDate.class));
                inOrder.verify(ledgerReadService).findLedgerByCondiction(eq(MemberTestData.DEFAULT_ID), any(LocalDate.class), any(LocalDate.class), any(LedgerSearchCondition.class));
            }

            @Test
            @DisplayName("기간 메뉴는 요청한 날짜로 가계부 내역을 조회한다.")
            void fetchesLedgers_whenPeriodMenuIsGiven() {
                //given
                LedgerSearchRequest request = LedgerSearchRequest.builder()
                        .type("year")
                        .menu("period")
                        .fromDate("20260315")
                        .toDate("20260317")
                        .build();

                when(ledgerReadService.findLedgerByCondiction(
                        anyString(),
                        any(LocalDate.class),
                        any(LocalDate.class),
                        any(LedgerSearchCondition.class)
                ))
                        .thenReturn(List.of());

                //when
                List<LedgerHistoryDisplay> result = target.searchLedgersByCondition(request);

                //then
                verify(ledgerReadService).findLedgerByCondiction(
                        eq(MemberTestData.DEFAULT_ID),
                        eq(LocalDate.of(2026, 3, 15)),
                        eq(LocalDate.of(2026, 3, 17)),
                        any(LedgerSearchCondition.class)
                );
            }

            @ParameterizedTest
            @MethodSource("validLedgerSearchRequests")
            @DisplayName("메뉴별 LedgerSearchCondition을 다르게 생성한다.")
            void createsLedgerSearchCondition_whenMenuTypeIsGiven(LedgerSearchRequest request, HistoryMenu menu, String keyword, List<String> keywords) {
                //given
                when(ledgerPolicy.resolveHistoryPeriod(any(HistoryType.class), any(LocalDate.class)))
                        .thenReturn(LedgerPeriod.of(
                                LocalDate.of(2026, 3, 1),
                                LocalDate.of(2026, 3, 31)
                        ));

                when(ledgerReadService.findLedgerByCondiction(
                        anyString(),
                        any(LocalDate.class),
                        any(LocalDate.class),
                        any(LedgerSearchCondition.class)
                ))
                        .thenReturn(List.of());

                //when
                target.searchLedgersByCondition(request);

                //then
                ArgumentCaptor<LedgerSearchCondition> captor = ArgumentCaptor.forClass(LedgerSearchCondition.class);

                verify(ledgerReadService).findLedgerByCondiction(
                        eq(MemberTestData.DEFAULT_ID),
                        any(LocalDate.class),
                        any(LocalDate.class),
                        captor.capture()
                );

                LedgerSearchCondition searchCondition = captor.getValue();

                assertThat(searchCondition.getMenu()).isSameAs(menu);
                assertThat(searchCondition.getKeyword()).isEqualTo(keyword);
                assertThat(searchCondition.getKeywords()).isEqualTo(keywords);
            }

            static Stream<Arguments> validLedgerSearchRequests() {
                return Stream.of(
                        Arguments.of(
                                named("메뉴가 전체인 경우", LedgerSearchRequest.builder().menu("all").build()),
                                HistoryMenu.ALL,
                                null,
                                null
                        ),
                        Arguments.of(
                                named("메뉴가 수입/지출인 경우", LedgerSearchRequest.builder().menu("type").categories(List.of("010000")).build()),
                                HistoryMenu.CATEGORY,
                                null,
                                List.of("010000")
                        ),
                        Arguments.of(
                                named("메뉴가 카테고리인 경우", LedgerSearchRequest.builder().menu("category").categories(List.of("010101", "020101")).build()),
                                HistoryMenu.SUB_CATEGORY,
                                null,
                                List.of("010101", "020101")
                        ),
                        Arguments.of(
                                named("메뉴가 메모인 경우", LedgerSearchRequest.builder().menu("memo").memo("메모").build()),
                                HistoryMenu.MEMO,
                                "메모",
                                null
                        ),
                        Arguments.of(
                                named("메뉴가 기간인 경우", LedgerSearchRequest.builder().menu("period").fromDate("20260101").toDate("20260115").build()),
                                HistoryMenu.PERIOD,
                                null,
                                null
                        )
                );
            }

            @Test
            @DisplayName("내역이 존재하면 날짜별로 그룹화하여 반환한다.")
            void returnsGroupedLedgersByDate_whenLedgersExist() {
                //given
                LedgerSearchRequest request = LedgerSearchRequest.builder()
                        .type("month")
                        .menu("all")
                        .build();

                when(ledgerPolicy.resolveHistoryPeriod(any(HistoryType.class), any(LocalDate.class)))
                        .thenReturn(LedgerPeriod.of(
                                LocalDate.of(2026, 3, 1),
                                LocalDate.of(2026, 3, 31)
                        ));

                when(ledgerReadService.findLedgerByCondiction(
                        eq(MemberTestData.DEFAULT_ID),
                        eq(LocalDate.of(2026, 3, 1)),
                        eq(LocalDate.of(2026, 3, 31)),
                        any(LedgerSearchCondition.class)
                ))
                        .thenReturn(List.of(
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 1)).amount(5000L).build(),
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 1)).amount(5000L).build(),
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 17)).categoryCode("020101").amount(20000L).build(),
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 17)).amount(30000L).build(),
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 17)).categoryCode("020201").amount(10000L).build(),
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 17)).categoryCode("020301").amount(500L).build(),
                                LedgerHistoryQueryTestFixture.builder().date(LocalDate.of(2026, 3, 31)).categoryCode("020202").amount(50000L).build()
                        ));

                //when
                List<LedgerHistoryDisplay> result = target.searchLedgersByCondition(request);

                //then
                assertThat(result)
                        .extracting(
                                LedgerHistoryDisplay::getDate
                        )
                        .containsExactly("2026. 03. 31 (화)", "2026. 03. 17 (화)", "2026. 03. 01 (일)");

                assertThat(result)
                        .extracting(
                                LedgerHistoryDisplay::getRows
                        )
                        .hasSize(3);
            }

            @Test
            @DisplayName("내역이 존재하지 않으면 빈 목록을 반환한다.")
            void returnsEmptyList_whenLedgersDoesNotExist() {
                //given
                LedgerSearchRequest request = LedgerSearchRequest.builder()
                        .type("month")
                        .menu("all")
                        .build();

                when(ledgerPolicy.resolveHistoryPeriod(any(HistoryType.class), any(LocalDate.class)))
                        .thenReturn(LedgerPeriod.of(
                                LocalDate.of(2026, 3, 1),
                                LocalDate.of(2026, 3, 31)
                        ));

                when(ledgerReadService.findLedgerByCondiction(
                        eq(MemberTestData.DEFAULT_ID),
                        eq(LocalDate.of(2026, 3, 1)),
                        eq(LocalDate.of(2026, 3, 31)),
                        any(LedgerSearchCondition.class)
                ))
                        .thenReturn(List.of());

                //when
                List<LedgerHistoryDisplay> result = target.searchLedgersByCondition(request);

                //then
                assertThat(result).isEmpty();
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("메뉴 조건 검증에 실패하면 예외를 전파한다.")
            void throwsException_whenMenuValidationFails() {
                //given
                LedgerSearchRequest request = LedgerSearchRequest.builder()
                        .type("month")
                        .menu("all")
                        .build();

                doThrow(ApplicationException.class)
                        .when(ledgerPolicy)
                        .validateSearchCondition(any(), eq(request));

                //when
                assertThatThrownBy(() -> target.searchLedgersByCondition(request))
                        .isInstanceOf(ApplicationException.class);
            }

            @Test
            @DisplayName("기간 메뉴의 시작일이 종료일보다 이후면 예외를 전파한다.")
            void throwsException_whenStartDateIsAfterEndDate() {
                //given
                LedgerSearchRequest request = LedgerSearchRequest.builder()
                        .type("year")
                        .menu("period")
                        .fromDate("20260101")
                        .toDate("20251231")
                        .build();

                //when
                assertThatThrownBy(() -> target.searchLedgersByCondition(request))
                        .isInstanceOf(ApplicationException.class);
            }

        }

    }


    @Nested
    @DisplayName("내역 조회에 필요한 메뉴를 생성할 때")
    class CreateMenu {

        @Test
        @DisplayName("연간 내역이면 모든 메뉴를 생성한다.")
        void createsAllMenus_whenPeriodIsYearly() {
            //given
            String type = "year";

            //when
            MenuResponse result = target.buildSubMenu(type);

            //then
            assertThat(result).isNotNull();

            List<MenuItem> menus = result.getMenus();
            assertThat(menus).hasSize(HistoryMenu.values().length);
        }

        @ParameterizedTest
        @ValueSource(strings = {"month", "week"})
        @DisplayName("월과 주간 내역이면 기간 메뉴는 제외하고 생성한다.")
        void createsMenusExceptPeriodMenu_whenPeriodIsMonthlyOrWeekly(String type) {
            //when
            MenuResponse result = target.buildSubMenu(type);

            //then
            assertThat(result).isNotNull();

            List<MenuItem> menus = result.getMenus();
            assertThat(menus).hasSize(HistoryMenu.values().length - 1);
        }

        @Test
        @DisplayName("잘못된 내역이면 월 내역으로 메뉴를 생성한다.")
        void createsMonthlyMenus_whenTypeIsInvalid() {
            //given
            String type = "none";

            //when
            MenuResponse result = target.buildSubMenu(type);

            //then
            assertThat(result).isNotNull();

            List<MenuItem> menus = result.getMenus();
            assertThat(menus).hasSize(HistoryMenu.values().length - 1);
        }

    }

}