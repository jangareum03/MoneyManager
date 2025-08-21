package com.moneymanager.dao.member.history;


import com.moneymanager.dao.HistoryDao;
import com.moneymanager.dto.member.response.MemberMyPageResponse;
import com.moneymanager.entity.Member;
import com.moneymanager.entity.PointHistory;
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


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dao.member.history<br>
 *  * 파일이름       : PointHistoryDaoImpl<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 포인트 기록을 조작하는 클래스
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
			throw new RuntimeException("");
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
	public MemberMyPageResponse.Point findSumPointByType(String memberId ) {
		String query = "SELECT NVL(EARM, 0) 적립, NVL(USE, 0) 사용 " +
									"FROM (" +
										"SELECT type, points " +
											"FROM tb_point_logs " +
											"WHERE member_id = ?" +
									") " +
									"PIVOT ( SUM(points) FOR type IN('EARM' as EARM, 'USE' as USE ) )";

		return jdbcTemplate.queryForObject( query, ( (rs, rowNum) -> MemberMyPageResponse.Point.builder()
				.earmPoint( rs.getLong("적립") ).usePoint( rs.getLong("사용") )
				.build()
				), memberId );
	}
}
