package com.moneymanager.member.service;

import com.moneymanager.exception.exception.BusinessException;
import com.moneymanager.repository.member.MemberRepository;
import com.moneymanager.service.member.MemberReadService;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service<br>
 * 파일이름       : MemberReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 28<br>
 * 설명              : MemberReadService 클래스 로직을 검증하는 단위 테스트 클래스
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
@ExtendWith(MockitoExtension.class)
public class MemberReadServiceTest {

	@InjectMocks
	private MemberReadService target;

	@Mock
	private MemberRepository repository;

	@Nested
	@DisplayName("이미지 허용 개수 조회")
	class GetImageLimit {

		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@ParameterizedTest
			@ValueSource(ints = {0, 1, 2, 3})
			@DisplayName("회원 이미지 개수가 정책보다 작으면 회원 이미지 개수를 반환한다.")
			void returnsMemberImageCount_whenCountIsInRange(int expected) {
				//given: 회원의 이미지 개수가 반환되도록 동작이 정의되어 있다.
				when(repository.findImageLimitByMemberId(MemberTestData.MEMBER_ID))
						.thenReturn(expected);
				
				//when: 회원의 이미지 허용 개수를 반환한다.
				int result = target.getImageLimit(MemberTestData.MEMBER_ID);
				
				//then: 회원 이미지 개수가 반환된다.
				assertThat(result).isEqualTo(expected);
			}
			
			@ParameterizedTest
			@ValueSource(ints = {4, 5})
			@DisplayName("회원 이미지 개수가 정책보다 크면 정책 최대값을 반환한다.")
			void returnsPolicyMax_whenCountIsOutOfRange(int limitCount) {
				//given: 회원의 이미지 개수가 반환되도록 동작이 정의되어 있다.
				when(repository.findImageLimitByMemberId(MemberTestData.MEMBER_ID))
						.thenReturn(limitCount);

				//when: 회원의 이미지 허용 개수를 반환한다.
				int result = target.getImageLimit(MemberTestData.MEMBER_ID);
				
				//then: 정책 개수가 반환된다.
				assertThat(result).isEqualTo(3);
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {
			
			@Test
			@DisplayName("존재하지 않은 회원이면 NOT_FOUND_DATA 예외가 발생한다.")
			void throwsBusinessException_whenMemberDoesNotExist() {
				//given: 회원의 이미지 개수를 조회하면 EmptyResultDataAccessException이 발생한다.
				String memberId = MemberTestData.MEMBER_ID;

				when(repository.findImageLimitByMemberId(memberId))
						.thenThrow(new EmptyResultDataAccessException(1));

				//when & then: 회원의 이미지 허용 개수를 요청하면 예외가 발생한다.
				assertThatThrownBy(() -> target.getImageLimit(memberId))
						.isInstanceOf(BusinessException.class);
			}

		}

	}

}