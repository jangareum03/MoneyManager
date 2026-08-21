package com.moneymanager.member.repository;

import com.moneymanager.global.exception.code.MemberErrorCode;
import com.moneymanager.global.exception.exception.InternalException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.member.domain.dto.MemberAuth;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberInfo;
import com.moneymanager.member.domain.enums.MemberStatus;
import com.moneymanager.member.domain.enums.MemberType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : MemberRepository<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 11<br>
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
 * 		 	  <td>26. 8. 11</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Repository
public class MemberRepository {

	private final JdbcTemplate jdbcTemplate;

	public MemberRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private final RowMapper<Member> memberRowMapper = (rs, rowNum) -> {
		MemberType type = MemberType.fromValue(rs.getString("type"));
		MemberStatus status = MemberStatus.fromValue(rs.getString("status"));

		LocalDateTime deleted = rs.getString("deleted_at") == null
												? null
												: rs.getTimestamp("deleted_at").toLocalDateTime();

		return Member.builder()
				.id(rs.getString("id"))
				.username(rs.getString("username"))
				.password(rs.getString("password"))
				.name(rs.getString("name"))
				.birthdate(rs.getString("birthdate"))
				.nickname(rs.getString("nickname"))
				.email(rs.getString("email"))
				.role(rs.getString("role"))
				.type(type)
				.status(status)
				.createdAt(rs.getTimestamp("created_at").toLocalDateTime())
				.deletedAt(deleted)
				.build();
	};

	public void save(Member member) {
		String query = """
				INSERT INTO member(id, type, username, password, name, birthdate, nickname, email)
					VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""";

		jdbcTemplate.update(
				query,
				member.getId(), member.getType().getValue(), member.getUsername(), member.getPassword(),
				member.getName(), member.getBirthdate(), member.getNickname(), member.getEmail()
		);

		save(member.getInfo());
	}

	private void save(MemberInfo memberInfo) {
		String query = """
				INSERT INTO member_info(id, gender, login_at)
					VALUES (?, ?, ?)
				""";

		jdbcTemplate.update(
				query,
				memberInfo.getId(), memberInfo.getGender().getValue(), memberInfo.getLoginAt()
		);
	}

	public Member findById(String id) {
		String query = """
				SELECT *
				FROM member
				WHERE id = ?
				""";

		return jdbcTemplate.queryForObject(query, memberRowMapper, id);
	}


	public Optional<MemberAuth> findAuthByUsername(String username) {
		String query = """
				SELECT m.id, m.username, m.password, m.nickname, m.role, m.status, m.deleted_at, mi.profile, mi.failure_count
				FROM member m JOIN member_info mi
					ON m.id = mi.id
				WHERE m.username = ?
				""";

		try {
			return Optional.ofNullable(
					jdbcTemplate.queryForObject(
							query,
							(rs, rowNum) -> MemberAuth.builder()
										.memberId(rs.getString("id"))
										.username(rs.getString("username"))
										.password(rs.getString("password"))
										.nickname(rs.getString("nickname"))
										.profile(rs.getString("profile"))
										.role(rs.getString("role"))
										.status(MemberStatus.fromValue(rs.getString("status")))
										.loginFailCount(rs.getInt("failure_count"))
										.deletedDate(
												rs.getTimestamp("deleted_at") == null
														? null
														: rs.getTimestamp("deleted_at").toLocalDateTime()
										)
										.build(),
							username
					)
			);
		}catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	public Integer findImageUploadLimitByMemberId(String memberId) {
		String query = """
				SELECT image_limit
					FROM member_info
					WHERE id = ?
				""";

		try{
			return jdbcTemplate.queryForObject(
					query,
					Integer.class,
					memberId
			);
		}catch (EmptyResultDataAccessException e) {
			throw InternalException.of(
					MemberErrorCode.DATA_NOT_FOUND,
					LogContent.of(
							"등록 가능한 이미지 개수 조회",
							MemberInfo.class,
							"memberId",
							memberId
					).withCause("존재하지 않은 회원")
			);
		}
	}

	public void deleteAll() {
		String query = "DELETE FROM member";

		jdbcTemplate.update(query);
	}

}