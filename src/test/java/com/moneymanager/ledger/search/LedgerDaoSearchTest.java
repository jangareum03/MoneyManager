package com.moneymanager.ledger.search;

import com.moneymanager.dao.main.LedgerDao;
import com.moneymanager.domain.ledger.dto.CategoryResponse;
import com.moneymanager.domain.ledger.dto.LedgerListResponse;
import com.moneymanager.domain.ledger.vo.SearchPeriod;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * <p>
 * 패키지이름    : com.moneymanager.budgetBook<br>
 * 파일이름       : LedgerDaoSearchTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 24.<br>
 * 설명              : LedgerDao 기능을 검증하는 테스트 클래스
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
@ExtendWith(MockitoExtension.class)
class LedgerDaoSearchTest {

	@Mock
	private JdbcTemplate jdbcTemplate;
	private LedgerDao budgetBookDao;

	@BeforeEach
	void setUp() {
		this.budgetBookDao = new LedgerDao(jdbcTemplate);
	}

	private final String mockJson = "[{\"id\":1,\"code\":\"020101\",\"name\":\"식비\",\"price\":15000,\"memo\":\"점심\"}]";

	//=================================================
	// findBudgetBooksBySearch() 테스트
	//=================================================
	@DisplayName("월별 가계부에서 메모 검색 시 결과가 목록으로 반환되어야 한다.")
	@Test
	void 월단위_메모검색_목록반환(){
		//given
		LedgerListResponse.DayCards expected = LedgerListResponse.DayCards.builder()
				.date("20251120")
				.cardList(List.of(
						LedgerListResponse.Card.builder()
								.id(1L)
								.category(CategoryResponse.builder().code("0201010").name("식비").build())
								.price(15000L)
								.memo("점심")
								.build()
				))
				.build();

		when(jdbcTemplate.query(
				anyString(),
				any(PreparedStatementSetter.class),
				any(ResultSetExtractor.class)
		)).thenAnswer(invocationOnMock -> {
			ResultSetExtractor<List<LedgerListResponse.DayCards>> rse
					= invocationOnMock.getArgument(2);

			ResultSet rs = mock(ResultSet.class);
			when(rs.next()).thenReturn(true, false);

			when(rs.getString("transaction_date")).thenReturn(expected.getDate());
			when(rs.getString("datas")).thenReturn(mockJson);

			return rse.extractData(rs);
		});

		//when
		List<LedgerListResponse.DayCards> result = budgetBookDao.findLedgersBySearch(
				"member",
				"memo",
				List.of("점심"),
				new SearchPeriod("20251101", "20251130")
		);

		//then
		assertThat(result).isNotNull().hasSize(1);
		assertThat(result.get(0).getDate()).isEqualTo("20251120");

		assertThat(result.get(0).getCardList())
				.hasSize(1)
				.extracting("id", "category.code", "category.name", "price", "memo")
				.containsExactly(Tuple.tuple(1L, "020101", "식비", 15000L, "점심"));
	}
}
