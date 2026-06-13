package com.moneymanager.repository.ledger;

import com.moneymanager.domain.ledger.dto.query.LedgerHistoryQuery;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.FixCycle;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.vo.Money;
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
import java.util.List;
import java.util.Objects;

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

		FixedYN fixedYN = FixedYN.from(fixed);

		FixCycle cycleType = null;
		if( fixedYN == FixedYN.REPEAT ) {
			cycleType = FixCycle.from(fixCycle);
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
				.money(
						new Money(rs.getLong("amount"), rs.getString("payment_type"))
				)
				.place(
						new Place(rs.getString("place_name"),rs.getString("road_address"), rs.getString("detail_address"))
				)
				.createdAt(rs.getTimestamp("created_at").toLocalDateTime())
				.updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
				.build();
	};

	private final RowMapper<LedgerHistoryQuery> ledgerHistoryQueryRowMapper = (rs, rowNum) -> new LedgerHistoryQuery(
			rs.getString("code"),
			rs.getDate("transaction_date").toLocalDate(),
			rs.getLong("amount"),
			rs.getString("memo"),
			rs.getString("category_code"),
			rs.getString("category_name")
	);


	public Long insert(Ledger ledger) {
		String query = """
				INSERT INTO ledger(id, code, member_id, category_id, fix, fix_cycle, transaction_date, memo, amount, payment_type, place_name, road_address, detail_address)
				VALUES(ledger_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		String fix = ledger.getFix().getValue();
		String cycle = ledger.getFixCycle() != null ? ledger.getFixCycle().getValue() : null;

		Money money = ledger.getMoney();
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
					ps.setLong(8, money.getAmount());
					ps.setString(9, money.getPaymentType().getValue());
					ps.setString(10, place != null ? place.getPlaceName() : null);
					ps.setString(11, place != null ? place.getRoadAddress() : null);
					ps.setString(12, place != null ? place.getDetailAddress() : null);

					return ps;
				},

				keyHolder
		);

		return Objects.requireNonNull(keyHolder.getKey()).longValue();
	}

	public int update(Ledger ledger) {
		String query = """
				UPDATE ledger
				SET category_id = ?, fix = ?, fix_cycle = ?, memo = ?, amount = ?, payment_type = ?, place_name = ?, road_address = ?, detail_address = ?, updated_at = ?
				WHERE member_id = ? AND code = ?
				""";

		String cycle = ledger.getFixCycle() == null ? null : ledger.getFixCycle().getValue();
		Money money = ledger.getMoney();
		Place place = ledger.getPlace();

		return jdbcTemplate.update(
				query,

				ledger.getCategory(), ledger.getFix().getValue(), cycle, ledger.getMemo(),
				money.getAmount(), money.getPaymentType().getValue(),
				place.getPlaceName(), place.getRoadAddress(), place.getDetailAddress(),
				ledger.getUpdatedAt(),

				ledger.getMemberId(), ledger.getCode()
		);

	}


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
		String query = """
				SELECT id, code, member_id, category_id, fix, fix_cycle, transaction_date, memo, amount, payment_type, place_name, road_address, detail_address, created_at, updated_at
				FROM ledger
				WHERE id = ?
				""";

		return jdbcTemplate.queryForObject(
				query,
				ledgerRowMapper,
				id
		);
	}


	public Ledger findByCode(String memberId, String code) {
		String query = """
				SELECT *
				FROM ledger
				WHERE member_id = ?
					AND code = ?
				""";

		return jdbcTemplate.queryForObject(
				query,
				ledgerRowMapper,
				memberId, code
		);
	}


	public List<Ledger> findAll() {
		String query = """
				SELECT *
				FROM ledger
				""";

		return jdbcTemplate.query(
				query,
				ledgerRowMapper
		);
	}


	public List<LedgerHistoryQuery> findHistoriesByMemberAndDateBetween(String memberId, LocalDate startDate, LocalDate endDate) {
		String query = """
				SELECT l.code, transaction_date, c.code AS category_code, c.name AS category_name, amount, memo
				FROM ledger l
				JOIN ledger_category  c ON l.category_id = c.code
				WHERE l.member_id = ?
					AND l.transaction_date >= ?
					AND l.transaction_date < ?
				ORDER BY l.transaction_date DESC, l.id DESC
				""";

		return jdbcTemplate.query(
				query,

				ledgerHistoryQueryRowMapper,

				memberId,
				Date.valueOf(startDate),
				Date.valueOf(endDate.plusDays(1))
		);
	}

	public Long count() {
		String query = """
				SELECT COUNT(*)
				FROM ledger
				""";

		return jdbcTemplate.queryForObject(
				query,
				Long.class
		);
	}

	public void deleteAll() {
		String query = """
				DELETE FROM ledger
				""";

		jdbcTemplate.update(query);
	}

}
