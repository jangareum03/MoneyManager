package com.moneymanager.ledger.service.read;

import com.moneymanager.global.domain.vo.DateRange;
import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.security.utils.SecurityUtil;
import com.moneymanager.ledger.domain.dto.response.*;
import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.CategoryType;
import com.moneymanager.ledger.domain.enums.HistoryMenuType;
import com.moneymanager.ledger.domain.enums.HistoryType;
import com.moneymanager.ledger.domain.enums.SlotStatus;
import com.moneymanager.ledger.repository.LedgerRepository;
import com.moneymanager.ledger.service.mapper.LedgerMapper;
import com.moneymanager.ledger.service.policy.LedgerDatePolicy;
import com.moneymanager.ledger.service.policy.LedgerHistoryPolicy;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import com.moneymanager.support.fixture.entity.category.CategoryFixture;
import com.moneymanager.support.fixture.entity.category.CategoryHierarchyFixture;
import com.moneymanager.support.fixture.entity.category.IncomeCategoryFixture;
import com.moneymanager.support.fixture.entity.category.OutlayCategoryFixture;
import com.moneymanager.support.fixture.response.LedgerDetailResponseFixture;
import com.moneymanager.support.fixture.response.LedgerHistoryQueryFixture;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.moneymanager.global.exception.code.LedgerErrorCode.NOT_FOUND_DATA;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service<br>
 * 파일이름       : LedgerReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 24<br>
 * 설명              : LedgerReadService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 7. 24</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
public class LedgerReadServiceTest {

	@InjectMocks
	private LedgerReadService target;

	@Mock
	private SecurityUtil securityUtil;

	@Mock
	private LedgerHistoryPolicy historyPolicy;

	@Mock
	private LedgerDatePolicy datePolicy;

	@Mock
	private LedgerImageReadService imageReadService;

	@Mock
	private CategoryReadService categoryReadService;

	@Mock
	private LedgerRepository ledgerRepository;

	@Spy
	private LedgerMapper mapper;

	@Nested
	@DisplayName("작성 1단계 DTO를 조회할 때")
	class GetStep1DataTest {

		@BeforeEach
		void setUp() {
			LocalDate date = LocalDate.of(2025, 3, 10);

			when(datePolicy.minimum())
					.thenReturn(date.minusYears(5));

			when(datePolicy.maximum())
					.thenReturn(date);
		}

		@Test
		@DisplayName("작성 가능한 연도 목록을 반환한다.")
		void returnsAvailableYears() {
			//when
			LedgerWriteStep1Response result = target.getWriteStep1Data();

			//then
			assertThat(result.getYears())
					.hasSize(6)
					.containsExactly(2020, 2021, 2022, 2023, 2024, 2025);
		}

		@Test
		@DisplayName("작성 가능한 월 목록을 반환한다.")
		void returnsAvailableMonths() {
			//when
			LedgerWriteStep1Response result = target.getWriteStep1Data();

			//then
			assertThat(result.getMonths())
					.hasSize(3)
					.containsExactly(1, 2, 3);
		}

