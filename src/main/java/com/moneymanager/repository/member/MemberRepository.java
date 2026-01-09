package com.moneymanager.repository.member;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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

}
