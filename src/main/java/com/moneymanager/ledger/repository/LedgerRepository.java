package com.moneymanager.ledger.repository;

import com.moneymanager.global.exception.code.LedgerErrorCode;
import com.moneymanager.global.exception.exception.InternalException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.domain.query.LedgerHistoryQuery;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

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

	private final RowMapper<Ledger> ledgerRowMapper = (rs, rowNum) -> Ledger.create(
            rs.getLong("id"),
            rs.getString("code"),
            rs.getString("member_id"),
            rs.getDate("transaction_date").toLocalDate(),
            rs.getNString("category_id"),
            rs.getString("fix"),
            rs.getString("fix_cycle"),
            rs.getString("memo"),
            Money.of(rs.getLong("amount"), rs.getString("payment_type")),
            Place.ofOrNull(rs.getString("place_name"),rs.getString("road_address"), rs.getString("detail_address")),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null
            );

	private final RowMapper<LedgerHistoryQuery> ledgerHistoryQueryRowMapper = (rs, rowNum) -> new LedgerHistoryQuery(
			rs.getString("code"),
			rs.getDate("transaction_date").toLocalDate(),
			rs.getLong("amount"),
			rs.getString("memo"),
			rs.getString("category_name"),
			rs.getString("category_code")
	);


	public Long save(Ledger ledger) {
		if(ledger.getId() == null) {
			return insert(ledger);
		}else {
			update(ledger);
		}

		return ledger.getId();
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

		String placeName = null;
		String roadAddress = null;
		String detailAddress = null;

		if(place != null) {
			placeName = place.getPlaceName();
			roadAddress = place.getRoadAddress();
			detailAddress = place.getDetailAddress();
		}

		return jdbcTemplate.update(
				query,

				ledger.getCategory(), ledger.getFix().getValue(), cycle, ledger.getMemo(),
				money.getAmount(), money.getPaymentType().name(),
				placeName, roadAddress, detailAddress,
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

		try{
			return jdbcTemplate.queryForObject(
					query,
					ledgerRowMapper,
					id
			);
		} catch (EmptyResultDataAccessException e) {
            throw InternalException.of(
					LedgerErrorCode.DATA_NOT_FOUND,
					LogContent.of(
							"가계부 번호로 가계부 조회",
							Ledger.class,
							"id", id
					)
			);
        }
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


	//===== save 보조 메서드 =====
	private Long insert(Ledger ledger) {
		String query = """
				INSERT INTO ledger(id, code, member_id, category_id, fix, fix_cycle, transaction_date, memo, amount, payment_type, place_name, road_address, detail_address)
					VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		Long id = getNextId();

		jdbcTemplate.update(
				query,
				id,
				ledger.getCode(),
				ledger.getMemberId(),
				ledger.getCategory(),
				ledger.getFix().getValue(),
				Ledger.getValueOrNull(ledger.getFixCycle(), FixCycle::getValue),
				ledger.getDate(),
				ledger.getMemo(),
				ledger.getMoney().getAmount(),
				Ledger.getValueOrNull(ledger.getMoney().getPaymentType(), PaymentType::name),
				Ledger.getValueOrNull(ledger.getPlace(), Place::getPlaceName),
				Ledger.getValueOrNull(ledger.getPlace(), Place::getRoadAddress),
				Ledger.getValueOrNull(ledger.getPlace(), Place::getDetailAddress)
		);

		return id;
	}


	//===== 유틸 메서드 =====
	private Long getNextId() {
		String query = "SELECT ledger_seq.NEXTVAL FROM dual";

		return jdbcTemplate.queryForObject(query, Long.class);
	}

}