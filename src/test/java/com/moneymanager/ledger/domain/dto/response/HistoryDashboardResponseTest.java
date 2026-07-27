package com.moneymanager.ledger.domain.dto.response;

import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.domain.ledger.dto.response.HistoryDashboardResponse;
import com.moneymanager.domain.ledger.dto.response.HistoryItem;
import com.moneymanager.domain.ledger.dto.response.LedgerStatistics;
import com.moneymanager.domain.ledger.dto.response.MenuItem;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.support.fixture.response.HistoryItemFixture;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.Nledger.domain.dto.response<br>
 * 파일이름       : HistoryDashboardResponseTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 13<br>
 * 설명              : HistoryDashboardResponse 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 7. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class HistoryDashboardResponseTest {

	@Nested
	@DisplayName("HistoryDashboardResponse 변환")
	class OfTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("날짜가 원하는 문자열 포맷으로 변환된다.")
			void returnsFormattedString_whenStringFormatIsGiven() {
				//given: HistoryDashboardResponse 생성하기 위해 필요한 정보가 준비되어 있다.
				String title = "제목";
				List<MenuItem> menu = List.of(
						new MenuItem("메뉴1", "메뉴1"),
						new MenuItem("메뉴2", "메뉴2")
				);
				LedgerStatistics statistics = LedgerStatistics.of(10000L, 5000L);

				Map<LocalDate, List<HistoryItem>> histories = new LinkedHashMap<>();
				histories.put(LocalDate.of(2026, 3, 1), List.of(HistoryItemFixture.income()));

				//when: HistoryDashboardResponse을 생성한다.
				HistoryDashboardResponse result = HistoryDashboardResponse.of(title, menu, statistics, histories);
				
				//then: 지정한 날짜가 원하는 날짜 문자열 포맷으로 변환된다.
				assertThat(result.getHistoryGroups().keySet())
						.containsExactly("2026. 03. 01 (일)");

				//then: 제목, 메뉴, 금액통계는 요청값 그대로 반환된다.
				assertThat(result.getTitle()).isEqualTo(title);
				assertThat(result.getMenu()).isEqualTo(menu);
				assertThat(result.getStatistics())
						.extracting(
								LedgerStatistics::getTotal,
								LedgerStatistics::getIncome,
								LedgerStatistics::getOutlay
						).containsExactly(
								15000L, 10000L, 5000L
						);
			}
			
			@Test
			@DisplayName("HistoryItem 리스트가 순서 그대로 유지된다.")
			void sortsHistoryItemsByOriginalOrder_whenListIsGiven() {
				//given: HistoryDashboardResponse 생성하기 위해 필요한 정보가 준비되어 있다.
				String title = "제목";
				List<MenuItem> menu = List.of(
						new MenuItem("메뉴1", "메뉴1"),
						new MenuItem("메뉴2", "메뉴2")
				);
				LedgerStatistics statistics = LedgerStatistics.of(10000L, 5000L);

				Map<LocalDate, List<HistoryItem>> histories = new LinkedHashMap<>();
				histories.put(
						LocalDate.of(2026, 3, 1),
						List.of(
								HistoryItemFixture.income(),
								HistoryItemFixture.outlay()
						)
				);

				//when: HistoryDashboardResponse을 생성한다.
				HistoryDashboardResponse result = HistoryDashboardResponse.of(title, menu, statistics, histories);
				
				//then: HistoryItem 리스트가 순서대로 유지된다.
				Map<String, List<HistoryItem>> historyGroups = result.getHistoryGroups();

				String key = "2026. 03. 01 (일)";
				assertThat(historyGroups.get(key))
						.hasSize(2)
						.extracting(
								HistoryItem::getAmount,
								HistoryItem::getCategoryType,
								HistoryItem::getCategoryName
						).containsExactly(
								Tuple.tuple(50000L, CategoryType.INCOME, CategoryTestData.SALARY_NAME),
								Tuple.tuple(10000L, CategoryType.OUTLAY, CategoryTestData.FOOD_NAME)
						);
			}
			
			@Test
			@DisplayName("여러 날짜를 모두 변환하며 입력 순서대로 유지된다.")
			void returnsFormattedDatesInInputOrder_whenMultipleDatesAreGiven() {
				//given: HistoryDashboardResponse 생성하기 위해 필요한 정보가 준비되어 있다.
				String title = "제목";
				List<MenuItem> menu = List.of(
						new MenuItem("메뉴1", "메뉴1"),
						new MenuItem("메뉴2", "메뉴2")
				);
				LedgerStatistics statistics = LedgerStatistics.of(10000L, 5000L);

				Map<LocalDate, List<HistoryItem>> histories = new LinkedHashMap<>();
				histories.put(
						LocalDate.of(2026, 3, 1),
						List.of(
								HistoryItemFixture.income(),
								HistoryItemFixture.outlay()
						)
				);
				histories.put(
						LocalDate.of(2026, 3, 3),
						List.of(
								HistoryItemFixture.outlay(),
								HistoryItemFixture.create(5000L, "음메", "월급", "010102"),
								HistoryItemFixture.create(1000L, null, "간식", "020102")
						)
				);

				//when: HistoryDashboardResponse을 생성한다.
				HistoryDashboardResponse result = HistoryDashboardResponse.of(title, menu, statistics, histories);

				//then: 날짜별로 순서대로 유지된다.
				Map<String, List<HistoryItem>> historyGroups = result.getHistoryGroups();

				assertThat(historyGroups.keySet())
						.hasSize(2)
						.containsExactly(
								"2026. 03. 01 (일)", "2026. 03. 03 (화)"
						);

				assertThat(historyGroups.get("2026. 03. 01 (일)")).hasSize(2);
				assertThat(historyGroups.get("2026. 03. 03 (화)")).hasSize(3);
			}
			
			@Test
			@DisplayName("빈 Map이면 그대로 반환된다.")
			void doesNothing_whenMapIsEmpty() {
				//given: HistoryItem을 담은 Map을 빈 맵으로 준비되어 있다.
				String title = "제목";
				List<MenuItem> menu = List.of(
						new MenuItem("메뉴1", "메뉴1"),
						new MenuItem("메뉴2", "메뉴2")
				);
				LedgerStatistics statistics = LedgerStatistics.of(10000L, 5000L);

				Map<LocalDate, List<HistoryItem>> histories = Collections.emptyMap();

				//when: HistoryDashboardResponse을 생성한다.
				HistoryDashboardResponse result = HistoryDashboardResponse.of(title, menu, statistics, histories);
				
				//then: 빈 맵으로 반환된다.
				assertThat(result.getHistoryGroups()).isEmpty();
			}

		}

	}

}