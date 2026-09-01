package com.moneymanager.ledger.repository;

import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.ObjectUtils;
import com.moneymanager.ledger.domain.dto.response.history.LedgerSearchCondition;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedType;
import com.moneymanager.ledger.domain.enums.HistoryMenu;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.domain.query.LedgerCategoryStatQuery;
import com.moneymanager.ledger.domain.query.LedgerHistoryQuery;
import com.moneymanager.ledger.domain.query.LedgerMonthlyStatQuery;
import com.moneymanager.ledger.domain.query.LedgerWeeklyStatQuery;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
            return null;
        }
    }

    public List<Ledger> findByCodeIn(List<String> codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyList();

        String params = getParams(codes);

        String query = """
                        SELECT *
                        FROM ledger
                        WHERE code IN (%s)
                """.formatted(params);

        return jdbcTemplate.query(query, ledgerRowMapper, codes.toArray());
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

    public List<LedgerHistoryQuery> findByTransactionDateBetween(String memberId, LocalDate fromDate, LocalDate toDate) {
        String query = """
                        SELECT l.code, l.transaction_date, l.category_id, lc.name AS category_name, l.amount, l.memo
                        FROM ledger l
                            JOIN ledger_category lc
                            ON l.category_id = lc.code
                        WHERE l.member_id = ?
                            AND l.transaction_date >= ?
                            AND l.transaction_date < ?
                        ORDER BY l.transaction_date DESC, l.created_at DESC, l.id DESC
                """;

        return jdbcTemplate.query(
                query,
                (rs, rowNum) -> LedgerHistoryQuery.of(
                        rs.getString("code"),
                        rs.getDate("transaction_date").toLocalDate(),
                        rs.getString("category_id"),
                        rs.getString("category_name"),
                        rs.getLong("amount"),
                        rs.getString("memo")
                ),
                memberId, fromDate, toDate.plusDays(1)
        );
    }

    public List<LedgerHistoryQuery> findAllByConditionAndDateRange(String memberId, LocalDate fromDate, LocalDate toDate, LedgerSearchCondition searchCondition) {
        StringBuilder query = new StringBuilder("""
                SELECT l.code, l.transaction_date, l.category_id, lc.name AS category_name, l.amount, l.memo
                FROM ledger l
                    JOIN ledger_category lc
                    ON l.category_id = lc.code
                WHERE l.member_id = ?
                    AND l.transaction_date >= ?
                    AND l.transaction_date < ?
                """);

        List<Object> params = new ArrayList<>();
        params.add(memberId);
        params.add(fromDate);
        params.add(toDate.plusDays(1));

        HistoryMenu menu = searchCondition.getMenu();
        switch (menu) {
            case ALL, PERIOD -> {}
            case CATEGORY -> {
                query.append("""
                        AND l.category_id IN (
                            SELECT code
                            FROM ledger_category
                            START WITH code = ?
                            CONNECT BY PRIOR code = parent_code
                        )
                        """);

                params.add(searchCondition.getKeyword());
            }
            case SUB_CATEGORY -> {
                query.append("""
                                AND  l.category_id IN (%s)
                                """.formatted(getParams(searchCondition.getKeywords())));

                params.addAll(searchCondition.getKeywords());
            }
            case MEMO -> {
                query.append("""
                        AND l.memo LIKE ?
                        """);

                params.add("%" + searchCondition.getKeyword() + "%");
            }
        }

        return jdbcTemplate.query(
                query.toString(),
                (rs, rowNum) -> LedgerHistoryQuery.of(
                        rs.getString("code"),
                        rs.getDate("transaction_date").toLocalDate(),
                        rs.getString("category_id"),
                        rs.getString("category_name"),
                        rs.getLong("amount"),
                        rs.getString("memo")
                ),
                params.toArray()
        );
    }

    public List<LedgerMonthlyStatQuery> findMonthlyAmountSum(String memberId, LocalDate fromDate, LocalDate toDate) {
        String query = """
                WITH
                    MONTH_TABLE as (
                        SELECT
                            LEVEL as month,
                                CASE
                                    WHEN LEVEL = 1 THEN first_date
                                    ELSE ADD_MONTHS(first_date, LEVEL - 1)
                                END as start_date,
                                CASE
                                    WHEN LEVEL = 1 THEN last_date + 1
                                    ELSE ADD_MONTHS(last_date, LEVEL -1) + 1
                                END as end_date
                        FROM (
                            SELECT
                                TRUNC(?, 'YYYY') as first_date,
                                LAST_DAY(TRUNC(?, 'YYYY')) as last_date
                            FROM DUAL
                        )
                        CONNECT BY LEVEL <= 12
                    )

                SELECT
                    month,
                    NVL(
                        SUM(
                            CASE
                                WHEN root.name = '수입' THEN l.amount
                                ELSE 0
                            END
                        ),
                    0
                    ) as income,
                    NVL(
                        SUM(
                            CASE
                                WHEN root.name = '지출' THEN l.amount
                                ELSE 0
                            END
                        ),
                        0
                    ) as outlay
                FROM month_table m
                    LEFT JOIN ledger l
                        ON l.transaction_date >= m.start_date
                        AND l.transaction_date < m.end_date
                        AND l.member_id = ?
                    LEFT JOIN ledger_category low
                        ON low.code = l.category_id
                    LEFT JOIN ledger_category mid
                        ON mid.code = low.parent_code
                    LEFT JOIN ledger_category root
                        ON root.code = mid.parent_code
                WHERE root.parent_code IS NULL
                GROUP BY m.month
                ORDER BY m.month
                """;

        return jdbcTemplate.query(
                query,
                (rs, rowNum) -> LedgerMonthlyStatQuery.of(
                        rs.getInt("month"),
                        rs.getLong("income"),
                        rs.getLong("outlay")
                ),
                fromDate, toDate, memberId
        );
    }

    public List<LedgerWeeklyStatQuery> findMonthlyAmountForWeeklyStats(String memberId, LocalDate fromDate, LocalDate toDate) {
        String query = """
                WITH
                    DATE_TABLE as (
                        SELECT
                            TRUNC(?, 'MM') as first_day,
                            LAST_DAY(?) as last_day,
                            TRUNC(TRUNC(?, 'MM'), 'IW') + 7 AS first_monday
                        FROM DUAL
                    ),
                    PERIOD_TABLE AS (
                        SELECT
                            LEVEL as week,
                
                            CASE
                                WHEN LEVEL = 1
                                THEN first_day
                                ELSE first_monday + (LEVEL - 2) * 7
                            END AS start_date,
                
                            CASE
                                WHEN LEVEL = 1
                                THEN first_monday - 1
                                ELSE LEAST(
                                    first_monday + (LEVEL - 2) * 7 + 6,
                                    last_day
                                )
                            END AS end_date
                        FROM DATE_TABLE
                        CONNECT BY LEVEL <= (
                            1 + CEIL((last_day - first_monday + 1) / 7)
                        )
                    )
                
                SELECT
                    week,
                    NVL(
                        SUM(
                            CASE
                                WHEN root.name = '수입'
                                THEN l.amount
                                ELSE 0
                            END
                        ),
                        0
                    ) as income,
                    NVL(
                        SUM(
                            CASE
                                WHEN root.name = '지출'
                                THEN l.amount
                                ELSE 0
                            END
                        ),
                        0
                    ) as outlay
                FROM PERIOD_TABLE p
                    LEFT JOIN ledger l
                        ON l.transaction_date  >= p.start_date
                        AND l.transaction_date < end_date + 1
                        AND l.member_id = ?
                    LEFT JOIN ledger_category low
                        ON low.code = l.category_id
                    LEFT JOIN ledger_category mid
                        ON mid.code = low.parent_code
                    LEFT JOIN ledger_category root
                        ON root.code = mid.parent_code
                GROUP BY p.week
                ORDER BY p.week
                """;

        return jdbcTemplate.query(
                query,
                (rs, rowNum) -> LedgerWeeklyStatQuery.of(
                        rs.getInt("week"),
                        rs.getLong("income"),
                        rs.getLong("outlay")
                ),
                fromDate, toDate, fromDate, memberId
        );
    }

    public List<LedgerCategoryStatQuery> findOutlayStatsByCategory(String memberId, LocalDate fromDate, LocalDate toDate) {
        String query = """
                SELECT
                    mid.name as category,
                    NVL(SUM(l.amount), 0) as amount
                FROM ledger_category root
                    JOIN ledger_category mid
                        ON mid.parent_code = root.code
                    LEFT JOIN ledger_category low
                        ON low.parent_code = mid.code
                    LEFT JOIN ledger l
                        ON l.category_id = low.code
                        AND l.member_id = ?
                        AND transaction_date >= ?
                        AND transaction_date < ?
                WHERE root.name = '지출'
                GROUP BY mid.code, mid.name
                ORDER BY mid.code
                """;

        return jdbcTemplate.query(
                query,
                (rs, rowNum) -> LedgerCategoryStatQuery.of(
                        rs.getString("category"),
                        rs.getLong("amount")
                ),
                memberId, fromDate, toDate.plusDays(1)
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

    public int deleteByIdIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        String query = """
                DELETE FROM ledger
                WHERE id IN (%s)
                """.formatted(getParams(ids));

        return jdbcTemplate.update(query, ids.toArray());
    }


    //===== insert 보조 메서드 =====
    private Long getNextId() {
        String query = "SELECT ledger_seq.NEXTVAL FROM dual";

        return jdbcTemplate.queryForObject(query, Long.class);
    }


    //===== 유틸 메서드 =====
    private String getParams(List<?> param) {
        List<String> elements = Collections.nCopies(param.size(), "?");

        return String.join(", ", elements);
    }

}