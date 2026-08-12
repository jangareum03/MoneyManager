package com.moneymanager.ledger.repository;

import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedType;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.domain.query.LedgerHistoryQuery;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.Nledger.repository<br>
 * 파일이름       : LedgerRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 16<br>
 * 설명              : LedgerRepository 클래스 로직을 검증하는 통합 테스트 클래스
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
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LedgerRepositoryIT {

	@Autowired
	private LedgerRepository target;

	@Autowired
	private MemberRepository memberRepository;

	@Nested
	@DisplayName("가계부 저장")
	class SaveTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("Ledger 정보가 데이터베이스에 저장된다.")
			void savesLedger_whenRequestIsValid() {
				//given: 필수 정보와 선택 정보가 모두 있는 Ledger가 준비되어 있다.
				Ledger newLedger = LedgerFixture.newLedger()
						.memo("메모")
						.fix(FixedType.REPEAT)
						.fixCycle(FixCycle.YEARLY)
						.place(Place.of(LedgerTestData.PLACE_NAME, LedgerTestData.ROAD_ADDRESS, LedgerTestData.DETAIL_ADDRESS))
						.build();
				
				//when: 가계부를 저장한다.
				Long result = target.insert(newLedger);
				
				//then: 요청된 값이 데이터베이스에 저장된다.
				Ledger ledger = target.findById(result);

				assertThat(ledger.getId()).isEqualTo(result);
				assertThat(ledger.getCreatedAt()).isNotNull();
				assertThat(ledger.getUpdatedAt()).isNull();

				assertThat(ledger)
						.usingRecursiveComparison()
						.ignoringFields("id", "createdAt")
						.isEqualTo(newLedger);
			}

			@Test
			@DisplayName("장소 정보가 없어도 데이터베이스 저장된다.")
			void savesLedger_whenPlaceDoesNotExist() {
				//given: 장소 정보만 없는 Ledger가 준비되어 있다.
				Ledger newLedger = LedgerFixture.newLedger()
						.memo("메모")
						.fix(FixedType.REPEAT)
						.fixCycle(FixCycle.YEARLY)
						.build();

				//when: 가계부를 저장한다.
				Long result = target.insert(newLedger);
				
				//then: 장소는 저장되지 않는다.
				Ledger ledger = target.findById(result);

				assertThat(ledger.getPlace()).isNull();
			}
			
			@Test
			@DisplayName("고정주기 정보가 없어도 데이터베이스 저장된다.")
			void savesLedger_whenRecurringDoesNotExist() {
				//given: 고정주기가 없는 Ledger가 준비되어 있다.
				Ledger newLedger = LedgerFixture.newLedger().build();

				//when: 가계부를 저장한다.
				Long result = target.insert(newLedger);
				
				//then: 고정주기가 저장되지 않는다.
				Ledger ledger = target.findById(result);

				assertThat(ledger.getFix()).isEqualTo(newLedger.getFix());
				assertThat(ledger.getFixCycle()).isEqualTo(newLedger.getFixCycle());
			}
			
			@Test
			@DisplayName("가계부가 저장되면 Generated Key가 반환된다.")
			void returnsGeneratedKey_whenLedgerIsSaved() {
				//given: 저장할 수 있는 가계부가 준비되어 있다.
				Ledger ledger = LedgerFixture.newLedger().build();

				//when: 가계부를 저장한다.
				Long result = target.insert(ledger);
				
				//then: Key가 반환된다.
				assertThat(result).isNotNull();
				assertThat(target.findById(result)).isNotNull();
			}
			
			@Test
			@DisplayName("가계부를 저장하면 데이터베이스에 한 건이 추가된다.")
			void savesLedgerSuccessfully_whenRequestIsValid() {
				//given: 저장할 수 있는 가계부가 준비되어 있다.
				Ledger ledger = LedgerFixture.newLedger().build();
				
				long before = target.count();

				//when: 가계부를 저장한다.
				target.insert(ledger);

				//then: 총 가계부 수가 추가된다.
				long after = target.count();

				assertThat(after).isGreaterThan(before);
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("가계부 코드가 중복되면 예외가 발생한다.")
			void throwsException_whenLedgerCodeExist() {
				//given: 중복되는 코드를 가진 가계부가 준비되어 있다.
				String memberId = MemberTestData.MEMBER_ID;
				String ledgerCode = LedgerTestData.CODE;

				target.insert(
						LedgerFixture.savedLedger(1L)
								.memberId(memberId)
								.code(ledgerCode)
								.build()
				);

				Ledger newLedger = LedgerFixture.newLedger()
						.memberId(memberId)
						.code(ledgerCode)
						.build();

				//when & then: 동일한 가계부 저장 시 DuplicateKeyException이 발생한다.
				assertThatCode(() -> target.insert(newLedger))
						.isInstanceOf(DuplicateKeyException.class);
			}
			
		}

	}


	@Nested
	@DisplayName("가계부 수정")
	class UpdateTest {

		private Ledger savedLedger;

		@BeforeEach
		void setUp() {
			Long id = target.insert(
					LedgerFixture.newLedger()
							.memo("메모")
							.fix(FixedType.REPEAT)
							.fixCycle(FixCycle.MONTHLY)
							.place(Place.of(LedgerTestData.PLACE_NAME, LedgerTestData.ROAD_ADDRESS, null))
							.build()
			);

			savedLedger = target.findById(id);
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("가게부 정보가 데이터베이스에 수정된다.")
			void updatesLedger_whenRequestIsValid() {
				//given: 카테고리 코드와 메모가 수정된 상태이다.
				savedLedger.changeCategory(CategoryTestData.SNACK_CODE);
				savedLedger.changeMemo("수정 완료");
				
				//when: 가계부 수정한다.
				target.update(savedLedger);

				//then: 수정된 값이 저장된다.
				assertThat(savedLedger.getCategory()).isEqualTo(CategoryTestData.SNACK_CODE);
				assertThat(savedLedger.getMemo()).isEqualTo("수정 완료");

				assertThat(savedLedger.getUpdatedAt()).isNotNull();
			}
			
			@Test
			@DisplayName("장소 정보가 없어도 데이터베이스 수정된다.")
			void updatesLedger_whenPlaceDoesNotExist() {
				//given: 장소 정보가 없게 수정된 상태이다.
				savedLedger.changePlace(null);
				
				//when: 가계부 수정한다.
				target.update(savedLedger);
				
				//then: 가계부 장소 정보값이 null로 저장된다.
				assertThat(savedLedger.getPlace()).isNull();
			}
			
			@Test
			@DisplayName("고정주기 정보가 없어도 데이터베이스 수정된다.")
			void updatesLedger_whenRecurringDoesNotExist() {
				//given: 고정주기 정보가 없게 수정된 상태이다.
				savedLedger.changeFixInfo(LedgerTestData.FIX_N, null);

				//when: 가계부 수정한다.
				target.update(savedLedger);

				//then: 가계부 고정주기가 null로 저장된다.
				assertThat(savedLedger.getFix()).isEqualTo(FixedType.VARIABLE);
				assertThat(savedLedger.getFixCycle()).isNull();
			}
			
			@Test
			@DisplayName("가계부가 수정되면 1을 반환한다.")
			void returnsOne_whenLedgerIsUpdatedSuccessfully() {
				//given: 결제정보를 수정된 상태이다.
				savedLedger.changeMoney(Money.of(50000L, PaymentType.CARD));

				//when: 가계부 수정한다.
				int result = target.update(savedLedger);
				
				//then: 1이 반환된다.
				assertThat(result).isEqualTo(1);
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {
			
			@Test
			@DisplayName("존재하지 않은 회원으로 가계부를 수정하면 0을 반환한다.")
			void returnsZero_whenMemberDoesNotExist() {
				//given: 존재하지 않은 회원 ID를 가진 가계부가 준비되어 있다.
				Ledger ledger = LedgerFixture.newLedger()
						.memberId("error")
						.code(savedLedger.getCode())
						.build();

				ledger.changeMemo("수정");
				
				//when: 가계부 수정을 요청한다.
				int result = target.update(ledger);
				
				//then: 0을반환한다.
				assertThat(result).isZero();

				//then: 값이 변경되지 않는다.
				Ledger updatedLedger = target.findById(savedLedger.getId());
				assertThat(updatedLedger.getMemo()).isNotEqualTo("수정");
			}
			
			@Test
			@DisplayName("존재하지 않은 회원으로 가계부를 수정하면 0을 반환한다.")
			void returnsZero_whenLedgerDoesNotExist() {
				//given: 존재하지 않은 가계부 코드를 가진 가계부가 준비되어 있다.
				Ledger ledger = LedgerFixture.newLedger()
						.memberId(savedLedger.getMemberId())
						.code("error")
						.build();

				ledger.changeMemo("수정");

				//when: 가계부 수정을 요청한다.
				int result = target.update(ledger);

				//then: 0을반환한다.
				assertThat(result).isZero();

				//then: 값이 변경되지 않는다.
				Ledger updatedLedger = target.findById(savedLedger.getId());
				assertThat(updatedLedger.getMemo()).isNotEqualTo("수정");
			}

		}

	}


	@Nested
	@DisplayName("ID로 조회")
	class SelectByIdTest {

		private Ledger savedLedger;

		@BeforeEach
		void setUp() {
			Long ledgerId = target.insert(LedgerFixture.newLedger().build());

			savedLedger = target.findById(ledgerId);
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("저장된 가계부 ID로 가계부가 조회된다.")
			void returnsLedger_whenLedgerIdExists() {
				//when: 가계부를 조회한다.
				Ledger result = target.findById(savedLedger.getId());
				
				//then: 가계부 ID에 해당하는 가계부가 조회된다.
				assertThat(result)
						.usingRecursiveComparison()
						.isEqualTo(savedLedger);
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest
			@NullSource
			@DisplayName("가계부 ID가 null이면 조회가 실패한다.")
			void throwsException_whenLedgerIdIsNull(Long id) {
				//when & then: 가계부를 조회하면 EmptyResultDataAccessException이 발생한다.
				assertThatThrownBy(() -> target.findById(id))
						.isInstanceOf(EmptyResultDataAccessException.class);
			}
			
			@Test
			@DisplayName("저장하지 않은 가계부 ID로 조회가 실패한다.")
			void throwsException_whenLedgerIdDoesNotExist() {
				//given: 존재하지 않은 가계부 ID가 주어진다.
				Long id = 99999L;
				
				//when & then: 가계부를 조회하면 EmptyResultDataAccessException이 빌셍한다.
				assertThatThrownBy(() -> target.findById(id))
						.isInstanceOf(EmptyResultDataAccessException.class);
			}
			
		}

	}


	@Nested
	@DisplayName("CODE로 조회")
	class SelectByCodeTest {

		private Ledger savedLedger;

		@BeforeEach
		void setUp() {
			Long ledgerId = target.insert(LedgerFixture.newLedger().build());

			savedLedger = target.findById(ledgerId);
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("저장된 회원ID와 가계부 코드가 일치하면 가계부가 조회된다.")
			void returnsLedger_whenMemberAndLedgerExist() {
				//given: 회원 ID와 작성한 가계부 코드가 주어진다.
				String memberId = savedLedger.getMemberId();
				String code = savedLedger.getCode();
				
				//when: 가계부를 조회한다.
				Ledger result = target.findByCode(memberId, code);

				//then: 가계부 ID에 해당하는 가계부가 조회된다.
				assertThat(result)
						.usingRecursiveComparison()
						.isEqualTo(savedLedger);
			}
			
		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("다른 회원의 가계부는 조회되지 않는다.")
			void throwsException_whenMemberIsNotOwnerOfLedger() {
				//given: 다른 회원과 가계부가 저장된다.

				Long ledgerId = target.insert(LedgerFixture.newLedger().memberId(MemberTestData.MEMBER_ID).code("code").build());
				Ledger otherLedger = target.findById(ledgerId);
				
				//when & then: 가계부를 조회하면 EmptyResultDataAccessException이 발생한다.
				assertThatThrownBy(() -> target.findByCode(savedLedger.getMemberId(), otherLedger.getCode()))
						.isInstanceOf(EmptyResultDataAccessException.class);
			}
			
			@Test
			@DisplayName("존재하지 않은 가계부 코드는 조회가 실패한다.")
			void throwsException_whenLedgerDoesNotExist() {
				//given: 존재하지 않은 가계부 코드가 주어진다.
				String code = "error";

				//when & then: 가계부를 조회하면 EmptyResultDataAccessException이 발생한다.
				assertThatThrownBy(() -> target.findByCode(savedLedger.getMemberId(), code))
						.isInstanceOf(EmptyResultDataAccessException.class);
			}
			
		}

	}


	@Nested
	@DisplayName("전체 조회")
	class SelectAllTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@Test
			@DisplayName("저장된 전체 가계부가 조회한다.")
			void returnsAllLedger_whenLedgersExist() {
				//given: 여러 개의 가계부가 저장되어 있다.
				target.insert(LedgerFixture.newLedger().memberId(MemberTestData.MEMBER_ID).code("code1").build());
				target.insert(LedgerFixture.newLedger().memberId(MemberTestData.MEMBER_ID).code("code2").build());
				target.insert(LedgerFixture.newLedger().memberId(MemberTestData.MEMBER_ID).code("code3").build());

				//when: 전체 가계부를 조회한다.
				List<Ledger> result = target.findAll();
				
				//then: 모든 가계부가 반환된다.
				assertThat(result).hasSize(3);
				assertThat(result)
						.extracting(Ledger::getCode)
						.containsExactly("code1", "code2", "code3");
			}

			@Test
			@DisplayName("저장된 가계부가 없으면 빈 리스트가 반환된다.")
			void returnsEmptyList_whenLedgersDoNotExist() {
				//when: 전체 가계부를 조회한다.
				List<Ledger> result = target.findAll();

				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
			}

		}

	}


	@Nested
	@DisplayName("내역 조회")
	class SelectHistoryTest {

		@BeforeEach
		void setUp() {
			target.insert(LedgerFixture.newLedger().code("code1").date(LocalDate.of(2026, 1,1)).build());
			target.insert(LedgerFixture.newLedger().code("code2").date(LocalDate.of(2026, 1,1)).build());
			target.insert(LedgerFixture.newLedger().code("code3").date(LocalDate.of(2026, 1,3)).build());
			target.insert(LedgerFixture.newLedger().code("code4").date(LocalDate.of(2026, 1,3)).build());
			target.insert(LedgerFixture.newLedger().code("code5").date(LocalDate.of(2026, 1,5)).build());

			//다른 회원의 가계부 추가

			target.insert(LedgerFixture.newLedger().memberId("member123").code("code6").date(LocalDate.of(2026, 1,3)).build());
			target.insert(LedgerFixture.newLedger().memberId("member123").code("code7").date(LocalDate.of(2026, 1,4)).build());
			target.insert(LedgerFixture.newLedger().memberId("member123").code("code8").date(LocalDate.of(2026, 1,6)).build());
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {
		
			@Test
			@DisplayName("시작일과 종료일 사이에 저장된 가계부가 조회된다.")
			void returnsLedgers_whenDateIsInRange() {
				//given: 작성한 회원ID, 시작일과 종료일이 주어진다.
				String memberId = MemberTestData.MEMBER_ID;
				LocalDate start = LocalDate.of(2026, 1, 2);
				LocalDate end = LocalDate.of(2026, 1, 6);

				//when: 기간 내의 가계부를 조회한다.
				List<LedgerHistoryQuery> result = target.findHistoriesByMemberAndDateBetween(memberId, start, end);
				
				//then: 시작일과 종료일 사이에 작성돤 가계부가 반환된다.
				assertThat(result)
						.extracting(LedgerHistoryQuery::getDate)
						.allSatisfy(date ->
								assertThat(date).isBetween(start, end)
						);

				assertThat(result)
						.extracting(LedgerHistoryQuery::getCode)
						.containsOnlyOnce("code3", "code4");

				//객체 필드값이 정상적으로 매핑되어 있다.
				LedgerHistoryQuery history = result.stream()
						.filter(item -> item.getCode().equals("code3"))
						.findFirst()
						.orElseThrow();

				assertThat(history.getMemo()).isNull();
				assertThat(history.getCode()).isEqualTo("code3");
				assertThat(history.getDate()).isEqualTo(LocalDate.of(2026, 1, 3));
				assertThat(history.getAmount()).isEqualTo(LedgerTestData.AMOUNT);
				assertThat(history.getCategoryCode()).isEqualTo(CategoryTestData.SALARY_CODE);
				assertThat(history.getCategoryName()).isEqualTo(CategoryTestData.SALARY_NAME);
			}
			
			@Test
			@DisplayName("시작일이 포함된 가계부가 조회된다.")
			void returnsLedgers_whenDateIsEqualToStartDate() {
				//given: 작성한 회원ID, 시작일과 종료일이 주어진다.
				String memberId = MemberTestData.MEMBER_ID;
				LocalDate start = LocalDate.of(2026, 1, 1);
				LocalDate end = LocalDate.of(2026, 1, 4);

				//when: 기간 내의 가계부를 조회한다.
				List<LedgerHistoryQuery> result = target.findHistoriesByMemberAndDateBetween(memberId, start, end);
				
				//then: 조회된 가계부에 시작일도 포함되어 반환된다.
				assertThat(result)
						.extracting(LedgerHistoryQuery::getDate)
						.contains(start);
			}
			
			@Test
			@DisplayName("종료일이 포함된 가계부가 조회된다.")
			void returnsLedgers_whenDateIsEqualToEndDate() {
				//given: 작성한 회원ID, 시작일과 종료일이 주어진다.
				String memberId = MemberTestData.MEMBER_ID;
				LocalDate start = LocalDate.of(2026, 1, 2);
				LocalDate end = LocalDate.of(2026, 1, 3);

				//when: 기간 내의 가계부를 조회한다.
				List<LedgerHistoryQuery> result = target.findHistoriesByMemberAndDateBetween(memberId, start, end);

				//then: 조회된 가계부에 종료일도 포함되어 반환된다.
				assertThat(result)
						.extracting(LedgerHistoryQuery::getDate)
						.contains(end);
			}
			
			@Test
			@DisplayName("가계부 거래날짜와 가계부 ID가 최신순으로 가계부가 조회된다.")
			void sortsLedgersByDateAndIdDesc() {
				//given: 작성한 회원ID, 시작일과 종료일이 주어진다.
				String memberId = MemberTestData.MEMBER_ID;
				LocalDate start = LocalDate.of(2026, 1, 1);
				LocalDate end = LocalDate.of(2026, 1, 5);

				//when: 기간 내의 가계부를 조회한다.
				List<LedgerHistoryQuery> result = target.findHistoriesByMemberAndDateBetween(memberId, start, end);
				
				//then: 동일한 날짜면 가계부 ID가 오름차순으로 반환된다.
				assertThat(result)
						.extracting(
								LedgerHistoryQuery::getDate,
								LedgerHistoryQuery::getCode
						)
						.containsExactly(
								Tuple.tuple(LocalDate.of(2026, 1, 5), "code5"),
								Tuple.tuple(LocalDate.of(2026, 1, 3), "code4"),
								Tuple.tuple(LocalDate.of(2026, 1, 3), "code3"),
								Tuple.tuple(LocalDate.of(2026, 1, 1), "code2"),
								Tuple.tuple(LocalDate.of(2026, 1, 1), "code1")
						);
			}
			
			@Test
			@DisplayName("시작일과 종료일 사이에 저장된 가계부가 없으면 빈 리스트가 반환된다.")
			void returnsEmptyList_whenDateIsOutOfRange() {
				//given: 작성한 회원ID, 시작일과 종료일이 주어진다.
				String memberId = MemberTestData.MEMBER_ID;
				LocalDate start = LocalDate.of(2026, 1, 6);
				LocalDate end = LocalDate.of(2026, 1, 10);

				//when: 기간 내의 가계부를 조회한다.
				List<LedgerHistoryQuery> result = target.findHistoriesByMemberAndDateBetween(memberId, start, end);

				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
			}
			
			@Test
			@DisplayName("존재하지 않은 회원으로 가계부를 조회하면 빈 리스트가 반환된다.")
			void returnsEmptyList_whenMemberDoesNotExist() {
				//given: 작성한 회원ID, 시작일과 종료일이 주어진다.
				String memberId = "error";
				LocalDate start = LocalDate.of(2026, 1, 1);
				LocalDate end = LocalDate.of(2026, 1, 10);

				//when: 기간 내의 가계부를 조회한다.
				List<LedgerHistoryQuery> result = target.findHistoriesByMemberAndDateBetween(memberId, start, end);

				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
			}
			
		}
		
	}
	
	
	@Nested
	@DisplayName("전체 건수 조회")
	class TotalCountTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("저장된 전체 가계부 건수가 조회된다.")
			void returnsLedgerCount_whenLedgersExist() {
				//given: 여러 개의 가계부가 저장되어 있다.
				target.insert(LedgerFixture.newLedger().code("code1").date(LocalDate.of(2026, 1,1)).build());
				target.insert(LedgerFixture.newLedger().code("code2").date(LocalDate.of(2026, 1,1)).build());
				target.insert(LedgerFixture.newLedger().code("code3").date(LocalDate.of(2026, 1,3)).build());

				//when: 모든 가계부의 건수를 조회한다.
				Long result = target.count();
				
				//then: 저장된 가계부 건수가 반환된다.
				assertThat(result).isEqualTo(3);
			}
			
			@Test
			@DisplayName("저장된 가계부가 없으면 0이 반환된다.")
			void returnsZero_whenLedgersDoNotExist() {
				//when: 모든 가계부의 건수를 조회한다.
				Long result = target.count();

				//then: 저장된 가계부 건수가 반환된다.
				assertThat(result).isZero();
			}

		}

	}
	
	
	@Nested
	@DisplayName("전체 삭제")
	class DeleteAllTest {

		@BeforeEach
		void setUp() {
			target.insert(LedgerFixture.newLedger().code("code1").date(LocalDate.of(2026, 1,1)).build());
			target.insert(LedgerFixture.newLedger().code("code2").date(LocalDate.of(2026, 1,1)).build());
			target.insert(LedgerFixture.newLedger().code("code3").date(LocalDate.of(2026, 1,3)).build());
		}
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {
		
			@Test
			@DisplayName("저장된 가계부가 모두 삭제된다.")
			void deletesAllLedger_whenLedgersExist() {
				//when: 저장된 가계부 모두 삭제한다.
				target.deleteAll();
				
				//then: 가계부가 저장된게 없다.
				assertThat(target.count()).isZero();
			}
			
		}
		
	}

}