package com.moneymanager.ledger.service;

import com.moneymanager.config.MutableClock;
import com.moneymanager.config.TimeConfig;
import com.moneymanager.domain.ledger.dto.response.*;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.*;
import com.moneymanager.domain.ledger.vo.Money;
import com.moneymanager.domain.member.Member;
import com.moneymanager.exception.exception.BusinessException;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.IntegrationTestSupport;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import com.moneymanager.support.fixture.entity.LedgerImageFixture;
import com.moneymanager.support.fixture.entity.MemberFixture;
import com.moneymanager.support.security.WithMockCustomUser;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.moneymanager.exception.code.LedgerErrorCode.NOT_FOUND_DATA;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.integration.service.ledger<br>
 * 파일이름       : LedgerReadServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 15<br>
 * 설명              : LedgerReadService 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 4. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Import(TimeConfig.class)
public class LedgerReadServiceIT extends IntegrationTestSupport {

	@Autowired
	private LedgerReadService target;

	@Autowired
	private LedgerImageRepository imageRepository;

	@Autowired
	private MutableClock clock;

	private Member member;


	@BeforeEach
	void setUp() {
		member = memberRepository.save(MemberFixture.builder(MemberTestData.MEMBER_ID).build());
		clock.set(LocalDate.of(2026, 1, 10));
	}


