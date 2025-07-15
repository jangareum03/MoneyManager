package com.areum.moneymanager.dao.main;

import com.areum.moneymanager.entity.Admin;
import com.areum.moneymanager.entity.Notice;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;


/**
 * 공지사항을 처리하는 클래스</br>
 * 공지사항 조회, 수정, 삭제 등의 메서드 구현
 *
 * @version 1.0
 */
@Repository
public class NoticeDao {

	private final JdbcTemplate jdbcTemplate;

	public NoticeDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	/**
	 * 등록되어 있는 공지사항의 전체 개수를 반환하는 메서드
	 *
	 * @return 전체 공지사항 개수
	 */
	public Integer countAll() {
		String query = "SELECT COUNT(*) FROM tb_notice";

		return jdbcTemplate.queryForObject(
						query,
						Integer.class
		);
	}


	/**
	 * 번호에 해당하는 공지사항의 개수를 조회하는 메서드
	 *
	 * @param id 공지사항 번호
	 * @return 번호에 해당하는 공지사항이 존재하면 1, 존재하지 않으면 0
	 */
	public Integer countNoticeById(String id) {
		String query = "SELECT COUNT(*) FROM tb_notice WHERE id = ?";

		return jdbcTemplate.queryForObject(query, Integer.class, id);
	}


	/**
	 * 공지사항 번호 사이에 존재하는 공지사항들을 조회하는 메서드
	 *
	 * @param offset 공지사항 시작 번호
	 * @param size   한 페이지당 보여질 공지사항 개수
	 * @return 시작번호와 종료번호에 해당하는 공지사항
	 */
	public List<Notice> findNoticesByPage(int offset, int size) {
		String query = "SELECT * " +
						"FROM tb_notice " +
							"WHERE status = 'ACTIVE' " +
							"ORDER BY created_date DESC " +
							"OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

		return jdbcTemplate.query(
						query,
						(ResultSet rs, int row) ->
										Notice.builder()
														.id(rs.getString("id"))
														.type(rs.getString("type").charAt(0))
														.title(rs.getString("title")).content(rs.getString("content"))
														.createdDate(rs.getDate("created_date")).viewCount(rs.getLong("view_count"))
														.build(),
						offset, size
		);
	}


	/**
	 * 번호에 해당하는 공지사항을 조회하는 메서드
	 *
	 * @param id 공지사항 번호
	 * @return 공지사항
	 */
	public Notice findNoticeById(String id) {
		String sql = "SELECT * FROM tb_notice WHERE id =?";

		return jdbcTemplate.queryForObject(
						sql,
						(ResultSet rs, int row) -> {
							return Notice.builder()
											.id(rs.getString("id")).admin(Admin.builder().id(rs.getString("admin_id")).build())
											.type(rs.getString("type").charAt(0))
											.title(rs.getString("title")).content(rs.getString("content"))
											.rank(rs.getInt("rank"))
											.createdDate(rs.getDate("created_date")).updatedDate(rs.getDate("updated_date"))
											.viewCount(rs.getLong("view_count"))
											.build();
						},
						id
		);
	}


	/**
	 * 번호에 해당하는 공지사항 조회수를 증가하는 메서드
	 *
	 * @param id 공지사항 번호
	 */
	public Long updateReadCount(String id) {
		String query = "UPDATE tb_notice SET view_count = view_count + 1 WHERE id = ?";

		jdbcTemplate.update(
						query,
						id
		);

		return jdbcTemplate.queryForObject(
						"SELECT view_count FROM tb_notice WHERE id = ?",
						Long.class,
						id
		);
	}
}
