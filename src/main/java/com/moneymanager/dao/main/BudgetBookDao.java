package com.moneymanager.dao.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.dto.LedgerCategoryResponse;
import com.moneymanager.domain.ledger.dto.LedgerListResponse;
import com.moneymanager.domain.ledger.dto.LedgerSearchRequest;
import com.moneymanager.domain.global.dto.GoogleChartResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.member.Member;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dao.main<br>
 *  * 파일이름       : BudgetBookDao<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 가계부 데이터를 조작하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Repository
public class BudgetBookDao {

	@Autowired
	private ObjectMapper objectMapper;

	private final Logger logger = LogManager.getLogger(this);
	private final JdbcTemplate jdbcTemplate;

	public BudgetBookDao( DataSource dataSource ) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}



	/**
	 * 생성된 가계부를 반환합니다.
	 *
	 * @param ledger	가계부 정보
	 * @return	생성한 가계부
	 */
	public Ledger saveBudgetBook(Ledger ledger) {
		String sql = "INSERT INTO tb_budget_book(id, member_id, category_id, fix, fix_cycle, book_date, memo, price, payment_type, image1, image2, image3, place_name, road_address, address, created_at) " +
										"VALUES(seq_budgetbook.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
						new PreparedStatementCreator() {
							@NotNull
							@Override
							public PreparedStatement createPreparedStatement(@NotNull Connection con) throws SQLException {
								PreparedStatement stmt = con.prepareStatement(sql, new String[]{"id"});

								stmt.setString(1, ledger.getMember().getId());
								stmt.setString(2, ledger.getCategory().getCode());
								stmt.setString(3, ledger.getFix());
								stmt.setString(4, ledger.getFixCycle());
								stmt.setString(5, ledger.getBookDate());
								stmt.setString(6, ledger.getMemo());
								stmt.setLong(7, ledger.getPrice());
								stmt.setString(8, ledger.getPaymentType().getText());
								stmt.setString(9, ledger.getImage1());
								stmt.setString(10, ledger.getImage2());
								stmt.setString(11, ledger.getImage3());
								stmt.setString(12, ledger.getPlaceName());
								stmt.setString(13, ledger.getRoadAddress());
								stmt.setString(14, ledger.getAddress());

								return stmt;
							}
						}, keyHolder
		);

			Long budgetId = Objects.requireNonNull(keyHolder.getKey()).longValue();
			return findBudgetBookById(budgetId);
	}



	/**
	 *	가계부 번호에 해당하는 가계부를 반환합니다.<br>
	 * 번호에 해당하는 가계부가 존재하지 않으면 null을 반환합니다.
	 *
	 *
	 * @param id	가계부 번호
	 * @return	번호가 있으면 가계부, 없으면 null
	 */
	public Ledger findBudgetBookById(Long id ) {
		String sql = "SELECT tb.*, tc.name " +
								"FROM tb_budget_book tb, tb_category tc " +
								"WHERE tb.id = ? " +
									"AND tb.category_id = tc.code";
		try{
			return jdbcTemplate.queryForObject( sql, (ResultSet rs, int row) -> {
				return Ledger.builder().id(rs.getLong("id")).member(Member.builder().id(rs.getString("member_id")).build())
								.category(Category.builder().code(rs.getString("category_id")).name(rs.getString("name")).build())
								.fix(rs.getString("fix")).fixCycle(rs.getString("fix_cycle"))
								.bookDate(rs.getString("book_date")).memo(rs.getString("memo"))
								.price(rs.getLong("price")).paymentType(PaymentType.valueOf(rs.getString("payment_type")))
								.image1(rs.getString("image1")).image2(rs.getString("image2")).image3(rs.getString("image3"))
								.placeName(rs.getString("place_name")).roadAddress(rs.getString("road_address")).address(rs.getString("address"))
								.createdAt(rs.getTimestamp("created_at").toLocalDateTime()).updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
								.build();
			}, id );
		}catch( EmptyResultDataAccessException e ) {
			return null;
		}
	}



	/**
	 * 검색기간과 유형에 해당하는 가계부 리스트를 조회하는 메서드
	 *
	 * @param memberId		회원 식별번호
	 * @param date						가계부 검색 기간
	 * @param search				검색유형이 담긴 객체
	 * @return	검색유형에 해당하는 가계부 리스트
	 */
	public List<LedgerListResponse.DayCards> findBudgetBooksBySearch(String memberId, LocalDate[] date, LedgerSearchRequest search ) {
		StringBuilder query
				= new StringBuilder("SELECT book_date, " +
													"JSON_ARRAYAGG( " +
														"JSON_OBJECT( 'id' VALUE id, 'code' VALUE category_id, 'name' VALUE name, 'price' VALUE price, 'memo' VALUE memo ) " +
													") AS datas " +
														"FROM TB_BUDGET_BOOK tbb, TB_CATEGORY tc " +
														"WHERE member_id = ? " +
															"AND tc.code = tbb.category_id " +
															"AND TRUNC(tbb.book_date) BETWEEN TO_CHAR(?, 'YYYYMMDD') AND TO_CHAR(?, 'YYYYMMDD') ");

		List<Object> params = new ArrayList<>();
		params.add(memberId);
		switch ( search.getMode() ) {
			case "inout":
				query.append("AND tc.CODE IN (")
									.append("SELECT code FROM tb_category CONNECT BY PRIOR code = parent_code START WITH parent_code = ?")
									.append(") ")
									.append("GROUP BY book_date ")
									.append("ORDER BY tbb.book_date DESC");

				params.addAll(List.of(date));
				params.addAll(search.getKeywords());
				break;
			case "category":
				query.append("AND category_id IN (")
								.append(String.join(", ", Collections.nCopies(search.getKeywords().size(), "?")))
								.append(") ")
								.append("GROUP BY book_date ")
								.append("ORDER BY tbb.book_date DESC");

				params.addAll(List.of(date));
				params.addAll(search.getKeywords());
				break;
			case "memo"	:
				query.append("AND memo LIKE ? ")
									.append("GROUP BY book_date ")
									.append("ORDER BY tbb.book_date DESC");

				params.addAll(List.of(date));
				params.add( "%" + search.getKeywords().get(0) + "%" );
				break;
			case "period":
				query.append("GROUP BY book_date ")
						.append("ORDER BY tbb.book_date DESC");

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
				for( String keyword : search.getKeywords() ) {
					LocalDate period = LocalDate.parse( keyword, formatter );

					params.add(period);
				}
				break;
			case "all":
			default:
				query.append("GROUP BY book_date ")
						.append("ORDER BY book_date DESC");

				params.addAll(List.of(date));
		}

		return jdbcTemplate.query(
			query.toString(),

			ps -> {
				for( int i=0; i<params.size(); i++ ) {
					ps.setObject( i+1, params.get(i) );
				}
			},

			rs -> {
				List<LedgerListResponse.DayCards> result = new ArrayList<>();

				while ( rs.next() ) {
					String bookDate = rs.getString("book_date");
					String jsonList = rs.getString("datas");

					try{
						List<LedgerListResponse.Card> cards = new ArrayList<>();
						JsonNode node = new ObjectMapper().readTree(jsonList);

						for( JsonNode cardNode : node ) {
							LedgerListResponse.Card card = LedgerListResponse.Card.builder()
									.id(cardNode.get("id").asLong())
									.category(LedgerCategoryResponse.builder().code(cardNode.get("code").asText()).name(cardNode.get("name").asText()).build())
									.price(cardNode.get("price").asLong())
									.memo(cardNode.get("memo").asText())
									.build();

							cards.add(card);
						}

						LedgerListResponse.DayCards dayCards = LedgerListResponse.DayCards.builder()
								.date(bookDate).cardList(cards).build();

						result.add(dayCards);
					}catch ( JsonProcessingException e ) {
						throw new RuntimeException("JSON 파싱 오류");
					}
				}

			return result;
		});
	}



	/**
	 * 검색기간에 해당하는 가계부 가격별로 총합을 조회하는 메서드
	 *
	 * @param memberId		회원 식별번호
	 * @param dates					월별로 가계부 검색기간
	 * @return	월별 금액의 총합을 담은 리스트
	 */
	public List<GoogleChartResponse> findSumPriceByYear( String memberId, List<LocalDate[]> dates ) {
		List<GoogleChartResponse> resultList = new ArrayList<>();
		String sql = "SELECT " +
														"NVL(SUM( CASE WHEN SUBSTR(category_id, 1,2) = '01' THEN price ELSE 0 END ), 0) AS income, " +
														"NVL(SUM( CASE WHEN SUBSTR(category_id, 1,2) = '02' THEN price ELSE 0 END ), 0) AS outlay " +
												"FROM tb_budget_book " +
												"WHERE member_id = ? " +
														"AND book_date BETWEEN TO_CHAR(TO_DATE(?, 'YYYYMMDD'), 'YYYYMMDD') AND TO_CHAR(TO_DATE(?, 'YYYYMMDD'), 'YYYYMMDD')";

		for( int i=0; i<dates.size(); i++ ) {
			final int month = i+1;

			GoogleChartResponse yearChart = jdbcTemplate.queryForObject(
							sql,
							(ResultSet rs, int row) -> {
								return GoogleChartResponse.builder().label(month + "월").incomePrice(rs.getLong("income")).outlayPrice(rs.getLong("outlay")).build();
							},
							memberId,
							dates.get(i)[0].format(DateTimeFormatter.ofPattern("yyyyMMdd")), dates.get(i)[1].format(DateTimeFormatter.ofPattern("yyyyMMdd"))
			);

			resultList.add(yearChart);
		}


		return resultList;
	}



	/**
	 * 검색기간에 해당하는 가계부 카테고리별 지출금액을 조회하는 메서드
	 *
	 * @param memberId	회원 식별번호
	 * @param date					가계부 검색기간
	 * @return	카테고리별 지출금액을 담은 리스트
	 */
	public List<GoogleChartResponse> findSumPriceByCategoryAndMonth(String memberId, LocalDate[] date ) {
		String sql = "SELECT tcc.name, NVL(SUM(tbb.price), 0) price " +
														"FROM tb_category tc " +
																"LEFT JOIN tb_category tcc ON tc.parent_code  = tcc.code " +
																"LEFT JOIN tb_budget_book tbb " +
																			"ON				tc.code = tbb.category_id " +
																			"AND			tbb.member_id = ? " +
																			"AND			tbb.book_date BETWEEN TO_CHAR(TO_DATE(?, 'YYYYMMDD'), 'YYYYMMDD') AND TO_CHAR(TO_DATE(?, 'YYYYMMDD'), 'YYYYMMDD') " +
														"WHERE						tcc.parent_code IS NOT NULL " +
																				"AND		SUBSTR(tc.parent_code, 1, 2) = '02' " +
														"GROUP BY			tc.parent_code, tcc.name " +
														"ORDER BY 			tc.parent_code ASC";

		return jdbcTemplate.query(
						sql,
						(ResultSet rs, int row) -> {
							return GoogleChartResponse.builder().label(rs.getString("name")).outlayPrice(rs.getLong("price")).build();
						},
						memberId, date[0].format(DateTimeFormatter.ofPattern("yyyyMMdd")), date[1].format(DateTimeFormatter.ofPattern("yyyyMMdd"))
						);
	}



	/**
	 * 검색기간에 해당하는 가계부 가격별로 총합을 조회하는 메서드
	 *
	 * @param memberId		회원 식별번호
	 * @param dates					주차별로 가계부 검색기간
	 * @return	주차별 금액의 총합을 담은 리스트
	 */
	public List<GoogleChartResponse> findSumPriceByWeek( String memberId, List<LocalDate[]> dates ) {
		List<GoogleChartResponse> resultList = new ArrayList<>();
		String sql = "SELECT " +
														"NVL(SUM( CASE WHEN SUBSTR(category_id, 1,2) = '01' THEN price ELSE 0 END ), 0) AS income, " +
														"NVL(SUM( CASE WHEN SUBSTR(category_id, 1,2) = '02' THEN price ELSE 0 END ), 0) AS outlay " +
													"FROM tb_budget_book " +
													"WHERE member_id = ? " +
															"AND book_date BETWEEN TO_CHAR(TO_DATE(?, 'YYYYMMDD'), 'YYYYMMDD') AND TO_CHAR(TO_DATE(?, 'YYYYMMDD'), 'YYYYMMDD')";

		for( int i=0; i<dates.size(); i++ ) {
			final int week = i+1;

			GoogleChartResponse weekChart = jdbcTemplate.queryForObject(
							sql,
							(ResultSet rs, int row) -> {
								return GoogleChartResponse.builder().label(week + "주").incomePrice(rs.getLong("income")).outlayPrice(rs.getLong("outlay")).build();
							},
							memberId,
							dates.get(i)[0].format(DateTimeFormatter.ofPattern("yyyyMMdd")), dates.get(i)[1].format(DateTimeFormatter.ofPattern("yyyyMMdd"))
			);

			resultList.add(weekChart);
		}


		return resultList;
	}



	/**
	 * 가계부 이미지를 등록 가능한 수를 조회합니다.
	 *
	 * @param memberId		회원 고유번호
	 * @return	등록 가능한 수
	 */
	public Integer findImageLimit( String memberId ) {
		String query = "SELECT image_limit FROM tb_member_info WHERE id = ?";

		return jdbcTemplate.queryForObject(
				query,
				Integer.class,
				memberId
		);
	}



	/**
	 * 가계부 번호에 해당하는 이미지를 조회합니다.
	 * 이미지가 없으면 null을 반환합니다.
	 *
	 * @param id		가계부 번호
	 * @return	가계부 이미지 이름, 없으면 null
	 */
	public String findImageNameById( Long id ) {
		String query = "SELECT image1 FROM tb_budget_book WHERE id = ?";

		return jdbcTemplate.queryForObject(
						query,
						String.class,
						id
		);
	}


	/**
	 * 가계부 번호에 해당하는 가계부 정보(이미지 제외)를 변경합니다. <br>
	 *
	 * @param ledger 가계부 정보
	 */
	public boolean updateBudgetBook( Ledger ledger) {
		String query = "UPDATE tb_budget_book " +
																	"SET category_id = ?, fix = ?, fix_cycle = ?, memo = ?, price = ?, payment_type = ?, place_name = ?, road_address = ?, address = ?, updated_at = SYSDATE " +
																	"WHERE member_id = ? AND id = ?";


		return jdbcTemplate.update(
						query,
						ledger.getCategory().getCode(),	ledger.getFix(),	ledger.getFixCycle(),
						ledger.getMemo(), ledger.getPrice(), ledger.getPaymentType(),
						ledger.getPlaceName(), ledger.getRoadAddress(), ledger.getAddress(),
						ledger.getMember().getId(), ledger.getId()
		) == 1;
	}


	/**
	 * 가계부 번호에 해당하는 이미지명을 수정합니다.<br>
	 *
	 * @param memberId			회원번호
	 */
	public void updateImage( String memberId, Ledger ledger) {
		String query = "UPDATE tb_budget_book SET image1 = ?, image2 = ?, image3 = ? WHERE member_id = ? AND id = ?";

		jdbcTemplate.update(
						query,
						ledger.getImage1(),
						ledger.getImage2(),
						ledger.getImage3(),
						memberId, ledger.getId()
		);
	}


	/**
	 * 가계부 번호에 해당하는 가계부를 삭제한 후 성공여부를 반환합니다.<br>
	 * 삭제 성공하면 true, 실패하면 false 반환합니다.
	 *
	 * @param memberId	회원 식별번호
	 * @param id				삭제할 가계부 번호
	 * @return	삭제 성공하면 true, 실패하면 false
	 */
	public boolean deleteBudgetBookById( String memberId, List<Long> id ) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate =
						new NamedParameterJdbcTemplate(jdbcTemplate);

		String sql = "DELETE FROM tb_budget_book WHERE member_id = :memberId AND id IN ( :id )";

		MapSqlParameterSource parameter = new MapSqlParameterSource();
		parameter.addValue("memberId", memberId);
		parameter.addValue("id", id);

		return namedParameterJdbcTemplate.update(
						sql,
						parameter
		) > 0;
	}
}
