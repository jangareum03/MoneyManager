package com.moneymanager.member.service.application;

import com.moneymanager.member.domain.dto.response.SideBarUser;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.repository.SideBarRedisRepository;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.moneymanager.global.exception.code.ErrorCode.UNAUTHORIZED;
import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : SideBarMemberServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 9<br>
 * 설명              : SideBarMemberService 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 9. 9</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class SideBarMemberServiceIT extends IntegrationTest {

    @Autowired
    SideBarMemberService target;

    @Autowired
    SideBarRedisRepository redisRepository;


    @Nested
    @DisplayName("사이드바 정보 저장할 때")
    class Save {
        
        @Test
        @DisplayName("회원이 존재하면 회원의 닉네임과 프로필을 Redis에 저장한다.")
        void savesUserProfileInRedis_whenUserExists() {
        	//given
            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .build();

            insertMember(member);

        	//when
            target.saveSideBarInfo(member.getMemberNumber());
        	
        	//then
        	String savedProfile = redisRepository.getProfile(member.getMemberNumber()).stream().findFirst().orElse(null);
        	String savedNickname = redisRepository.getNickname(member.getMemberNumber()).stream().findFirst().orElse(null);

            assertThat(savedProfile).isEqualTo("/image/default/profile.png");
            assertThat(savedNickname).isEqualTo(member.getNickname());
        }

        @Test
        @DisplayName("회원이 존재하지 않으면 예외를 전파시킨다.")
        void throwsException_whenUserDoesNotExist() {
        	//given
        	String memberNumber = MemberTestData.DEFAULT_NUMBER;

        	//when
            Throwable throwable = catchThrowable(() -> target.saveSideBarInfo(memberNumber));

        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(UNAUTHORIZED)
                    .hasUserMessage("member.sidebar.failed");
        }
    }


    @Nested
    @DisplayName("사이드바 정보 조회할 때")
    class Get {

        @Test
        @DisplayName("Redis에 정보가 존재하면 해당 정보를 담은 SideBarUser 객체를 반환한다.")
        void returnsSideBarUserFromRedis_whenRedisInformationExists() {
        	//given
            String memberNumber = MemberTestData.DEFAULT_NUMBER;
            String nickname = MemberTestData.DEFAULT_NICKNAME;
            String profile = "test.png";

            redisRepository.saveNickname(memberNumber, nickname);
            redisRepository.saveProfile(memberNumber, profile);

        	//when
            SideBarUser result = target.get(memberNumber);
        	
        	//then
        	assertThat(result)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("nickname", nickname)
                    .hasFieldOrPropertyWithValue("profile", profile);
        }

        @Test
        @DisplayName("Redis에 정보가 존재하지 않으면 실제 DB에 있는 정보를 담은 SideBarUser 객체를 반환한다.")
        void returnsSideBarUserFromDatabase_whenRedisInformationDoesNotExist() {
        	//given
            String memberNumber = MemberTestData.DEFAULT_NUMBER;

            insertMember(
                    MemberTestFixture.builder()
                            .number(memberNumber)
                            .nickName("수정완료")
                            .withMemberInfo(MemberInfoTestFixture.builder())
                            .build()
            );

            redisRepository.deleteNickname(memberNumber);
            redisRepository.deleteProfile(memberNumber);

        	//when
            SideBarUser result = target.get(memberNumber);
        	
        	//then
            assertThat(result)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("nickname", "수정완료")
                    .hasFieldOrPropertyWithValue("profile", null);
        }

    }


    @Nested
    @DisplayName("사이드바 정보 삭제할 때")
    class Delete {
        
        @Test
        @DisplayName("회원번호에 해당하는 닉네임과 프로필을 삭제한다.")
        void deletesNicknameAndProfile_whenMemberExists() {
        	//given
            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .build();

            insertMember(member);

            target.saveSideBarInfo(member.getMemberNumber());
        	
        	//when
            target.delete(member.getMemberNumber());
        	
        	//then
        	assertThat(redisRepository.getProfile(member.getMemberNumber())).isEmpty();
        	assertThat(redisRepository.getNickname(member.getMemberNumber())).isEmpty();
        }

    }

}