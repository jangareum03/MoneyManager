package com.moneymanager.dao.member;

import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.MemberInfo;
import com.moneymanager.domain.member.enums.MemberStatus;
import com.moneymanager.domain.member.enums.MemberType;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.time.LocalDateTime;

/**
 * <p>
 * 패키지이름    : com.moneymanager.dao.member<br>
 * 파일이름       : MemberDaoImpl<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 5.<br>
 * 설명              : 회원 데이터를 조작하는 클래스
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
public class MemberDaoImpl {

	private static final String TABLE = "tb_member";

	private final JdbcTemplate jdbcTemplate;

	public MemberDaoImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 * 회원 정보를 데이터베이스에 저장합니다.
	 *
	 * <p>
	 * 회원가입 시 전달받은 {@link Member} 객체의 정보를 <code>tb_member</code> 테이블에 INSERT 합니다.
	 * </p>
	 *
	 * @param member 저장할 회원정보
	 * @return 저장이 성공하면 {@code true}, 실패하면 {@code false}
	 */
	public boolean saveMember(Member member) {
		String query = String.format("INSERT INTO %s (id, type, role, username, password, name, nickname, email) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", TABLE);

		int rowsNum = jdbcTemplate.update(query,
				member.getId(), member.getType(), member.getRole(), member.getUserName(),
				member.getPassword(), member.getName(), member.getNickName(), member.getEmail()
		);

		return rowsNum == 1;
	}


	/**
	 * 회원번호(ID)에 해당하는 회원의 기본 정보를 데이터베이스에서 조회합니다.
	 * <p>
	 * 조회 컬럼은 회원유형(type), 이름(name), 닉네임(nickname), 이메일(email), 가입일(created_at)이며,
	 * {@link Member} 객체로 매핑 후 반환됩니다.
	 * </p>
	 * <p>
	 * 주로 회원 상세 조회나 회원 관련 기본 정보가 필요할 때 사용됩니다.
	 * </p>
	 *
	 * @param id 회원의 고유 식별자(PK)
	 * @return 해당 ID에 매칭되는 회원 기본정보 객체
	 * @throws org.springframework.dao.EmptyResultDataAccessException 조회 결과가 없을 경우 발생
	 */
	public Member findMemberById(String id) {
		String query = String.format("SELECT type, name, nickname, email, created_at FROM %s WHERE id = ?", TABLE);

		return jdbcTemplate.queryForObject(
				query,
				(rs, rowNum) ->
						Member.builder()
								.type(MemberType.valueOf(rs.getString("type"))).name(rs.getString("name"))
								.nickName(rs.getString("nickname")).email(rs.getString("email"))
								.createdAt(rs.getTimestamp("created_at").toLocalDateTime())
								.build()
				,
				id
		);
	}


	/**
	 * 회원 이름과 생년월일과 일치하는 회원의 아이디 정보를 데이터베이스에서 조회합니다.
	 * <p>
	 * 조회 컬럼은 아이디(username), 회원 상태(status), 탈퇴일(delete_at)이며,
	 * 회원상태는 {@link MemberStatus} Enum 클래스로, 탈퇴일은 {@link java.sql.Timestamp} -> {@link LocalDateTime}으로 형변환 후 반환됩니다. <br>
	 * 회원번호(ID)를 모르는 상태에서 아이디를 찾는 용도로 사용됩니다.
	 * </p>
	 *
	 * @param member 아이디 찾기에 사용되는 회원 정보
	 * @throws EmptyResultDataAccessException 조회 결과가 없을 경우 발생
	 * @return 입력한 회원정보와 일치하는 회원의 아이디 및 상태정보
	 */
	public Member findMemberByNameAndBirth(Member member) {
		String query = String.format("SELECT username, status, delete_at FROM %s WHERE name = ? AND birthdate= ?", TABLE);

		return jdbcTemplate.queryForObject(
				query,
				((rs, rowNum) -> {
					return Member.builder()
							.userName(rs.getString("username"))
							.status(MemberStatus.fromCode(rs.getString("status").charAt(0)))
							.deletedAt(rs.getTimestamp("delete_at").toLocalDateTime())
							.build();
				}),
				member.getName(), member.getBirthDate()
		);
	}


	/**
	 * 회원 아이디와 이름이 일치하는 회원의 이메일(email) 정보를 데이터베이스에서 조회합니다.
	 * <p>
	 * 조회 컬럼은 이메일(email), 회원 상태(status), 탈퇴일(delete_at)이며, 탈퇴일은 {@link java.sql.Timestamp} -> {@link LocalDateTime}으로 형변환 후 반환됩니다. <br>
	 * 회원번호(ID)를 모르는 상태에서 비밀번호를 찾는 용도로 사용됩니다.
	 * </p>
	 *
	 * @param member 비밀번호 찾기에서 입력한 회원 정보
	 * @throws EmptyResultDataAccessException 조회 결과가 없을 경우 발생
	 * @return 입력한 회원정보에 일치하는 회원 이메일
	 */
	public Member findMemberByUsernameAndName(Member member) {
		String query = String.format("SELECT email, status, delete_at FROM %s WHERE username = ? AND name = ?", TABLE);

		return jdbcTemplate.queryForObject(
				query,
				((rs, rowNum) -> {
					return Member.builder()
							.email(rs.getString("email"))
							.status(MemberStatus.fromCode(rs.getString("status").charAt(0)))
							.deletedAt(rs.getTimestamp("delete_at").toLocalDateTime())
							.build();
				}),
				member.getUserName(), member.getName()
		);
	}


	/**
	 * 로그인 시 입력한 아이디(username)에 해당하는 회원의 기본 정보를 데이터베이스에서 조회합니다.
	 * <p>
	 * 조회 컬럼은 회원 식별자(id), 아이디(username), 비밀번호(password), 권한(role), 상태(status), 실패횟수(failure_count)이며, {@link Member}객체로 매핑 후 반환됩니다.<br>
	 * 로그인 인증 처리할때 사용됩니다.
	 * </p>
	 *
	 * @param username 로그인 시도한 아이디
	 * @throws org.springframework.dao.EmptyResultDataAccessException 조회 결과가 없을 경우 발생
	 * @return 아이디에 매칭되는 회원 인증정보 객체
	 */
	public Member findAuthMemberByUsername(String username) {
		String query = String.format(
				"SELECT username, password, role, status, nickname, profile, failure_count " +
						"FROM %s tm INNER JOIN tb_member_info tmi " +
						"ON tm.id = tmi.id " +
						"WHERE username = ?", TABLE);

		return jdbcTemplate.queryForObject(
				query,
				(rs, rowNum) ->
						Member.builder()
								.role(rs.getString("role")).status(MemberStatus.fromCode( rs.getString("status").charAt(0) ))
								.userName(rs.getString("username")).password(rs.getString("password"))
								.nickName(rs.getString("nickname"))
								.memberInfo(MemberInfo.builder().profile(rs.getString("profile")).failureCount(rs.getInt("failure_count")).build())
								.build(),
				username
		);
	}


	/**
	 * 회원번호(ID)에 해당하는 회원의 아이디를 데이터베이스에서 조회합니다. <br>
	 * <p>
	 * 조회 컬럼은 아이디(username)이며 그대로 반환됩니다. <br>
	 * 주로 회원번호만 알고 있는 상태에서 아이디가 필요할 때 사용됩니다.
	 * </p>
	 *
	 * @param id 회원 고유 식별자(PK)
	 * @throws EmptyResultDataAccessException 조회 결과가 없을 경우 발생
	 * @return 해당 ID에 매칭되는 회원의 아이디
	 */
	public String findUsernameByMemberId(String id) {
		String query = String.format("SELECT username FROM %s WHERE id = ?", TABLE);

		return jdbcTemplate.queryForObject(query, String.class, id);
	}


	/**
	 * 회원 아이디(username)에 해당하는 비밀번호(password)를 데이터베이스에서 조회합니다.
	 * <p>
	 * 조회 컬럼은 비밀번호(password)이며 그대로 반환됩니다.<br>
	 * 회원번호(ID)를 모르는 상태에서 비밀번호를 찾는 용도로 사용됩니다.
	 * </p>
	 *
	 * @param username 회원 아이디
	 * @throws EmptyResultDataAccessException 조회 결과가 없을 경우 발생
	 * @return 입력한 아이디에 해당하는 암호화된 비밀번호
	 */
	public String findPasswordByUsername(String username) {
		String query = String.format("SELECT password FROM %s WHERE username = ?", TABLE);

		return jdbcTemplate.queryForObject(query, (ResultSet rs, int row) -> rs.getString("password"), username);
	}


	/**
	 * 주어진 접두사(prefix)를 포함하는 회원 식별번호(ID) 중 가장 최신 값(=가장 큰 값)을 데이터베이스에서 조회합니다.
	 * <p>
	 * 조회 컬럼은 회원 식별자(id)이며 해당 컬럼 중 최대값을 반환됩니다.<br>
	 * 신규 회원이 가입 중 고유 식별번호 중복 방지를 위해서 최신 번호가 필요할 때 사용됩니다.
	 * </p>
	 *
	 * @param prefix 조회할 식별번호의 접두사 문자열(예: "UA01")
	 * @throws EmptyResultDataAccessException 조회 결과가 없을 경우 발생
	 * @return 접두사로 시작하는 가장 최신 식별번호
	 */
	public String findLatestId(String prefix) {
		String query = String.format("SELECT MAX(id) FROM %s WHERE id LIKE ?", TABLE);

		return jdbcTemplate.queryForObject(query, String.class, Strings.concat(prefix, "%"));
	}


	/**
	 * 회원 아이디(username)와 일치하는 아이디의 개수를 데이터베이스에서 조회합니다.
	 * <p>
	 * 일치하는 아이디의 개수를 반환됩니다.<br>
	 * 동일한 아이디가 이미 존재하는지 확인이 필요할 때 사용됩니다.
	 * </p>
	 *
	 * @param username 중복 확인할 아이디
	 * @return 일치하는 아이디 개수, 없으면 0
	 */
	public int countByUsername(String username) {
		String query = String.format("SELECT COUNT(*) FROM %s WHERE username = ?", username);

		return jdbcTemplate.queryForObject(query, Integer.class, username);
	}
	/**
	 * 회원 닉네임(nickName)과 일치하는 닉네임의 개수를 데이터베이스에서 조회합니다.
	 * <p>
	 * 일치하는 닉네임의 개수를 반환됩니다.<br>
	 * 동일한 닉네임이 이미 존재하는지 확인이 필요할 때 사용됩니다.
	 * </p>
	 *
	 * @param nickName 중복 확인할 닉네임
	 * @return 일치하는 닉네임 개수, 없으면 0
	 */
	public int countByNickName(String nickName) {
		String query = String.format("SELECT COUNT(*) FROM %s WHERE nickname = ?", nickName);

		return jdbcTemplate.queryForObject(query, Integer.class, nickName);
	}


	/**
	 * 회원 이메일(email)과 일치하는 이메일의 개수를 데이터베이스에서 조회합니다.
	 * <p>
	 * 일치하는 이메일의 개수를 반환됩니다.<br>
	 * 동일한 이메일이 이미 존재하는지 확인이 필요할 때 사용됩니다.
	 * </p>
	 *
	 * @param email 중복 확인할 닉네임
	 * @return 일치하는 이메일 개수, 없으면 0
	 */
	public int countByEmail(String email) {
		String query = String.format("SELECT COUNT(*) FROM %s WHERE email = ?", email);

		return jdbcTemplate.queryForObject(query, Integer.class, email);
	}


	/**
	 * 회원 상태(status)를 데이터베이스에 수정합니다.
	 *
	 * <p>
	 * 전달받은 {@link Member} 객체의 회원 식별번호(ID)를 기준으로 <code>tb_member</code> 테이블에서 상태를 UPDATE 합니다.
	 * </p>
	 *
	 * @param member 상태 변경할 회원정보
	 * @return 상태 수정이 성공하면 {@code true}, 실패하면 {@code false}
	 */
	public boolean updateStatus(Member member) {
		String query = String.format("UPDATE %s SET status = ? WHERE id = ?", TABLE);

		return jdbcTemplate.update(query, member.getStatus(), member.getId()) == 1;
	}


	/**
	 * 회원의 비밀번호(password)를 데이터베이스에 수정합니다.
	 *
	 * <p>
	 * 전달받은 {@link Member} 객체의 회원 아이디(username) 기준으로 <code>tb_member</code> 테이블에서 비밀번호를 UPDATE 합니다.
	 * </p>
	 *
	 * @param member 비밀번호 변경할 회원정보
	 * @return 비밀번호 수정이 성공하면 {@code true}, 실패하면 {@code false}
	 */
	public boolean updatePassword(Member member) {
		String query = String.format("UPDATE %s SET password = ? WHERE username = ?", TABLE);

		return jdbcTemplate.update(query, member.getPassword(), member.getUserName()) == 1;
	}


	/**
	 * 회원의 이름(name)을 데이터베이스에 수정합니다.
	 *
	 * <p>
	 * 전달받은 {@link Member} 객체의 회원 식별번호(id)를 기준으로 <code>tb_member</code> 테이블에서 이름를 UPDATE 합니다. <br>
	 * </p>
	 *
	 * @param member 이름 변경할 회원정보
	 * @return 이름 수정이 성공하면 {@code true}, 실패하면 {@code false}
	 */
	public boolean updateName(Member member) {
		String query = String.format("UPDATE %s SET name = ? WHERE id = ? AND name != ?", TABLE);

		return jdbcTemplate.update(query, member.getName(), member.getId(), member.getName()) == 1;
	}


	/**
	 * 회원의 이메일(email)을 데이터베이스에 수정합니다.
	 *
	 * <p>
	 * 전달받은 {@link Member} 객체의 회원 식별번호(id)를 기준으로 <code>tb_member</code> 테이블에서 이메일을 UPDATE 합니다. <br>
	 * </p>
	 *
	 * @param member 이메일 변경할 회원정보
	 * @return 이메일 수정이 성공하면 {@code true}, 실패하면 {@code false}
	 */
	public boolean updateEmail(Member member) {
		String query = String.format("UPDATE %s SET email = ? WHERE id = ? AND email != ?", TABLE);

		return jdbcTemplate.update(query, member.getEmail(), member.getId(), member.getEmail()) == 1;
	}


	/**
	 * 회원의 탈퇴일(deleted_at)을 데이터베이스에 수정합니다.
	 * <p>
	 * 회원 아이디(username) 기준으로 <cdoe>tb_member</cdoe> 테이블에서 탈퇴일을 UPDATE 합니다.
	 * </p>
	 *
	 * @param username 탈퇴할 회원 아이디
	 * @return 탈퇴일이 오늘로 수정이 성공하면 {@code true}, 실패하면 {@code  false}
	 */
	public boolean updateResignDateByUsername(String username) {
		String query = String.format("UPDATE %s SET deleted_at = SYSDATE WHERE username = ?", TABLE);

		return jdbcTemplate.update(query, username) == 1;
	}

}
