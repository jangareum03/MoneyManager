package com.moneymanager.unit.repository.ledger;

import com.moneymanager.config.DatabaseConfig;
import com.moneymanager.domain.ledger.dto.query.LedgerHistoryQuery;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.unit.fixture.LedgerFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.repository.ledger<br>
 * 파일이름       : LedgerRepositoryTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 8<br>
 * 설명              : 가계부와 관련된 테이블의 데이터 조작 기능을 검증하는 클래스
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
 * 		 	  <td>26. 4. 8</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
		DatabaseConfig.class,
		LedgerRepository.class
})
public class LedgerRepositoryTest {

	@Autowired private LedgerRepository repository;

	//==================[ findHistoriesByMemberAndDateBetween ]==================
	@Test
	@DisplayName("기간 내에 회원이 작성한 가계부 내역을 조회한다.")
	void findHistoriesByMemberAndDateBetween_existingMember_returnHistories() {
		//given
		String memberId = "test";
		LocalDate start = LocalDate.of(2026, 1, 2);
		LocalDate end = start.plusDays(7);

		saveLedger("test", "20260103", 10000L);
		saveLedger("test", "20260104", 10000L);
		saveLedger("test", "20260105", 10000L);
		saveLedger("UCa12001", "20260105", 10000L);
		saveLedger("test", "20260105", 20000L);

		//when
		List<LedgerHistoryQuery> result = repository.findHistoriesByMemberAndDateBetween(memberId, start, end);

		//then
		assertThat(result).isNotEmpty().hasSize(4);

		assertThat(result)
				.allMatch(r -> {
					LocalDate date = LocalDate.parse(r.getDate(), DateTimeFormatter.BASIC_ISO_DATE);
					return !date.isBefore(start) && !date.isAfter(end);
				});

		assertThat(result)
				.extracting(LedgerHistoryQuery::getAmount)
				.contains(10000L, 20000L);

		assertThat(result)
				.filteredOn(r -> r.getDate().equals("20260105"))
				.hasSize(2);

		assertThat(result)
				.extracting(LedgerHistoryQuery::getDate)
				.isSortedAccordingTo(Comparator.reverseOrder());
	}


	@Test
	@DisplayName("시작일과 종료일에 해당하는 내역이 내림차순으로 조회한다.")
	void findHistoriesByMemberAndDateBetween_includeBoundaryDates_returnHistories() {
		//given
		saveLedger("test", "20260105", 10000L);
		saveLedger("test", "20260107", 20000L);
		saveLedger("test", "20260108", 20000L);

		LocalDate start = LocalDate.of(2026, 1, 5);
		LocalDate end = LocalDate.of(2026, 1, 7);

		//when
		List<LedgerHistoryQuery> result = repository.findHistoriesByMemberAndDateBetween("test", start, end);

		//then
		assertThat(result).hasSize(2);
		assertThat(result)
				.extracting(LedgerHistoryQuery::getDate)
				.containsExactly("20260107", "20260105");
	}


	@Sql(statements = "DELETE FROM ledger WHERE member_id = 'test'", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Test
	@DisplayName("등록한 가계부가 없다면 내역이 조회되지 않는다.")
	void findHistoriesByMemberAndDateBetween_noData_returnEmptyList() {
		//given
		LocalDate start = LocalDate.of(2026, 1, 1);
		LocalDate end = LocalDate.of(2026, 1, 7);

		//when
		List<LedgerHistoryQuery> result = repository.findHistoriesByMemberAndDateBetween("test", start, end);

		//then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("없는 회원의 내역은 조회되지 않는다.")
	void findHistoriesByMemberAndDateBetween_nonExistingMember_returnEmptyList() {
		//given
		LocalDate start = LocalDate.of(2020, 1, 1);
		LocalDate end = LocalDate.of(2026, 12, 31);

		//when
		List<LedgerHistoryQuery> result = repository.findHistoriesByMemberAndDateBetween("noMember", start, end);

		//then
		assertThat(result).isEmpty();
	}

	private void saveLedger(String memberId, String  date, Long amount) {
		Ledger ledger = LedgerFixture.defaultLedger()
						.memberId(memberId)
						.date(date)
						.amount(amount)
						.build();

		repository.save(ledger);
	}
}
