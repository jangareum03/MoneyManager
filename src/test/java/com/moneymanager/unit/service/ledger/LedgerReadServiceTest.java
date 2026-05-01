package com.moneymanager.unit.service.ledger;

import com.moneymanager.BusinessExceptionAssert;
import com.moneymanager.domain.global.Policy;
import com.moneymanager.domain.global.enums.DatePatterns;
import com.moneymanager.domain.global.vo.DateRange;
import com.moneymanager.domain.ledger.dto.query.LedgerHistoryQuery;
import com.moneymanager.domain.ledger.dto.response.*;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.domain.ledger.enums.AmountType;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.domain.ledger.enums.HistoryType;
import com.moneymanager.domain.ledger.policy.LedgerHistoryPolicy;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ServiceAction;
import com.moneymanager.mapper.LedgerMapper;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.security.support.WithMockCustomUser;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.ledger.CategoryReadService;
import com.moneymanager.service.ledger.LedgerImageReadService;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.service.member.MemberReadService;
import com.moneymanager.fixture.category.CategoryTreeFixture;
import com.moneymanager.fixture.ledger.LedgerFixture;
import com.moneymanager.fixture.ledger.LedgerImageFixture;
import com.moneymanager.utils.date.DateTimeUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.moneymanager.domain.ledger.enums.CategoryType.INCOME;
import static com.moneymanager.domain.ledger.enums.CategoryType.OUTLAY;
import static com.moneymanager.exception.error.ErrorCode.*;
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

	private LedgerReadService target;

	@Mock	private SecurityUtil securityUtil;
	@Mock	private CategoryReadService categoryReadService;
	@Mock	private LedgerImageReadService imageReadService;
	@Mock	private LedgerRepository ledgerRepository;
	@Mock	private LedgerHistoryPolicy ledgerHistoryPolicy;
	@Mock	private LedgerMapper ledgerMapper;
	@Mock	private Clock clock;


	@BeforeEach
	void setUp() {
		LocalDate fixedDate = LocalDate.of(2025, 5, 5);

		clock = Clock.fixed(
				fixedDate.atStartOfDay().atZone(ZoneId.of("Asia/Seoul")).toInstant(),
				ZoneId.of("Asia/Seoul")
		);

		target = new LedgerReadService(
			securityUtil,
			categoryReadService,
			imageReadService,
			ledgerRepository,
			ledgerHistoryPolicy,
			ledgerMapper,
			clock
		);

		when(securityUtil.getMemberId()).thenReturn("UCt01001");
	}


	@Nested
	@DisplayName("작성 1단계 요청 데이터 생성")
	class Step1DataCreateTest {

		@Test
		@DisplayName("연도 리스트는 정책상 허용하는 과거연도부터 현재연도까지 가진다.")
		void returnsYearList_fromPastYearToCurrentYear() {
			//when
			LedgerWriteStep1Response result = target.getWriteStep1Data();

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
			LedgerWriteStep1Response result = target.getWriteStep1Data();

			//then
			assertThat(result.getMonths()).containsExactly(1, 2, 3, 4, 5);
		}

		@Test
		@DisplayName("일 리스트는 1일부터 최대일까지 가진다.")
		void returnsDayList_fromFirstDayToLastDay() {
			//when
			LedgerWriteStep1Response result = target.getWriteStep1Data();

			//then
			int expectedLastDay = LocalDate.now(clock).getDayOfMonth();

			assertThat(result.getDays().get(0)).isEqualTo(1);
			assertThat(result.getDays().get(result.getDays().size() - 1)).isEqualTo(expectedLastDay);
		}

		@Test
		@DisplayName("현재 연월일은 오늘 날짜와 일치한다.")
		void returnsCurrentDate_whenTodayBased() {
			//when
			LedgerWriteStep1Response result = target.getWriteStep1Data();

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
			LedgerWriteStep1Response result = target.getWriteStep1Data();

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
			LedgerWriteStep1Response result = target.getWriteStep1Data();

			//then
			assertThat(result.getTypes()).isNotNull();
			assertThat(result.getTypes()).isNotEmpty();
			assertThat(result.getTypes()).hasSize(2);
		}

	}


	@Nested
	@DisplayName("작성 2단계 요청 데이터 생성")
	class Step2DataCreateTest {

		@Mock
		private MemberReadService memberReadService;

		@Test
		@DisplayName("수입 유형이면 수입용 작성 2단계 응답을 반환한다.")
		void createsIncomeResponse_whenCategoryTypeIsIncome() {
			//given
			CategoryType type = INCOME;
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
			LedgerWriteStep2Response result = target.getWriteStep2Data(type, date);

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
			when(memberReadService.getImageLimit("test")).thenReturn(1);

			//when
			LedgerWriteStep2Response result = target.getWriteStep2Data(type, date);

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
			CategoryType type = INCOME;
			LocalDate date = LocalDate.of(2026, 3, 7);

			List<CategoryResponse> categories = Collections.emptyList();

			when(categoryReadService.getCategoriesByTypeAndLevel(type, CategoryLevel.MIDDLE))
					.thenReturn(categories);
			when(memberReadService.getImageLimit("test")).thenReturn(1);

			//when
			LedgerWriteStep2Response result = target.getWriteStep2Data(type, date);

			//then
			assertThat(result.getCategories()).isEmpty();
		}

		@Test
		@DisplayName("중간 레벨 카테고리를 조회한다.")
		void getsMiddleLevelCategories() {
			//given
			CategoryType type = INCOME;
			LocalDate date = LocalDate.of(2026, 1, 1);

			//when
			target.getWriteStep2Data(type, date);

			//then
			verify(categoryReadService).getCategoriesByTypeAndLevel(eq(type), eq(CategoryLevel.MIDDLE));
		}

		@Test
		@DisplayName("화면 표시용 제목은 지정된 포맷으로 날짜를 변환한다.")
		void createsTitle_whenDateIsGiven() {
			//given
			LocalDate date = LocalDate.of(2026,1, 1);

			List<CategoryResponse> categories = List.of(
					CategoryResponse.builder().name("수입1").code("010101").build()
			);

			//when
			LedgerWriteStep2Response result = target.getWriteStep2Data(INCOME, date);

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

				LocalDate date = LocalDate.of(2026, 1, 1);
				DateRange dateRange = new DateRange(date, date.with(TemporalAdjusters.lastDayOfMonth()));

				List<LedgerHistoryQuery> histories = List.of(
						createHistory(null, INCOME, 1000),
						createHistory(date, OUTLAY, 2000)
				);

				when(securityUtil.getMemberId()).thenReturn("member");
				when(ledgerHistoryPolicy.calculateDateRange(eq(type), any(LocalDate.class))).thenReturn(dateRange);
				when(ledgerRepository.findHistoriesByMemberAndDateBetween(eq("member"), any(), any())).thenReturn(histories);
				when(ledgerHistoryPolicy.getTitleByHistoryType(type)).thenReturn("2026년 01월");

				//when
				HistoryDashboardResponse result = target.getHistoryDashboard(type);

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

				LocalDate date = LocalDate.of(2026, 1, 1);
				DateRange dateRange = new DateRange(date, date.plusDays(7));

				List<LedgerHistoryQuery> histories = List.of(
						createHistory(date, INCOME, 1000),
						createHistory(date, OUTLAY, 2000),
						createHistory(date.plusDays(1), OUTLAY, 2500)
				);

				when(securityUtil.getMemberId()).thenReturn("member");
				when(ledgerHistoryPolicy.calculateDateRange(eq(type), any(LocalDate.class))).thenReturn(dateRange);
				when(ledgerRepository.findHistoriesByMemberAndDateBetween(eq("member"), any(), any())).thenReturn(histories);
				when(ledgerHistoryPolicy.getTitleByHistoryType(type)).thenReturn("2026년 01월 1주");

				//when
				Map<String, List<HistoryItem>> result = target.getHistoryDashboard(type).getHistoryGroups();

				//then
				String dateKey = DateTimeUtils.formatDate(date, DATE_FORMAT);
				String nextDateKey = DateTimeUtils.formatDate(date.plusDays(1), DATE_FORMAT);

				assertThat(result).hasSize(2);

				assertThat(result.get(dateKey)).hasSize(2);
				assertThat(result.get(nextDateKey)).hasSize(1);

				verifyHistoryDashboard(type, dateRange);
			}

			@Test
			@DisplayName("내역은 거래일자 최신순부터 정렬된다.")
			void ordersHistoriesByByDateDesc() {
				//given
				HistoryType type = HistoryType.YEAR;

				LocalDate date = LocalDate.of(2026, 1, 1);
				DateRange dateRange = new DateRange(date, date.with(TemporalAdjusters.lastDayOfYear()));

				List<LedgerHistoryQuery> histories = List.of(
						createHistory(date, INCOME, 1000),
						createHistory(date, OUTLAY, 2000),
						createHistory(date.plusDays(1), OUTLAY, 2500)
				);

				when(securityUtil.getMemberId()).thenReturn("member");
				when(ledgerHistoryPolicy.calculateDateRange(eq(type), any(LocalDate.class))).thenReturn(dateRange);
				when(ledgerRepository.findHistoriesByMemberAndDateBetween(eq("member"), any(), any())).thenReturn(histories);
				when(ledgerHistoryPolicy.getTitleByHistoryType(type)).thenReturn("2026년");

				//when
				Map<String, List<HistoryItem>> groups = target.getHistoryDashboard(type).getHistoryGroups();
				List<String> result = new LinkedList<>(groups.keySet());

				//then
				assertThat(result).containsExactly(
						DateTimeUtils.formatDate(date.plusDays(1), DATE_FORMAT),
						DateTimeUtils.formatDate(date, DATE_FORMAT)
				);

				verifyHistoryDashboard(type, dateRange);
			}

			private void verifyHistoryDashboard(HistoryType type, DateRange dateRange) {
				verify(securityUtil).getMemberId();
				verify(ledgerHistoryPolicy).calculateDateRange(eq(type), any(LocalDate.class));
				verify(ledgerHistoryPolicy).validate(dateRange);
				verify(ledgerRepository).findHistoriesByMemberAndDateBetween(MEMBER_ID, dateRange.getFrom(), dateRange.getTo());
			}

		}


		@Nested
		@DisplayName("카테고리별 금액 계산")
		class AmountCalculate {

			@Test
			@DisplayName("수입과 지출별 금액 합계를 계산한다.")
			void calculatesTotal_whenDatesExist() {
				//given
				HistoryType type = HistoryType.YEAR;

				LocalDate date = LocalDate.of(2026, 1, 1);
				DateRange dateRange = new DateRange(date, date.with(TemporalAdjusters.lastDayOfYear()));

				List<LedgerHistoryQuery> histories = List.of(
						createHistory(date, INCOME, 1000),
						createHistory(date, OUTLAY, 2000),
						createHistory(date.plusDays(1), OUTLAY, 2500)
				);

				when(securityUtil.getMemberId()).thenReturn("member");
				when(ledgerHistoryPolicy.calculateDateRange(eq(type), any(LocalDate.class))).thenReturn(dateRange);
				when(ledgerRepository.findHistoriesByMemberAndDateBetween(eq("member"), any(), any())).thenReturn(histories);
				when(ledgerHistoryPolicy.getTitleByHistoryType(type)).thenReturn("2026년");

				//when
				LedgerStatistics result = target.getHistoryDashboard(type).getStatistics();

				//then
				assertThat(result).isNotNull();

				assertThat(result.getTotal()).isEqualTo(5500);
				assertThat(result.getIncome()).isEqualTo(1000);
				assertThat(result.getOutlay()).isEqualTo(4500);
			}

			@Test
			@DisplayName("수입 또는 지출이 내역에 없으면 합계는 0으로 계산된다.")
			void calculatesTotal_whenIncomeOrOutlayDoesNotExist() {
				//given
				HistoryType type = HistoryType.YEAR;

				LocalDate date = LocalDate.of(2026, 1, 1);
				DateRange dateRange = new DateRange(date, date.with(TemporalAdjusters.lastDayOfYear()));

				List<LedgerHistoryQuery> histories = List.of(
						createHistory(date, OUTLAY, 2000),
						createHistory(date.plusDays(1), OUTLAY, 2500)
				);

				when(securityUtil.getMemberId()).thenReturn("member");
				when(ledgerHistoryPolicy.calculateDateRange(eq(type), any(LocalDate.class))).thenReturn(dateRange);
				when(ledgerRepository.findHistoriesByMemberAndDateBetween(eq("member"), any(), any())).thenReturn(histories);
				when(ledgerHistoryPolicy.getTitleByHistoryType(type)).thenReturn("2026년");

				//when
				LedgerStatistics result = target.getHistoryDashboard(type).getStatistics();

				//then
				assertThat(result).isNotNull();

				assertThat(result.getTotal()).isEqualTo(4500);
				assertThat(result.getIncome()).isEqualTo(0);
				assertThat(result.getOutlay()).isEqualTo(4500);
			}

		}

		@Nested
		@DisplayName("메뉴 생성")
		class MenuCreate {

			@Test
			@DisplayName("가계부 내역을 조회할 수 있는 메뉴를 생성한다.")
			void returnsMenu_whenLedgerHistories() {
				//given
				HistoryType type = HistoryType.YEAR;

				LocalDate date = LocalDate.of(2026, 1, 1);
				DateRange dateRange = new DateRange(date, date.with(TemporalAdjusters.lastDayOfYear()));

				List<LedgerHistoryQuery> histories = List.of(
						createHistory(date, OUTLAY, 2000),
						createHistory(date.plusDays(1), OUTLAY, 2500)
				);

				when(securityUtil.getMemberId()).thenReturn("member");
				when(ledgerHistoryPolicy.calculateDateRange(eq(type), any(LocalDate.class))).thenReturn(dateRange);
				when(ledgerRepository.findHistoriesByMemberAndDateBetween(eq("member"), any(), any())).thenReturn(histories);
				when(ledgerHistoryPolicy.getTitleByHistoryType(type)).thenReturn("2026년");

				//when
				List<MenuItem> result = target.getHistoryDashboard(type).getMenu();

				//then
				assertThat(result).hasSize(5);

				assertThat(result.get(0).getLabel()).isEqualTo("전체");
				assertThat(result.get(1).getLabel()).isEqualTo("수입/지출");
				assertThat(result.get(2).getLabel()).isEqualTo("카테고리");
				assertThat(result.get(3).getLabel()).isEqualTo("메모");
				assertThat(result.get(4).getLabel()).isEqualTo("기간");
			}

		}

		private LedgerHistoryQuery createHistory(LocalDate date, CategoryType type, int amount) {
			Ledger ledger = createLedger(date, amount);
			Category category = createCategory(type);

			return new LedgerHistoryQuery(ledger.getCode(), ledger.getDate(), ledger.getAmount(), ledger.getMemo(), category.getName(), category.getCode());
		}

		private Ledger createLedger(LocalDate date, int amount) {
			return LedgerFixture.defaultLedger()
					.date(date)
					.amount((long) amount)
					.build();
		}

		private Category createCategory(CategoryType type) {
			return CategoryTreeFixture.low().get(type).get(0);
		}

	}


	@WithMockCustomUser
	@Nested
	@DisplayName("가계부 상세 조회")
	class LedgerDetailTest {

		@BeforeEach
		void setUp() {
			when(securityUtil.getMemberId()).thenReturn("UCt01001");
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			LedgerDetailResponse.LedgerDetailResponseBuilder mockResponse = LedgerDetailResponse.builder()
					.date("제목")
					.amount(10000L)
					.paymentType(AmountType.NONE);

			@Test
			@DisplayName("이미지가 없는 가계부 정보가 조회된다.")
			void returnsResponse_whenLedgerHasNoImage() {
				//given
				Ledger incomeLedger = LedgerFixture.create();
				Category category = CategoryTreeFixture.incomeSalary();

				when(ledgerRepository.findByCode(any(), any())).thenReturn(incomeLedger);
				when(categoryReadService.getCategory(any())).thenReturn(category);
				when(imageReadService.getLedgerImages(any())).thenReturn(Collections.emptyList());

				when(ledgerMapper.toDetailDto(any(), any(), anyList()))
						.thenReturn(
								mockResponse.
										type(INCOME)
										.category(CategoryResponse.from(category))
										.images(Arrays.asList(null, null, null))
										.build()
						);

				//when
				LedgerDetailResponse result = target.getLedgerDetail(incomeLedger.getCode());

				//then
				assertThat(result).isNotNull();
				assertThat(result)
						.extracting(
								LedgerDetailResponse::getPlaceName,
								LedgerDetailResponse::getRoadAddress,
								LedgerDetailResponse::getDetailAddress
						).containsOnlyNulls();

				assertThat(result.getDate()).isEqualTo("제목");
				assertThat(result.getAmount()).isEqualTo(10000L);
				assertThat(result.getType()).isSameAs(INCOME);
				assertThat(result.getPaymentType()).isSameAs(AmountType.NONE);

				assertThat(result.getCategory())
						.hasFieldOrPropertyWithValue("name", "월급")
						.hasFieldOrPropertyWithValue("code", "010101");

				//이미지 검증
				assertThat(result.getImages()).hasSize(3)
						.containsOnlyNulls();
			}

			@Test
			@DisplayName("이미지가 1개 있는 가계부 정보를 조회한다.")
			void returnsResponse_whenLedgerHasImage() {
				//given
				Category category = CategoryTreeFixture.outlayFood();
				Ledger outlayLedger = LedgerFixture.defaultLedger().category(category.getCode()).build();

				List<LedgerImage> imageList = List.of(
						LedgerImageFixture.defaultImage(1L, outlayLedger.getId()).build()
				);

				when(ledgerRepository.findByCode(any(), any())).thenReturn(outlayLedger);
				when(categoryReadService.getCategory(any())).thenReturn(category);
				when(imageReadService.getLedgerImages(any())).thenReturn(imageList);

				when(ledgerMapper.toDetailDto(any(),any(),anyList()))
						.thenReturn(
								mockResponse
										.type(OUTLAY)
										.category(CategoryResponse.from(category))
										.images(Arrays.asList("test-mid/images/default.png", null, null))
										.build()
						);


				//when
				LedgerDetailResponse result = target.getLedgerDetail(outlayLedger.getCode());

				//then
				assertThat(result).isNotNull();

				List<String> images = result.getImages();
				assertThat(images).hasSize(3)
						.containsExactly("test-mid/images/default.png", null, null);
			}

			@Test
			@DisplayName("이미지가 여러개 있는 가계부 정보를 조회한다.")
			void returnsResponse_whenLedgerHasImages() {
				//given
				Category category = CategoryTreeFixture.outlayFood();
				Ledger outlayLedger = LedgerFixture.defaultLedger().category(category.getCode()).build();

				List<LedgerImage> imageList = List.of(
						LedgerImageFixture.defaultImage(1L, outlayLedger.getId()).build(),
						LedgerImageFixture.defaultImage(2L, outlayLedger.getId()).id(2L).sortOrder(2).imagePath("images/2.png").build()
				);

				when(ledgerRepository.findByCode(any(), any())).thenReturn(outlayLedger);
				when(categoryReadService.getCategory(any())).thenReturn(category);
				when(imageReadService.getLedgerImages(any())).thenReturn(imageList);

				when(ledgerMapper.toDetailDto(any(),any(),anyList()))
						.thenReturn(
								mockResponse
										.type(OUTLAY)
										.category(CategoryResponse.from(category))
										.images(Arrays.asList("test-mid/images/default.png", "test-mid/images/2.png", null))
										.build()
						);


				//when
				LedgerDetailResponse result = target.getLedgerDetail(outlayLedger.getCode());

				//then
				assertThat(result).isNotNull();

				List<String> images = result.getImages();
				assertThat(images).hasSize(3)
						.containsExactly("test-mid/images/default.png", "test-mid/images/2.png", null);
			}

		}


		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("인증되지 않은 회원이 요청하면 BusinessException이 발생한다.")
			void throwsException_whenUnauthorized() {
				//given
				when(securityUtil.getMemberId())
						.thenThrow(BusinessException.of(MEMBER_AUTHORITY_UNAUTHORIZED, "인증 예외"));

				//when
				Throwable throwable = catchThrowable(() -> target.getLedgerDetail("code"));

				//then
				BusinessExceptionAssert.assertThatBusinessException(throwable)
								.hasErrorCode(MEMBER_AUTHORITY_UNAUTHORIZED)
								.hasLogMessage("인증 예외")
								.hasServiceAction(ServiceAction.LEDGER_DETAIL);
			}

			@Test
			@DisplayName("없는 가계부를 조회하면 BusinessException이 발생한다.")
			void throwsException_whenLedgerDoesNotExist() {
				//given
				when(ledgerRepository.findByCode(any(), any())).thenThrow(EmptyResultDataAccessException.class);

				//when & then
				BusinessExceptionAssert.assertThatBusinessException(
						catchThrowable(() -> target.getLedgerDetail("code"))
				).hasErrorCode(LEDGER_TARGET_NOT_FOUND);

			}

			@Test
			@DisplayName("없는 카테고리를 조회하면 BusinessException이 발생한다.")
			void throwsException_whenCategoryDoesNotExist() {
				//given
				when(ledgerRepository.findByCode(any(), any())).thenReturn(LedgerFixture.create());
				when(categoryReadService.getCategory(any())).thenThrow(BusinessException.of(LEDGER_CATEGORY_TARGET_NOT_FOUND, "카테고리 없음"));

				//when & then
				BusinessExceptionAssert.assertThatBusinessException(
						catchThrowable(() -> target.getLedgerDetail("code"))
				).hasErrorCode(LEDGER_CATEGORY_TARGET_NOT_FOUND);
			}

			@Test
			@DisplayName("DB 문제로 데이터 조회 불가능하면 ")
			void throwsException_whenImageDbAccessFails() {
				//given
				Ledger incomeLedger = LedgerFixture.create();
				Category category = CategoryTreeFixture.incomeSalary();

				when(ledgerRepository.findByCode(any(), any())).thenReturn(incomeLedger);
				when(categoryReadService.getCategory(any())).thenReturn(category);

				when(imageReadService.getLedgerImages(any())).thenThrow(new DataAccessException("DB 예외") {});

				//when & then
				assertThatThrownBy(() -> target.getLedgerDetail("code"))
						.isInstanceOf(DataAccessException.class);
			}
		}

	}

}