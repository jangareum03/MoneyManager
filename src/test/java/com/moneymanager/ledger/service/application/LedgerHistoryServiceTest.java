package com.moneymanager.ledger.service.application;

import com.moneymanager.global.security.CurrentUser;
import com.moneymanager.ledger.domain.dto.response.history.HistoryDashboardResponse;
import com.moneymanager.ledger.domain.dto.response.history.LedgerHistoryDisplay;
import com.moneymanager.ledger.domain.dto.response.history.LedgerStatistics;
import com.moneymanager.ledger.domain.dto.response.item.HistoryItem;
import com.moneymanager.ledger.domain.dto.vo.LedgerPeriod;
import com.moneymanager.ledger.domain.enums.HistoryMenu;
import com.moneymanager.ledger.domain.enums.HistoryType;
import com.moneymanager.ledger.service.policy.LedgerPolicy;
import com.moneymanager.ledger.service.read.LedgerReadService;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.response.LedgerHistoryQueryTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @InjectMocks
    LedgerHistoryService target;

    @Mock
    CurrentUser currentUser;

    @Mock
    LedgerPolicy ledgerPolicy;

    @Mock
    LedgerReadService ledgerReadService;

    @Nested
    @DisplayName("내역 유형별 가계부 조회할 때")
    class GetHistories {

        @BeforeEach
        void setUp() {
            when(currentUser.getMemberId())
                    .thenReturn(MemberTestData.DEFAULT_ID);

            when(ledgerPolicy.resolveHistoryPeriod(any(HistoryType.class)))
                    .thenReturn(LedgerPeriod.of(
                            LocalDate.of(2026, 3, 1),
                            LocalDate.of(2026, 3, 31)
                    ));

            when(ledgerReadService.findLedgerByDate(
                            MemberTestData.DEFAULT_ID,
                            LocalDate.of(2026, 3, 1),
                            LocalDate.of(2026, 3, 31)
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

            when(ledgerPolicy.getTitleByHistoryType(any(HistoryType.class)))
                    .thenReturn("2026년 03월");
        }

        @Test
        @DisplayName("기간내 작성한 가계부 내역을 조회한다.")
        void fetchesLedgerHistories_whenDateRangeIsValid() {
            //given
            String type = "month";

            //when
            HistoryDashboardResponse result = target.searchLedgersByDate(type);

            //then
            assertThat(result).isNotNull();

            assertThat(result.getTitle()).isEqualTo("2026년 03월");

            assertThat(result.getMenu())
                    .hasSize(5)
                    .extracting(HistoryMenu::name)
                    .containsExactly("ALL", "CATEGORY", "SUB_CATEGORY", "MEMO", "DATE");

            assertThat(result.getStatistics())
                    .extracting(
                            LedgerStatistics::getTotal, LedgerStatistics::getIncome, LedgerStatistics::getOutlay
                    )
                    .containsExactly(120500L, 40000L, 80500L);

            //then: 가계부 내역 그룹 검증
            List<LedgerHistoryDisplay> historyDisplays = result.getHistoryGroups();

            assertThat(historyDisplays).hasSize(3);

            //then: 날짜 그룹 검증
            assertThat(historyDisplays)
                    .extracting(LedgerHistoryDisplay::getDate)
                            .containsExactly("2026. 03. 01 (일)", "2026. 03. 17 (화)", "2026. 03. 31 (화)");

            //then: 각 그룹별 요소 검증
            assertThat(historyDisplays.get(1).getRows()).hasSize(2);

            assertThat(historyDisplays.get(1).getRows().get(0))
                    .extracting(HistoryItem::getAmount)
                    .containsExactly(20000L, 30000L, 10000L);

            assertThat(historyDisplays.get(1).getRows().get(1))
                    .extracting(HistoryItem::getAmount)
                    .containsExactly(500L);
        }

        @Test
        @DisplayName("잘못된 내역 유형이면 MONTH으로 진행하여 내역을 조회한다.")
        void fetchesLedgerHistoriesByMonth_whenHistoryTypeIsInvalid() {
            //given
            String type = "none";

            //when
            HistoryDashboardResponse result = target.searchLedgersByDate(type);

            //then
            verify(ledgerPolicy).resolveHistoryPeriod(eq(HistoryType.MONTH));
        }

        @Test
        @DisplayName("기간 내 작성한 가계부가 없으면 빈 목록과 통계가 0으로 조회한다.")
        void returnsEmptyListAndZeroStatistics_whenLedgerDoesNotExist() {
            //given
            String type = "week";

            when(ledgerReadService.findLedgerByDate(
                    MemberTestData.DEFAULT_ID,
                    LocalDate.of(2026, 3, 1),
                    LocalDate.of(2026, 3, 31)
            ))
                    .thenReturn(List.of());

            //when
            HistoryDashboardResponse result = target.searchLedgersByDate(type);

            //then
            assertThat(result.getStatistics())
                    .extracting(
                            LedgerStatistics::getTotal, LedgerStatistics::getIncome, LedgerStatistics::getOutlay
                    )
                    .containsExactly(0L, 0L, 0L);

            //then: 가계부 내역 그룹 검증
            List<LedgerHistoryDisplay> historyDisplays = result.getHistoryGroups();

            assertThat(historyDisplays).hasSize(0);
        }

    }

}