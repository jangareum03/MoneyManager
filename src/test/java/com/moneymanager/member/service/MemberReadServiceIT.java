package com.moneymanager.member.service;

import com.moneymanager.delete.domain.member.Member;
import com.moneymanager.delete.repository.member.MemberRepository;
import com.moneymanager.delete.service.member.MemberReadService;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.fixture.entity.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.moneymanager.global.exception.code.MemberErrorCode.NOT_FOUND_DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service<br>
 * 파일이름       : MemberReadServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 28<br>
 * 설명              : MemberReadService 클래스 로직을 검증하는 통합 테스트 클래스
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
public class MemberReadServiceIT {

	@Autowired
	private MemberReadService target;

	@Autowired
	private MemberRepository memberRepository;

	@Nested
	@DisplayName("이미지 허용 개수 조회")
	class GetImageLimit {

		private Member member;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(
					MemberFixture.builder().build()
			);
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@Test
			@DisplayName("존재하는 회원이면 이미지 제한 수를 정상 반환한다.")
			void returnsImageLimit_whenMemberExists() {
				//given: 회원번호가 주어진다.
				String memberId = member.getId();

				//when: 회원의 이미지 허용 개수를 반환한다.
				int result = target.getImageLimit(memberId);
				
				//then: 이미지 허용 개수가 반환된다.
				assertThat(result).isEqualTo(member.getMemberInfo().getImageLimit());
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {
			
			@Test
			@DisplayName("존재하지 않은 회원이면 예외가 발생한다.")
			void throwsBusinessException_whenMemberDoesNotExist() {
				//given: 존재하지 않은 회원번호가 주어진다.
				String memberId = "error";

				//when: 회원의 이미지 허용 개수를 반환 중 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.getImageLimit(memberId));
				
				//then: NOT_FOUND_DATA 예외가 발생된다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(NOT_FOUND_DATA)
						.hasWork("회원 정보 조회")
						.hasCauseMessage("데이터 없음")
						.hasTarget(Member.class)
						.hasField("id")
						.hasValue(memberId);
			}

		}

	}

}