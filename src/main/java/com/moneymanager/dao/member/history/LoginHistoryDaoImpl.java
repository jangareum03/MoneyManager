package com.moneymanager.dao.member.history;

import com.moneymanager.dao.HistoryDao;
import com.moneymanager.domain.member.LoginLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dao.member.history<br>
 *  * 파일이름       : LoginHistoryDaoImpl<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 로그인 기록을 조작하는 클래스
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
public class LoginHistoryDaoImpl implements HistoryDao<LoginLog, Long> {


	private final JdbcTemplate jdbcTemplate;

	public LoginHistoryDaoImpl(DataSource dataSource ) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	/**
	 * 서비스 로그인을 시도한 내역을 저장합니다.
	 *
	 * @param log	로그정보
	 * @return 추가한 로그인 내역
	 */
	@Override
	public LoginLog saveHistory( LoginLog log ) {
		String sql = "INSERT INTO tb_login_logs " +
																"VALUES(seq_loginLogs.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";


		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
						new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement stmt = con.prepareStatement(sql, new String[]{"id"});

								stmt.setString(1, log.getMemberId());
								stmt.setString(2, log.getUserName());
								stmt.setString(3, log.getType());
								stmt.setString(4, String.valueOf(log.getSuccess()));
								stmt.setString(5, log.getBrowser());
								stmt.setString(6, log.getIp());
								stmt.setString(7, log.getFailureReason());

								return stmt;
							}
						}, keyHolder
		);

		try{
			Long historyId = keyHolder.getKey().longValue();

			return findHistory(historyId);
		}catch( NullPointerException e ) {
			throw new RuntimeException();
		}

	}



	/**
	 *	로그번호에 해당하는 로그인 내역을 조회하는 메서드
	 *
	 * @param id	로그번호
	 * @return	로그번호에 해당하는 로그인 내역
	 */
	@Override
	public LoginLog findHistory( Long id ) {
		String sql = "SELECT * FROM tb_login_logs WHERE id = ?";

		return jdbcTemplate.queryForObject( sql, (ResultSet rs, int row) -> {
			return LoginLog.builder().id(rs.getLong("id")).userName(rs.getString("username"))
							.browser(rs.getString("browser")).ip(rs.getString("ip"))
							.accessAt(rs.getTimestamp("access_at"))
							.success(rs.getString("success").charAt(0)).failureReason(rs.getString("failure_reason"))
							.build();
		}, id);
	}
}
