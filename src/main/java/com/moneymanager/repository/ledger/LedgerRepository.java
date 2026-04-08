package com.moneymanager.repository.ledger;

import com.moneymanager.domain.ledger.dto.query.LedgerHistoryQuery;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.FixCycle;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.enums.AmountType;
import com.moneymanager.domain.ledger.vo.Place;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.repository.ledger<br>
 * 파일이름       : LedgerRepository<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 10.<br>
 * 설명              : 가계부 데이터를 조작하는 클래스
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
 * 		 	  <td>26. 1. 10.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Repository
public class LedgerRepository {

	private final JdbcTemplate jdbcTemplate;

	public LedgerRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private final RowMapper<Ledger> ledgerRowMapper = (rs, rowNum) -> {
		String fixed = rs.getString("fix");
		String fixCycle = rs.getString("fix_cycle");

		FixedYN fixedYN = FixedYN.of(fixed);

		FixCycle cycleType = null;
		if( fixedYN.isFixed() ) {
			cycleType = FixCycle.of(fixCycle);
		}

		return Ledger.builder()
				.id(rs.getLong("id"))
				.code(rs.getString("code"))
				.memberId(rs.getString("member_id"))
				.category(rs.getNString("category_id"))
				.fix(fixedYN)
				.fixCycle(cycleType)
				.date(rs.getDate("transaction_date").toLocalDate())
				.memo(rs.getString("memo"))
				.amount(rs.getLong("amount"))
				.amountType(AmountType.of(rs.getString("payment_type")))
				.place(
						new Place(rs.getString("place_name"),rs.getString("road_address"), rs.getString("detail_address"))
				)
				.createdAt(rs.getTimestamp("created_at").toLocalDateTime())
				.updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
				.build();
	};


	/**
	 *	{@code ledger} 테이블에 가계부 정보를 저장합니다.
	 *<p>
	 *    다음과 같은 정보를 저장할 수 있습니다.
	 *    <ul>
	 *        <li>필수정보 - 고유번호({@code code}), 회원ID({@code memberId}), 거래날짜({@code date}), 카테고리({@code category}), 금액({@code amount})</li>
	 *        <li>선택정보 - 메모({@code memo}), 금액유형({@code paymentType}), 주소({@code placeName}, {@code roadAddress}, {@code detailAddress}), 가계부 반복({@code fix}, {@code fixCycle})</li>
	 *    </ul>
	 *</p>
	 * 필수 정보 중 하나라도 없거나 {@code NULL}인 경우
	 * {@link org.springframework.dao.DataIntegrityViolationException}이 발생합니다.
	 *
	 * @param ledger 저장할 가계부 정보를 담은 {@link Ledger} 객체
	 * @return	저장된 가계부의 생성된 식별자
	 * @throws org.springframework.dao.DataIntegrityViolationException 제약 조건을 위반했을 경우 발생
	 */
	public Long save(Ledger ledger) {
		if(ledger.getId() == null) {
			return insert(ledger);
		}else {
			update(ledger);

			return ledger.getId();
		}
	}

	private Long insert(Ledger ledger) {
		String query = "INSERT INTO ledger(id, code, member_id, category_id, fix, fix_cycle, transaction_date, memo, amount, payment_type, place_name, road_address, detail_address) " +
				"VALUES(ledger_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		String fix = ledger.getFix().getValue();
		String cycle = ledger.getFixCycle() != null ? ledger.getFixCycle().getValue() : null;
		String paymentType = ledger.getAmountType().getValue();

		Place place = ledger.getPlace();

		jdbcTemplate.update(
				con -> {
					PreparedStatement ps =
							con.prepareStatement(query, new String[] {"id"});

					ps.setString(1, ledger.getCode());
					ps.setString(2, ledger.getMemberId());
					ps.setString(3, ledger.getCategory());
					ps.setString(4, fix);
					ps.setString(5, cycle);
					ps.setObject(6, ledger.getDate());
					ps.setString(7, ledger.getMemo());
					ps.setLong(8, ledger.getAmount());
					ps.setString(9, paymentType);
					ps.setString(10, place != null ? place.getName() : null);
					ps.setString(11, place != null ? place.getRoadAddress() : null);
					ps.setString(12, place != null ? place.getDetailAddress() : null);

					return ps;
				},
				keyHolder
		);

		return Objects.requireNonNull(keyHolder.getKey()).longValue();
	}

	private void update(Ledger ledger) {}


	/**
	 * {@code ledger}테이블에 저장된 가계부 정보를 조회합니다.
	 * <p>
	 *     가계부 번호({@code id})를 기준으로 가계부 정보가 존재하면 반환하며,
	 *     조회된 정보가 없는 경우 {@link org.springframework.dao.EmptyResultDataAccessException}이 발생합니다.
	 * </p>
	 *
	 * @param id	가계부 번호
	 * @return	번호에 해당하는 가계부 정보를 담은 {@link Ledger} 객체
	 * @throws org.springframework.dao.EmptyResultDataAccessException 조회된 정보가 없는 경우
	 */
	public Ledger findById(Long id) {
		String sql = "SELECT id, code, member_id, category_id, fix, fix_cycle, transaction_date, memo, amount, payment_type, place_name, road_address, detail_address, created_at, updated_at " +
				"FROM ledger " +
				"WHERE id = ?";

		return jdbcTemplate.queryForObject(
				sql, ledgerRowMapper, id
		);
	}


	public List<Ledger> findAll() {
		String sql = "SELECT * FROM ledger";

		return jdbcTemplate.query(sql, ledgerRowMapper);
	}


	public List<LedgerHistoryQuery> findHistoriesByMemberAndDateBetween(String memberId, LocalDate startDate, LocalDate endDate) {
		String sql = "SELECT l.code, transaction_date, c.code AS category_code, c.name AS category_name, amount, memo " +
							"FROM ledger l " +
							"JOIN ledger_category  c ON l.category_id = c.code " +
							"WHERE l.member_id = ? " +
							"AND l.transaction_date >= ? " +
							"AND l.transaction_date < ? " +
							"ORDER BY l.transaction_date DESC, l.created_at DESC";

		return jdbcTemplate.query(
				sql,

				(rs, rowNum) -> {
					Ledger ledger = Ledger.builder()
							.code(rs.getString("code"))
							.date(rs.getDate("transaction_date").toLocalDate())
							.amount(rs.getLong("amount"))
							.memo(rs.getString("memo"))
							.build();

					Category category = Category.builder()
							.code(rs.getString("category_code"))
							.name(rs.getString("category_name"))
							.build();

					return new LedgerHistoryQuery(ledger, category);
				},

				memberId,
				Date.valueOf(startDate),
				Date.valueOf(endDate.plusDays(1))
		);
	}


	public Integer count() {
		String sql = "SELECT COUNT(*) FROM ledger";

		return jdbcTemplate.queryForObject(sql, Integer.class);
	}


	public void deleteAll() {
		String sql ="DELETE FROM ledger";

		jdbcTemplate.update(sql);
	}
}
