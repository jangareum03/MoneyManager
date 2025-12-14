package com.moneymanager.dao.member;

import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.MemberInfo;
import com.moneymanager.domain.member.enums.MemberGender;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * <p>
 * 패키지이름    : com.moneymanager.dao.member<br>
 * 파일이름       : MemberInfoDaoImpl<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 5.<br>
 * 설명              : 회원상세 데이터를 조작하는 클래스
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
 * 		 	  <td>25. 8. 5.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Repository
public class MemberInfoDaoImpl {
	private static final String TABLE = "tb_member_info";

	private final JdbcTemplate jdbcTemplate;

	public MemberInfoDaoImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	/**
	 * 회원 상세정보를 데이터베이스에 저장합니다.
	 *
	 * <p>
	 * 회원가입 시 전달받은 {@link Member} 객체의 정보를 <code>tb_member_info</code> 테이블에 INSERT 합니다.
	 * </p>
	 *
	 * @param member 저장할 회원 상세정보
	 * @return 저장이 성공하면 {@code true}, 실패하면 {@code false}
	 */
	public boolean saveMemberInfo( Member member ) {
		String query = String.format("INSERT INTO %s (id, gender) VALUES(?, ?)", TABLE);

		return jdbcTemplate.update(query, member.getId(), member.getDetail().getGender()) == 1;
	}


