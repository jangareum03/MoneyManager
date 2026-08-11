package com.moneymanager.member.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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

	public void saveToken(String memberId, String accessToken, String refreshToken) {
		String query = """
					INSERT INTO member_token (id, access_token, refresh_token, access_expire_at, refresh_expire_at)
						VALUES (?, ?, ?, ?, ?)
				""";

		jdbcTemplate.update(
				query,
				memberId,
				accessToken,
				refreshToken
		);
	}

}