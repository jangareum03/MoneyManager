package com.moneymanager.member.service.application;

import com.moneymanager.member.domain.query.MemberProfileQuery;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.read.ImagePathResolver;
import com.moneymanager.support.ApplicationExceptionAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Optional;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberProfileImageServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 15<br>
 * 설명              : MemberProfileImageService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 9. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class MemberProfileImageServiceTest {

    @InjectMocks
    MemberProfileImageService target;

    @Mock
    MemberRepository memberRepository;

    @Mock
    ImagePathResolver pathResolver;

    @Nested
    @DisplayName("회원 프로필 조회할 때")
    class Get {
        
        @Test
        @DisplayName("회원 번호의 프로필 경로를 반환한다.")
        void returnsProfilePath_whenMemberExists() {
        	//given
            String memberNumber = "memberNumber";

            MemberProfileQuery query = mock(MemberProfileQuery.class);
            
            when(memberRepository.findProfileByMemberNumber(memberNumber))
                    .thenReturn(Optional.of(query));
            
            when(query.getProfile())
                    .thenReturn("profile");

            when(pathResolver.getRootPath())
                    .thenReturn(Path.of("/root/image"));
            
        	//when
            String result = target.get(memberNumber);
        	
        	//then
        	assertThat(result).isEqualTo("\\root\\image\\profile");
        }
        
        @Test
        @DisplayName("회원 번호의 프로필이 null이면 기본 프로필 경로를 반환한다.")
        void returnsDefaultProfilePath_whenProfileIsNull() {
            //given
            String memberNumber = "memberNumber";

            MemberProfileQuery query = mock(MemberProfileQuery.class);

            when(memberRepository.findProfileByMemberNumber(memberNumber))
                    .thenReturn(Optional.of(query));

            when(query.getProfile())
                    .thenReturn(null);

            //when
            String result = target.get(memberNumber);

            //then
            assertThat(result).isEqualTo("/image/default/profile.png");
        }
        
        @Test
        @DisplayName("회원 번호가 없으면 예외를 발생시킨다.")
        void throwsException_whenMemberDoesNotExist() {
        	//given
            String memberNumber = "member-number";

            when(memberRepository.findProfileByMemberNumber(memberNumber))
                    .thenReturn(Optional.empty());
        	
        	//when
            Throwable throwable = catchThrowable(() -> target.get(memberNumber));
        	
        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(DATA_NOT_FOUND)
                    .hasWork("회원 프로필 조회")
                    .hasTarget(Member.class)
                    .hasValue("memberNumber", memberNumber);
        }

    }

}