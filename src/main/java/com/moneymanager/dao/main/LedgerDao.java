package com.moneymanager.dao.main;

import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.FixedPeriod;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.AmountInfo;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.domain.global.vo.DateGroupable;
import com.moneymanager.domain.global.dto.GoogleChartResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.vo.LedgerDate;
import com.moneymanager.domain.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dao.main<br>
 *  * 파일이름       : LedgerDao<br>
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
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 12. 10</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[매서드 삭제] findImageLimit</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 12. 17</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[매서드 삭제] findImageNameById, updateImage</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Slf4j
@Repository
public class LedgerDao {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public LedgerDao(DataSource dataSource ) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public LedgerDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}



	/**
	 * 생성된 가계부를 반환합니다.
	 *
	 * @param ledger	가계부 정보
	 * @return	생성한 가계부
	 */
	public Ledger saveLedger(Ledger ledger) {
		String sql = "INSERT INTO ledger(id, member_id, category_id, fix, fix_cycle, book_date, memo, price, payment_type, image, place_name, road_address, address, created_at) " +
										"VALUES(ledger_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
						new PreparedStatementCreator() {
							@NotNull
							@Override
							public PreparedStatement createPreparedStatement(@NotNull Connection con) throws SQLException {
								PreparedStatement stmt = con.prepareStatement(sql, new String[]{"id"});

								stmt.setString(1, ledger.getMember().getId());
								stmt.setString(2, ledger.getCategory().getCode());
								stmt.setString(3, ledger.getFixed().getValue());
								stmt.setString(4, ledger.getCycleType().getValue());
								stmt.setDate(5, Date.valueOf(ledger.getTransActionDate()));
								stmt.setString(6, ledger.getMemo());
								stmt.setLong(7, ledger.getAmountInfo().getAmount());
								stmt.setString(8, ledger.getAmountInfo().getType().getDbCode());
								stmt.setString(10, ledger.getPlace().getPlaceName());
								stmt.setString(11, ledger.getPlace().getRoadAddress());
								stmt.setString(12, ledger.getPlace().getDetailAddress());

								return stmt;
							}
						}, keyHolder
		);

			String id = Objects.requireNonNull(keyHolder.getKey()).toString();
			return findLedgerDetailForUser(id);
	}


	/**
	 *	특정 가계부 ID에 해당하는 가계부 상세 정보를 조회합니다.
	 *
	 * <p>
	 *     사용자 화면에서 필요한 카테고리, 거래일, 메모, 금액, 결제 타입, 이미지, 장소 정보 등을 포함한 {@link Ledger} 객체를 반환합니다.
	 *
	 *
	 * @param id	조회할 가계부 번호
	 * @return	가계부 상세 정보를 담은 {@link Ledger} 객체
	 * @throws org.springframework.dao.EmptyResultDataAccessException	조회 결과가 없을 경우 발생
	 */
	public Ledger findLedgerDetailForUser(String id) {
		String sql = "SELECT num, member_id, category_id, name, transaction_date, memo, amount, payment_type, place_name, road_address, address " +
								"FROM ledger l JOIN ledger_category lc " +
								"ON l.category_id = lc.code " +
								"WHERE l.id = ? ";


		return jdbcTemplate.queryForObject(
				sql,

				(ResultSet rs, int row) -> {
					Ledger.LedgerBuilder builder = Ledger.builder()
							.num(rs.getLong("num"))
							.member(Member.builder().id("member_id").build())
							.category(
									Category.builder()
											.code(rs.getString("category_id"))
											.name(rs.getString("name"))
											.build()
							)
							.date(new LedgerDate(rs.getString("transaction_date")))
							.memo(rs.getString("memo"))
							.amountInfo(
									AmountInfo.builder()
											.amount(rs.getLong("amount"))
											.type(PaymentType.from(rs.getString("payment_type")))
											.build()
							);

					if( rs.getString("place_name") != null ) {
						builder.place(
								Place.builder()
								.placeName(rs.getString("place_name"))
								.roadAddress(rs.getString("road_address"))
								.detailAddress(rs.getString("address"))
								.build()
						);
					}

					return builder.build();
				},

				id
		);
	}


	/**
	 * 특정 가계부 ID에 해당하는 가계부 상세 정보를 조회합니다.
	 * <p>
	 * 가계부 수정에서 필요한 카테고리, 거래일, 메모, 금액, 결제 타입, 이미지, 장소 정보 등을 포함한 Ledger 객체를 반환합니다.
	 * </p>
	 *
	 * @param id		조회할 가계부 번호
	 * @return	수정할 가계부 정보를 담은 {@link Ledger} 객체
	 * @throws org.springframework.dao.EmptyResultDataAccessException	조회 결과가 없을 경우
	 */
	public Ledger findLedgerEditForUser(String id) {
		String sql = "SELECT 		member_id, fix, fix_cycle, transaction_date, category_id, name, memo, amount, payment_type, place_name, road_address, address " +
								"FROM		ledger l JOIN ledger_category lc " +
								"ON			l.category_id = lc.code " +
								"WHERE	id = ?";

		return jdbcTemplate.queryForObject(
				sql,

				(ResultSet rs, int row) -> {
					Ledger.LedgerBuilder builder =	Ledger.builder()
							.member(Member.builder().id(rs.getString("member_id")).build())
							.date(new LedgerDate(rs.getString("transaction_date")))
							.fixed(FixedYN.of(rs.getString("fix")))
							.cycleType(FixedPeriod.of(rs.getString("fix_cycle")))
							.category(Category.builder().name(rs.getString("name")).code(rs.getString("category_id")).build())
							.memo(rs.getString("memo"))
							.amountInfo(AmountInfo.builder().amount(rs.getLong("amount")).type(PaymentType.from(rs.getString("payment_type"))).build());

					if( rs.getString("place_name") != null ) {
						builder.place(
								Place.builder()
										.placeName(rs.getString("place_name"))
										.roadAddress(rs.getString("road_address"))
										.detailAddress(rs.getString("address"))
										.build()
						);
					}

					return builder.build();
				},

				id
		);
	}


	/**
	 * 회원번호와 검색 조건에 따라 가계부 내역 리스트를 조회합니다.
	 * <ul>
	 *     <li>mode값에 따라 검색 조건이 달라집니다.</li>
	 *     <ul>
	 *         <li>"inout" : 카테고리 계층 구조 기반 검색</li>
	 *         <li>"category" : 선택한 카테고리 코드 기반  검색</li>
	 *         <li>"memo" : 작성한 메모 내용 기반  검색</li>
	 *     </ul>
	 * </ul>
	 *	반환된 내역은 {@link Ledger} 객체 리스트로 제공되어, 각 Ledger에는 거래일, 카테고리, 금액, 메모 등의 정보가 포함되어 있습니다.
	 *
	 * @param memberId			조회할 회원 번호
	 * @param mode					검색모드("inout", "category", ... ,"all")
	 * @param keywords			검색 키워드 리스트
	 * @param date					날짜 검색기간 객체
	 * @return	조건에 맞는 {@link Ledger} 객체 리스트
	 */
	public List<Ledger> findLedgersBySearch(String memberId, String mode, List<String> keywords, DateGroupable date) {
		StringBuilder sql
				= new StringBuilder("SELECT id, category_id, name, transaction_date, memo, amount " +
														"FROM ledger, ledger_category " +
														"WHERE member_id = ? " +
															"AND category_id = code " +
															"AND transaction_date BETWEEN TO_CHAR(?, 'YYYYMMDD') AND TO_CHAR(?, 'YYYYMMDD') ");

		List<Object> params = new ArrayList<>();
		params.add(memberId);
		params.add(date.getStartDate());
		params.add(date.getEndDate());

		switch (mode) {
			case "inout":
				sql.append("AND category_id IN (")
						.append("SELECT code FROM ledger_category START WITH parent_code = ? CONNECT BY PRIOR code = parent_code")
						.append(") ");

				params.addAll(keywords);
				break;
			case "category":
				params.addAll(keywords);

				sql.append("AND category_id IN (")
						.append(String.join(", ", Collections.nCopies(keywords.size(), "?")))
						.append(") ");
				break;
			case "memo":
				params.add("%" + keywords.get(0) + "%");

				sql.append("AND memo LIKE ? ");
				break;
		}

		return jdbcTemplate.query(
			sql.toString(),

			ps -> {
				for( int i=0; i<params.size(); i++ ) {
					ps.setObject( i+1, params.get(i) );
				}
			},

			rs -> {
				List<Ledger> ledgers = new ArrayList<>();

				while ( rs.next() ) {
					Ledger ledger = Ledger.builder()
							.id(rs.getString("id"))
							.date(new LedgerDate(rs.getString("transaction_date")))
							.category(Category.builder()
									.code(rs.getString("category_id"))
									.name(rs.getString("name"))
									.build())
							.memo(rs.getString("memo"))
							.amountInfo(AmountInfo.builder()
									.amount(rs.getLong("amount"))
									.build())
							.build();

					ledgers.add(ledger);
				}

				return ledgers;
			}
		);
	}


	/**
	 * 회원 ID와 연도의 월별 기간 리스트를 기준으로 수입과 지출 합계를 조회하여 {@link GoogleChartResponse} 리스트로 반환합니다.
	 *
	 * @param memberId		조회할 회원 ID
	 * @param dates			연도의 월별 기간 리스트({@link DateGroupable})
	 * @return	월별 수입과 지출 합계를 담은 {@link GoogleChartResponse} 리스트
	 */
	public List<GoogleChartResponse> findSumPriceByMonthRange(String memberId, List<DateGroupable> dates ) {
		List<GoogleChartResponse> resultList = new ArrayList<>();
		String sql = "SELECT " +
														"NVL(SUM( CASE WHEN SUBSTR(category_id, 1,2) = '01' THEN price ELSE 0 END ), 0) AS income, " +
														"NVL(SUM( CASE WHEN SUBSTR(category_id, 1,2) = '02' THEN price ELSE 0 END ), 0) AS outlay " +
												"FROM ledger " +
												"WHERE member_id = ? " +
														"AND transaction_date BETWEEN TO_CHAR(TO_DATE(?, 'YYYYMMDD'), 'YYYYMMDD') AND TO_CHAR(TO_DATE(?, 'YYYYMMDD'), 'YYYYMMDD')";

		for (DateGroupable period : dates) {
			GoogleChartResponse yearChart = jdbcTemplate.queryForObject(
					sql,
					(ResultSet rs, int row) -> {
						return GoogleChartResponse.builder()
								.label(period.getStartDate().getYear() + "월")
								.incomePrice(rs.getLong("income"))
								.outlayPrice(rs.getLong("outlay")).build();
					},
					memberId,
					period.getStartDate(), period.getEndDate()
			);

			resultList.add(yearChart);
		}


		return resultList;
	}


	/**
	 * 회원 ID와 월 범위 내역 기준으로 카테고리별 지출합계를 조회하여 {@link GoogleChartResponse} 리스트로 반환합니다.
	 *
	 * @param memberId		조회할 회원 ID
	 * @param period			월의 시작일과 종료일을 담은 객체({@link DateGroupable})
	 * @return	카테고리별 지출합계를 담은 {@link GoogleChartResponse} 리스트
	 */
	public List<GoogleChartResponse> findSumPriceByCategoryAndMonth(String memberId, DateGroupable period ) {
		String sql = "SELECT tcc.name, NVL(SUM(tbb.price), 0) price " +
														"FROM ledger_category tc " +
																"LEFT JOIN ledger_category tcc ON tc.parent_code  = tcc.code " +
																"LEFT JOIN ledger tbb " +
																			"ON				tc.code = tbb.category_id " +
																			"AND			tbb.member_id = ? " +
																			"AND			tbb.transaction_date BETWEEN TO_CHAR(TO_DATE(?, 'YYYYMMDD'), 'YYYYMMDD') AND TO_CHAR(TO_DATE(?, 'YYYYMMDD'), 'YYYYMMDD') " +
														"WHERE						tcc.parent_code IS NOT NULL " +
																				"AND		SUBSTR(tc.parent_code, 1, 2) = '02' " +
														"GROUP BY			tc.parent_code, tcc.name " +
														"ORDER BY 			tc.parent_code ASC";

		return jdbcTemplate.query(
						sql,
						(ResultSet rs, int row) -> {
							return GoogleChartResponse.builder().label(rs.getString("name")).outlayPrice(rs.getLong("price")).build();
						},
						memberId, period.getStartDate(), period.getEndDate()
						);
	}


	/**
	 * 회원 ID와 월의 주차별 수입과 지출 합계를 조회하여 {@link GoogleChartResponse} 리스트로 반환합니다.
	 *
	 * @param memberId		조회할 회원 ID
	 * @param dates			월의 주차별 시작일과 종료일을 담은 리스트({@link DateGroupable})
	 * @return	주차별 수입과 지출 합계를 담은 {@link GoogleChartResponse} 리스트
	 */
	public List<GoogleChartResponse> findSumPriceByWeek( String memberId, List<DateGroupable> dates ) {
		List<GoogleChartResponse> resultList = new ArrayList<>();
		String sql = "SELECT " +
														"NVL(SUM( CASE WHEN SUBSTR(category_id, 1,2) = '01' THEN price ELSE 0 END ), 0) AS income, " +
														"NVL(SUM( CASE WHEN SUBSTR(category_id, 1,2) = '02' THEN price ELSE 0 END ), 0) AS outlay " +
													"FROM ledger " +
													"WHERE member_id = ? " +
															"AND transaction_date BETWEEN TO_CHAR(TO_DATE(?, 'YYYYMMDD'), 'YYYYMMDD') AND TO_CHAR(TO_DATE(?, 'YYYYMMDD'), 'YYYYMMDD')";

		for( int i=0; i<dates.size(); i++ ) {
			final int week = i+1;
			DateGroupable period = dates.get(i);

			GoogleChartResponse weekChart = jdbcTemplate.queryForObject(
							sql,
							(ResultSet rs, int row) -> {
								return GoogleChartResponse.builder().label(week + "주").incomePrice(rs.getLong("income")).outlayPrice(rs.getLong("outlay")).build();
							},
							memberId,
							period.getStartDate(), period.getEndDate()
			);

			resultList.add(weekChart);
		}


		return resultList;
	}



	/**
	 * 가계부 번호에 해당하는 가계부 정보(이미지 제외)를 변경합니다. <br>
	 *
	 * @param ledger 가계부 정보
	 */
	public boolean updateLedger(Ledger ledger) {
		String query = "UPDATE ledger " +
																	"SET category_id = ?, fix = ?, fix_cycle = ?, memo = ?, price = ?, payment_type = ?, place_name = ?, road_address = ?, address = ?, updated_at = SYSDATE " +
																	"WHERE member_id = ? AND id = ?";

		return jdbcTemplate.update(
						query,
						ledger.getCategory().getCode(), ledger.getFixed().getValue(), ledger.getCycleType().getValue(),
						ledger.getMemo(), ledger.getAmountInfo().getAmount(), ledger.getAmountInfo().getType(),
						ledger.getPlace().getPlaceName(), ledger.getPlace().getRoadAddress(), ledger.getPlace().getDetailAddress(),
						ledger.getMember().getId(), ledger.getId()
		) == 1;
	}



	/**
	 * 가계부 번호에 해당하는 가계부를 삭제한 후 성공여부를 반환합니다.<br>
	 * 삭제 성공하면 true, 실패하면 false 반환합니다.
	 *
	 * @param memberId	회원 식별번호
	 * @param id				삭제할 가계부 번호
	 * @return	삭제 성공하면 true, 실패하면 false
	 */
	public boolean deleteLedgerById(String memberId, List<Long> id ) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate =
						new NamedParameterJdbcTemplate(jdbcTemplate);

		String sql = "DELETE FROM ledger WHERE member_id = :memberId AND id IN ( :id )";

		MapSqlParameterSource parameter = new MapSqlParameterSource();
		parameter.addValue("memberId", memberId);
		parameter.addValue("id", id);

		return namedParameterJdbcTemplate.update(
						sql,
						parameter
		) > 0;
	}
}
