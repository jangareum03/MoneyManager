package com.areum.moneymanager.dao.member.history;

import com.areum.moneymanager.dao.HistoryDao;
import com.areum.moneymanager.entity.LoginLog;
import com.areum.moneymanager.enums.ErrorCode;
import com.areum.moneymanager.exception.ErrorException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;


/**
 * 회원 로그인의 내역을 처리하는 클래스</br>
 * 회원 로그인을 조회, 수정, 삭제 등의 메서드 구현
 *
 * @version 1.0
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
			throw new ErrorException(ErrorCode.DB_PK_FOUND);
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
