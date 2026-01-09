package com.moneymanager.unit.repository.member;

import com.moneymanager.config.DatabaseConfig;
import com.moneymanager.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.repository.member<br>
 * 파일이름       : MemberRepositoryTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 9<br>
 * 설명              : 회원과 관련된 테이블의 데이터를 조작하는 기능을 검증하는 클래스
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
 * 		 	  <td>26. 1. 9.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
		DatabaseConfig.class,
		MemberRepository.class
})
public class MemberRepositoryTest {

	@Autowired	private MemberRepository repository;

	//==================[ 📌findImageLimitByMemberId  ]==================
	@Test
	@DisplayName("회원이 등록 가능한 이미개 수를 조회한다.")
	void 회원이_등록_가능한_이미지_개수_조회(){
		//given
		String memberId = "UNn12001";

		//when
		Integer result = repository.findImageLimitByMemberId(memberId);

		//then
		assertThat(result).isEqualTo(3);
	}

	@Test
	@DisplayName("존재하지 않은 회원번호는 EmptyResultDataAccessException이 발생한다.")
	void 없는_회원이면_예외발생(){
		//given
		String memberId = "no";

		//when & then
		assertThatExceptionOfType(EmptyResultDataAccessException.class)
				.isThrownBy(() -> repository.findImageLimitByMemberId(memberId));
	}
}
