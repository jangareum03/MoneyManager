package com.moneymanager.ledger.repository;

import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.ObjectUtils;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedType;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.domain.query.LedgerHistoryQuery;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;
import static com.moneymanager.global.exception.code.ErrorCode.INTERVAL_SERVER_ERROR;

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

    private final RowMapper<Ledger> ledgerRowMapper = (rs, rowNum) -> Ledger.restore(
            rs.getLong("id"),
            rs.getString("code"),
            rs.getString("member_id"),
            rs.getDate("transaction_date").toLocalDate(),
            rs.getNString("category_id"),
            FixedType.from(rs.getString("fix")),
            ObjectUtils.getValueOrNull(rs.getString("fix_cycle"), FixCycle::from),
            rs.getString("memo"),
            Money.of(rs.getLong("amount"), rs.getString("payment_type")),
            Place.ofOrNull(rs.getString("place_name"), rs.getString("road_address"), rs.getString("detail_address")),
            rs.getTimestamp("created_at").toLocalDateTime(),
            ObjectUtils.getValueOrNull(rs.getTimestamp("updated_at"), Timestamp::toLocalDateTime)
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
        if (ledger.getId() == null) {
            return insert(ledger);
        } else {
            update(ledger);
        }

        return ledger.getId();
    }

    public Ledger findById(Long id) {
        String query = """
                SELECT id, code, member_id, category_id, fix, fix_cycle, transaction_date, memo, amount, payment_type, place_name, road_address, detail_address, created_at, updated_at
                FROM ledger
                WHERE id = ?
                """;

        try {
            return jdbcTemplate.queryForObject(
                    query,
                    ledgerRowMapper,
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new ApplicationException(
                    DATA_NOT_FOUND,
                    LogContent.of(
                            "가계부 번호로 가계부 조회",
                            Ledger.class,
                            "id", id
                    )
            );
        }
    }


    public Ledger findByCode(String code) {
        String query = """
                SELECT *
                FROM ledger
                WHERE code = ?
                """;

        try {
            return jdbcTemplate.queryForObject(
                    query,
                    ledgerRowMapper,
                    code
            );
        } catch (EmptyResultDataAccessException e) {
            throw new ApplicationException(
                    DATA_NOT_FOUND,
                    LogContent.of(
                            "가계부 코드로 가계부 조회",
                            Ledger.class,
                            "code", code
                    )
            );
        }
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
                ObjectUtils.getValueOrNull(ledger.getFixCycle(), FixCycle::getValue),
                ledger.getDate(),
                ledger.getMemo(),
                ledger.getMoney().getAmount(),
                ObjectUtils.getValueOrNull(ledger.getMoney().getPaymentType(), PaymentType::name),
                ObjectUtils.getValueOrNull(ledger.getPlace(), Place::getPlaceName),
                ObjectUtils.getValueOrNull(ledger.getPlace(), Place::getRoadAddress),
                ObjectUtils.getValueOrNull(ledger.getPlace(), Place::getDetailAddress)
        );

        return id;
    }

    private void update(Ledger ledger) {
        String query = """
                UPDATE ledger
                SET category_id = ?, fix = ?, fix_cycle = ?, memo = ?, amount = ?, payment_type = ?, place_name = ?, road_address = ?, detail_address = ?, updated_at = ?
                WHERE member_id = ? AND id = ?
                """;

        int row = jdbcTemplate.update(
                query,

                ledger.getCategory(),
                ledger.getFix().getValue(),
                ObjectUtils.getValueOrNull(ledger.getFixCycle(), FixCycle::getValue),
                ledger.getMemo(),
                ledger.getMoney().getAmount(),
                ObjectUtils.getValueOrNull(ledger.getMoney().getPaymentType(), PaymentType::name),
                ObjectUtils.getValueOrNull(ledger.getPlace(), Place::getPlaceName),
                ObjectUtils.getValueOrNull(ledger.getPlace(), Place::getRoadAddress),
                ObjectUtils.getValueOrNull(ledger.getPlace(), Place::getDetailAddress),
                ledger.getUpdatedAt(),

                ledger.getMemberId(), ledger.getId()
        );

        if (row == 0) {
            throw new ApplicationException(
                    INTERVAL_SERVER_ERROR,
                    LogContent.of(
                            "가계부 수정",
                            Ledger.class,
                            "ledgerId", ledger.getId(),
                            "memberId", ledger.getMemberId()
                    )
            );
        }

    }


    //===== 유틸 메서드 =====
    private Long getNextId() {
        String query = "SELECT ledger_seq.NEXTVAL FROM dual";

        return jdbcTemplate.queryForObject(query, Long.class);
    }

}