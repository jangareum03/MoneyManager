package com.moneymanager.member.info;

import com.moneymanager.dao.member.MemberInfoDaoImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.info<br>
 * 파일이름       : MemberInfoDaoTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 10<br>
 * 설명              : 회원 상세정보를 DB에서 조회 가능한지 확인하는 테스트 클래스
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
 * 		 	  <td>25. 12. 10.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MemberInfoDaoTest {

	@Autowired
	private MemberInfoDaoImpl dao;

	//=================================================
	// findImageLimit() 테스트
	//=================================================
	@DisplayName("존재하지 않은 회원번호로 정보를 조회하면 EmptyResultDataAccessException 예외가 발생한다.")
	@Test
	void 회원번호_없으면_예외발생(){
		//given
		String memberId = "UCh12100";

		//when & then
		assertThatExceptionOfType(EmptyResultDataAccessException.class)
				.isThrownBy(() -> dao.findImageLimit(memberId));

	}

	@DisplayName("존재한 회원번호로 정보 조회가 가능하다.")
	@Test
	void 회원번호_있으면_조회성공(){
		//given
		String memberId = "UKb11001";

		//when
		Integer result = dao.findImageLimit(memberId);

		//then
		assertThat(result).isEqualTo(1);
	}


}
