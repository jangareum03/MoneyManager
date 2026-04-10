package com.moneymanager.unit.service.ledger;

import com.moneymanager.domain.global.enums.DatePatterns;
import com.moneymanager.domain.global.vo.DateRange;
import com.moneymanager.domain.ledger.dto.query.LedgerHistoryQuery;
import com.moneymanager.domain.ledger.dto.response.HistoryDashboardResponse;
import com.moneymanager.domain.ledger.dto.response.HistoryItem;
import com.moneymanager.domain.ledger.enums.HistoryType;
import com.moneymanager.domain.ledger.policy.LedgerHistoryPolicy;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.utils.date.DateTimeUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.ledger<br>
 * 파일이름       : LedgerReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 9<br>
 * 설명              : LedgerReadService 클래스 로직을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 4. 9</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class LedgerReadServiceTest {

	@InjectMocks
	private LedgerReadService service;

	@Mock
	private SecurityUtil securityUtil;

	@Mock
	private LedgerHistoryPolicy ledgerHistoryPolicy;

	@Mock
	private LedgerRepository ledgerRepository;

	private static final String MEMBER_ID = "test";
	private static final String TITLE = "제목";
	private static final String CATEGORY = "020101";
	private static final String DATE_FORMAT = DatePatterns.DATE_DOT_WITH_DAY.getPattern();


	//==================[ getHistoryDashboard ]==================
	@ParameterizedTest
	@EnumSource(HistoryType.class)
	@DisplayName("내역이 존재하면 대시보드를 정상 반환한다.")
	void getHistoryDashboard_success(HistoryType type) {
		//given
		DateRange dateRange = range(2026, 1, 1, 2026, 1, 31);
		stubHistoryDashboard(type, dateRange, List.of());

		//when
		HistoryDashboardResponse result = service.getHistoryDashboard(type);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo(TITLE);

		verifyCHistoryDashboard(type, dateRange);
	}


	@Test
	@DisplayName("작성된 가계부가 없으면 빈 내역을 반환한다.")
	void getHistoryDashboard_success_emptyHistories() {
		//given
		HistoryType type = HistoryType.MONTH;
		DateRange dateRange = range(2026, 1, 1, 2026, 1,31);

		stubHistoryDashboard(type, dateRange, Collections.emptyList());

		//when
		HistoryDashboardResponse result = service.getHistoryDashboard(type);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getHistoryGroups()).isEmpty();

		verifyCHistoryDashboard(type, dateRange);
	}


	@Test
	@DisplayName("날짜가 null인 내역은 제외된다.")
	void getHistoryDashboard_success_excludeNullDateHistory() {
		//given
		HistoryType type = HistoryType.YEAR;
		DateRange dateRange = range(2026, 1, 1, 2026, 1, 31);

		LedgerHistoryQuery h1 = mock(LedgerHistoryQuery.class);
		when(h1.getDate()).thenReturn(null);

		LocalDate date = LocalDate.of(2026, 1, 1);
		LedgerHistoryQuery h2 = history(date);

		stubHistoryDashboard(type, dateRange, List.of(h1, h2));

		//when
		HistoryDashboardResponse result = service.getHistoryDashboard(type);

		//then
		assertThat(result).isNotNull();

		Map<String, List<HistoryItem>> groups = result.getHistoryGroups();
		assertThat(groups).hasSize(1);
		assertThat(groups).containsKeys(DateTimeUtils.formatDate(date, DATE_FORMAT));
		assertThat(groups).doesNotContainKey(null);

		verifyCHistoryDashboard(type, dateRange);
	}


	@Test
	@DisplayName("같은 날짜의 내역은 하나의 그룹으로 묶인다.")
	void getHistoryDashboard_success_groupedSameDates() {
		//given
		HistoryType type = HistoryType.WEEK;
		DateRange dateRange = range(2026, 1, 1, 2026, 1, 7);

		LocalDate today = LocalDate.of(2026, 1, 2);
		LocalDate yesterday = today.minusDays(1);

		LedgerHistoryQuery h1 = history(today);
		LedgerHistoryQuery h2 = history(yesterday);
		LedgerHistoryQuery h3 = history(yesterday);

		stubHistoryDashboard(type, dateRange, List.of(h1, h2, h3));

		//when
		Map<String, List<HistoryItem>> result = service.getHistoryDashboard(type).getHistoryGroups();

		//then
		assertThat(result).hasSize(2);

		assertThat(result.get(DateTimeUtils.formatDate(yesterday, DATE_FORMAT))).hasSize(2);
		assertThat(result.get(DateTimeUtils.formatDate(today, DATE_FORMAT))).hasSize(1);

		verifyCHistoryDashboard(type, dateRange);
	}


	@Test
	@DisplayName("내역은 거래일자 최신순부터 정렬된다.")
	void getHistoryDashboard_success_descOrderByDate() {
		//given
		HistoryType type = HistoryType.YEAR;
		DateRange dateRange = range(2026, 1, 1, 2026, 12, 31);

		LocalDate today = LocalDate.of(2026, 1, 2);
		LocalDate yesterday = today.minusDays(1);

		LedgerHistoryQuery h1 = history(yesterday);
		LedgerHistoryQuery h2 = history(today);

		stubHistoryDashboard(type, dateRange, List.of(h1, h2));

		//when
		Map<String, List<HistoryItem>> groups = service.getHistoryDashboard(type).getHistoryGroups();
		List<String> result = new LinkedList<>(groups.keySet());

		//then
		assertThat(result).containsExactly(DateTimeUtils.formatDate(today, DATE_FORMAT), DateTimeUtils.formatDate(yesterday, DATE_FORMAT));

		verifyCHistoryDashboard(type, dateRange);
	}

	private DateRange range(int fromYear, int fromMonth, int fromDay, int toYear, int toMonth, int toDay) {
		return new DateRange(
				LocalDate.of(fromYear, fromMonth, fromDay),
				LocalDate.of(toYear, toMonth, toDay)
		);
	}

	private LedgerHistoryQuery history(LocalDate localDate) {
		LedgerHistoryQuery history = mock(LedgerHistoryQuery.class);

		when(history.getDate()).thenReturn(localDate);
		when(history.getCategoryCode()).thenReturn(CATEGORY);

		return history;
	}

	private void stubHistoryDashboard(HistoryType type, DateRange dateRange, List<LedgerHistoryQuery> historyQueries) {
		when(securityUtil.getMemberId()).thenReturn(MEMBER_ID);

		when(ledgerHistoryPolicy.calculateDateRange(eq(type), any(LocalDate.class))).thenReturn(dateRange);
		doNothing().when(ledgerHistoryPolicy).validate(dateRange);
		when(ledgerHistoryPolicy.getTitleByHistoryType(type)).thenReturn(TITLE);

		when(ledgerRepository.findHistoriesByMemberAndDateBetween(MEMBER_ID, dateRange.getFrom(), dateRange.getTo()))
				.thenReturn(historyQueries);
	}

	private void verifyCHistoryDashboard(HistoryType type, DateRange dateRange) {
		verify(securityUtil).getMemberId();
		verify(ledgerHistoryPolicy).calculateDateRange(eq(type), any(LocalDate.class));
		verify(ledgerHistoryPolicy).validate(dateRange);
		verify(ledgerRepository).findHistoriesByMemberAndDateBetween(MEMBER_ID, dateRange.getFrom(), dateRange.getTo());
	}
}
