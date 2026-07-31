package com.moneymanager.member.repository;

import com.moneymanager.delete.domain.member.Member;
import com.moneymanager.delete.repository.member.MemberRepository;
import com.moneymanager.support.fixture.entity.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : MemberRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 28<br>
 * 설명              : MemberRepository 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 7. 28</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MemberRepositoryIT {

	@Autowired
	private MemberRepository target;

	private Member member;

	@BeforeEach
	void setUp() {
		member = target.save(
				MemberFixture.builder().build()
		);
	}

	@Nested
	@DisplayName("회원번호로 이미지 수 조회")
	class FindImageLimitTest {

		
		@Nested
		@DisplayName("성공 케이스")
		class Success {
		
			@Test
			@DisplayName("존재하는 회원번호로 이미지 개수를 조회한다.")
			void returnsImageCount_whenMemberExists() {
				//given: 회원번호가 주어진다.
				String memberId = member.getId();

				//when: 이미지 개수를 조회한다.
				Integer result = target.findImageLimitByMemberId(memberId);
				
				//then: 저장된 이미지 개수를 반환한다.
				assertThat(result).isEqualTo(1);
			}
			
			@Test
			@DisplayName("존재하지 않은 회원번호로 이미지 개수를 조회하면 예외가 발생한다.")
			void throwsEmptyResultDataAccessException_whenMemberDoesNotExist() {
				//given: 존재하지 않은 회원번호가 주어진다.
				String memberId = "error";
				
				//when & then: 이미지 개수를 조회하면 예외가 발생한다.
				assertThatThrownBy(() -> target.findImageLimitByMemberId(memberId))
						.isInstanceOf(EmptyResultDataAccessException.class);
			}
			
		}

	}

}