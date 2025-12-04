package com.moneymanager.ledger.search;

import com.moneymanager.dao.main.LedgerDao;
import com.moneymanager.domain.global.vo.DateGroupable;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.vo.AmountInfo;
import com.moneymanager.domain.ledger.vo.LedgerDate;
import com.moneymanager.domain.ledger.vo.SearchPeriod;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * <p>
 * 패키지이름    : com.moneymanager.budgetBook<br>
 * 파일이름       : LedgerDaoSearchTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 24.<br>
 * 설명              : 가계부 검색 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>25. 11. 24.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LedgerDaoSearchTest {

	private static DateGroupable dateGroupable;

	@Autowired
	private LedgerDao dao;

	@BeforeAll
	static void 설정() {
		dateGroupable = new SearchPeriod("20251101", "20251130");
	}

	//=================================================
	// findLedgersBySearch() 테스트
	//=================================================
	@DisplayName("전체 조회 시 저장된 가계부 내역을 모두 반환한다.")
	@Test
	void 전체모드_내역_있으면_리스트_반환(){
		//given
		String memberId = "UCh11001";
		List<String> keywords = List.of();

		List<Ledger> expected = List.of(
				Ledger.builder().id("01ARZ3NDEKTSV4RRFFQ69G5FAV").category(Category.builder().code("020101").build()).date(new LedgerDate("20251101")).amountInfo(AmountInfo.builder().amount(15000).build()).build(),
				Ledger.builder().id("01H5HZ8X9E7EY2XKZCW2FQX16B").category(Category.builder().code("010101").build()).date(new LedgerDate("20251130")).amountInfo(AmountInfo.builder().amount(2500000).build()).build()
		);

		//when
		List<Ledger> result = dao.findLedgersBySearch( memberId, "all", keywords, dateGroupable);

		//then
		assertThat(result)
				.hasSize(2)
				.usingRecursiveComparison()
				.isEqualTo(expected);
	}

	@DisplayName("전체 조회 시 가계부 내역이 없으면 빈 리스트를 반환한다.")
	@Test
	void 전체모드_내역_없으면_빈리스트_반환(){
		//given
		String memberId = "UCc06001";
		List<String> keywords = List.of();

		//when
		List<Ledger> result = dao.findLedgersBySearch( memberId, "all", keywords, dateGroupable);

		//then
		assertThat(result).isEmpty();
	}

	@DisplayName("수입/지출 조회 시 저장된 가계부 내역을 반환한다.")
	@Test
	void inout모드_내역_있으면_리스트반환(){
		//given
		String memberId = "UKb11001";
		List<String> keywords = List.of("010000");
		DateGroupable dateGroupable = new SearchPeriod("20251201", "20251207");

		List<Ledger> expected = List.of(
			Ledger.builder()
					.id("01F8Z6YQJ3G5Z7K1V2A9B0C1D7")
					.category(Category.builder().code("010101").build())
					.date(new LedgerDate("20251202"))
					.memo("12월 월급")
					.amountInfo(AmountInfo.builder().amount(2500000).build())
					.build()
		);

		//when
		List<Ledger> result = dao.findLedgersBySearch( memberId, "inout", keywords, dateGroupable);

		//then
		assertThat(result)
				.hasSize(1)
				.usingRecursiveComparison()
				.isEqualTo(expected);
	}

	@DisplayName("수입/지출 조회 시 가계부 내역이 없으면 빈 리스트를 반환한다.")
	@Test
	void inout모드_내역_없으면_빈리스트반환(){
		//given
		String memberId = "UKb11001";
		List<String> keywords = List.of("020000");
		DateGroupable dateGroupable = new SearchPeriod("20251201", "20251207");

		//when
		List<Ledger> result = dao.findLedgersBySearch( memberId, "inout", keywords, dateGroupable);

		//then
		assertThat(result).isEmpty();
	}

	@DisplayName("상세 카테고리 조회 시 저장된 가계부 내역을 반환한다.")
	@Test
	void category모드_내역_있으면_리스트_반환(){
		//given
		String memberId = "UCa12001";
		List<String> keywords = List.of("020301");

		List<Ledger> expected = List.of(
				Ledger.builder()
						.id("01F8Z6YQJ3G5Z7K1V2A9B0C1D3")
						.category(Category.builder().code("020301").build())
						.date(new LedgerDate("20251108"))
						.memo("주토피아2")
						.amountInfo(AmountInfo.builder().amount(15000).build())
						.build()
		);

		//when
		List<Ledger> result = dao.findLedgersBySearch( memberId, "category", keywords, dateGroupable);

		//then
		assertThat(result)
				.hasSize(1)
				.usingRecursiveComparison()
				.isEqualTo(expected);
	}

	@DisplayName("상세 카테고리 조회 시 가계부 내역이 없으면 빈 리스트를 반환한다.")
	@Test
	void category모드_내역_없으면_리스트_반환(){
		//given
		String memberId = "UCa12001";
		List<String> keywords = List.of("020202, 020203");
		DateGroupable dateGroupable = new SearchPeriod("20251201", "20251207");

		//when
		List<Ledger> result = dao.findLedgersBySearch( memberId, "category", keywords, dateGroupable);

		//then
		assertThat(result).isEmpty();
	}

	@DisplayName("메모 내용 조회 시 저장된 가계부 내역을 반환한다.")
	@Test
	void memo모드_내역_있으면_리스트_반환(){
		//given
		String memberId = "UNn12001";
		List<String> keywords = List.of("넷");
		DateGroupable dateGroupable = new SearchPeriod("20251201", "20251207");

		List<Ledger> expected = List.of(
				Ledger.builder()
						.id("01F8Z6YQJ3G5Z7K1V2A9B0C1D8")
						.category(Category.builder().code("020704").build())
						.date(new LedgerDate("20251201"))
						.memo("넷플릭스")
						.amountInfo(AmountInfo.builder().amount(9900).build())
						.build()
		);

		//when
		List<Ledger> result = dao.findLedgersBySearch( memberId, "memo", keywords, dateGroupable);

		//then
		assertThat(result)
				.hasSize(1)
				.usingRecursiveComparison()
				.isEqualTo(expected);
	}

	@DisplayName("메모 내용 조회 시 가계부 내역이 없으면 빈 리스트를 반환한다.")
	@Test
	void memo모드_내역_없으면_리스트_반환(){
		//given
		String memberId = "UNn12001";
		List<String> keywords = List.of("점심");
		DateGroupable dateGroupable = new SearchPeriod("20251201", "20251207");

		//when
		List<Ledger> result = dao.findLedgersBySearch( memberId, "memo", keywords, dateGroupable);

		//then
		assertThat(result).isEmpty();
	}

	@DisplayName("기간 조회 시 저장된 가계부 내역을 반환한다.")
	@Test
	void period모드_내역_있으면_리스트_반환(){
		//given
		String memberId = "UKb11001";
		DateGroupable dateGroupable = new SearchPeriod("20251123", "20251205");
		List<String> keywords = Collections.emptyList();

		List<Ledger> expected = List.of(
				Ledger.builder()
						.id("01F8Z6YQJ3G5Z7K1V2A9B0C1D6")
						.category(Category.builder().code("020105").build())
						.date(new LedgerDate("20251123"))
						.memo("도시락")
						.amountInfo(AmountInfo.builder().amount(9500).build())
						.build(),
				Ledger.builder()
						.id("01F8Z6YQJ3G5Z7K1V2A9B0C1D7")
						.category(Category.builder().code("010101").build())
						.date(new LedgerDate("20251202"))
						.memo("12월 월급")
						.amountInfo(AmountInfo.builder().amount(2500000).build())
						.build()
		);

		//when
		List<Ledger> result = dao.findLedgersBySearch( memberId, "period", keywords, dateGroupable);

		//then
		assertThat(result)
				.hasSize(2)
				.usingRecursiveComparison()
				.isEqualTo(expected);
	}

	@DisplayName("기간 조회 시 가계부 내역이 없으면 빈 리스트를 반환한다.")
	@Test
	void period모드_내역_없으면_리스트_반환(){
		//given
		String memberId = "UKb11001";
		DateGroupable dateGroupable = new SearchPeriod("20251110", "20251116");
		List<String> keywords = Collections.emptyList();

		//when
		List<Ledger> result = dao.findLedgersBySearch( memberId, "period", keywords, dateGroupable);

		//then
		assertThat(result).isEmpty();
	}
}