		@Test
		@DisplayName("작성 가능한 일 목록을 반환한다.")
		void returnsAvailableDays() {
			//when
			LedgerWriteStep1Response result = target.getWriteStep1Data();

			//then
			assertThat(result.getDays())
					.hasSize(10)
					.containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		}

	}


//	@Nested
//	@DisplayName("작성 2단계 데이터 얻기")
//	class GetStep2DataTest {
//
//		@Nested
//		@DisplayName("성공 케이스")
//		class Success {
//
//			LocalDate date = LocalDate.now(clock);
//
//			@BeforeEach
//			void setUp() {
//				clock.set(LocalDate.of(2026, 1, 15));
//
//				when(imageReadService.resolveImageSlots())
//						.thenReturn(List.of(
//								ImageSlot.ofEmptySlot(),
//								ImageSlot.ofLockedSlot(),
//								ImageSlot.ofLockedSlot()
//						));
//			}
//
//			@Test
//			@DisplayName("수입 유형이면 정상적인 데이터가 반환된다.")
//			void returnsData_whenIncomeTypeIsGiven() {
//				//given: 수입 유형과 오늘 날짜가 주어진다.
//				CategoryType type = CategoryType.INCOME;
//
//				when(categoryReadService.getMiddleCategories(type))
//						.thenReturn(List.of(
//								CategoryItem.from(CategoryFixture.income())
//						));
//
//				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
//				LedgerWriteStep2Response result = target.getWriteStep2Data(type, date);
//
//				//then: 응답 데이터가 반환된다.
//				assertThat(result).isNotNull();
//
//				assertThat(result.getTitle()).isEqualTo("2026년 01월 15일 목요일");
//
//				assertThat(result.getType()).isEqualTo(type);
//				assertThat(result.getImageSlot()).hasSize(3);
//				assertThat(result.getCategories()).hasSize(1);
//
//				assertThat(result.getFixed())
//						.hasSize(2)
//						.containsExactly(FixedType.REPEAT, FixedType.VARIABLE);
//
//				assertThat(result.getPaymentTypes())
//						.hasSize(4)
//						.containsExactly(
//								PaymentType.NONE, PaymentType.CASH, PaymentType.CARD, PaymentType.BANK
//						);
//
//				//then: 카테고리와 이미지 서비스가 요청된다.
//				verify(categoryReadService).getMiddleCategories(type);
//				verify(imageReadService).resolveImageSlots();
//			}
//
//			@Test
//			@DisplayName("지출 유형이면 정상적인 데이터가 반환된다.")
//			void returnsData_whenExpenseTypeIsGiven() {
//				//given: 지출 유형과 오늘 날짜가 주어진다.
//				clock.set(LocalDate.of(2026, 1, 20));
//
//				CategoryType type = CategoryType.OUTLAY;
//				LocalDate date = LocalDate.now(clock);
//
//				when(categoryReadService.getMiddleCategories(type))
//						.thenReturn(List.of(
//								CategoryItem.from(CategoryFixture.outlay())
//						));
//
//				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
//				LedgerWriteStep2Response result = target.getWriteStep2Data(type, date);
//
//				//then: 응답 데이터가 반환된다.
//				assertThat(result).isNotNull();
//
//				assertThat(result.getType()).isEqualTo(type);
//
//				//then: 카테고리와 이미지 서비스가 요청된다.
//				verify(categoryReadService).getMiddleCategories(type);
//				verify(imageReadService).resolveImageSlots();
//			}
//
//			@Test
//			@DisplayName("카테고리가 없으면 응답 데이터에 빈 리스트가 포함된다.")
//			void returnsEmptyList_whenCategoryDoesNotExist() {
//				//given: 카테고리 목록이 빈 리스트가 반환되도록 CategoryReadService 동작이 정의되어 있다.
//				CategoryType type = CategoryType.INCOME;
//
//				when(categoryReadService.getMiddleCategories((type)))
//						.thenReturn(Collections.emptyList());
//
//				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
//				LedgerWriteStep2Response result = target.getWriteStep2Data(type, date);
//
//				//then: 카테고리 리스트가 비어있다.
//				assertThat(result.getCategories()).isEmpty();
//			}
//
//			@Test
//			@DisplayName("이미지 슬롯이 없으면 응답 데이터에 빈 리스트가 포함된다.")
//			void returnsEmptyList_whenImageSlotDoesNotExist() {
//				//given: 이미지 슬롯이 빈 리스트가 반환되도록 LedgerImageReadService 동작이 정의되어 있다.
//				CategoryType type = CategoryType.OUTLAY;
//
//				when(imageReadService.resolveImageSlots())
//						.thenReturn(Collections.emptyList());
//
//				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
//				LedgerWriteStep2Response result = target.getWriteStep2Data(type, date);
//
//				//then: 이미지 슬롯 리스트가 비어있다.
//				assertThat(result.getImageSlot()).isEmpty();
//			}
//
//			@Test
//			@DisplayName("카테고리와 이미지 정보 둘 다 없으면 응답 데이터에 빈 리스트가 포함된다.")
//			void returnsEmptyLists_whenCategoryAndImageDoNotExist() {
//				//given: 카테고리 리스트와 이미지 슬롯이 빈 리스트가 반환되도록 동작이 정의되어 있다.
//				CategoryType type = CategoryType.OUTLAY;
//
//				when(categoryReadService.getMiddleCategories(type))
//						.thenReturn(Collections.emptyList());
//
//				when(imageReadService.resolveImageSlots())
//						.thenReturn(Collections.emptyList());
//
//				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
//				LedgerWriteStep2Response result = target.getWriteStep2Data(type, date);
//
//				//then: 이미지 슬롯 리스트가 비어있다.
//				assertThat(result.getCategories()).isEmpty();
//				assertThat(result.getImageSlot()).isEmpty();
//			}
//
//		}
//
//	}


	@Nested
	@DisplayName("내역 리스트 얻기")
	class GetHistoryDashboard {
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@BeforeEach
			void setUp() {
				when(securityUtil.getMemberId())
						.thenReturn(MemberTestData.MEMBER_ID);

				when(historyPolicy.calculateDateRange(any(), any()))
						.thenReturn(new DateRange("20260101", "20260131"));

				when(historyPolicy.getTitleByHistoryType(any()))
						.thenReturn("제목");
			}
		
