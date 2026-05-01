package com.moneymanager.integration.repository.ledger;

import com.moneymanager.domain.ledger.dto.query.LedgerHistoryQuery;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.AmountType;
import com.moneymanager.domain.ledger.enums.FixCycle;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.domain.member.Member;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.repository.member.MemberRepository;
import com.moneymanager.fixture.MemberFixture;
import com.moneymanager.fixture.ledger.LedgerFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

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
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LedgerRepositoryTest {

	@Autowired
	private LedgerRepository target;

	@Autowired
	private MemberRepository memberRepository;

	private Member member;

	@BeforeEach
	void setUp() {
		memberRepository.deleteAll();
		target.deleteAll();

		member = MemberFixture.create();

		memberRepository.save(member);
	}

	@Nested
	@DisplayName("가계부 저장")
	class SaveTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("id가 null인 가계부를 저장하면 새 id를 반환된다.")
			void returnsNewId_whenLedgerIdIsNull() {
				//given
				Ledger ledger = LedgerFixture.defaultLedger().build();

				//when
				Long id = target.save(ledger);

				//then
				assertThat(id).isGreaterThan(0L);
			}

			@Test
			@DisplayName("유효한 가계부를 저장 후 주요 필드가 정확히 저장된다.")
			void savesAllFields_whenLedgerIsValid(){
				//given
				Ledger ledger = LedgerFixture.defaultLedger().build();

				//when
				Long ledgerId = target.save(ledger);
				Ledger result = target.findById(ledgerId);

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
				Long ledgerId = target.save(ledger);
				Ledger result = target.findById(ledgerId);

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
				Long ledgerId = target.save(ledger);
				Ledger result = target.findById(ledgerId);

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
				Long ledgerId = target.save(ledger);
				Ledger result = target.findById(ledgerId);

				//then
				assertThat(result).isNotNull();
				assertThat(result.getPlace())
						.extracting(Place::getName, Place::getRoadAddress, Place::getDetailAddress)
						.containsExactly(null, null, null);

			}

		}


		@Nested
		@DisplayName("실패 케이스")
		class Failure {

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
				assertThatThrownBy(() -> target.save(ledger))
						.isInstanceOf(DataIntegrityViolationException.class);
			}

			@Disabled("TODO: 기능 구현 후 진행 예정")
			@Test
			@DisplayName("id가 존재하는 가계부를 저장하면 기존 id를 반환된다.")
			void returnsExistingId_whenLedgerIsExist() {	}

		}

	}


	@Nested
	@DisplayName("가계부 조회")
	class FindTest {

		@Nested
		@DisplayName("ID로 조회")
		class FindByIdTest {

			@Test
			@DisplayName("유효한 id로 가계부를 조회하면 해당 가계부를 반환한다.")
			void returnsLedger_whenLedgerIdExists() {
				//given
				Ledger ledger = LedgerFixture.withPlace().build();
				Long id = target.save(ledger);

				//when
				Ledger result = target.findById(id);

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
				assertThatThrownBy(() -> target.findById(id))
						.isInstanceOf(EmptyResultDataAccessException.class);
			}

			@Test
			@DisplayName("id가 null이면 가계부를 조회하면 예외가 발생한다.")
			void throwsException_whenLedgerIdIsNull() {
				//when & then
				assertThatThrownBy(() -> target.findById(null))
						.isInstanceOf(EmptyResultDataAccessException.class);
			}

		}


		@Nested
		@DisplayName("Code로 조회")
		class FindByCodeTest {

			@BeforeEach
			void setUp() {
				Ledger ledger = LedgerFixture.defaultLedger().id(null).memberId(member.getId()).build();

				target.save(ledger);
			}

			@Test
			@DisplayName("자신의 가계부는 반환한다.")
			void returnsLedger_when() {
				//when
				Ledger result = target.findByCode(member.getId(), "test-code");

				//then
				assertThat(result).isNotNull();
				assertThat(result.getId()).isNotNull();

				assertThat(result.getMemberId()).isEqualTo(member.getId());
				assertThat(result.getCode()).isEqualTo("test-code");
			}

			@Test
			@DisplayName("존재하지 않은 code면 EmptyResultDataAccessException이 발생한다.")
			void throwsException_whenLedgerCodeDoesNotExist() {
				//when & then
				assertThatExceptionOfType(EmptyResultDataAccessException.class)
						.isThrownBy(() -> target.findByCode(member.getId(), "no-code"));
			}

			@Test
			@DisplayName("다른 회원의 가계부면 EmptyResultDataAccessException이 발생한다.")
			void throwsException_whenOtherMember() {
				//given
				Member otherMember = MemberFixture.defaultMember().name("김영희").build();
				Ledger otherLedger = LedgerFixture.defaultLedger().id(null).code("other-code").memberId(otherMember.getId()).build();

				memberRepository.save(otherMember);
				target.save(otherLedger);

				//when & then
				assertThatExceptionOfType(EmptyResultDataAccessException.class)
						.isThrownBy(() -> target.findByCode(member.getId(), otherLedger.getCode()));
			}

			@ParameterizedTest
			@NullAndEmptySource
			@DisplayName("null값이 들어오면 EmptyResultDataAccessException이 발생한다.")
			void throwsException_whenRequestIsMissing(String code) {
				//when & then
				assertThatExceptionOfType(EmptyResultDataAccessException.class)
						.isThrownBy(() -> target.findByCode(member.getId(), code));
			}

		}


		@Nested
		@DisplayName("전체 조회")
		class FindAllTest {

			@Test
			@Sql(statements = "DELETE FROM ledger", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("저장된 가계부가 있으면 전체 목록을 반환한다.")
			void  returnsAllLedgers_whenLedgersExist() {
				//given
				target.save(LedgerFixture.defaultLedger().build());
				target.save(LedgerFixture.defaultLedger().code("020101").build());

				//when
				List<Ledger> result = target.findAll();

				//then
				assertThat(result).hasSize(2);
			}

			@Test
			@Sql(statements = "DELETE FROM ledger", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("저장된 가계부가 없으면 빈 리스트를 반환한다.")
			void returnsEmptyList_whenNoLedger() {
				//when
				List<Ledger> result = target.findAll();

				//then
				assertThat(result).isEmpty();
			}

		}


		@Nested
		@DisplayName("내역 조회")
		class FindHistoryTest {

			@Test
			@Sql(statements = "DELETE FROM ledger", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("기간 내 가계부 내역을 조회한다.")
			void returnsHistories_whenDateIsWithinPeriod() {
				//given
				String memberId = "test";
				LocalDate from = LocalDate.of(2026, 1, 1);
				LocalDate to = from.with(TemporalAdjusters.lastDayOfMonth());

				//데이터 저장
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 1)).build());
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 1)).build());
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 9)).build());
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 15)).build());
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 2, 1)).build());

				//when
				List<LedgerHistoryQuery> result = target.findHistoriesByMemberAndDateBetween(memberId, from, to);

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
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2025, 12, 31)).build());
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 1)).build());
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 5)).build());
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 7)).build());
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 8)).build());

				//when
				List<LedgerHistoryQuery> result = target.findHistoriesByMemberAndDateBetween(memberId, from, to);

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
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 1)).build());
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 5)).build());
				target.save(LedgerFixture.defaultLedger().memberId("UCa12001").date(LocalDate.of(2026, 1, 5)).build());
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 7)).build());

				//when
				List<LedgerHistoryQuery> result = target.findHistoriesByMemberAndDateBetween(memberId, from, to);

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
				List<LedgerHistoryQuery> result = target.findHistoriesByMemberAndDateBetween(memberId, date, date);

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
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 1)).build());

				//when
				List<LedgerHistoryQuery> result = target.findHistoriesByMemberAndDateBetween(memberId, from, to);

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
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 12, 31)).build());
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 2)).build());
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 3)).build());
				target.save(LedgerFixture.defaultLedger().date(LocalDate.of(2026, 1, 10)).build());

				//when
				List<LedgerHistoryQuery> result = target.findHistoriesByMemberAndDateBetween(memberId, from, to);

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
				List<LedgerHistoryQuery> result = target.findHistoriesByMemberAndDateBetween(noMember, from, to);

				//then
				assertThat(result).isEmpty();;
			}

		}


		@Nested
		@DisplayName("총개수 조회")
		class FindAllCountTest {

			@Test
			@DisplayName("저장된 가계부내역 총 개수를 조회한다.")
			void returnsHistoriesCount_whenHistoriesExist() {
				//when
				Long result = target.count();

				//then
				assertThat(result).isGreaterThan(0);
			}

			@Test
			@Sql(statements = "DELETE FROM ledger", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("저장된 가계부 내역이 없으면 개수는 0이다.")
			void returnsZero_whenNoLedgers() {
				//when
				Long result = target.count();

				//then
				assertThat(result).isZero();
			}

		}

	}


	@Nested
	@DisplayName("가계부 삭제")
	class DeleteTest {

		@Test
		@DisplayName("저장된 가게부 내역을 모두 삭제한다.")
		void returnsZero_whenAllHistoriesAreDeleted() {
			//given
			Long beforeCount = target.count();
			assertThat(beforeCount).isGreaterThan(0);

			//when
			target.deleteAll();
			Long result = target.count();

			//then
			assertThat(result).isZero();
		}

	}

}