	@Nested
	@DisplayName("작성 1단계 데이터 얻기")
	@WithMockCustomUser
	class GetStep1DataTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("오늘 날짜로 가계부 1단계 작성에 필요한 데이터가 반환된다.")
			void returnsStep1Data_whenTodayIsGiven() {
				//when: 가계부 작성 1단계에 필요한 데이터를 요청한다.
				LedgerWriteStep1Response result = target.getWriteStep1Data();

				//then: 작성에 필요한 데이터가 반환된다.
				assertThat(result).isNotNull();

				assertThat(result.getDisplayDate()).isEqualTo("2026년 01월 10일 토요일");

				assertThat(result.getTypes())
						.hasSize(2)
						.extracting(
								LedgerTypeResponse::getLabel,
								LedgerTypeResponse::getValue
						)
						.containsExactly(
								Tuple.tuple("수입", "01"),
								Tuple.tuple("지출", "02")
						);

				assertThat(result)
						.extracting(
								LedgerWriteStep1Response::getCurrentYear,
								LedgerWriteStep1Response::getCurrentMonth,
								LedgerWriteStep1Response::getCurrentDay
						)
						.containsExactly(
								2026,
								1,
								15
						);
			}

		}

	}


	@Nested
	@DisplayName("작성 2단계 데이터 얻기")
	@WithMockCustomUser
	class GetStep2DataTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("수입 유형이면 정상적으로 응답 데이터가 반환된다.")
			void returnsResponseData_whenIncomeTypeIsGiven() {
				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
				LedgerWriteStep2Response result = target.getWriteStep2Data(CategoryType.INCOME, LocalDate.now(clock));
				
				//then: 작성에 필요한 데이터가 반환된다.
				assertThat(result).isNotNull();

				assertThat(result.getTitle()).isEqualTo("2026년 01월 10일 토요일");

				assertThat(result.getImageSlot()).hasSize(3);

				assertThat(result.getCategories())
						.allMatch(item -> item.getCode().startsWith("01"));
			}
			
			@Test
			@DisplayName("지출 유형이면 정상적으로 응답 데이터가 반환된다.")
			void returnsResponseData_whenExpenseTypeIsGiven() {
				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
				LedgerWriteStep2Response result = target.getWriteStep2Data(CategoryType.OUTLAY, LocalDate.now(clock));

				//then: 작성에 필요한 데이터가 반환된다.
				assertThat(result).isNotNull();

				assertThat(result.getCategories())
						.allMatch(item -> item.getCode().startsWith("02"));
			}
			
			@Test
			@Sql(statements = "DELETE FROM ledger_category", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("카테고리가 없어도 응답 데이터가 반환된다.")
			void returnsResponseData_whenCategoryIsNull() {
				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
				LedgerWriteStep2Response result = target.getWriteStep2Data(CategoryType.OUTLAY, LocalDate.now(clock));

				//then: 빈 카테고리 목록이 응답에 저장된다.
				assertThat(result.getCategories()).isEmpty();
			}
			
			@Test
			@Sql(statements = "UPDATE member_info SET image_limit = 0", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("이미지 정보가 없어도 응답 데이터가 반환된다.")
			void returnsResponseData_whenImageInfoIsNull () {
				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
				LedgerWriteStep2Response result = target.getWriteStep2Data(CategoryType.INCOME, LocalDate.now(clock));

				//then: 빈 이미지 슬롯 정보가 응답에 저장된다.
				assertThat(result.getImageSlot())
						.hasSize(3)
						.extracting(
								ImageSlot::getStatus
						)
						.containsExactly(SlotStatus.EMPTY, SlotStatus.LOCKED ,SlotStatus.LOCKED);
			}
			
		}

	}


	@Nested
	@DisplayName("내역 리스트 얻기")
	@WithMockCustomUser
	class GetHistoryDashboard {
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@BeforeEach
			void setUp() {
				saveIncomeLedger("code1", LocalDate.of(2026, 1, 3), 20000L, PaymentType.NONE);
				saveIncomeLedger("code2", LocalDate.of(2026, 1, 5), 15000L, PaymentType.CASH);
				saveIncomeLedger("code3", LocalDate.of(2026, 1, 10), 50000L, PaymentType.BANK);
			}

			private void saveIncomeLedger(String code, LocalDate date, long amount , PaymentType type) {
				ledgerRepository.insert(
						LedgerFixture.newLedger()
								.code(code)
								.date(date)
								.money(Money.of(amount, type))
								.build()
				);
			}

			private void saveOutlayLedger(String code, LocalDate date, long amount, PaymentType type) {
				ledgerRepository.insert(
						LedgerFixture.newLedger()
								.code(code)
								.date(date)
								.money(Money.of(amount, type))
								.category(CategoryTestData.SNACK_CODE)
								.build()
				);
			}

			@Test
			@DisplayName("저장된 가계부가 있으면 정상적으로 응답 데이터가 반환된다.")
			void returnsResponseData_whenLedgerExists() {
				//when: 가계부 내역 조회를 요청한다.
				HistoryDashboardResponse result = target.getHistoryDashboard(HistoryType.MONTH);
				
				//then: 가계부 내역 조회 응답이 정상적으로 조회된다.
				assertThat(result).isNotNull();
				assertThat(result.getTitle()).isNotNull();

				assertThat(result.getStatistics())
						.satisfies(statistics -> {
							assertThat(statistics.getTotal()).isGreaterThanOrEqualTo(0L);
							assertThat(statistics.getIncome()).isGreaterThanOrEqualTo(0L);
							assertThat(statistics.getOutlay()).isGreaterThanOrEqualTo(0L);
						});

				assertThat(result.getHistoryGroups()).hasSize(3);

				assertThat(result.getMenu())
						.hasSize(5)
						.extracting(
								MenuItem::getLabel,
								MenuItem::getValue
						)
						.containsExactly(
								Tuple.tuple("전체", HistoryMenuType.ALL.name()),
								Tuple.tuple("수입/지출", HistoryMenuType.CATEGORY.name()),
								Tuple.tuple("카테고리", HistoryMenuType.SUB_CATEGORY.name()),
								Tuple.tuple("메모", HistoryMenuType.MEMO.name()),
								Tuple.tuple("기간", HistoryMenuType.DATE.name())
						);
			}
			
			@Test
			@DisplayName("저장된 가계부가 없으면 내역이 빈 리스트로 반환된다.")
			void returnsEmptyList_whenLedgerDoesNotExist() {
				//given: 저장된 가게부가 모두 삭제된 상태이다.
				ledgerRepository.deleteAll();

				//when: 가계부 내역 조회를 요청한다.
				HistoryDashboardResponse result =target.getHistoryDashboard(HistoryType.MONTH);

				//then: 내역 그룹화는 비어있고, 통계는 모두 0이다.
				assertThat(result.getHistoryGroups()).isEmpty();

				assertThat(result.getStatistics())
						.extracting(
								LedgerStatistics::getTotal,
								LedgerStatistics::getIncome,
								LedgerStatistics::getOutlay
						)
						.containsExactly(0L, 0L, 0L);
			}
			
			@Test
			@DisplayName("여러 날짜의 내역이 그룹화가 된다.")
			void createsDateGroups_whenMultipleDatesAreGiven() {
				//when: 가계부 내역 조회를 요청한다.
				HistoryDashboardResponse result =target.getHistoryDashboard(HistoryType.MONTH);

				//then: 내역 그룹은 3개로 생성된다.
				assertThat(result.getHistoryGroups()).hasSize(3);

				assertThat(result.getHistoryGroups().keySet())
						.contains("2026. 01. 03 (토)", "2026. 01. 05 (월)", "2026. 01. 10 (토)");
			}
			
			@Test
			@DisplayName("수입과 지출 금액별로 통계가 올바르게 계산된다.")
			void validatesStatistics_whenHistoriesExist() {
				//given: 지출 내역이 저장되어 있다.
				saveOutlayLedger("code4", LocalDate.of(2026, 1, 15), 20000L, PaymentType.NONE);

				//when: 가계부 내역 조회를 요청한다.
				HistoryDashboardResponse result =target.getHistoryDashboard(HistoryType.MONTH);
				
				//then: 저장된 수입과 지출별 구분하여 금액 통계가 반환된다.
				assertThat(result.getStatistics())
						.extracting(
								LedgerStatistics::getTotal,
								LedgerStatistics::getIncome,
								LedgerStatistics::getOutlay
						)
						.containsExactly(
							105000L, 85000L, 20000L
						);
			}
			
			@ParameterizedTest
			@MethodSource("validHistories")
			@DisplayName("HistoryType에 맞는 기간의 내역만 조회된다.")
			void returnsHistories_whenHistoryTypeIsGiven(HistoryType type, int expectedCount) {
				//given: 여러 날짜를 가진 가계부 내역이 저장되어 있다.
				saveOutlayLedger("code4", LocalDate.of(2026, 1, 29), 30000L, PaymentType.CASH);
				saveOutlayLedger("code5", LocalDate.of(2026, 2, 1), 10000L, PaymentType.NONE);
				saveOutlayLedger("code6", LocalDate.of(2026, 2, 15), 1000L, PaymentType.BANK);

				//when: 가계부 내역 조회를 요청한다.
				HistoryDashboardResponse result =target.getHistoryDashboard(type);
				
				//then: HistoryType에 따라 조회된 내역의 개수가 다르다.
				assertThat(result.getHistoryGroups()).hasSize(expectedCount);
			}

			static Stream<Arguments> validHistories() {
				return Stream.of(
						Arguments.of(
								named("YEAR인 경우", HistoryType.YEAR),
								6
						),
						Arguments.of(
								named("MONTH인 경우", HistoryType.MONTH),
								4
						),
						Arguments.of(
								named("WEEK인 경우", HistoryType.WEEK),
								2
						)
				);
			}
			
		}

	}


	@Nested
	@DisplayName("가계부 조회")
	@WithMockCustomUser
	class GetLedgerTest {

		@BeforeEach
		void setUp() {
		ledgerRepository.insert(
					LedgerFixture.newLedger()
							.memberId(member.getId())
							.code(LedgerTestData.CODE)
							.build()
			);
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@Test
			@DisplayName("자신이 작성한 가계부를 조회할 수 있다.")
			void returnsLedger_whenUserIsOwner() {
				//given: 저장된 회원번호와 가계부가 주어진다.
				String memberId = member.getId();
				String code = LedgerTestData.CODE;
				
				//when: 가계부를 조회한다.
				Ledger result = target.getLedger(memberId, code);
				
				//then: 가계부가 반환된다.
				assertThat(result).isNotNull();
				assertThat(result)
						.extracting(
								Ledger::getMemberId,
								Ledger::getCode
						)
						.containsExactly(
								memberId,
								code
						);
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {
			
			@Test
			@DisplayName("존재하지 않은 가계부를 조회하면 BusinessException이 발생한다.")
			void throwsBusinessException_whenLedgerDoesNotExist() {
				//given: 존재하지 않은 가계부 코드가 주어진다.
				String memberId = member.getId();
				String code = "error";
				
				//when: 가계부를 조회한다.
				Throwable throwable = catchThrowable(() -> target.getLedger(memberId, code));

				//then: NOT_FOUND_DATA 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(NOT_FOUND_DATA)
						.hasWork("가계부 조회")
						.hasCauseMessage("데이터 없음")
						.hasTarget(Ledger.class)
						.hasValue(memberId)
						.hasValue(code)
						.hasUserMessage("존재하지 않은 가계부");
			}
			
			@Test
			@DisplayName("다른 회원의 가계부를 조회하면 BusinessException이 발생한다.")
			void throwsBusinessException_whenUserIsNotOwner() {
				//given: 다른 회원의 가계부가 저장되어 있다.
				String memberId = member.getId();
				String code = "code1";

				Member otherMember = MemberFixture.builder().build();

				memberRepository.save(otherMember);

				ledgerRepository.insert(
						LedgerFixture.newLedger()
								.memberId(otherMember.getId())
								.code(code)
								.build()
				);
				
				//when & then: 가계부를 조회 중 예외가 발생한다.
				assertThatThrownBy(() -> target.getLedger(memberId, code))
						.isInstanceOf(BusinessException.class);
			}

		}

	}


	@Nested
	@DisplayName("가계부 상세 조회")
	@WithMockCustomUser
	class GetDetailTest {

		private Ledger ledger;

		@BeforeEach
		void setUp() {
			Long id = ledgerRepository.insert(
					LedgerFixture.newLedger().memberId(member.getId()).build()
			);

			ledger = ledgerRepository.findById(id);
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("이미지가 없는 가계부를 조회하면 상세 정보가 조회된다.")
			void returnsLedgerDetail_whenLedgerHasNoImage() {
				//given: 가계부 코드가 주어진다.
				String code = ledger.getCode();
				
				//when: 가계부 상세 정보를 조회한다.
				LedgerDetailResponse result = target.getDetailData(code);
				
				//then: 상세 정보를 반환한다.
				assertThat(result).isNotNull();

				assertThat(result.getDate()).isEqualTo("2026. 01. 01 (목)");

				assertThat(result.getType()).isEqualTo(CategoryType.INCOME);
				assertThat(result.getCategory())
						.extracting(
								CategoryItem::getCode,
								CategoryItem::getName
						)
						.containsExactly(
								CategoryTestData.SALARY_CODE,
								CategoryTestData.SALARY_NAME
						);

				assertThat(result.getImages())
						.hasSize(3)
						.containsOnlyOnce(
								"/image/ledger/slot-unlock.svg"
						);
			}

			@Test
			@DisplayName("이미지가 있는 가계부를 조회하면 상세 정보에 이미지 정보가 반환된다.")
			void returnsLedgerDetailWithImages_whenLedgerImageExists() {
				//given: 가계부 이미지가 저장되어 있다.
				String code = ledger.getCode();

				imageRepository.saveAll(
						List.of(
								LedgerImageFixture.newImage(ledger.getId(), 1)
						)
				);

				//when: 가계부 상세 정보를 조회한다.
				LedgerDetailResponse result = target.getDetailData(code);

				//then: 이미지 정보가 저장된다.
				assertThat(result.getImages())
						.containsExactly(
								"/uploads/ledger/" + member.getId() + "/image1.jpg",
								"/image/ledger/slot-lock.svg",
								"/image/ledger/slot-lock.svg"
						);
			}

		}
		
		@Nested
		@DisplayName("실패 케이스")
		class Failure {
			
			@Test
			@DisplayName("존재하지 않은 가계부를 조회하면 예외가 발생한다.")
			void throwsBusinessException_whenLedgerDoesNotExist() {
				//given: 가계부 코드가 주어진다.
				String code = "error";
				
				//when & then: 가계부 상세 정보를 조회하면 예외가 발생한다.
				assertThatThrownBy(() ->target.getDetailData(code))
						.isInstanceOf(BusinessException.class);
			}
			
			@Test
			@DisplayName("다른 회원의 가계부를 조회하면 예외가 발생한다.")
			void throwsBusinessException_whenLedgerBelongsToAnotherMember() {
				//given: 다른 회원의 가계부 코드가 주어진다.
				Member member = memberRepository.save(
						MemberFixture.builder("abc").build()
				);

				Long id = ledgerRepository.insert(
						LedgerFixture.newLedger().memberId(member.getId()).code("code1").build()
				);

				String code = ledgerRepository.findById(id).getCode();

				//when & then: 가계부 상세 정보를 조회하면 예외가 발생한다.
				assertThatThrownBy(() ->target.getDetailData(code))
						.isInstanceOf(BusinessException.class);
			}
		
		}
		
	}

}