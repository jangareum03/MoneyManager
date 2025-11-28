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
	 * 부모 카테고리가 없는 최상위 카테고리 목록을 조회합니다.
	 * <p>
	 *     <code>parent_code</code>가 NULL인 레코드만 조회하여 모든 최상위 계층 카테고리를 가져옵니다.
	 *     조회된 결과는 code 기준으로 오름차순으로 정렬되어 {@link Category} 엔티티로 매핑됩니다.
	 * </p>
	 *
	 * @return	최상위 카테고리 정보를 담은 {@link Category} 리스트
	 */
	public List<Category> findTopCategories() {
		String sql = "SELECT name, code " +
								"FROM ledger_category " +
								"WHERE parent_code IS NULL " +
								"ORDER BY code";

		return jdbcTemplate.query(
				sql,
				(ResultSet rs, int row) ->
					Category.builder()
							.name(rs.getString("name"))
							.code(rs.getString("code"))
							.build()
		);
	}


	/**
	 * 부모 카테고리가 {@code parentCode}인 하위 카테고리 목록을 조회합니다.
	 * <p>
	 *    <code>parent_code</code>가 전달된 값과 일치하는 모든 카테고리를 조회합니다.
	 *    조회된 결과는 code 기준 오름차순으로 정렬되어, {@link Category} 엔티티로 매핑됩니다.
	 * </p>
	 *
	 * @param parentCode		조회할 부모 카테고리 코드
	 * @return	주어진 부모코드의 직접적인 하위 카테고리 리스트
	 */
		public List<Category> findCategoryCodesByParentCode(String parentCode) {
			String sql = "SELECT name, code " +
									"FROM ledger_category " +
									"WHERE parent_code = ?" +
									" ORDER BY code";

			return jdbcTemplate.query(
					sql,
					(ResultSet rs, int row) ->
						Category.builder()
								.name(rs.getString("name"))
								.code(rs.getString("code"))
								.build(),
					parentCode
			);
		}


	/**
	 *	지정한 카테고리 코드(code)를 기준으로 해당 카테고리에서 부모방향으로 이동하여 상위 계층 카테고리를 모두 조회합니다.
	 *<p>
	 * 해당 메서드로 시작 카테고리 코드(cod)에서 시작해서 내 부모, 조부모 등 상위계층으로 이동하여 루트까지의 상위 카테고리를 가져오는 구조입니다.
	 * 조회 결과를 code 기준으로 오름차순으로 정렬됩니다.
	 *
	 * @param code		상위 계층 조회를 시작할 하위 카테고리 코드
	 * @return	코드 기준으로 정렬된 상위 계층의 {@link Category} 리스트
	 */
	public List<Category> findAncestorCategoriesByCode(String code ) {
		String sql = "SELECT name, code " +
								"FROM ledger_category " +
								"START WITH code = ? " +
								"CONNECT BY PRIOR parent_code = code " +
								"ORDER BY code";

		return jdbcTemplate.query(
				sql,
				(ResultSet rs, int row) ->
						Category.builder()
								.name(rs.getString("name"))
								.code(rs.getString("code"))
								.build(),
				code
		);
	}
}
