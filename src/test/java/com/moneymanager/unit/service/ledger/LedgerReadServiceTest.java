package com.moneymanager.unit.service.ledger;

import com.moneymanager.domain.global.Policy;
import com.moneymanager.domain.global.enums.DatePatterns;
import com.moneymanager.domain.global.vo.DateRange;
import com.moneymanager.domain.ledger.dto.query.LedgerHistoryQuery;
import com.moneymanager.domain.ledger.dto.response.*;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.domain.ledger.enums.HistoryType;
import com.moneymanager.domain.ledger.policy.LedgerHistoryPolicy;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.ledger.CategoryReadService;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.service.member.MemberReadService;
import com.moneymanager.utils.date.DateTimeUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
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

	@Mock
	private SecurityUtil securityUtil;

	@Mock
	private LedgerHistoryPolicy ledgerHistoryPolicy;

	@Mock
	private CategoryReadService categoryReadService;
	@Mock
	private MemberReadService memberReadService;

	@Mock
	private LedgerRepository ledgerRepository;

	private LedgerReadService service;
	private Clock clock;


	@BeforeEach
	void setUp() {
		LocalDate fixedDate = LocalDate.of(2025, 5, 5);

		clock =Clock.fixed(
				fixedDate.atStartOfDay().atZone(ZoneId.of("Asia/Seoul")).toInstant(),
				ZoneId.of("Asia/Seoul")
		);

		service = new LedgerReadService(
				securityUtil,
				categoryReadService,
				memberReadService,
				ledgerRepository,
				ledgerHistoryPolicy,
				clock
		);
	}


	@Nested
	@DisplayName("작성 1단계 요청 데이터 생성")
	class Step1DataCreate {

		@Test
		@DisplayName("연도 리스트는 정책상 허용하는 과거연도부터 현재연도까지 가진다.")
		void returnsYearList_fromPastYearToCurrentYear() {
			//when
			LedgerWriteStep1Response result = service.getWriteStep1Data();

			//then
			int expectedStartYear = LocalDate.now(clock).minusYears(Policy.LEDGER_MAX_YEAR).getYear();
			int expectedEndYear = LocalDate.now(clock).getYear();

			assertThat(result.getYears()).isNotNull();
			assertThat(result.getYears().get(0)).isEqualTo(expectedStartYear);
			assertThat(result.getYears().get(result.getYears().size() - 1)).isEqualTo(expectedEndYear);
		}

		@Test
		@DisplayName("월 리스트는 1월부터 현재월까지 가진다.")
		void returnsMonthList_fromJanuaryToCurrentMonth() {
			//when
			LedgerWriteStep1Response result = service.getWriteStep1Data();

			//then
			assertThat(result.getMonths()).containsExactly(1, 2, 3, 4, 5);
		}

		@Test
		@DisplayName("일 리스트는 1일부터 최대일까지 가진다.")
		void returnsDayList_fromFirstDayToLastDay() {
			//when
			LedgerWriteStep1Response result = service.getWriteStep1Data();

			//then
			int expectedLastDay = LocalDate.now(clock).getDayOfMonth();

			assertThat(result.getDays().get(0)).isEqualTo(1);
			assertThat(result.getDays().get(result.getDays().size() - 1)).isEqualTo(expectedLastDay);
		}

		@Test
		@DisplayName("현재 연월일은 오늘 날짜와 일치한다.")
		void returnsCurrentDate_whenTodayBased() {
			//when
			LedgerWriteStep1Response result = service.getWriteStep1Data();

			//then
			LocalDate today = LocalDate.now(clock);

			assertThat(result.getCurrentYear()).isEqualTo(today.getYear());
			assertThat(result.getCurrentMonth()).isEqualTo(today.getMonthValue());
			assertThat(result.getCurrentDay()).isEqualTo(today.getDayOfMonth());
		}

		@Test
		@DisplayName("화면 표시용 제목은 지정된 포맷으로 날짜를 변환한다.")
		void returnsTitle_whenDateFormatGiven() {
			//when
			LedgerWriteStep1Response result = service.getWriteStep1Data();

			//then
			String expectedTitle = DateTimeUtils.formatDate(
					LocalDate.now(clock),
					DatePatterns.KOREAN_DATE_WITH_DAY.getPattern()
			);

			assertThat(result.getDisplayDate()).isEqualTo(expectedTitle);
		}

		@Test
		@DisplayName("가계부 타입 목록이 포함된다.")
		void returnsLedgerTypes() {
			//when
			LedgerWriteStep1Response result = service.getWriteStep1Data();

			//then
			assertThat(result.getTypes()).isNotNull();
			assertThat(result.getTypes()).isNotEmpty();
			assertThat(result.getTypes()).hasSize(2);
		}

	}


	@Nested
	@DisplayName("작성 2단계 요청 데이터 생성")
	class Step2DataCreate {

		@Test
		@DisplayName("수입 유형이면 수입용 작성 2단계 응답을 반환한다.")
		void createsIncomeResponse_whenCategoryTypeIsIncome() {
			//given
			CategoryType type = CategoryType.INCOME;
			LocalDate date = LocalDate.of(2026, 3, 7);

			List<CategoryResponse> categories = List.of(
					CategoryResponse.builder().name("수입1").code("010101").build(),
					CategoryResponse.builder().name("수입2").code("010102").build()
			);
			List<Boolean> images = List.of(true, false, false);

			when(categoryReadService.getCategoriesByTypeAndLevel(type, CategoryLevel.MIDDLE))
					.thenReturn(categories);

			when(securityUtil.getMemberId()).thenReturn("test");
			when(memberReadService.getImageLimit("test")).thenReturn(1);

			//when
			LedgerWriteStep2Response result = service.getWriteStep2Data(type, date);

			//then
			String expectedTitle = DateTimeUtils.formatDate(date, DatePatterns.KOREAN_DATE_WITH_DAY.getPattern());

			assertThat(result).isNotNull();
			assertThat(result.getTitle()).isEqualTo(expectedTitle);
			assertThat(result.getCategories()).isEqualTo(categories);
			assertThat(result.getImageSlot()).isEqualTo(images);

			verify(categoryReadService).getCategoriesByTypeAndLevel(eq(type), eq(CategoryLevel.MIDDLE));
			verify(memberReadService).getImageLimit("test");
		}

		@Test
		@DisplayName("지출 유형이면 지출용 작성 2단계 응답을 반환한다.")
		void createsOutlayResponse_whenCategoryTypeIsOutlay() {
			//given
			CategoryType type = CategoryType.OUTLAY;
			LocalDate date = LocalDate.of(2026, 3, 7);

			List<CategoryResponse> categories = List.of(
					CategoryResponse.builder().name("지출1").code("020101").build(),
					CategoryResponse.builder().name("지출2").code("020102").build()
			);
			List<Boolean> images = List.of(true, false, false);

			when(categoryReadService.getCategoriesByTypeAndLevel(type, CategoryLevel.MIDDLE))
					.thenReturn(categories);

			when(securityUtil.getMemberId()).thenReturn("test");
			when(memberReadService.getImageLimit("test")).thenReturn(1);

			//when
			LedgerWriteStep2Response result = service.getWriteStep2Data(type, date);

			//then
			String expectedTitle = DateTimeUtils.formatDate(date, DatePatterns.KOREAN_DATE_WITH_DAY.getPattern());

			assertThat(result).isNotNull();
			assertThat(result.getTitle()).isEqualTo(expectedTitle);
			assertThat(result.getCategories()).isEqualTo(categories);
			assertThat(result.getImageSlot()).isEqualTo(images);
		}

		@Test
		@DisplayName("카테고리 목록이 없어도 응답을 반환한다.")
		void createsResponse_whenCategoriesDoesNotExist() {
			//given
			CategoryType type = CategoryType.INCOME;
			LocalDate date = LocalDate.of(2026, 3, 7);

			List<CategoryResponse> categories = Collections.emptyList();

			when(categoryReadService.getCategoriesByTypeAndLevel(type, CategoryLevel.MIDDLE))
					.thenReturn(categories);

			when(securityUtil.getMemberId()).thenReturn("test");
			when(memberReadService.getImageLimit("test")).thenReturn(1);

			//when
			LedgerWriteStep2Response result = service.getWriteStep2Data(type, date);

			//then
			assertThat(result.getCategories()).isEmpty();
		}

		@Test
		@DisplayName("중간 레벨 카테고리를 조회한다.")
		void getsMiddleLevelCategories() {
			//given
			CategoryType type = CategoryType.INCOME;
			LocalDate date = LocalDate.of(2026, 1, 1);

			//when
			service.getWriteStep2Data(type, date);

			//then
			verify(categoryReadService).getCategoriesByTypeAndLevel(eq(type), eq(CategoryLevel.MIDDLE));
		}

		@Test
		@DisplayName("화면 표시용 제목은 지정된 포맷으로 날짜를 변환한다.")
		void createsTitle_whenDateIsGiven() {
			//given
			CategoryType type = CategoryType.INCOME;
			LocalDate date = LocalDate.of(2026,1, 1);

			List<CategoryResponse> categories = List.of(
					CategoryResponse.builder().name("수입1").code("010101").build()
			);

			//when
			LedgerWriteStep2Response result = service.getWriteStep2Data(type, date);

			//then
			String expectedTitle = DateTimeUtils.formatDate(date, DatePatterns.KOREAN_DATE_WITH_DAY.getPattern());
			assertThat(result.getTitle()).isEqualTo(expectedTitle);
		}

	}


	@Nested
	@DisplayName("가계부 내역 생성")
	class HistoryCreateTest {

		private final static String TITLE = "제목";
		private final static String MEMBER_ID = "member";
		private final static String DATE_FORMAT = DatePatterns.DATE_DOT_WITH_DAY.getPattern();
		private final static String CATEGORY = "010101";

		@Nested
		@DisplayName("날짜별로 내역 그룹화")
		class GroupByDate {

			@Test
			@DisplayName("날짜가 null인 내역은 제외된다.")
			void groupsHistoriesExcludingNullDate_whenDateIsNull() {
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
				assertThat(groups).containsKey(DateTimeUtils.formatDate(date, DATE_FORMAT));
				assertThat(groups).doesNotContainKey(null);

				verifyHistoryDashboard(type, dateRange);
			}


			@Test
			@DisplayName("같은 날짜의 내역은 하나의 그룹으로 묶인다.")
			void groupsHistories_whenDatesAreSame() {
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

				verifyHistoryDashboard(type, dateRange);
			}


			@Test
			@DisplayName("내역은 거래일자 최신순부터 정렬된다.")
			void ordersHistoriesByByDateDesc() {
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

				verifyHistoryDashboard(type, dateRange);
			}

			private void verifyHistoryDashboard(HistoryType type, DateRange dateRange) {
				verify(securityUtil).getMemberId();
				verify(ledgerHistoryPolicy).calculateDateRange(eq(type), any(LocalDate.class));
				verify(ledgerHistoryPolicy).validate(dateRange);
				verify(ledgerRepository).findHistoriesByMemberAndDateBetween(MEMBER_ID, dateRange.getFrom(), dateRange.getTo());
			}
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

	}


	@Nested
	@DisplayName("이미지 슬롯 생성")
	class ImageSlotCreate {

		@Test
		@DisplayName("기본적으로 1개의 슬롯만 true다.")
		void returnsBooleanList_whenDefaultMember() {
			//given
			when(securityUtil.getMemberId()).thenReturn("member");
			when(memberReadService.getImageLimit("member")).thenReturn(1);

			//when
			List<Boolean> result = service.fetchBooleanList();

			//then
			assertThat(result).hasSize(3);
			assertThat(result).containsExactly(true, false, false);
		}

		@Test
		@DisplayName("2개의 슬롯만 true이 가능하다.")
		void returnsBooleanList_whenTwoSlots() {
			//given
			when(securityUtil.getMemberId()).thenReturn("member");
			when(memberReadService.getImageLimit("member")).thenReturn(2);

			//when
			List<Boolean> result = service.fetchBooleanList();

			//then
			assertThat(result).hasSize(3);
			assertThat(result).containsExactly(true, true, false);
		}

		@Test
		@DisplayName("최대 슬롯인 3개까지 true가 가능하다.")
		void returnsBooleanList_whenMaxSlots() {
			//given
			when(securityUtil.getMemberId()).thenReturn("member");
			when(memberReadService.getImageLimit("member")).thenReturn(3);

			//when
			List<Boolean> result = service.fetchBooleanList();

			//then
			assertThat(result).hasSize(3);
			assertThat(result).containsExactly(true, true, true);
		}

		@Test
		@DisplayName("회원의 허용범위가 3개보다 커도 최대 슬롯인 3개까지 true가 가능하다.")
		void returnsBooleanList_whenSlotOutOfRange() {
			//given
			when(securityUtil.getMemberId()).thenReturn("member");
			when(memberReadService.getImageLimit("member")).thenReturn(5);

			//when
			List<Boolean> result = service.fetchBooleanList();

			//then
			assertThat(result).hasSize(3);
			assertThat(result).containsExactly(true, true, true);
		}

	}

}
