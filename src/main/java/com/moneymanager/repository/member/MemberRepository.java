package com.moneymanager.repository.member;

import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.MemberInfo;
import com.moneymanager.exception.BusinessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;

import static com.moneymanager.exception.error.ErrorCode.*;

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


	@Transactional
	public String save(Member member) {
		boolean isExists = existsId(member.getId());

		if(!isExists) {
			return insert(member);
		}else {
			update(member);

			return member.getId();
		}
	}

	private String insert(Member member) {
		insertMember(member);

		if(member.getMemberInfo() == null) {
			throw BusinessException.of(
					MEMBER_TARGET_MISSING,
					"회원 저장 실패   |   reason=필수값누락   |   field=memberInfo   |   value=null"
			);
		}

		MemberInfo memberInfo = member.getMemberInfo().withMemberId(member.getId());

		insertMemberInfo(memberInfo);

		return member.getId();
	}

	private void insertMember(Member member) {
		String query = """
				INSERT INTO member(id, type, username, password, name, birthdate, nickname, email)
				VALUES(?, ?, ?, ?, ?, ?, ?, ?)
				""";

		String type = String.valueOf(member.getType().getValue());

		int insertedRow = jdbcTemplate.update(
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

		if(insertedRow != 1) {
			throw BusinessException.of(
					MEMBER_ETC_DB_ERROR,
					"회원 저장 실패   |   reason=DB저장실패   |   detail=저장건수 불일치(기대=1, 실제=0)   |   object=Member"
			);
		}
	}

	private void insertMemberInfo(MemberInfo memberInfo) {
		String query = """
				INSERT INTO member_info(id, gender)
				VALUES(?, ?)
				""";

		String gender = String.valueOf(memberInfo.getGender().getType());

		int insertedRow = jdbcTemplate.update(
				con -> {
					PreparedStatement ps = con.prepareStatement(query);

					ps.setString(1, memberInfo.getMemberId());
					ps.setString(2, gender);

					return ps;
				}
		);

		if(insertedRow != 1) {
			throw BusinessException.of(
					MEMBER_ETC_DB_ERROR,
					"회원 저장 실패   |   reason=DB저장실패   |   detail=저장건수 불일치(기대=1, 실제=0)   |   object=MemberInfo"
			);
		}
	}


	private void update(Member member) {
		//TODO: 회원 수정 기능 구현
	}


	private boolean existsId(String memberId) {
		String query = """
				SELECT COUNT(*) FROM member WHERE id = ?
				""";

		Integer count = jdbcTemplate.queryForObject(query, Integer.class,	memberId);

		return count > 0;
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
		String sql = "SELECT image_limit" +
							"	FROM member_info" +
							"	WHERE id = ?";

		return jdbcTemplate.queryForObject(
				sql,
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
