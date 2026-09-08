package com.moneymanager.member.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Date;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : MemberTokenRepository<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 12<br>
 * 설명              : 회원이 로그인에 필요한 데이터를 조작하는 클래스
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
 * 		 	  <td>26. 8. 12</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Repository
public class MemberTokenRepository {

	private final JdbcTemplate jdbcTemplate;

	public MemberTokenRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void saveToken(String memberId, String refreshToken, Date expiration) {
		String query = """
					INSERT INTO member_token (member_id, refresh_token, refresh_expire_at)
						VALUES (?, ?, ?)
				""";

		if(exists(memberId)) {
			update(memberId, refreshToken, expiration);
			return;
		}

		jdbcTemplate.update(
				query,
				memberId,
				refreshToken,
				expiration
		);
	}

	private void update(String memberId, String refreshToken, Date expiration) {
		String query = """
						UPDATE member_token
							SET refresh_token=?, refresh_expire_at=?, updated_at=SYSDATE
							WHERE member_id=?
					""";

		jdbcTemplate.update(
				query,
				refreshToken,
				expiration,
				memberId
		);
	}

	private boolean exists(String memberId) {
		String query = """
					SELECT COUNT(*)
					FROM member_token
					WHERE member_id = ?
		""";

		Integer rows = jdbcTemplate.queryForObject(
				query,
				Integer.class,
				memberId
		);

		return rows > 0;
	}

}