			@ParameterizedTest
			@EnumSource(HistoryType.class)
			@DisplayName("정상적인 흐름이면 응답 데이터가 생성된다.")
			void createsResponseData_whenRequestIsValid(HistoryType type) {
				//given: 정상적인 응답 생성을 위한 조회 결과들을 반환되도록 정의되어 있다.
				when(ledgerRepository.findHistoriesByMemberAndDateBetween(any(), any(), any()))
						.thenReturn(List.of(
								LedgerHistoryQueryFixture.incomeHistory(LocalDate.of(2026, 1, 1), 30000),
								LedgerHistoryQueryFixture.outlayHistory(LocalDate.of(2026, 1, 5), 15000)
						));
				
				//when: 가계부 내역 조회 응답을 생성한다.
				HistoryDashboardResponse result = target.getHistoryDashboard(type);
				
				//then: 가계부 내역 응답이 반환된다.
				assertThat(result.getTitle()).isEqualTo("제목");
				assertThat(result.getMenu()).hasSize(HistoryMenuType.values().length);

				assertThat(result.getStatistics())
						.extracting(
								LedgerStatistics::getTotal,
								LedgerStatistics::getIncome,
								LedgerStatistics::getOutlay
						)
						.containsExactly(
								45000L, 30000L, 15000L
						);

				assertThat(result.getHistoryGroups())
						.hasSize(2)
						.containsOnlyKeys("2026. 01. 01 (목)", "2026. 01. 05 (월)");
			}
			
			@Test
			@DisplayName("저장된 내역이 1건이면 한 개의 그룹이 생성된다.")
			void createsSingleGroup_whenSingleHistoryExists() {
				//given: 가계부 내역이 1건이 반환되도록 동작이 정의되어 있다.
				when(ledgerRepository.findHistoriesByMemberAndDateBetween(any(), any(), any()))
						.thenReturn(List.of(
								LedgerHistoryQueryFixture.create()
						));

				//when: 가계부 내역 조회 응답을 생성한다.
				HistoryDashboardResponse result = target.getHistoryDashboard(HistoryType.MONTH);
				
				//then: 하나의 가계부 내역으로 하나의 그룹이 생성된다.
				Map<String, List<HistoryItem>> groups = result.getHistoryGroups();

				assertThat(groups).hasSize(1);

				assertThat(groups.values())
						.singleElement()
						.satisfies(items -> assertThat(items).hasSize(1));
			}
			
			@Test
			@DisplayName("저장된 내역이 여러건이면 여러 개의 그룹이 생성된다.")
			void createsMultipleGroups_whenMultipleHistoriesExist() {
				//given: 가계부 내역이 여러 건이 반환되도록 동작이 정의되어 있다.
				when(ledgerRepository.findHistoriesByMemberAndDateBetween(any(), any(), any()))
						.thenReturn(List.of(
								LedgerHistoryQueryFixture.incomeHistory(LocalDate.of(2026, 1, 2), 30000),
								LedgerHistoryQueryFixture.incomeHistory(LocalDate.of(2026, 1, 11), 15000),
								LedgerHistoryQueryFixture.outlayHistory(LocalDate.of(2026, 1, 11), 5000)
						));

				//when: 가계부 내역 조회 응답을 생성한다.
				HistoryDashboardResponse result = target.getHistoryDashboard(HistoryType.MONTH);

				//then: 가계부 내역으로 여러 개의 그룹이 생성된다.
				Map<String, List<HistoryItem>> groups = result.getHistoryGroups();

				assertThat(groups).hasSize(2);

				assertThat(groups.get("2026. 01. 02 (금)")).hasSize(1);
				assertThat(groups.get("2026. 01. 11 (일)")).hasSize(2);
			}
			
			@Test
			@DisplayName("내역 그룹에서 가계부 거래날짜 내림차순으로 정렬된다.")
			void sortsHistoriesByTransactionDateDescending() {
				//given: 가계부 내역이 여러 건이 반환되도록 동작이 정의되어 있다.
				when(ledgerRepository.findHistoriesByMemberAndDateBetween(any(), any(), any()))
						.thenReturn(List.of(
								LedgerHistoryQueryFixture.incomeHistory(LocalDate.of(2026, 1, 2), 30000),
								LedgerHistoryQueryFixture.incomeHistory(LocalDate.of(2026, 1, 5), 15000),
								LedgerHistoryQueryFixture.outlayHistory(LocalDate.of(2026, 1, 11), 5000)
						));

				//when: 가계부 내역 조회 응답을 생성한다.
				HistoryDashboardResponse result = target.getHistoryDashboard(HistoryType.MONTH);
				
				//then: 거래일 내림차순으로 그룹이 생성된다.
				assertThat(result.getHistoryGroups().keySet())
						.containsExactly(
							"2026. 01. 11 (일)",
							"2026. 01. 05 (월)",
							"2026. 01. 02 (금)"
						);
			}
			
