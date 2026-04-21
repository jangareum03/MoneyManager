package com.moneymanager.integration.repository.ledger;

import com.moneymanager.config.DatabaseConfig;
import com.moneymanager.domain.ledger.dto.query.LedgerHistoryQuery;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.AmountType;
import com.moneymanager.domain.ledger.enums.FixCycle;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.unit.fixture.LedgerFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

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

	@Autowired
	private LedgerRepository repository;


	@Nested
	@DisplayName("가계부 저장")
	class SaveTest {

		@Test
		@DisplayName("id가 null인 가계부를 저장하면 새 id를 반환된다.")
		void returnsNewId_whenLedgerIdIsNull() {
			//given
			Ledger ledger = LedgerFixture.defaultLedger()
					.build();

			//when
			Long id = repository.save(ledger);

			//then
			assertThat(id).isGreaterThan(0L);
		}

		@Test
		@DisplayName("유효한 가계부를 저장 후 주요 필드가 정확히 저장된다.")
		void savesAllFields_whenLedgerIsValid(){
			//given
			Ledger ledger = LedgerFixture.defaultLedger().build();

			//when
			Long ledgerId = repository.save(ledger);
			Ledger result = repository.findById(ledgerId);

			//then
			assertThat(result).isNotNull();
			assertThat(result.getCode()).isNotNull();
			assertThat(result.getCreatedAt()).isNotNull();
			assertThat(result.getUpdatedAt()).isNull();

			assertThat(result)
					.extracting(Ledger::getMemberId, Ledger::getCategory, Ledger::getDate, Ledger::getAmount, Ledger::getAmountType)
					.containsExactly("test", "010101", LocalDate.of(2026, 1, 1), 10000L, AmountType.NONE);
		}

		@Test
		@DisplayName("선택 필드에 값이 있으면 같이 저장된다.")
		void savesOptionalFields_whenOptionalFieldsArePresent() {
			//given
			Ledger ledger = LedgerFixture.withPlace()
					.fix(FixedYN.REPEAT)
					.fixCycle(FixCycle.MONTHLY)
					.build();

			//when
			Long ledgerId = repository.save(ledger);
			Ledger result = repository.findById(ledgerId);

			//then
			assertThat(result).isNotNull();

			assertThat(result)
					.extracting(Ledger::getFix, Ledger::getFixCycle, Ledger::getPlace)
					.containsExactly(FixedYN.REPEAT, FixCycle.MONTHLY, new Place("CGV 강남점", "서울특별시 강남구 강남대로 438 스타플렉스", "4층"));
		}

		@Test
		@DisplayName("fixCycle이 null이어도 가계부는 저장된다.")
		void savesLedger_whenFixCycleIsNull() {
			//given
			Ledger ledger = LedgerFixture.defaultLedger()
					.fixCycle(null)
					.build();

			//when
			Long ledgerId = repository.save(ledger);
			Ledger result = repository.findById(ledgerId);

			//then
			assertThat(result).isNotNull();
			assertThat(result.getFixCycle()).isNull();
		}

		@Test
		@DisplayName("Place가 null이어도 가계부는 저장된다.")
		void savesLedger_whenPlaceIsNull() {
			//given
			Ledger ledger = LedgerFixture.defaultLedger()
					.place(new Place(null, null, null))
					.build();

			//when
			Long ledgerId = repository.save(ledger);
			Ledger result = repository.findById(ledgerId);

			//then
			assertThat(result).isNotNull();
			assertThat(result.getPlace())
					.extracting(Place::getName, Place::getRoadAddress, Place::getDetailAddress)
					.containsExactly(null, null, null);

		}

		@Test
		@DisplayName("필수값 누락 시 예외가 발생한다.")
		void throwsException_whenRequiredFieldsAreMissing() {
			//given
			Ledger ledger = Ledger.builder()
					.code("code")
					.date(LocalDate.now())
					.fix(FixedYN.VARIABLE)
					.amount(100000L)
					.amountType(AmountType.BANK)
					.category("020101")
					.build();

			//when & then
			assertThatThrownBy(() -> repository.save(ledger))
					.isInstanceOf(DataIntegrityViolationException.class);
		}

		@Disabled("TODO: 기능 구현 후 진행 예정")
		@Test
		@DisplayName("id가 존재하는 가계부를 저장하면 기존 id를 반환된다.")
		void returnsExistingId_whenLedgerIsExist() {

		}

	}


	@Nested
	@DisplayName("가계부 조회")
	class FindTest {

		@Test
		@DisplayName("유효한 id로 가계부를 조회하면 해당 가계부를 반환한다.")
		void returnsLedger_whenLedgerIdExists() {
			//given
			Ledger ledger = LedgerFixture.withPlace().build();
			Long id = repository.save(ledger);

			//when
			Ledger result = repository.findById(id);

			//then
			assertThat(result).isNotNull();
			assertThat(result.getCreatedAt()).isNotNull();

			assertThat(result)
					.usingRecursiveComparison()
					.ignoringFields("id", "createdAt", "updatedAt")
					.isEqualTo(ledger);
		}

		@Test
		@DisplayName("없는 id로 가계부를 조회하면 예외가 발생한다.")
		void throwsException_whenLedgerIdDoesNotExist() {
			//given
			Long id = 100L;

			//when & then
			assertThatThrownBy(() -> repository.findById(id))
					.isInstanceOf(EmptyResultDataAccessException.class);
		}

		@Test
		@DisplayName("id가 null이면 가계부를 조회하면 예외가 발생한다.")
		void throwsException_whenLedgerIdIsNull() {
			//when & then
			assertThatThrownBy(() -> repository.findById(null))
					.isInstanceOf(EmptyResultDataAccessException.class);
		}

		@Test
		@Sql(statements = "DELETE FROM ledger", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
		@DisplayName("저장된 가계부가 있으면 전체 목록을 반환한다.")
		void  returnsAllLedgers_whenLedgersExist() {
			//given
			repository.save(LedgerFixture.defaultLedger().build());
			repository.save(LedgerFixture.defaultLedger().code("020101").build());

			//when
			List<Ledger> result = repository.findAll();

			//then
			assertThat(result).hasSize(2);
		}

		@Test
		@Sql(statements = "DELETE FROM ledger", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
		@DisplayName("저장된 가계부가 없으면 빈 리스트를 반환한다.")
		void returnsEmptyList_whenNoLedger() {
			//when
			List<Ledger> result = repository.findAll();

			//then
			assertThat(result).isEmpty();
		}

		@Test
		@Sql(statements = "DELETE FROM ledger", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
		@DisplayName("기간 내 가계부 내역을 조회한다.")
		void returnsHistories_whenDateIsWithinPeriod() {
			//given
			String memberId = "test";
			LocalDate from = LocalDate.of(2026, 1, 1);
			LocalDate to = from.with(TemporalAdjusters.lastDayOfMonth());

			//데이터 저장
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 1)).build());
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 1)).build());
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 9)).build());
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 15)).build());
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 2, 1)).build());

			//when
			List<LedgerHistoryQuery> result = repository.findHistoriesByMemberAndDateBetween(memberId, from, to);

			//then
			assertThat(result).hasSize(4);

			assertThat(result)
					.allSatisfy(history -> {
						assertThat(history.getDate()).isBetween(from ,to);
					});
		}

		@Test
		@Sql(statements = "DELETE FROM ledger", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
		@DisplayName("조회 기간의 시작일과 종료일을 포함해 가계부 내역을 조회한다.")
		void returnsHistories_whenDateIsBoundaryValue() {
			//given
			String memberId = "test";
			LocalDate from = LocalDate.of(2026, 1, 1);
			LocalDate to = LocalDate.of(2026, 1, 7);

			//데이터 저장
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2025, 12, 31)).build());
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 1)).build());
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 5)).build());
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 7)).build());
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 8)).build());

			//when
			List<LedgerHistoryQuery> result = repository.findHistoriesByMemberAndDateBetween(memberId, from, to);

			//then
			assertThat(result).hasSize(3);

			assertThat(result)
					.extracting(LedgerHistoryQuery::getDate)
					.containsExactlyInAnyOrder(
							LocalDate.of(2026, 1, 7),
							LocalDate.of(2026, 1, 5),
							LocalDate.of(2026, 1, 1)
					);

			assertThat(result)
					.allSatisfy(history -> assertThat(history.getDate()).isBetween(from ,to));
		}

		@Test
		@Sql(statements = "DELETE FROM ledger", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
		@DisplayName("특정 회원의 가계부 내역만 조회한다.")
		void returnsOnlyMemberHistories_whenMemberIdIsValid() {
			//given
			String memberId = "test";
			LocalDate from = LocalDate.of(2026, 1, 1);
			LocalDate to = LocalDate.of(2026, 1, 7);

			//데이터 저장
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 1)).build());
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 5)).build());
			repository.save(LedgerFixture.defaultLedger().memberId("UCa12001").date(LocalDate.of(2026, 1, 5)).build());
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 7)).build());

			//when
			List<LedgerHistoryQuery> result = repository.findHistoriesByMemberAndDateBetween(memberId, from, to);

			//then
			assertThat(result).hasSize(3);
		}

		@Test
		@DisplayName("같은 거래일이면 최신 등록순으로 조회된다.")
		void returnsHistories_whenDateIsSame() {
			//given
			String memberId = "UCa12001";
			LocalDate date = LocalDate.of(2025, 11, 8);

			//when
			List<LedgerHistoryQuery> result = repository.findHistoriesByMemberAndDateBetween(memberId, date, date);

			//then
			assertThat(result).hasSize(2);

			assertThat(result)
					.extracting(LedgerHistoryQuery::getMemo)
					.containsExactly("두번째", "첫번째");
		}

		@Test
		@Sql(statements = "DELETE FROM ledger", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
		@DisplayName("조회한 가계부 내역의 개수와 각각의 필드값이 정확하다.")
		void returnsCountAndFields_whenSearchConditionIsValid() {
			//given
			String memberId = "test";
			LocalDate from = LocalDate.of(2026, 1, 1);
			LocalDate to = LocalDate.of(2026, 1, 1);

			//데이터 저장
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 1)).build());

			//when
			List<LedgerHistoryQuery> result = repository.findHistoriesByMemberAndDateBetween(memberId, from, to);

			//then
			assertThat(result).hasSize(1);
			assertThat(result.get(0).getCode()).isNotNull();

			assertThat(result.get(0))
					.extracting(LedgerHistoryQuery::getDate, LedgerHistoryQuery::getAmount, LedgerHistoryQuery::getMemo, LedgerHistoryQuery::getCategoryName, LedgerHistoryQuery::getCategoryCode)
					.containsExactly(
							LocalDate.of(2026, 1, 1),
							10000L,
							null,
							"월급",
							"010101"
					);
		}

		@Test
		@Sql(statements = "DELETE FROM ledger", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
		@DisplayName("기간 내 작성한 가계부가 없으면 빈 리스트를 반환한다.")
		void returnsEmptyList_whenNoHistoryExistsInPeriod() {
			//given
			String memberId = "test";
			LocalDate from = LocalDate.of(2026, 1, 1);
			LocalDate to = LocalDate.of(2026, 1, 1);

			//데이터 저장
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 12, 31)).build());
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 2)).build());
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 3)).build());
			repository.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 10)).build());

			//when
			List<LedgerHistoryQuery> result = repository.findHistoriesByMemberAndDateBetween(memberId, from, to);

			//then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("존재하지 않은 회원이면 빈 리스트를 반환한다.")
		void returnsEmptyList_whenMemberIdDoesNotExist() {
			//given
			String noMember = "noMember";
			LocalDate from = LocalDate.of(2026, 1, 1);
			LocalDate to = from.with(TemporalAdjusters.lastDayOfMonth());

			//when
			List<LedgerHistoryQuery> result = repository.findHistoriesByMemberAndDateBetween(noMember, from, to);

			//then
			assertThat(result).isEmpty();;
		}

		@Test
		@DisplayName("저장된 가계부내역 총 개수를 조회한다.")
		void returnsHistoriesCount_whenHistoriesExist() {
			//when
			Long result = repository.count();

			//then
			assertThat(result).isGreaterThan(0);
		}

		@Test
		@Sql(statements = "DELETE FROM ledger", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
		@DisplayName("저장된 가계부 내역이 없으면 개수는 0이다.")
		void returnsZero_whenNoLedgers() {
			//when
			Long result = repository.count();

			//then
			assertThat(result).isZero();
		}

	}


	@Nested
	@DisplayName("가계부 삭제")
	class DeleteTest {

		@Test
		@DisplayName("저장된 가게부 내역을 모두 삭제한다.")
		void returnsZero_whenAllHistoriesAreDeleted() {
			//given
			Long beforeCount = repository.count();
			assertThat(beforeCount).isGreaterThan(0);

			//when
			repository.deleteAll();
			Long result = repository.count();

			//then
			assertThat(result).isZero();
		}

	}

}
