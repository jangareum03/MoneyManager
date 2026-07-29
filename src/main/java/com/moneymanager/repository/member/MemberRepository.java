package com.moneymanager.repository.member;

import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.MemberInfo;
import com.moneymanager.domain.member.enums.MemberGender;
import com.moneymanager.domain.member.enums.MemberStatus;
import com.moneymanager.domain.member.enums.MemberType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

/**
 * <p>
 * 패키지이름    : com.moneymanager.repository.member<br>
 * 파일이름       : MemberRepository<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 8<br>
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
 * 		 	  <td>26. 1. 8.</td>
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
		 jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private final RowMapper<Member> memberRowMapper = (rs, rowNum) -> {
		MemberType type = MemberType.match(rs.getString("type").charAt(0));
		MemberStatus status = MemberStatus.fromCode(rs.getString("status").charAt(0));
		MemberGender gender = MemberGender.match(rs.getString("gender").charAt(0));

		LocalDateTime loginDate = rs.getTimestamp("login_at") == null ? null : rs.getTimestamp("login_at").toLocalDateTime();

		MemberInfo memberInfo = MemberInfo.builder()
				.memberId(rs.getString("id"))
				.imageLimit(rs.getInt("image_limit"))
				.profile(rs.getString("profile"))
				.point(rs.getLong("point"))
				.consecutiveDays(rs.getLong("consecutive_days"))
				.failureCount(rs.getInt("failure_count"))
				.loginAt(loginDate)
				.gender(gender)
				.build();

		return Member.builder()
				.id(rs.getString("id"))
				.type(type)
				.status(status)
				.role(rs.getString("role"))
				.userName(rs.getString("username"))
				.password(rs.getString("password"))
				.name(rs.getString("name"))
				.birthDate(rs.getString("birthdate"))
				.nickName(rs.getString("nickname"))
				.email(rs.getString("email"))
				.createdAt(rs.getTimestamp("created_at").toLocalDateTime())
				.deletedAt(rs.getTimestamp("deleted_at") == null ? null : rs.getTimestamp("deleted_at").toLocalDateTime())
				.memberInfo(memberInfo)
				.build();
	};



	@Transactional
	public Member save(Member member) {
		boolean isExists = existsId(member.getId());

		if(!isExists) {
			insert(member);
		}else {
			update(member);
		}

		return findById(member.getId());
	}

	private void insert(Member member) {
		int insertMember = insertMember(member);

		if(insertMember == 1) {
			insertMemberInfo(member.getMemberInfo().withMemberId(member.getId()));
		}
	}

	private int insertMember(Member member) {
		String query = """
				INSERT INTO member(id, type, username, password, name, birthdate, nickname, email)
				VALUES(?, ?, ?, ?, ?, ?, ?, ?)
				""";

		String type = String.valueOf(member.getType().getValue());

		return jdbcTemplate.update(
				con -> {
					PreparedStatement ps = con.prepareStatement(query, new String[] {"id"});

					ps.setString(1, member.getId());
					ps.setString(2, type);
					ps.setString(3, member.getUserName());
					ps.setString(4, member.getPassword());
					ps.setString(5, member.getName());
					ps.setString(6, member.getBirthDate());
					ps.setString(7, member.getNickName());
					ps.setString(8, member.getEmail());

					return ps;
				}
		);
	}

	private void insertMemberInfo(MemberInfo memberInfo) {
		String query = """
				INSERT INTO member_info(id, gender)
				VALUES(?, ?)
				""";

		String gender = String.valueOf(memberInfo.getGender().getType());

		jdbcTemplate.update(
				con -> {
					PreparedStatement ps = con.prepareStatement(query);

					ps.setString(1, memberInfo.getMemberId());
					ps.setString(2, gender);

					return ps;
				}
		);

	}


	private void update(Member member) {
		//TODO: 회원 수정 기능 구현
	}


	private boolean existsId(String memberId) {
		String query = """
				SELECT COUNT(*) 
				FROM member WHERE id = ?
				""";

		Integer count = jdbcTemplate.queryForObject(query, Integer.class,	memberId);

		return count > 0;
	}


	public Member findById(String id) {
		String query = """
				SELECT m.*, mi.gender, mi.profile, mi.point, mi.consecutive_days, mi.image_limit, mi.login_at, mi.failure_count
				FROM member m
				JOIN member_info mi
				ON m.id = mi.id
				WHERE m.id = ?
				""";

		return jdbcTemplate.queryForObject(
				query,
				memberRowMapper,
				id
		);

	}


	/**
	 * 회원의 등록 가능한 이미지 개수를 조회합니다.
	 * <p>
	 *     회원이 가계부 사진을 업로드할 때, 몇 장까지 저장할 수 있는지 확인할 수 있습니다.
	 * </p>
	 *
	 * @param memberId		업로드 가능한 이미지 정보를 조회할 회원번호
	 * @return	회원이 업로드할 수 있는 이미지 최대 개수
	 */
	public Integer findImageLimitByMemberId(String memberId) {
		String query = """
				SELECT image_limit
				FROM member_info
				WHERE id = ?
				""";

		return jdbcTemplate.queryForObject(
				query,
				Integer.class,
				memberId
		);
	}

	public void deleteAll() {
		String query = """
				DELETE FROM member
				""";

		jdbcTemplate.update(query);
	}
}
