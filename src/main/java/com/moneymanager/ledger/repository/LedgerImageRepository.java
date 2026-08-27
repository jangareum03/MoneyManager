package com.moneymanager.ledger.repository;

import com.moneymanager.ledger.domain.entity.LedgerImage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.repository.ledger<br>
 * 파일이름       : LedgerImageRepository<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 13<br>
 * 설명              : 가계부 이미지 데이터를 조작하는 클래스
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
 * 		 	  <td>26. 1. 13.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Repository
public class LedgerImageRepository {

    private final JdbcTemplate jdbcTemplate;

    public LedgerImageRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private final RowMapper<LedgerImage> ledgerImageRowMapper = (rs, rowNum) -> LedgerImage.restore(
            rs.getLong("id"),
            rs.getLong("ledger_id"),
            rs.getString("image_path"),
            rs.getInt("sort_order"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null
    );


    public void saveAll(List<LedgerImage> images) {
        String query = """
                INSERT INTO ledger_image(id, ledger_id, image_path, sort_order)
                VALUES(ledger_image_seq.NEXTVAL, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(
                query,

                images,

                images.size(),

                (ps, image) -> {
                    ps.setLong(1, image.getLedgerId());
                    ps.setString(2, image.getImagePath());
                    ps.setInt(3, image.getSortOrder());
                }
        );
    }

    public List<LedgerImage> findByLedgerId(Long ledgerId) {
        String query = """
                SELECT *
                FROM ledger_image
                WHERE ledger_id = ?
                ORDER BY sort_order
                """;

        return jdbcTemplate.query(
                query, ledgerImageRowMapper, ledgerId
        );
    }

    public List<LedgerImage> findByLedgerCode(String code) {
        String query = """
                SELECT *
                FROM ledger_image li
                	JOIN ledger l
                	ON l.id = li.ledger_id
                WHERE l.code = ?
                ORDER BY sort_order
                """;

		return jdbcTemplate.query(
				query,
				ledgerImageRowMapper,
				code
		);
    }

    public List<LedgerImage> findAll() {
        String query = """
                SELECT *
                FROM ledger_image
                """;

        return jdbcTemplate.query(
                query,
                ledgerImageRowMapper
        );
    }

    public Integer count() {
        String query = """
                SELECT COUNT(*)
                FROM ledger_image
                """;

        return jdbcTemplate.queryForObject(
                query,
                Integer.class
        );
    }

    public void deleteAll() {
        String query = """
                DELETE FROM ledger_image
                """;

        jdbcTemplate.update(query);
    }

    public int deleteByLedgerId(Long ledgerId) {
        String query = """
                DELETE FROM ledger_image
                WHERE ledger_id = ?
                """;

        return jdbcTemplate.update(
                query,
                ledgerId
        );
    }

}
