package com.moneymanager.dao.member;

import com.moneymanager.domain.member.MemberToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;

/**
 * <p>
 * 패키지이름    : com.moneymanager.dao.member<br>
 * 파일이름       : MemberTokenDao<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 12.<br>
 * 설명              : 회원 토큰 데이터를 조작하는 클래스
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
 * 		 	  <td>25. 11. 12.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Repository
public class MemberTokenDao {

	private static final String TABLE = "tb_member_token";

	private final JdbcTemplate jdbcTemplate;

	public MemberTokenDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 * 토큰 정보를 데이터베이스에 저장합니다.
	 * <p>
	 *     로그인 성공 시 생성된 {@link MemberToken} 객체의 정보를 <code>tb_member_token</code> 테이블에 INSERT 합니다.
	 * </p>
	 *
	 * @param token		저장할 토큰 정보
	 */
	public void saveToken(MemberToken token) {
		String query = String.format(
				"INSERT INTO %s (id, access_token, refresh_token, access_expire_at, refresh_expire_at) " +
						"VALUES((SELECT id FROM tb_member WHERE username=?), ?, ?, ?, ?)",
				TABLE);

		jdbcTemplate.update(query,
				token.getMember().getUserName(),
				token.getAccessToken(), token.getRefreshToken(),
				token.getAccessExpireAt(), token.getRefreshExpireAt()
		);
	}


	/**
	 * 회원 아이디로 해당하는 Refresh 토큰 정보를 데이터베이스에서 조회합니다.
	 * <p>
	 *     회원 아이디에 해당한하는 Refresh 토큰이 없다면 <code>null</code>이 반환됩니다.
	 * </p>
	 *
	 * @param username		회원 아이디
	 * @return Refresh 토큰 정보
	 */
	public String findRefreshToken(String username) {
		String query = String.format("SELECT refresh_token FROM %s WHERE username = ?", TABLE);

		return jdbcTemplate.queryForObject(
				query,
				(ResultSet rs, int row) -> rs.getString("refresh_token"),
				username
		);
	}


	/**
	 * 엑세스 토큰(access_token) 필드를 갱신합니다.
	 *
	 * @param token	갱신할 토큰 객체
	 */
	public void updateAccessToken(MemberToken token) {
		String query = String.format("UPDATE SET access_token = ?, access_expire_at = ? FROM %s WHERE id = (SELECT id FROM tb_member WHERE username = ?)", TABLE);

		jdbcTemplate.update(
				query,
				token.getAccessToken(), token.getAccessExpireAt(), token.getMember().getUserName()
		);
	}


	/**
	 * 저장된 토큰 필드를 모두 Null로 갱신합니다.
	 *
	 * @param username	토큰을 초기화할 아이디
	 */
	public void updateTokenIsNull(String username) {
		String query = "UPDATE SET access_token IS NULL, refresh_token IS NULL, access_expire_at IS NULL, refresh_expire_at IS NULL " +
				"FROM tb_member_token " +
				"WHERE id = (SELECT id FROM tb_member WHERE username = ?)";

		jdbcTemplate.update(
				query,
				username
		);
	}
}
