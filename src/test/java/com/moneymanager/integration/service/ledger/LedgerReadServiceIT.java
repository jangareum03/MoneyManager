package com.moneymanager.integration.service.ledger;

import com.moneymanager.domain.global.Policy;
import com.moneymanager.domain.global.enums.DatePatterns;
import com.moneymanager.domain.ledger.dto.response.*;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.domain.ledger.enums.HistoryType;
import com.moneymanager.domain.member.Member;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.repository.member.MemberRepository;
import com.moneymanager.security.support.WithMockCustomUser;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.fixture.ledger.LedgerFixture;
import com.moneymanager.fixture.MemberFixture;
import com.moneymanager.utils.date.DateTimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class LedgerReadServiceIT {

	@Autowired
	private LedgerReadService target;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private LedgerRepository ledgerRepository;

	private static final LocalDate TEST_DATE = LocalDate.of(2026, 3, 10);
	private Member member;

	@TestConfiguration
	static class TestConfig {
		@Bean
		public Clock clock() {
			LocalDate fixedDate = LocalDate.of(2026, 3, 20);

			return Clock.fixed(
					fixedDate.atStartOfDay().atZone(ZoneId.of("Asia/Seoul")).toInstant(),
					ZoneId.of("Asia/Seoul")
			);
		}
	}


	@BeforeEach
	void setUp() {
		memberRepository.deleteAll();;

		member = MemberFixture.create();
		memberRepository.save(member);
	}


	@Test
	@DisplayName("작성 1단계 요청 데이터가 정상 생성된다.")
	void createsWriteStep1Data_successfully() {
		//when
		LedgerWriteStep1Response result = target.getWriteStep1Data();

		//then
		assertThat(result).isNotNull();
		assertThat(result.getTypes()).isNotNull().isNotEmpty();
		assertThat(result.getYears()).isNotNull().isNotEmpty();
		assertThat(result.getMonths()).isNotNull().isNotEmpty();
		assertThat(result.getDays()).isNotNull().isNotEmpty();
		assertThat(result.getCurrentYear()).isNotZero();
		assertThat(result.getCurrentMonth()).isNotZero();
		assertThat(result.getCurrentDay()).isNotZero();
		assertThat(result.getDisplayDate()).isNotBlank();
	}


	@WithMockCustomUser
	@Nested
	@DisplayName("작성 2단계 응답 데이터")
	class Step2ResponseTest {

		@Test
		@DisplayName("수입유형 요청 시 수입 작성 2단계 응답을 반환한다.")
		void returnsIncomeResponse_whenCategoryTypeIsIncome() {
			//given
			CategoryType type = CategoryType.INCOME;

			//when
			LedgerWriteStep2Response result = target.getWriteStep2Data(type, TEST_DATE);

			//then
			assertThat(result).isNotNull();

			assertThat(result.getCategories()).hasSize(3);
			assertThat(result.getCategories())
					.allMatch(category -> category.getCode().startsWith("01"))
					.allMatch(category -> category.getCode().endsWith("00"));

			assertThat(result.getImageSlot()).hasSize(Policy.LEDGER_MAX_IMAGE);

			assertThat(result.getTitle()).isEqualTo("2026년 03월 10일 화요일");
		}

		@Test
		@DisplayName("지출유형 요청 시 지출 작성 2단계 응답을 반환한다.")
		void returnsOutlayResponse_whenCategoryTypeIsOutlay() {
			//given
			CategoryType type = CategoryType.OUTLAY;

			//when
			LedgerWriteStep2Response result = target.getWriteStep2Data(type, TEST_DATE);

			//then
			assertThat(result).isNotNull();

			assertThat(result.getCategories()).hasSize(9);
			assertThat(result.getCategories())
					.allMatch(category -> category.getCode().startsWith("02"))
					.allMatch(category -> category.getCode().endsWith("00"));

			assertThat(result.getImageSlot()).hasSize(Policy.LEDGER_MAX_IMAGE);

			assertThat(result.getTitle()).isEqualTo("2026년 03월 10일 화요일");
		}

		@ParameterizedTest
		@EnumSource(CategoryType.class)
		@DisplayName("중분류 카테고리만 조회되어 응답에 포함한다.")
		void returnsStep2Response_onlyMiddleLevelCategories(CategoryType type) {
			//when
			LedgerWriteStep2Response result = target.getWriteStep2Data(type, TEST_DATE);

			//then
			List<CategoryResponse> categories = result.getCategories();

			assertThat(categories)
					.allMatch(c -> !c.getCode().startsWith("00", 2));
		}

	}


	@WithMockCustomUser
	@Nested
	@DisplayName("거래내역 대시보드")
	class HistoryDashboardTest {

		@Test
		@DisplayName("정상적으로 거래내역 응답을 반환한다.")
		void returnsHistoryDashboard() {
			//given
			HistoryType type = HistoryType.MONTH;

			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 1), "010101", 1000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 1), "020101", 2000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 5), "020201", 3000));

			//when
			HistoryDashboardResponse result = target.getHistoryDashboard(type);

			//then
			assertThat(result).isNotNull();

			assertThat(result.getTitle()).isEqualTo("2026년 03월");
			assertThat(result.getMenu()).hasSize(5);

			//그룹 검증
			assertThat(result.getHistoryGroups()).hasSize(2);
			assertThat(result.getHistoryGroups()).containsKeys(getKey(2026,3,1), getKey(2026,3,5));
			assertThat(result.getHistoryGroups().get(getKey(2026, 3, 1))).hasSize(2);
			assertThat(result.getHistoryGroups().get(getKey(2026, 3, 5))).hasSize(1);

			//금액 검증
			assertThat(result.getStatistics())
					.extracting("total", "income", "outlay")
					.containsExactly(6000L, 1000L, 5000L);
		}

		@Test
		@DisplayName("내역 조회가 없어도 정상적으로 거래내역 응답을 반환한다.")
		void returnsHistoryDashboard_whenHistoryDoesNotExist() {
			//given
			HistoryType type = HistoryType.MONTH;

			//when
			HistoryDashboardResponse result = target.getHistoryDashboard(type);

			//then
			assertThat(result).isNotNull();
			assertThat(result.getMenu()).isNotEmpty();

			//그룹 검증
			assertThat(result.getHistoryGroups()).isEmpty();

			//금액 검증
			assertThat(result.getStatistics())
					.extracting(
							LedgerStatistics::getTotal, LedgerStatistics::getIncome, LedgerStatistics::getOutlay
					).containsOnly(0L);
		}

		@Test
		@DisplayName("특정 기간 안의 내역만 거래내역에 포함된다.")
		void returnsHistoryDashboard_whenDateWithinPeriod() {
			//given
			HistoryType type = HistoryType.MONTH;

			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 2, 28), "010102", 2000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 1), "010101", 1000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 1), "020101", 2000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 5), "020201", 3000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 9), "020501", 4500));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 4, 1), "020302", 500));

			//when
			HistoryDashboardResponse result = target.getHistoryDashboard(type);

			//then
			assertThat(result).isNotNull();

			assertThat(result.getHistoryGroups()).hasSize(3);
			assertThat(result.getHistoryGroups())
					.containsKeys(getKey(2026, 3, 1), getKey(2026, 3, 5), getKey(2026, 3, 9));
			assertThat(result.getHistoryGroups())
					.doesNotContainKeys(getKey(2026, 2, 28), getKey(2026, 4, 1));
		}

		@Test
		@DisplayName("날짜별로 내역을 그룹화되어 거래내역에 포함된다.")
		void returnsHistoryDashboard_whenGropingHistoriesSameDate() {
			//given
			HistoryType type = HistoryType.MONTH;

			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 1), "010101", 1000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 5), "020101", 2000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 5), "020201", 3000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 5), "020201", 1000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 12), "020302", 1500));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 12), "020102", 2000));

			//when
			HistoryDashboardResponse result = target.getHistoryDashboard(type);

			//then
			assertThat(result.getHistoryGroups()).hasSize(3);

			assertThat(result.getHistoryGroups().get(getKey(2026, 3, 1))).hasSize(1);
			assertThat(result.getHistoryGroups().get(getKey(2026, 3, 5))).hasSize(3);
			assertThat(result.getHistoryGroups().get(getKey(2026, 3, 12))).hasSize(2);
		}

		@Test
		@DisplayName("수입과 지출 계산이 정확하게 표현된다.")
		void returnsHistoryDashboard_whenIncomeAndOutlayExist() {
			//given
			HistoryType type = HistoryType.MONTH;

			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 1), "010101", 1000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 5), "010101", 2000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 5), "020201", 3000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 5), "020201", 1000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 12), "020102", 2000));

			//when
			HistoryDashboardResponse result = target.getHistoryDashboard(type);

			//then
			LedgerStatistics statistics = result.getStatistics();
			assertThat(statistics.getTotal()).isEqualTo(9000);
			assertThat(statistics.getIncome()).isEqualTo(3000);
			assertThat(statistics.getOutlay()).isEqualTo(6000);
		}

		@Test
		@DisplayName("다른 회원의 거래내역은 포함되지 않는다.")
		void returnsHistoryDashboard_whenDifferentMemberExists() {
			//given
			HistoryType type = HistoryType.MONTH;

			memberRepository.save(
					MemberFixture.defaultMember().name("김영희").build()
			);

			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 1), "010101", 1000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 5), "010101", 2000));
			ledgerRepository.save(createLedger("UCt01001", LocalDate.of(2026, 3, 5), "020201", 3000));
			ledgerRepository.save(createLedger("UCt01002", LocalDate.of(2026, 3, 5), "020201", 1000));
			ledgerRepository.save(createLedger("UCt01002", LocalDate.of(2026, 3, 12), "020102", 2000));

			//when
			HistoryDashboardResponse result = target.getHistoryDashboard(type);

			//then
			Map<String, List<HistoryItem>> histories = result.getHistoryGroups();

			assertThat(histories.values()).hasSize(3);
			assertThat(histories.values().stream().flatMap(List::stream).toList())
					.extracting(HistoryItem::getAmount)
					.containsExactlyInAnyOrder(1000L, 2000L, 3000L);

		}

		private Ledger createLedger(String memberId, LocalDate date, String category, int amount) {
			return LedgerFixture.defaultLedger()
					.memberId(memberId)
					.date(date)
					.category(category)
					.amount((long)amount)
					.build();
		}

		private String getKey(int year, int month, int day) {
			LocalDate date = LocalDate.of(year, month, day);

			return DateTimeUtils.formatDate(date, DatePatterns.DATE_DOT_WITH_DAY.getPattern());
		}

	}

}
