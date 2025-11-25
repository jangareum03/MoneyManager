package com.moneymanager.dao.main;

import com.moneymanager.domain.ledger.entity.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dao.main<br>
 *  * 파일이름       : CategoryDao<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 가계부 카테고리 데이터를 조작하는 클래스
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
 *		</tbody>
 * </table>
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