	/**
	 * 회원의 이미지 업로드 가능한 개수를 조회합니다.
	 * <p>
	 *     회원이 가계부 사진을 업로드할 때, 몇 장까지 저장할 수 있는지 확인할 수 있습니다.
	 * </p>
	 *
	 * @param memberId		업로드 가능한 이미지 정보를 조회할 회원 ID
	 * @return	회원이 업로드할 수 있는 이미지 최대 개수
	 */
	public Integer findImageLimit( String memberId ) {
		String sql = "SELECT 	image_limit " +
							"FROM 		member_info " +
							"WHERE 	id = ?";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class,
				memberId
		);
	}


	/**
	 * 회원번호(ID)에 해당하는 회원의 상세 정보를 데이터베이스에서 조회합니다.
	 * <p>
	 * 조회 컬럼은 성별(gender), 프로필명(profile), 연속출석일자(consecutive_days), 마지막 접속일(login_at)이며,
	 * {@link MemberInfo} 객체로 매핑 후 반환됩니다.
	 * </p>
	 * <p>
	 *     주로 회원 상세 조회나 회원 관련 기본 정보가 필요할 때 사용됩니다.
	 * </p>
	 *
	 * @param id 회원의 고유 식별자(PK)
	 * @throws org.springframework.dao.EmptyResultDataAccessException 조회 결과가 없을 경우 발생
	 * @return 해당 ID에 매칭되는 회원 상세정보 객체
	 */
	public MemberInfo findMemberInfoById( String id )  {
		String query = String.format("SELECT gender, profile, consecutive_days, login_at FROM %s WHERE id = ?", TABLE);

		return jdbcTemplate.queryForObject(
				query,
				(rs, rowNum) ->
						MemberInfo.builder()
								.gender(MemberGender.match(rs.getString("gender").charAt(0)))
								.profile(rs.getString("profile"))
								.consecutiveDays(rs.getLong("consecutive_days"))
								.loginAt(rs.getTimestamp("login_at").toLocalDateTime())
								.build(),
				id
		);
	}


	/**
	 * 회원번호(ID)에 해당하는 회원의 프로필 이미지명(profile)을 데이터베이스에서 조회합니다. <br>
	 * <p>
	 * 조회 컬럼은 프로필(profile)이며, 프로필은  임의의 문자열로 변경 후 서버에 저장된 이미지명과 이미지 확장자를 함께 반환됩니다. <br>
	 * 주로 회원번호만 알고 있는 상태에서 프로필 이미지가 필요할 때 사용됩니다.
	 * </p>
	 *
	 * @param id 회원 고유 식별자(PK)
	 * @throws EmptyResultDataAccessException 조회 결과가 없을 경우 발생
	 * @return 해당 ID에 매칭되는 회원의 프로필 정보
	 */
	public String findProfileImageNameById( String id ) {
		String query = String.format("SELECT profile FROM %s WHERE id = ?", TABLE);

		return jdbcTemplate.queryForObject( query, String.class, id );
	}


	/**
	 * 회원번호(ID)에 해당하는 회원의 포인트(point)를 데이터베이스에서 조회합니다. <br>
	 * <p>
	 * 조회 컬럼은 포인트(point)이며, 그대로 반환됩니다.. <br>
	 * 주로 회원번호만 알고 있는 상태에서 포인트가 필요할 때 사용됩니다.
	 * </p>
	 *
	 * @param id 회원 고유 식별자(PK)
	 * @throws EmptyResultDataAccessException 조회 결과가 없을 경우 발생
	 * @return 해당 ID에 매칭되는 회원의 포인트
	 */
	public Long findPointByMemberId( String id ) {
		String query = String.format("SELECT point FROM %s WHERE id = ?", TABLE);

		return jdbcTemplate.queryForObject(query, Long.class, id );
	}


	/**
	 * 회원 아이디(username)에 해당하는 마지막 접속일(login_at)을 데이터베이스에서 조회합니다.
	 * <p>
	 *     조회 컬럼은 마지막 접속일(login_at)이며 {@link Timestamp}에서 {@link LocalDateTime}으로 형변환 후 반환됩니다.
	 * </p>
	 *
	 * @param username		회원 아이디
	 * @return	입력한 아이디에 해당하는 마지막 접속일
	 * @throws org.springframework.dao.EmptyResultDataAccessException	조회 결과가 없을 경우 발생
	 * @throws NullPointerException	날짜(datetime)이 null인 경우 발생
	 */
	public LocalDateTime findLastLoginDateByUserName(String username ) {
		String query = String.format("SELECT login_at FROM %s WHERE id = ?", TABLE);

		return Timestamp.valueOf(jdbcTemplate.queryForObject(query, LocalDateTime.class, username )).toLocalDateTime();
	}


	/**
	 * 회원의 성별(gender)를 데이터베이스에 수정합니다.
	 *
	 * <p>
	 * 전달받은 {@link Member} 객체의 회원 식별번호(id) 기준으로 <code>tb_member_info</code> 테이블에서 성별을 UPDATE 합니다.
	 * </p>
	 *
	 * @param member 성별 변경할 회원정보
	 * @return 성별 수정이 성공하면 {@code true}, 실패하면 {@code false}
	 */
	public boolean updateGender( Member member ) {
		String query = String.format("UPDATE %s SET gender = ? WHERE id = ? AND gender != ?", TABLE);

		return jdbcTemplate.update( query, member.getDetail().getGender(), member.getId(), member.getDetail().getGender() ) == 1;
	}


	/**
	 * 회원의 프로필(profile)을 데이터베이스에 수정합니다.
	 *
	 * <p>
	 * 전달받은 {@link Member} 객체의 회원 식별번호(id) 기준으로 <code>tb_member_info</code> 테이블에서 프로필을 UPDATE 합니다.
	 * </p>
	 *
	 * @param member 프로필 변경할 회원정보
	 * @return 프로필 수정이 성공하면 {@code true}, 실패하면 {@code false}
	 */
	public boolean updateProfile( Member member ) {
		String query = String.format("UPDATE %s SET profile = ? WHERE id = ?", TABLE);

		return jdbcTemplate.update( query, member.getDetail().getProfile(), member.getId() ) == 1;
	}


	/**
	 * 회원의 포인트(point)를 데이터베이스에 수정합니다.
	 * <p>
	 *     전달받은 {@link MemberInfo}객체의 회원 식별번호(id) 기준으로 <code>tb_member_info</code>테이블에서 포인트를 UPDATE 합니다.
	 * </p>
	 *
	 * @param member		포인트 변경할 회원정보
	 * @return 포인트 수정이 성공하면 수정된 포인트, 실패하면 -1L
	 */
	public Long updatePointAndReturn( Member member ) {
		String query = String.format("UPDATE %s SET point = point + ? WHERE id = ?", TABLE);

		int updateRow = jdbcTemplate.update(query, member.getDetail().getPoint(), member.getId());
		if( updateRow == 1 ) {
			query = String.format("SELECT point FROM %s WHERE id = ?", TABLE);

			return	jdbcTemplate.queryForObject( query, Long.class, member.getId() );
		}

		return -1L;
	}


	/**
	 * 회원의 접속일(login_at)을 데이터베이스에 수정합니다.
	 * <p>
	 *     전달받은 {@link MemberInfo}객체의 회원 식별번호(id) 기준으로 <code>tb_member_info</code>테이블에서 접속일을 UPDATE 합니다.
	 * </p>
	 *
	 * @param member		접속일 변경할 회원정보
	 * @return 접속일 수정이 성공하면 {@code true}, 실패하면 {@code false}
	 */
	public boolean updateLoginDate( Member member ) {
		String query = String.format("UPDATE %s SET login_at = ? WHERE id = ?", TABLE);

		return jdbcTemplate.update(query, member.getDetail().getLoginAt(), member.getId()) == 1;
	}

}
