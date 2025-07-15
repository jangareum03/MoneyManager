package com.areum.moneymanager.dao.main;

import com.areum.moneymanager.entity.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;


/**
 *	상품 카테고리를 생성, 조회, 삭제하기 위한 메서드를 제공하는 클래스
 *
 * @version 1.0
 */
@Repository
public class CategoryDao {

	private final JdbcTemplate jdbcTemplate;

	public CategoryDao( DataSource dataSource ) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}




	/**
	 * 최상위 카테고리의 이름과 코드를 조회하는 메서드
	 *
	 * @return	이름과 코드를 담은 리스트
	 */
	public List<Category> findCategory() {
		String sql = "SELECT name, code FROM tb_category WHERE parent_code IS NULL ORDER BY code ASC";

		return jdbcTemplate.query( sql, (ResultSet rs, int row) ->
			Category.builder().name(rs.getString("name")).code(rs.getString("code")).build()
		);
	}



	/**
	 * 코드에 해당하는 카테고리를 조회하는 메서드
	 *
	 * @param code	카테고리 코드
	 * @return	코드에 해당하는 카테고리 리스트
	 */
	public List<Category> findCategoryByCode( String code ) {
		String sql = "SELECT name, code " +
														"FROM tb_category " +
														"WHERE parent_code = ? " +
														"ORDER BY code";

		return jdbcTemplate.query( sql, (ResultSet rs, int row) ->
			Category.builder().name(rs.getString("name")).code(rs.getString("code")).build()
		, code );
	}



	/**
	 *	하위카테고리의 모든 상위 카테고리를 조회하는 메서드
	 *
	 * @param code		하위 카테고리
	 * @return	하위카테고리 포함한 상위 카테고리 리스트
	 */
	public List<Category> findCategoryByStep( String code ) {
		String sql = "SELECT name, code " +
														"FROM tb_category " +
														"START WITH code = ? " +
															"CONNECT BY PRIOR parent_code = code " +
														"ORDER BY code";

		return jdbcTemplate.query(
						sql,
						(ResultSet rs, int row) ->
										Category.builder().name(rs.getString("name")).code(rs.getString("code")).build(),
						code
		);
	}
}
