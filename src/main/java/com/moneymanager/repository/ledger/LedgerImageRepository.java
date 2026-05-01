package com.moneymanager.repository.ledger;

import com.moneymanager.domain.ledger.entity.LedgerImage;
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


	private final RowMapper<LedgerImage> ledgerImageRowMapper = (rs, rowNum) -> {
		return LedgerImage.builder()
				.id(rs.getLong("id"))
				.ledgerId(rs.getLong("ledger_id"))
				.imagePath(rs.getString("image_path"))
				.sortOrder(rs.getInt("sort_order"))
				.createdAt(rs.getTimestamp("created_at").toLocalDateTime())
				.updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
				.build();
	};


	/**
	 * {@code ledger_image}테이블에 가계부와 연결된 이미지 정보를 저장합니다.
	 * <p>
	 *     다음과 같은 정보를 저장할 수 있습니다.
	 *     <ul>
	 *         <li>
	 *             이미지 고유번호({@code id}),
	 *         	가계부 식별자({@code code}),
	 *         	이미지 상대 경로({@code image_path}),
	 *         	정렬 순서({@code sort_order})
 *         	</li>
	 *     </ul>
	 * </p>
	 *
	 * @param images	저장할 가계부 이미지 정보를 담은 {@link LedgerImage} 객체 리스트
	 */
	public void saveAll(List<LedgerImage> images) {
		String sql = "INSERT INTO ledger_image(id, ledger_id, image_path, sort_order)" +
							"VALUES(ledger_image_seq.NEXTVAL, ?, ?, ?)";

		jdbcTemplate.batchUpdate(
				sql,
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
				""";

		return jdbcTemplate.query(
			query, ledgerImageRowMapper, ledgerId
		);
	}

	public List<LedgerImage> findAll() {
		String sql = "SELECT * FROM ledger_image";

		return jdbcTemplate.query(sql, ledgerImageRowMapper);
	}

	public Integer count() {
		String sql = "SELECT COUNT(*) FROM ledger_image";

		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public void deleteAll() {
		String sql = "DELETE FROM ledger_image";

		jdbcTemplate.update(sql);
	}
}