			@Test
			@DisplayName("내역에서 날짜가 없으면 그룹화에 포함되지 않는다.")
			void filtersHistories_whenTransactionDateIsNull() {
				//given: 가계부 내역 중 날짜가 없는 내역이 반환되도록 동작이 정의되어 있다.
				when(ledgerRepository.findHistoriesByMemberAndDateBetween(any(), any(), any()))
						.thenReturn(List.of(
								LedgerHistoryQueryFixture.incomeHistory(LocalDate.of(2026, 1, 2), 30000),
								LedgerHistoryQueryFixture.incomeHistory(null, 15000),
								LedgerHistoryQueryFixture.outlayHistory(LocalDate.of(2026, 1, 11), 5000)
						));

				//when: 가계부 내역 조회 응답을 생성한다.
				HistoryDashboardResponse result = target.getHistoryDashboard(HistoryType.MONTH);

				//then: 거래일이 null인 내역은 그룹에 포함되지 않는다.
				assertThat(result.getHistoryGroups().keySet()).hasSize(2);
			}
			
			@Test
			@DisplayName("수입 내역만 있으면 지출 합계는 0이 된다.")
			void validatesExpenseTotalAsZero_whenOnlyIncomeHistoriesExist() {
				//given: 수입 내역만 반환되도록 동작이 정의되어 있다.
				when(ledgerRepository.findHistoriesByMemberAndDateBetween(any(), any(), any()))
						.thenReturn(List.of(
								LedgerHistoryQueryFixture.incomeHistory(LocalDate.of(2026, 1, 2), 30000),
								LedgerHistoryQueryFixture.incomeHistory(LocalDate.of(2026, 1, 5), 15000),
								LedgerHistoryQueryFixture.incomeHistory(LocalDate.of(2026, 1, 11), 5000)
						));

				//when: 가계부 내역 조회 응답을 생성한다.
				HistoryDashboardResponse result = target.getHistoryDashboard(HistoryType.MONTH);

				//then: 내역 전체의 금액 합계는 수입에 저장된다.
				assertThat(result.getStatistics())
						.extracting(
								LedgerStatistics::getTotal,
								LedgerStatistics::getIncome,
								LedgerStatistics::getOutlay
						)
						.containsExactly(
								50000L, 50000L, 0L
						);
			}
			
			@Test
			@DisplayName("지출 내역만 있으면 수입 합계는 0이 된다.")
			void validatesIncomeTotalAsZero_whenOnlyExpenseHistoriesExist() {
				//given: 지출 내역만 반환되도록 동작이 정의되어 있다.
				when(ledgerRepository.findHistoriesByMemberAndDateBetween(any(), any(), any()))
						.thenReturn(List.of(
								LedgerHistoryQueryFixture.outlayHistory(LocalDate.of(2026, 1, 2), 30000),
								LedgerHistoryQueryFixture.outlayHistory(LocalDate.of(2026, 1, 5), 15000)
						));

				//when: 가계부 내역 조회 응답을 생성한다.
				HistoryDashboardResponse result = target.getHistoryDashboard(HistoryType.MONTH);

				//then: 내역 전체의 금액 합계는 지출에 저장된다.
				assertThat(result.getStatistics())
						.extracting(
								LedgerStatistics::getTotal,
								LedgerStatistics::getIncome,
								LedgerStatistics::getOutlay
						)
						.containsExactly(
								45000L, 0L, 45000L
						);
			}
			
		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("회원 조회가 실패하면 예외가 전달된다.")
			void throwsException_whenMemberFetchFails() {
				//given: 회원 인증 중 예외가 발생하도록 동작이 정의되어 있다.
				HistoryType type = HistoryType.MONTH;

				when(securityUtil.getMemberId())
						.thenThrow(BusinessException.class);

				//when & then: 가계부 내역 조회 응답 반환 중 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.getHistoryDashboard(type))
						.isInstanceOf(BusinessException.class);

				//then: LedgerHistoryPolicy와 LedgerRepository 클래스의 메서드는 호출되지 않는다.
				verify(historyPolicy, never()).calculateDateRange(eq(type), any(LocalDate.class));
				verify(historyPolicy, never()).validate(any());
				verify(historyPolicy, never()).getTitleByHistoryType(eq(type));

				verify(ledgerRepository, never()).findHistoriesByMemberAndDateBetween(any(), any(), any());
			}

