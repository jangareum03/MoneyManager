package com.areum.moneymanager.dao.member.history;

import com.areum.moneymanager.dao.HistoryDao;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.MemberLog;
import com.areum.moneymanager.exception.code.ErrorCode;
import com.areum.moneymanager.exception.ErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dao.member.history<br>
 *  * 파일이름       : MemberHistoryDaoImpl<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 정보 기록을 조작하는 클래스
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
public class MemberHistoryDaoImpl implements HistoryDao<MemberLog, Long> {

	private final Logger logger = LogManager.getLogger(this);
	private final JdbcTemplate jdbcTemplate;


	public MemberHistoryDaoImpl(DataSource dataSource ) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}



	/**
	 * 회원 수정내역을 저장합니다.
	 *
	 * @param updateHistory	수정된 회원정보
	 */
	@Override
	public MemberLog saveHistory(MemberLog updateHistory ) {
		String sql = "INSERT INTO tb_member_logs VALUES(seq_memberLogs.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
		List<Object> params = new ArrayList<>();

		params.add(updateHistory.getMember().getId());
		params.add(String.valueOf(updateHistory.getSuccess()).toUpperCase());
		params.add(updateHistory.getType());
		params.add(updateHistory.getItem());
		params.add(updateHistory.getBeforeInfo());
		params.add(updateHistory.getAfterInfo());
		params.add(updateHistory.getFailureReason());

		if( updateHistory.getItem().equals("프로필") ) {
			sql = "INSERT INTO tb_member_logs VALUES(seq_memberLogs.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";

			params.add(updateHistory.getUpdatedAt());
		}


		KeyHolder keyHolder = new GeneratedKeyHolder();

		String query = sql;
		jdbcTemplate.update(
						new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement stmt = con.prepareStatement(query, new String[]{"id"});

								for( int i=0; i<params.size(); i++ ) {
									stmt.setObject(i+1, params.get(i));
								}

								return stmt;
							}
						}, keyHolder
		);

		try{
			Long historyId = Objects.requireNonNull(keyHolder.getKey()).longValue();

			return findHistory(historyId);
		}catch( NullPointerException e ){
			throw new ErrorException(ErrorCode.DB_PK_FOUND);
		}
	}



	/**
	 *	수정내역 번호에 해당하는 수정내역을 조회하는 메서드
	 *
	 * @param id	회원정보 수정내역 번호
	 * @return	수정내역 번호에 해당하는 수정내역
	 */
	@Override
	public MemberLog findHistory(Long id) {
		String sql = "SELECT * FROM tb_member_logs WHERE id = ?";

		return jdbcTemplate.queryForObject( sql, (ResultSet rs, int row) -> {
			return MemberLog.builder().id(rs.getLong("id"))
							.member(Member.builder().id(rs.getString("member_id")).build())
							.success(rs.getString("success").charAt(0)).type(rs.getString("type"))
							.item(rs.getString("item"))
							.beforeInfo(rs.getString("before_info")).afterInfo(rs.getString("after_info"))
							.failureReason(rs.getString("failure_reason")).updatedAt(rs.getTimestamp("updated_at"))
							.build();
		}, id );
	}



	/**
	 * 회원번호와 수정유형에 해당하는 최근 수정내역을 조회합니다.
	 *
	 * @param memberId		회원번호
	 * @param item						수정항목
	 * @return	회원번호가 있으면 최근 수정내역, 없으면 null
	 */
	public MemberLog findUpdateHistoryByMemberIdAndItem(String memberId, String item ) {
		String sql = "SELECT * " +
								"FROM tb_member_logs	" +
								"WHERE type = 'UPDATE' AND success = 'Y' " +
									"AND member_id  = ? AND item = ? " +
								"ORDER BY updated_at DESC";

		List<MemberLog> updateHistoryList = jdbcTemplate.query(
						sql, (ResultSet rs, int row) -> {
							return MemberLog.builder().id(rs.getLong("id")).member(Member.builder().id(rs.getString("member_id")).build())
											.success(rs.getString("success").charAt(0)).type(rs.getString("type")).item(rs.getString("item"))
											.beforeInfo(rs.getString("before_info")).afterInfo(rs.getString("after_info"))
											.failureReason(rs.getString("failure_reason")).updatedAt(rs.getTimestamp("updated_at"))
											.build();
						},
						memberId, item
		);

		return updateHistoryList.get(0);
	}
}
