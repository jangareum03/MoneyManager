package com.moneymanager.member.service.read;

import com.moneymanager.global.exception.code.MemberErrorCode;
import com.moneymanager.global.exception.exception.InternalException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.member.domain.entity.MemberInfo;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.read<br>
 * 파일이름       : MemberReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 13<br>
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
 * 		 	  <td>26. 8. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class MemberReadServiceTest {

	@InjectMocks
	private MemberReadService target;

	@Mock
	private MemberRepository repository;

	@Nested
	@DisplayName("회원의 등록 가능한 개수를 조회할 때")
	class GetAvailableImageCountTest {

		@Nested
		@DisplayName("성공")
		class Success {

			@Test
			@DisplayName("회원이 존재하면 가능한 개수를 반환한다.")
			void returnsAvailableCount_whenUserExists() {
				//given
				String memberId = MemberTestData.MEMBER_ID;

				when(repository.findImageUploadLimitByMemberId(memberId))
						.thenReturn(1);
				
				//when
				int result = target.getAvailableImageCount(memberId);
				
				//then
				assertThat(result).isEqualTo(1);
			}

		}

		@Nested
		@DisplayName("실패")
		class Failure {
			
			@Test
			@DisplayName("회원이 존재하지 않으면 InternalException 예외를 전파한다.")
			void throwsInternalException_whenUserDoesNotExist() {
				//given
				String memberId = "nonexistent";

				when(repository.findImageUploadLimitByMemberId(memberId))
						.thenThrow(InternalException.of(
								MemberErrorCode.DATA_INTEGRITY_ERROR,
								LogContent.of(
										"등록 가능한 이미지 개수 조회",
										MemberInfo.class,
										"memberId",
										memberId
								).withCause("존재하지 않은 회원")
						));

				//when & then
				assertThatThrownBy(() -> target.getAvailableImageCount(memberId))
						.isInstanceOf(InternalException.class);
			}

		}

	}

}