			@Test
			@DisplayName("날짜 기간 생성에서 실패하면 예외가 전달된다.")
			void throwsException_whenDatePeriodCreationFails() {
				//given: 회원 인증은 성공하지만, 기간 생성 중 예외가 발생하도록 동작이 정의되어 있다.
				when(securityUtil.getMemberId())
						.thenReturn(MemberTestData.MEMBER_ID);

				when(historyPolicy.calculateDateRange(any(), any()))
						.thenThrow(ValidationException.class);

				//when & then: 가계부 내역 조회 응답 반환 중 ValidationException이 발생한다.
				assertThatThrownBy(() -> target.getHistoryDashboard(HistoryType.YEAR))
						.isInstanceOf(ValidationException.class);

				//then: 기간 검증, 내역 조회, 제목 생성 메서드는 호출되지 않는다.
				verify(securityUtil).getMemberId();

				verify(historyPolicy, never()).validate(any());
				verify(historyPolicy, never()).getTitleByHistoryType(any());
				verify(ledgerRepository, never()).findHistoriesByMemberAndDateBetween(any(), any(), any());
			}

		}

	}


	@Nested
	@DisplayName("가계부 조회")
	class GetLedgerTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("회원의 가계부를 반환한다.")
			void returnsLedger_whenRepositoryReturnsLedger() {
				//given: 가계부가 저장되어 있다.
				String memberId = MemberTestData.MEMBER_ID;
				String code = LedgerTestData.CODE;

				Ledger ledger = LedgerFixture.savedLedger(1L)
						.memberId(memberId)
						.code(code)
						.build();

				when(ledgerRepository.findByCode(memberId, code))
						.thenReturn(ledger);
				
				//when: 회원의 가계부를 조회한다.
				Ledger result = target.getLedger(memberId, code);

				//then: 가계부가 반환된다.
				assertThat(result).isEqualTo(ledger);
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {
			
			@Test
			@DisplayName("존재하지 않은 가계부면 예외가 발생한다.")
			void throwsBusinessException_whenEmptyResultDataAccessExceptionOccurs() {
				//given: Repository 조회 결과가 없다.
				String memberId = MemberTestData.MEMBER_ID;
				String code = LedgerTestData.CODE;

				when(ledgerRepository.findByCode(memberId, code))
						.thenThrow(new EmptyResultDataAccessException(1));
				
				//when: 회원의 가계부를 조회한다.
				Throwable throwable = catchThrowable(() -> target.getLedger(memberId, code));

				//then: NOT_FOUND_DATA 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(NOT_FOUND_DATA)
						.hasWork("가계부 조회")
						.hasCauseMessage("데이터 없음")
						.hasTarget(Ledger.class)
						.hasValue(memberId)
						.hasValue(code)
						.hasUserMessage("않은 가계부");
			}

		}

	}


	@Nested
	@DisplayName("가계부 상세 조회")
	class GetDetailTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@Test
			@DisplayName("가계부 이미지가 없어도 상세정보가 조회된다.")
			void returnsLedgerDetail_whenLedgerImageDoesNotExist() {
				//given: 상제 정보가 조회되도록 동작이 정의되어 있다.
				String memberId = MemberTestData.MEMBER_ID;
				String code = LedgerTestData.CODE;

				Ledger ledger = LedgerFixture.savedLedger(1L).build();
				Category category = CategoryFixture.salary();
				List<String> images = List.of(
						"/image/ledger/slot-unlock.svg",
						"/image/ledger/slot-unlock.svg",
						"/image/ledger/slot-unlock.svg"
				);

				LedgerDetailResponse response = LedgerDetailResponseFixture.create();

				when(ledgerRepository.findByCode(memberId, code))
						.thenReturn(ledger);

				when(categoryReadService.getCategory(ledger.getCategory()))
						.thenReturn(category);

				when(imageReadService.resolveImageSlots(ledger.getId()))
						.thenReturn(List.of(
								ImageSlot.ofEmptySlot(), ImageSlot.ofEmptySlot(), ImageSlot.ofEmptySlot()
						));

				when(mapper.toDetailDto(ledger, category, images))
						.thenReturn(response);

				//when: 가계부 상세 조회를 요청한다.
				LedgerDetailResponse result = target.getDetailData(ledger.getCode());

				//then: 상세 정보를 반환한다.
				assertThat(result)
						.isNotNull()
						.usingRecursiveComparison()
						.isEqualTo(response);
			}
			
			@Test
			@DisplayName("가계부 이미지가 있으면 파일경로 리스트가 반환된다.")
			void returnsImageFilePaths_whenLedgerImageExists() {
				//given: 상제 정보가 조회되도록 동작이 정의되어 있다.
				String memberId = MemberTestData.MEMBER_ID;
				String code = LedgerTestData.CODE;

				Ledger ledger = LedgerFixture.savedLedger(1L).build();
				Category category = CategoryFixture.salary();
				List<String> images = List.of(
						"/uploads/ledger/파일이름",
						"/image/ledger/slot-unlock.svg",
						"/image/ledger/slot-lock.svg"
				);

				LedgerDetailResponse response = LedgerDetailResponseFixture.create();

				when(ledgerRepository.findByCode(memberId, code))
						.thenReturn(ledger);

				when(categoryReadService.getCategory(ledger.getCategory()))
						.thenReturn(category);

				when(imageReadService.resolveImageSlots(ledger.getId()))
						.thenReturn(List.of(
								ImageSlot.ofFilledSlot("파일이름"), ImageSlot.ofEmptySlot(), ImageSlot.ofLockedSlot()
						));

				when(mapper.toDetailDto(ledger, category, images))
						.thenReturn(response);

				//when: 가계부 상세 조회를 요청한다.
				LedgerDetailResponse result = target.getDetailData(ledger.getCode());

				//then: 상세 정보를 반환한다.
				assertThat(result)
						.isNotNull()
						.usingRecursiveComparison()
						.isEqualTo(response);
			}
			
		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("가계부 조회가 실패하면 예외가 발생한다.")
			void throwsBusinessException_whenLedgerSearchFails() {
				//given: 가계부 조회가 실패하도록 동작이 정의되어 있다.
				String memberId = MemberTestData.MEMBER_ID;
				String code = LedgerTestData.CODE;

				when(ledgerRepository.findByCode(memberId, code))
						.thenThrow(new EmptyResultDataAccessException(1));

				//when & then: 가계부 상세 조회를 요청하면 예외가 발생한다.
				assertThatThrownBy(() -> target.getDetailData(code))
						.isInstanceOf(BusinessException.class);
				
				//then: 카테고리와 이미지 서비스가 호출되지 않는다.
				verify(categoryReadService, never()).getCategory(any());
				verify(imageReadService, never()).resolveImageSlots(any());
				verify(mapper, never()).toDetailDto(any(), any(), any());
			}
			
			@Test
			@DisplayName("카테고리 조회가 실패하면 예외가 발생한다.")
			void throwsBusinessException_whenCategorySearchFails() {
				//given: 카테고리 조회가 실패하도록 동작이 정의되어 있다.
				String memberId = MemberTestData.MEMBER_ID;
				String code = LedgerTestData.CODE;

				Ledger ledger = LedgerFixture.savedLedger(1L).build();

				when(ledgerRepository.findByCode(memberId, code))
						.thenReturn(ledger);

				when(categoryReadService.getCategory(ledger.getCategory()))
						.thenThrow(BusinessException.class);

				//when & then: 가계부 상세 조회를 요청하면 예외가 발생한다.
				assertThatThrownBy(() -> target.getDetailData(code))
						.isInstanceOf(BusinessException.class);

				//then: 카테고리와 이미지 서비스가 호출되지 않는다.
				verify(imageReadService, never()).resolveImageSlots(any());
				verify(mapper, never()).toDetailDto(any(), any(), any());
			}
			
		}

	}


	@Nested
	@DisplayName("가계부 수정 정보 조회")
	class GetEditTest {
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {

			private Ledger ledger  = LedgerFixture.savedLedger(1L)
					.memberId(MemberTestData.MEMBER_ID)
					.build();

			@BeforeEach
			void setUp() {
				when(ledgerRepository.findByCode(MemberTestData.MEMBER_ID, ledger.getCode()))
						.thenReturn(ledger);
			}
		
			@Test
			@DisplayName("존재하는 가계부 코드로 수정 데이터를 조회한다.")
			void returnsUpdateData_whenLedgerCodeExists() {
				//given: 수정 데이터를 반환하도록 동작이 정의되어 있다.
				String code = ledger.getCode();

				CategoryEditInfo categoryEditInfo = createCategoryEdit(CategoryType.INCOME);
				List<ImageSlot> images = createImageSlots();

				mockCategory(categoryEditInfo);
				mockSlots(images);
				
				//when: 가계부 수정 정보를 조회한다.
				LedgerEditResponse result = target.getEditData(code);
				
				//then: 저장된 가계부 정보가 반환된다.
				assertThat(result.getDate()).isEqualTo("2026년 01월 01일 목요일");
				assertThat(result.getAmount()).isEqualTo(ledger.getMoney().getAmount());
				assertThat(result.getPaymentType()).isEqualTo(ledger.getMoney().getPaymentType());
				assertThat(result.getMemo()).isEqualTo(ledger.getMemo());

				assertThat(result.getPlaceName()).isNull();
				assertThat(result.getRoadAddress()).isNull();
				assertThat(result.getDetailAddress()).isNull();

				assertThat(result.getFixed())
						.extracting(
								LedgerFixed::getFix,
								LedgerFixed::getCycle
						)
						.containsExactly(
								ledger.getFix(),
								ledger.getFixCycle()
						);
			}
			
			@Test
			@DisplayName("가계부 코드와 부모 카테고리가 포함한 선택된 카테고리 목록이 반환된다.")
			void returnsSelectedCategories_whenLedgerCodeAndParentCategoryAreGiven() {
				//given: 수정 데이터를 반환하도록 동작이 정의되어 있다.
				String code = ledger.getCode();

				CategoryEditInfo categoryEditInfo = createCategoryEdit(CategoryType.INCOME);
				List<ImageSlot> images = createImageSlots();

				mockCategory(categoryEditInfo);
				mockSlots(images);

				when(categoryReadService.findCategoryHierarchy(ledger.getCategory()))
						.thenReturn(hierarchyItems(CategoryType.INCOME));

				//when: 가계부 수정 정보를 조회한다.
				LedgerEditResponse result = target.getEditData(code);
				
				//then: 선택된 카테고리 목록이 포함된다.
				CategoryEditInfo resultCategoryEditInfo = result.getCategoryEditInfo();

				assertThat(resultCategoryEditInfo).isNotNull();
				assertThat(resultCategoryEditInfo.getSelected())
						.hasSize(2)
						.containsExactly("010100", ledger.getCategory());
			}

			@Test
			@DisplayName("가계부 카테고리가 01로 시작하면 수입 카테고리 목록이 반환된다.")
			void returnsIncomeCategories_whenCategoryCodeStartsWith01() {
				//given: 수입 카테고리 코드가 주어진다.
				ledger.changeCategory(CategoryTestData.SALARY_CODE);

				String code = ledger.getCode();
				CategoryType type = CategoryType.INCOME;

				CategoryEditInfo categoryEditInfo = createCategoryEdit(type);
				List<ImageSlot> images = createImageSlots();

				mockCategory(categoryEditInfo);
				mockSlots(images);

				when(categoryReadService.findCategoryHierarchy(ledger.getCategory()))
						.thenReturn(hierarchyItems(type));

				//when: 가계부 수정 정보를 조회한다.
				LedgerEditResponse result = target.getEditData(code);

				//then: 수입 카테고리 목록이 반환된다.
				CategoryEditInfo resultCategoryEditInfo = result.getCategoryEditInfo();

				assertThat(resultCategoryEditInfo).isNotNull();

				assertThat(resultCategoryEditInfo.getMiddleOptions()).isEqualTo(categoryEditInfo.getMiddleOptions());
				assertThat(resultCategoryEditInfo.getLowOptions()).isEqualTo(categoryEditInfo.getLowOptions());

				verify(categoryReadService).getMiddleCategories(type);
				verify(categoryReadService).getLowCategories(type);
			}
			
			@Test
			@DisplayName("가계부 코드가 02로 시작하면 지출 카테고리 목록이 반환된다.")
			void returnsExpenseCategories_whenLedgerCodeStartsWith02() {
				//given: 지출 카테고리 코드가 주어진다.
				ledger.changeCategory(CategoryTestData.FOOD_CODE);

				String code = ledger.getCode();
				CategoryType type = CategoryType.OUTLAY;

				CategoryEditInfo categoryEditInfo = createCategoryEdit(type);
				List<ImageSlot> images = createImageSlots();

				mockCategory(categoryEditInfo);
				mockSlots(images);

				when(categoryReadService.findCategoryHierarchy(ledger.getCategory()))
						.thenReturn(hierarchyItems(type));

				//when: 가계부 수정 정보를 조회한다.
				LedgerEditResponse result = target.getEditData(code);

				//then: 지출 카테고리 목록이 반환된다.
				CategoryEditInfo resultCategoryEditInfo = result.getCategoryEditInfo();

				assertThat(resultCategoryEditInfo).isNotNull();

				assertThat(resultCategoryEditInfo.getMiddleOptions()).isEqualTo(categoryEditInfo.getMiddleOptions());
				assertThat(resultCategoryEditInfo.getLowOptions()).isEqualTo(categoryEditInfo.getLowOptions());

				verify(categoryReadService).getMiddleCategories(type);
				verify(categoryReadService).getLowCategories(type);
			}

			@Test
			@DisplayName("가계부에 이미지가 없으면 Empty 슬롯이 포함된다.")
			void returnsEmptySlot_whenLedgerHasNoImage() {
				//given: 수정 데이터를 반환하도록 동작이 정의되어 있다.
				String code = ledger.getCode();

				CategoryEditInfo categoryEditInfo = createCategoryEdit(CategoryType.INCOME);
				List<ImageSlot> images = createImageSlots();

				mockCategory(categoryEditInfo);
				mockSlots(images);

				when(categoryReadService.findCategoryHierarchy(ledger.getCategory()))
						.thenReturn(hierarchyItems(CategoryType.INCOME));

				//when: 가계부 수정 정보를 조회한다.
				LedgerEditResponse result = target.getEditData(code);

				//then: Empty 슬롯이 포함된다.
				List<ImageSlot> imageSlots = result.getImages();

				assertThat(imageSlots)
						.hasSize(3)
						.extracting(
								ImageSlot::getStatus
						)
						.doesNotContain(SlotStatus.FILLED);
			}
			
			@Test
			@DisplayName("가계부에 이미지가 있으면 Filled 슬롯이 포함된다.")
			void returnsFilledSlot_whenLedgerHasImage() {
				//given: 수정 데이터를 반환하도록 동작이 정의되어 있다.
				String code = ledger.getCode();

				CategoryEditInfo categoryEditInfo = createCategoryEdit(CategoryType.INCOME);
				List<ImageSlot> images = List.of(
						ImageSlot.ofFilledSlot("이미지"),
						ImageSlot.ofEmptySlot(),
						ImageSlot.ofLockedSlot()
				);

				mockCategory(categoryEditInfo);
				mockSlots(images);

				when(categoryReadService.findCategoryHierarchy(ledger.getCategory()))
						.thenReturn(hierarchyItems(CategoryType.INCOME));

				//when: 가계부 수정 정보를 조회한다.
				LedgerEditResponse result = target.getEditData(code);

				//then: Empty 슬롯이 포함된다.
				List<ImageSlot> imageSlots = result.getImages();

				assertThat(imageSlots)
						.hasSize(3)
						.extracting(
								ImageSlot::getStatus
						)
						.contains(SlotStatus.FILLED);
			}

			private void mockCategory(CategoryEditInfo categoryEditInfo) {
				when(categoryReadService.getMiddleCategories(any(CategoryType.class)))
						.thenReturn(categoryEditInfo.getMiddleOptions());

				when(categoryReadService.getLowCategories(any(CategoryType.class)))
						.thenReturn(categoryEditInfo.getLowOptions());
			}

			private void mockSlots(List<ImageSlot> slots) {
				when(imageReadService.resolveImageSlots(ledger.getId()))
						.thenReturn(slots);
			}

			private CategoryEditInfo createCategoryEdit(CategoryType type) {
				return switch (type) {
					case INCOME -> CategoryEditInfo.builder()
							.selected(List.of(CategoryTestData.EARNED_CODE, CategoryTestData.SALARY_CODE))
							.middleOptions(CategoryItem.from(IncomeCategoryFixture.createMiddleAll()))
							.lowOptions(CategoryItem.from(IncomeCategoryFixture.createLowAll()))
							.build();
					case OUTLAY -> CategoryEditInfo.builder()
							.selected(List.of(CategoryTestData.FOOD_CODE, CategoryTestData.SNACK_CODE))
							.middleOptions(CategoryItem.from(OutlayCategoryFixture.createMiddleAll()))
							.lowOptions(CategoryItem.from(OutlayCategoryFixture.createLowAll()))
							.build();
				};
			}

			private List<ImageSlot> createImageSlots() {
				return List.of(
						ImageSlot.ofEmptySlot(),
						ImageSlot.ofEmptySlot(),
						ImageSlot.ofLockedSlot()
				);
			}

			private List<CategoryItem> hierarchyItems(CategoryType type) {
				return switch (type) {
					case INCOME ->
							CategoryHierarchyFixture.incomeHierarchy()
									.stream()
									.map(CategoryItem::from)
									.toList();
					case OUTLAY -> CategoryHierarchyFixture.outlayHierarchy()
							.stream()
							.map(CategoryItem::from)
							.toList();
				};
			}

		}
		
		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("다른 회원의 가계부 코드면 예외가 발생한다.")
			void throwsException_whenLedgerCodeBelongsToAnotherMember() {
				//given: 다른 회원의 가계부가 존재한다.
				String code = "otherCode";

				when(ledgerRepository.findByCode(MemberTestData.MEMBER_ID, code))
						.thenThrow(BusinessException.class);

				//when & then: 가계부 수정 정보를 조회하면 예외가 발생한다.
				assertThatThrownBy(() -> target.getEditData(code))
						.isInstanceOf(BusinessException.class);

				//then: 메서드 호출 검증한다.
				verify(categoryReadService, never()).getMiddleCategories(any());
				verify(categoryReadService, never()).getLowCategories(any());

				verify(imageReadService, never()).resolveImageSlots(any());
				verify(mapper, never()).toEditDto(any(), any(), any());
			}
		
			@Test
			@DisplayName("존재하지 않은 가계부 코드는 예외가 발생한다.")
			void throwsBusinessException_whenLedgerCodeDoesNotExist() {
				//given: 가계부 코드가 주어진다.
				String code = "none";

				when(ledgerRepository.findByCode(MemberTestData.MEMBER_ID, code))
						.thenThrow(BusinessException.class);

				//when & then: 가계부 수정 정보를 조회하면 예외가 발생한다.
				assertThatThrownBy(() -> target.getEditData(code))
						.isInstanceOf(BusinessException.class);
			}
			
			@ParameterizedTest
			@NullSource
			@DisplayName("가계부 코드가 null이면 예외가 발생한다.")
			void throwsException_whenLedgerCodeIsNull(String code) {
				when(ledgerRepository.findByCode(MemberTestData.MEMBER_ID, code))
						.thenThrow(BusinessException.class);

				//when & then: 가계부 수정 정보를 조회하면 예외가 발생한다.
				assertThatThrownBy(() -> target.getEditData(code))
						.isInstanceOf(BusinessException.class);
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("가계부 코드가 비어있으면 예외가 발생한다.")
			void throwsException_whenLedgerCodeIsEmpty(String code) {
				when(ledgerRepository.findByCode(MemberTestData.MEMBER_ID, code))
						.thenThrow(BusinessException.class);

				//when & then: 가계부 수정 정보를 조회하면 예외가 발생한다.
				assertThatThrownBy(() -> target.getEditData(code))
						.isInstanceOf(BusinessException.class);
			}
			
		}

	}

}