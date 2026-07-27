package com.moneymanager.ledger.service;

import com.moneymanager.config.MutableClock;
import com.moneymanager.domain.global.vo.DateRange;
import com.moneymanager.domain.ledger.dto.response.*;
import com.moneymanager.domain.ledger.enums.*;
import com.moneymanager.domain.ledger.policy.LedgerHistoryPolicy;
import com.moneymanager.exception.exception.BusinessException;
import com.moneymanager.exception.exception.ValidationException;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.ledger.CategoryReadService;
import com.moneymanager.service.ledger.LedgerImageReadService;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.CategoryFixture;
import com.moneymanager.support.fixture.response.LedgerHistoryQueryFixture;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
@ExtendWith(MockitoExtension.class)
public class LedgerReadServiceTest {

	@InjectMocks
	private LedgerReadService target;

	@Mock
	private SecurityUtil securityUtil;

	@Mock
	private LedgerHistoryPolicy historyPolicy;

	@Mock
	private LedgerImageReadService imageReadService;

	@Mock
	private CategoryReadService categoryReadService;

	@Mock
	private LedgerRepository ledgerRepository;

	@Spy
	private MutableClock clock = new MutableClock();


	@Nested
	@DisplayName("작성 1단계 데이터 얻기")
	class GetStep1DataTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("가계부 1단계 작성에 필요한 데이터가 반환된다.")
			void returnsStep1Data_whenRequestIsValid() {
				//when: 가계부 작성 1단계에 필요한 데이터를 요청한다.
				LedgerWriteStep1Response result = target.getWriteStep1Data();
				
				//then: 응답 데이터가 반환된다.
				assertThat(result).isNotNull();

				assertThat(result.getTypes())
						.hasSize(2)
						.extracting(
								LedgerTypeResponse::getLabel,
								LedgerTypeResponse::getValue
						)
						.containsExactly(
								Tuple.tuple(CategoryType.INCOME.getLabel(), CategoryType.INCOME.getPrefix()),
								Tuple.tuple(CategoryType.OUTLAY.getLabel(), CategoryType.OUTLAY.getPrefix())
						);

				//then: 현재 날짜가 저장된다.
				assertThat(result.getDisplayDate()).isEqualTo("2026년 01월 15일 목요일");

				assertThat(result)
						.extracting(
								LedgerWriteStep1Response::getCurrentYear,
								LedgerWriteStep1Response::getCurrentMonth,
								LedgerWriteStep1Response::getCurrentDay
						)
						.containsExactly(
							2026, 1, 15
						);

				//then: 날짜 리스트가 저장된다.
				assertThat(result.getYears()).hasSize(6);
				assertThat(result.getMonths()).hasSize(1);
				assertThat(result.getDays()).hasSize(15);
			}
			
		}

	}


	@Nested
	@DisplayName("작성 2단계 데이터 얻기")
	class GetStep2DataTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			private final LocalDate date = LocalDate.now(clock);

			@BeforeEach
			void setUp() {
				when(imageReadService.resolveImageSlots())
						.thenReturn(List.of(
								ImageSlot.ofEmptySlot(),
								ImageSlot.ofLockedSlot(),
								ImageSlot.ofLockedSlot()
						));
			}

			@Test
			@DisplayName("수입 유형이면 정상적인 데이터가 반환된다.")
			void returnsData_whenIncomeTypeIsGiven() {
				//given: 수입 유형과 오늘 날짜가 주어진다.
				CategoryType type = CategoryType.INCOME;

				when(categoryReadService.getMiddleCategories(type))
						.thenReturn(List.of(
								CategoryItem.from(CategoryFixture.top())
						));
				
				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
				LedgerWriteStep2Response result = target.getWriteStep2Data(type, date);
				
				//then: 응답 데이터가 반환된다.
				assertThat(result).isNotNull();

				assertThat(result.getTitle()).isEqualTo("2026년 01월 15일 목요일");

				assertThat(result.getType()).isEqualTo(type);
				assertThat(result.getImageSlot()).hasSize(3);
				assertThat(result.getCategories()).hasSize(1);

				assertThat(result.getFixed())
						.hasSize(2)
						.containsExactly(FixedYN.REPEAT, FixedYN.VARIABLE);

				assertThat(result.getPaymentTypes())
						.hasSize(4)
						.containsExactly(
								PaymentType.NONE, PaymentType.CASH, PaymentType.CARD, PaymentType.BANK
						);

				//then: 카테고리와 이미지 서비스가 요청된다.
				verify(categoryReadService).getMiddleCategories(type);
				verify(imageReadService).resolveImageSlots();
			}
			
			@Test
			@DisplayName("지출 유형이면 정상적인 데이터가 반환된다.")
			void returnsData_whenExpenseTypeIsGiven() {
				//given: 지출 유형과 오늘 날짜가 주어진다.
				clock.set(LocalDate.of(2026, 1, 20));

				CategoryType type = CategoryType.OUTLAY;
				LocalDate date = LocalDate.now(clock);

				when(categoryReadService.getMiddleCategories(type))
						.thenReturn(List.of(
								CategoryItem.from(CategoryFixture.top())
						));

				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
				LedgerWriteStep2Response result = target.getWriteStep2Data(type, date);

				//then: 응답 데이터가 반환된다.
				assertThat(result).isNotNull();

				assertThat(result.getType()).isEqualTo(type);

				//then: 카테고리와 이미지 서비스가 요청된다.
				verify(categoryReadService).getMiddleCategories(type);
				verify(imageReadService).resolveImageSlots();
			}
			
			@Test
			@DisplayName("카테고리가 없으면 응답 데이터에 빈 리스트가 포함된다.")
			void returnsEmptyList_whenCategoryDoesNotExist() {
				//given: 카테고리 목록이 빈 리스트가 반환되도록 CategoryReadService 동작이 정의되어 있다.
				CategoryType type = CategoryType.INCOME;

				when(categoryReadService.getMiddleCategories((type)))
						.thenReturn(Collections.emptyList());

				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
				LedgerWriteStep2Response result = target.getWriteStep2Data(type, date);
				
				//then: 카테고리 리스트가 비어있다.
				assertThat(result.getCategories()).isEmpty();
			}
			
			@Test
			@DisplayName("이미지 슬롯이 없으면 응답 데이터에 빈 리스트가 포함된다.")
			void returnsEmptyList_whenImageSlotDoesNotExist() {
				//given: 이미지 슬롯이 빈 리스트가 반환되도록 LedgerImageReadService 동작이 정의되어 있다.
				CategoryType type = CategoryType.OUTLAY;

				when(imageReadService.resolveImageSlots())
						.thenReturn(Collections.emptyList());

				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
				LedgerWriteStep2Response result = target.getWriteStep2Data(type, date);

				//then: 이미지 슬롯 리스트가 비어있다.
				assertThat(result.getImageSlot()).isEmpty();
			}
			
			@Test
			@DisplayName("카테고리와 이미지 정보 둘 다 없으면 응답 데이터에 빈 리스트가 포함된다.")
			void returnsEmptyLists_whenCategoryAndImageDoNotExist() {
				//given: 카테고리 리스트와 이미지 슬롯이 빈 리스트가 반환되도록 동작이 정의되어 있다.
				CategoryType type = CategoryType.OUTLAY;

				when(categoryReadService.getMiddleCategories(type))
						.thenReturn(Collections.emptyList());

				when(imageReadService.resolveImageSlots())
						.thenReturn(Collections.emptyList());

				//when: 가계부 작성 2단계에 필요한 데이터를 요청한다.
				LedgerWriteStep2Response result = target.getWriteStep2Data(type, date);

				//then: 이미지 슬롯 리스트가 비어있다.
				assertThat(result.getCategories()).isEmpty();
				assertThat(result.getImageSlot()).isEmpty();
			}

		}

	}


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

}