package com.areum.moneymanager.dao.member.history;


import com.areum.moneymanager.dao.HistoryDao;
import com.areum.moneymanager.dto.response.member.MemberResponseDTO;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.PointHistory;
import com.areum.moneymanager.enums.ErrorCode;
import com.areum.moneymanager.exception.ErrorException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;


/**
 * 회원 포인트의 수정내역을 처리하는 클래스</br>
 * 회원 포인트 내역을 조회, 수정, 삭제 등의 메서드 구현
 *
 * @version 1.0
 */
@Repository
public class PointHistoryDaoImpl implements HistoryDao<PointHistory, Long> {


	private final JdbcTemplate jdbcTemplate;

	public PointHistoryDaoImpl(DataSource dataSource ) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	/**
	 * 회원 포인트 변경내역을 저장합니다.
	 *
	 * @param pointHistory	회원포인트 정보
	 */
	@Override
	public PointHistory saveHistory( PointHistory pointHistory ) {
		String sql = "INSERT INTO tb_point_logs VALUES(seq_pointLogs.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
						new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement pstmt = con.prepareStatement(sql, new String[] {"id"});

								pstmt.setString(1, pointHistory.getMember().getId());
								pstmt.setString(2, pointHistory.getType());
								pstmt.setLong(3, pointHistory.getPoints());
								pstmt.setString(4, pointHistory.getReason());
								pstmt.setLong(5, pointHistory.getBalancePoints());

								return pstmt;
							}
						}, keyHolder
		);

		try{
			Long historyId =  keyHolder.getKey().longValue();

			return findHistory(historyId);
		}catch ( NullPointerException e ) {
			throw new ErrorException(ErrorCode.DB_PK_FOUND);
		}

	}

	@Override
	public PointHistory findHistory( Long id ) {
		String sql = "SELECT * FROM tb_point_logs WHERE id = ?";
		return jdbcTemplate.queryForObject( sql, (ResultSet rs, int row) -> PointHistory.builder()
						.id(rs.getLong("id")).member(Member.builder().id(rs.getString("member_id")).build())
						.type(rs.getString("type")).points(rs.getLong("points")).reason(rs.getString("reason"))
						.balancePoints(rs.getLong("balance_points")).usedAt(rs.getTimestamp("used_at"))
						.build(), id );
	}



	/**
	 * 회원번호의 포인트 유형별로 합계를 조회합니다.
	 *
	 * @param memberId		회원 식별번호
	 * @return	포인트 유형별 합계
	 */
	public MemberResponseDTO.Point findSumPointByType(String memberId ) {
		String query = "SELECT NVL(EARM, 0) 적립, NVL(USE, 0) 사용 " +
									"FROM (" +
										"SELECT type, points " +
											"FROM tb_point_logs " +
											"WHERE member_id = ?" +
									") " +
									"PIVOT ( SUM(points) FOR type IN('EARM' as EARM, 'USE' as USE ) )";

		return jdbcTemplate.queryForObject( query, ( (rs, rowNum) -> MemberResponseDTO.Point.builder()
				.earmPoint( rs.getLong("적립") ).usePoint( rs.getLong("사용") )
				.build()
				), memberId );
	}
}
