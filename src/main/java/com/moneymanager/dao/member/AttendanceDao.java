package com.moneymanager.dao.member;

import com.moneymanager.domain.member.Attendance;
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
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 8. 12</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>
 *		 	      [메서드 수정] findCompleteAttendBetweenDate - 매개변수 타입 String → LocalDate 변경<br>
 *		 	      [상수 추가] TABLE
 *		 	  </td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Repository
public class AttendanceDao {

	private static final String TABLE = "tb_member_attendance";
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
	 * 날짜(date)가 회원의 출석정보에 저장되어 있는지 데이터베이스에서 조회합니다.
	 * <p>
	 *     조회된 컬럼의 총 건수를 조회 후 건수가 1인지 확인합니다.
	 * </p>
	 *
	 * @param id				회원의 고유 식별자(PK)
	 * @param date			확인할 날짜
	 * @return	날짜가 있으면 true, 없으면 false
	 * @throws org.springframework.dao.EmptyResultDataAccessException	조회 결과가 없을 경우 발생
	 */
	public boolean hasCheckedInDate(String id, LocalDate date ) {
		String query = String.format(
				"SELECT COUNT(*) " +
					"FROM %s " +
					"WHERE member_id = ? " +
						"AND TO_DATE(attendance_date) = ?",
				TABLE
		);

		return jdbcTemplate.queryForObject(
				query,
				Integer.class,
				id, Date.valueOf(date)
		) == 1;
	}


	/**
	 * 시작일(start)과 종료일(end) 사이 날짜에서 회원번호(ID)에 해당하는 회원의 출석정보를 데이터베이스에서 조회합니다.
	 * <p>
	 *     조회 컬럼은 출석일(attendance_date)이며, {@link Attendance}객체로 매핑 후 반환됩니다.<br>
	 *     주로 회원의 출석정보가 필요할 때 사용됩니다.
	 * </p>
	 *
	 *
	 * @param id				회원의 고유 식별자(PK)
	 * @param start			출석 조회기간 시작일
	 * @param end			출석 조회기간 종료일
	 * @return	기간 내 해당 ID에 매칭되는 출석 완료된 {@link Attendance}객체를 담은 {@link List}
	 * @throws org.springframework.dao.EmptyResultDataAccessException	조회 결과가 없을 경우 발생
	 */
	public List<Attendance> findCompleteAttendBetweenDate( String id, LocalDate start, LocalDate end ) {
		String query = String.format(
				"SELECT attendance_date " +
					"FROM %s " +
					"WHERE member_id = ? " +
						"AND attendance_date >= ? AND attendance_date <= ? " +
					"ORDER BY attendance_date",
				TABLE
		);

		return jdbcTemplate.query(
				query,
				( ResultSet rs, int row ) ->
						Attendance.builder().attendanceDate(rs.getDate("attendance_date").toLocalDate()).build(),
				id, Date.valueOf(start),  Date.valueOf(end.plusDays(1))
		);
	}
}
