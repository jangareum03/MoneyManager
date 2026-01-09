package com.moneymanager.repository.ledger;

import com.moneymanager.domain.ledger.entity.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.repository.ledger<br>
 * 파일이름       : CategoryRepository<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 7<br>
 * 설명              : 가계부 카테고리 데이터를 조작하는 클래스
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
 * 		 	  <td>26. 1. 7.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Repository
public class CategoryRepository {

	private final JdbcTemplate jdbcTemplate;

	public CategoryRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 *{@code ledger_category} 테이블에 존재하는 모든 정보를 조회합니다.
	 * <p>
	 *     어떤 조건으로도 구분하지 않고, 전체 카테고리 데이터를 한 번에 조회하여 {@link Category} 객체 리스트로 반환합니다.
	 *     조회 대상 컬럼은 {@code name}, {@code code}, {@code parent_code}이며, 각 행은 {@link Category} 객체로 매핑됩니다.
	 * </p>
	 *
	 * @return	전체 카테고리 정보를 담은 {@link Category} 리스트
	 */
	public List<Category> findAllCategory() {
		String sql = "SELECT name, code, parent_code" +
							"	FROM ledger_category";

		return jdbcTemplate.query(
				sql,

				(rs, row) ->
						Category.builder()
								.name(rs.getString("name"))
								.code(rs.getString("code"))
								.parentCode(rs.getString("parent_code"))
								.build()
		);
	}

}