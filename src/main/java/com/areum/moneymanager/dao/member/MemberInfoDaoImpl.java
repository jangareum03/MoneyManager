package com.areum.moneymanager.dao.member;

import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.MemberInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 *	회원 정보를 삽입, 삭제, 변경, 조회하기 위한 메서드를 제공하는 클래스
 *
 * @version 1.0
 */
@Repository
public class MemberInfoDaoImpl implements MemberInfoDao {

	private final JdbcTemplate jdbcTemplate;
	private Logger logger = LogManager.getLogger(this);

	public MemberInfoDaoImpl(DataSource dataSource ) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}



	/**
	 *  회원 테이블에 데이터를 추가하는 메서드
	 *
	 * @param member	사용자 식별번호와 회원타입
	 * @return	데이터 추가 성공하면 true, 실패하면 false
	 */
	@Override
	public boolean saveMember( Member member ) {
		String sql = "INSERT INTO tb_member(id, type, role, username, password, name, nickname, email) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

		int rowsNum = jdbcTemplate.update( sql,
						member.getId(), member.getType(), member.getRole(), member.getUserName(),
						member.getPassword(), member.getName(), member.getNickName(), member.getEmail()
		);

		return rowsNum == 1;
	}



	/**
	 * 회원번호에 해당하는 회원정보를 조회합니다.
	 *
	 * @param id		회원번호
	 * @return		회원 기본정보
	 */
	@Override
	public Member findMemberById( String id ) {
		String query = "SELECT type, name, nickname, email, created_at " +
																"FROM tb_member " +
																"WHERE id = ?";

		return jdbcTemplate.queryForObject(
						query,
						(rs,rowNum) ->
								Member.builder()
												.type(rs.getString("type")).name(rs.getString("name"))
												.nickName(rs.getString("nickname")).email(rs.getString("email")).createdAt(rs.getTimestamp("created_at"))
												.build()
						,
					id
		);
	}


	/**
	 * 회원 기본정보와 상세정보를 저장합니다.
	 *
	 * @param memberInfo		저장할 회원 정보
	 * @return	저장 완료하면 true, 실패하면 false
	 */
	@Override
	public boolean saveMemberInfo( MemberInfo memberInfo ) {

		if( saveMember(memberInfo.getMember()) ) {
			String sql = "INSERT INTO tb_member_info(id, gender) VALUES(?, ?)";

			return jdbcTemplate.update(
							sql,
							memberInfo.getMember().getId(), memberInfo.getGender()
			) == 1;
		}

		return false;
	}



	/**
	 * 회원번호에 해당하는 회원 상세정보를 조회합니다.
	 *
	 * @param id		회원번호
	 * @return	회원상세정보
	 */
	@Override
	public MemberInfo findMemberInfoById( String id )  {
		String query = "SELECT gender, profile, consecutive_days, login_at " +
																	"FROM tb_member_info " +
																	"WHERE id = ?";

		return jdbcTemplate.queryForObject(
						query,
						(rs, rowNum) ->
										MemberInfo.builder().gender(rs.getString("gender").charAt(0)).profile(rs.getString("profile"))
														.consecutiveDays(rs.getLong("consecutive_days")).loginAt(rs.getTimestamp("login_at"))
														.build(),
						id
		);
	}


	/**
	 * 인증을 성공한 아이디에 해당하는 회원정보를 조회합니다.
	 * @param username	아이디
	 * @return	인증 관련 회원정보, 없으면 null
	 */
	@Override
	public Member findAuthMemberByUsername( String username ) {
		String sql = "SELECT username, password, role " +
													"FROM tb_member " +
													"WHERE username = ?";

		try{
			return jdbcTemplate.queryForObject(
							sql,
							(rs, rowNum) ->
											Member.builder()
															.userName(rs.getString("username")).password(rs.getString("password")).role(rs.getString("role"))
															.build(),
							username
			);
		}catch ( EmptyResultDataAccessException e ) {
			return null;
		}
	}



	/**
	 * 아이디에 해당하는 로그인에 필요한 회원정보를 조회합니다.
	 *
	 * @param username		아이디
	 * @return 아이디가 있으면 회원정보, 없으면 null
	 */
	@Override
	public Member findLoginMemberByUsername( String username ) {
		String sql = "SELECT id, status, nickname, deleted_at " +
														"FROM tb_member " +
														"WHERE username = ?";

		try{
			return jdbcTemplate.queryForObject(
							sql,
							(rs, rowNum) ->
											Member.builder().id(rs.getString("id")).status(rs.getString("status"))
															.nickName(rs.getString("nickname")).deletedAt(rs.getTimestamp("deleted_at")).build(),
							username
			);
		}catch ( EmptyResultDataAccessException e ) {
			return null;
		}
	}


	/**
	 *	아이디에 해당하는 회원번호, 회원상태, 탈퇴일을 조회합니다.<br>
	 * 탈퇴하지 않은 회원은 탈퇴일은 null입니다.
	 *
	 * @param 	username		아이디
	 * @return	비밀번호 찾기에 필요한 회원정보
	 */
	@Override
	public Member findStatusByUsername( String username ) {
		String sql = "SELECT id, status, deleted_at " +
													"FROM tb_member " +
													"WHERE username = ?";

		return jdbcTemplate.queryForObject( sql,
						(rs, rowNum) ->
										Member.builder().id( rs.getString("id") ).status( rs.getString("status") ).deletedAt(rs.getTimestamp("deleted_at")).build()
						, username
		);
	}


	/**
	 * 일부분의 회원번호에 해당하는 가장 최근에 생성된 회원번호를 조회합니다.
	 *
	 * @param memberId		회원번호 일부분
	 * @return 최근에 생성된 회원번호, 없으면 자기자신
	 */
	@Override
	public String findMaxMemberIdByLike( String memberId ) {
		String sql = "SELECT MAX(id) " +
														"FROM tb_member " +
														"WHERE id LIKE ?";

		return jdbcTemplate.queryForObject( sql, String.class, memberId);
	}





	/**
	 *  아이디의 개수를 조회합니다.
	 *
	 * @param username	아이디
	 * @return 아이디 개수
	 */
	@Override
	public int countByUsernameEquals( String username ) {
		String sql = "SELECT COUNT(*) " +
														"FROM tb_member " +
														"WHERE username = ?";

		return jdbcTemplate.queryForObject( sql, Integer.class, username );
	}



	/**
	 * 회원번호에 해당하는 아이디를 조회합니다. <br>
	 *
	 * @param memberId	회원번호
	 * @return 아이디가 있으면 아이디, 없으면 null
	 */
	@Override
	public String findUsernameByMemberId( String memberId ) {
		String sql = "SELECT username " +
													"FROM tb_member " +
													"WHERE id = ?";

		try{
			return jdbcTemplate.queryForObject( sql, String.class, memberId );
		}catch ( EmptyResultDataAccessException e ) {
			logger.debug("{} 회원번호가 존재하지 않아 null 반환합니다.");
			return null;
		}
	}


	/**
	 * 이름과 이메일에 해당하는 아이디를 조회합니다.
	 *
	 * @param member 회원정보
	 * @return 회원정보가 있으면 아이디, 없으면 null
	 */
	@Override
	public String findUsernameByNameAndBirth(Member member) {
		String sql = "SELECT username " +
				"FROM tb_member " +
				"WHERE name = ? AND birthdate= ?";

		try{
			return jdbcTemplate.queryForObject( sql, String.class, member.getName(), member.getBirthDate() );
		}catch ( EmptyResultDataAccessException e ) {
			return null;
		}
	}


	/**
	 * 아이디에 해당하는 비밀번호를 조회합니다. <br>
	 *
	 * @param username 		아이디
	 * @return 아이디가 있으면 비밀번호, 없으면 null
	 */
	@Override
	public String findPasswordByUsername( String username ) {
		String sql = "SELECT password " +
													"FROM tb_member " +
													"WHERE username = ?";

		try{
			return jdbcTemplate.queryForObject( sql, (ResultSet rs, int row) -> rs.getString("password") , username );
		}catch ( EmptyResultDataAccessException e ) {
			logger.debug("{} 아이디가 존재하지 않아 비밀번호를 null 로 반환합니다.");
			return null;
		}
	}



	/**
	 * 닉네임의 개수를 조회합니다.
	 *
	 * @param nickName	닉네임
	 * @return 닉네임 개수
	 */
	@Override
	public int countByNickNameEquals( String nickName ) {
		String sql = "SELECT COUNT(*) " +
														"FROM tb_member " +
														"WHERE nickname = ?";

		return jdbcTemplate.queryForObject( sql, Integer.class, nickName );
	}


	/**
	 * 이메일의 개수를 조회합니다.
	 *
	 * @param email 이메일
	 * @return 이메일 개수
	 */
	@Override
	public int countByEmailEquals( String email ) {
		String sql = "SELECT COUNT(*) " +
														"FROM tb_member " +
														"WHERE email = ?";

		return jdbcTemplate.queryForObject( sql, Integer.class, email );
	}



	/**
	 * 아이디와 이름에 해당하는 이메일을 조회합니다.<br>
	 *
	 * @param member	 회원정보
	 * @return	회원 정보가 있으면 이메일, 없으면 null
	 */
	@Override
	public String findEmailByIdAndName( Member member ) {
		String sql = "SELECT email " +
													"FROM tb_member " +
													"WHERE username = ? AND name = ?";

		try{
			return jdbcTemplate.queryForObject( sql, String.class, member.getId(), member.getName() );
		}catch ( EmptyResultDataAccessException e ) {
			return null;
		}
	}


	/**
	 * 회원번호에 해당하는 이름과 이메일을 반환합니다.
	 *
	 * @param memberId 	회원번호
	 * @return	회원번호가 있으면 이메일과 이름, 없으면 null
	 */
	@Override
	public Member findEmailAndNameByMemberId( String memberId ) {
		String sql = "SELECT name, email " +
														"FROM tb_member " +
														"WHERE id = ?";

		try{
			return jdbcTemplate.queryForObject( sql, (ResultSet rs, int row) -> {
				return Member.builder().name(rs.getString("name")).email(rs.getString("email")).build();
			}, memberId );
		}catch ( EmptyResultDataAccessException e ) {
			return null;
		}
	}



	/**
	 * 아이디의 최근 접속일을 조회합니다.
	 *
	 * @param username	아이디
	 * @return 최근 접속일
	 */
	@Override
	public Timestamp findLastLoginDateByUserName( String username ) {
		String sql = "SELECT login_at " +
													"FROM tb_member_info " +
													"WHERE id = (SELECT id FROM tb_member WHERE username = ?)";

		return jdbcTemplate.queryForObject( sql, Timestamp.class, username );
	}



	/**
	 * 회원의 현재 포인트를 조회합니다.
	 *
	 * @param memberId		회원 식별번호
	 * @return	현제 포인트
	 */
	public Long findPointByMemberId( String memberId ) {
		String query = "SELECT point " +
									"FROM tb_member_info " +
									"WHERE id = ?";

		return jdbcTemplate.queryForObject(query, Long.class, memberId );
	}



	@Override
	public String findProfileImageNameById( String id ) {
		String query = "SELECT profile " +
									"FROM tb_member_info " +
									"WHERE id = ?";

		return jdbcTemplate.queryForObject( query, String.class, id );
	}


	/**
	 *	회원 상태를 변경합니다.
	 *
	 * @param member		 회원정보
	 */
	@Override
	public void updateStatus( Member member ) {
		String sql = "UPDATE tb_member SET status = ? WHERE id = ?";
		jdbcTemplate.update( sql, member.getStatus(), member.getId() );
	}




	/**
	 * 입력한 비밀번호가 기존 비밀번호와 다르면 비밀번호를 변경합니다.
	 *
	 * @param username 						아이디
	 * @param password 변경할 비밀번호
	 * @return 비밀번호 변경이 성공하면 true, 아니면 false
	 */
	@Override
	public boolean updatePassword( String username, String password ) {
		String sql = "UPDATE tb_member SET password = ? WHERE username = ?";
		return jdbcTemplate.update( sql, password, username ) == 1;
	}



	/**
	 * 입력한 이름이 기존 이름과 다르면 이름을 변경합니다.
	 *
	 * @param memberId				회원번호
	 * @param name							변경할 이름
	 * @return 이름 변경이 성공하면 true, 아니면 false
	 */
	@Override
	public boolean updateName( String memberId, String name ) {
		String sql = "UPDATE tb_member SET name = ? WHERE id = ? AND name != ?";
		return jdbcTemplate.update(sql, name, memberId, name) == 1;
	}



	/**
	 * 	입력한 성별이 기존 성별과 다르면 성별을 변경합니다.
	 *
	 * @param memberId					회원번호
	 * @param gender							변경할 성별
	 * @return 성별 변경이 성공하면 true, 아니면 false
	 */
	@Override
	public boolean updateGender( String memberId, char gender ) {
		String sql = "UPDATE tb_member_info SET gender = ? WHERE id = ? AND gender != ?";
		return jdbcTemplate.update( sql, gender, memberId, gender ) == 1;
	}



	/**
	 * 입력한 이메일로 변경하는 메서드
	 *
	 * @param username				 아이디
	 * @param email									변경할 이메일
	 * @return 이메일 변경이 성공하면 true, 아니면 false
	 */
	@Override
	public boolean updateEmail( String username, String email ) {
		String sql = "UPDATE tb_member SET email = ? WHERE username = ? AND email != ?";
		return jdbcTemplate.update( sql, email, username, email ) == 1;
	}



	/**
	 * 선택한 프로필 이미지 이름으로 변경하는 메서드
	 *
	 * @param id									회원 번호
	 * @param imageName		변경할 이미지 이름
	 * @return 프로필 변경이 성공하면 true, 아니면 false
	 */
	@Override
	public boolean updateProfile( String id, String imageName ) {
		String query = "UPDATE tb_member_info SET profile = ? WHERE id = ?";
		return jdbcTemplate.update( query, imageName, id ) == 1;
	}



	/**
	 * 아이디에 해당하는 회원 탈퇴일을 오늘로 변경합니다. <br>
	 *
	 * @param username 아이디
	 * @return 탈퇴일을 오늘로 변경하면 true, 아니면 false
	 */
	@Override
	public boolean updateResignDate( String username ) {
		String sql = "UPDATE tb_member SET deleted_at = SYSDATE WHERE username = ?";
		return jdbcTemplate.update(sql, username) == 1;
	}



	/**
	 * 회원번호에 해당하는 마지막 접속일을 변경합니다.
	 *
	 * @param memberInfo	회원 상세정보
	 */
	@Override
	public void updateLastLoginDate( MemberInfo memberInfo ) {
		String sql = "UPDATE tb_member_info SET login_at = ? WHERE id = ?";
		jdbcTemplate.update(sql, memberInfo.getLoginAt(), memberInfo.getMember().getId());
	}


	/**
	 * 포인트 변경 후 회원의 포인트를 조회합니다. <br>
	 * 회원의 포인트를 변경하지 못 한다면 포인트가 null로 조회됩니다.
	 *
	 * @param memberId		회원번호
	 * @param point					추가할 포인트 값
	 * @return	변경된 포인트, 변경이 되지 않으면 null
	 */
	public Long updatePointAndReturn( String memberId, int point ) {
		String updateSql = "UPDATE tb_member_info SET point = point + ? WHERE id = ?";

		int updateRow = jdbcTemplate.update(updateSql, point, memberId);
		if( updateRow == 1 ) {
			String selectSql = "SELECT point FROM tb_member_info WHERE id = ?";

			return	jdbcTemplate.queryForObject( selectSql, Long.class, memberId );
		}

		return null;
	}

}
