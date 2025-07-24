package com.areum.moneymanager.dao.member;

import com.areum.moneymanager.entity.Attendance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dao.member<br>
 *  * 파일이름       : AttendanceDao<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 출석 데이터를 조작하는 클래스
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
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Repository
public class AttendanceDao {

	private final Logger logger = LogManager.getLogger(this);
	private final JdbcTemplate jdbcTemplate;


	public AttendanceDao(DataSource dataSource ) {
		this.jdbcTemplate = new JdbcTemplate( dataSource );
	}


	/**
	 * 회원의 출석날짜에 오늘날짜 저장 및 연속출석일자 변경합니다.
	 *
	 * @param memberId		회원번호
	 */
	public void saveContinuousAttend( String memberId ) {
		//출석일자 추가
		String insertSql = "INSERT INTO tb_member_attendance VALUES(seq_attendance.NEXTVAL, ?, SYSDATE)";
		jdbcTemplate.update( insertSql, memberId );

		//연속출석일 추가
		String updateSql = "UPDATE tb_member_info SET consecutive_days = consecutive_days + 1 WHERE id = ?";
		jdbcTemplate.update( updateSql, memberId );
	}




	/**
	 * 연속 출석일을 0으로 초기화합니다.
	 *
	 * @param memberId	회원번호
	 */
	public void resetContinuousDate( String memberId ) {
		String sql = "UPDATE tb_member_info SET consecutive_days = 0 WHERE id = ? AND consecutive_days != 0";
		jdbcTemplate.update( sql, memberId );
	}



	/**
	 * 회원의 출석정보에서 날짜가 있는지 확인합니다.<br>
	 * 해당 날짜로 출석이 된 상태면 true, 아니면 false 반환합니다.
	 *
	 * @param memberId	회원번호
	 * @param date			확인할 날짜
	 * @return	날짜로 출석 완료된 상태면 true, 아니면 false
	 */
	public boolean hasCheckedInDate(String memberId, LocalDate date ) {
		String query = "SELECT COUNT(*) " +
									"FROM tb_member_attendance " +
									"WHERE member_id = ? " +
										"AND TO_DATE(attendance_date) = TO_DATE(?, 'YYYY-MM-DD')";

		return jdbcTemplate.queryForObject( query, Integer.class, memberId, date.toString() ) == 1;
	}



	/**
	 * 검색 기간 내에 완료된 출석날짜를 조회합니다.
	 *
	 *
	 * @param memberId		회원번호
	 * @param start						검색 시작일
	 * @param end							검색 종료일
	 * @return	기간 내에 완료된 출석날짜
	 */
	public List<Attendance> findCompleteAttendBetweenDate( String memberId, String start, String end ) {
		String sql = "SELECT attendance_date " +
													"FROM tb_member_attendance " +
													"WHERE member_id = ? " +
														"AND attendance_date BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') " +
													"ORDER BY attendance_date";

		return jdbcTemplate.query( sql,( ResultSet rs, int row ) ->
			Attendance.builder().attendanceDate(rs.getDate("attendance_date")).build()
		, memberId, start, end );
	}
}
