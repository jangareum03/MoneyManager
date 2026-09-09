package com.moneymanager.member.service.application;

import com.moneymanager.member.domain.dto.response.SideBarUser;
import com.moneymanager.member.repository.SideBarRedisRepository;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : SideBarMemberServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 9<br>
 * 설명              : SideBarMemberService 클래스 로직을 검증하는 단위 테스트 클래스
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
@ExtendWith(MockitoExtension.class)
class SideBarMemberServiceTest {

    @InjectMocks
    SideBarMemberService target;

    @Mock
    SideBarRedisRepository redisRepository;

    @Mock
    MemberReadService memberReadService;


    @Nested
    @DisplayName("사이드바 정보 조회할 때")
    class Get {

        @Test
        @DisplayName("Redis에 정보가 존재하면 memberReadService를 호출하지 않는다.")
        void doesNotCallMemberReadService_whenRedisInformationExists() {
        	//given
            String memberNumber = MemberTestData.DEFAULT_NUMBER;
            
            when(redisRepository.getNickname(memberNumber))
                    .thenReturn(Optional.of(MemberTestData.DEFAULT_NICKNAME));

            when(redisRepository.getProfile(memberNumber))
                    .thenReturn(Optional.of("test.png"));
        	
        	//when
            SideBarUser result = target.get(memberNumber);
        	
        	//then
            assertThat(result).isNotNull()
                    .hasFieldOrPropertyWithValue("nickname", MemberTestData.DEFAULT_NICKNAME)
                    .hasFieldOrPropertyWithValue("profile", "test.png");
        }
        
        @Test
        @DisplayName("Redis에 닉네임이 존재하지 않으면 memberReadService를 호출한다.")
        void callsMemberReadService_whenRedisNicknameDoesNotExist() {
        	//given
            String memberNumber = MemberTestData.DEFAULT_NUMBER;

            when(redisRepository.getNickname(memberNumber))
                    .thenReturn(Optional.empty());

            when(redisRepository.getProfile(memberNumber))
                    .thenReturn(Optional.of("test.png"));

            //when
            SideBarUser result = target.get(memberNumber);
        	
        	//then
        	verify(memberReadService).getSideBarUser(memberNumber);
        }
        
        @Test
        @DisplayName("Redis에 프로필이 존재하지 않으면 memberReadService를 호출한다.")
        void callsMemberReadService_whenRedisProfileDoesNotExist() {
        	//given
            String memberNumber = MemberTestData.DEFAULT_NUMBER;

            when(redisRepository.getNickname(memberNumber))
                    .thenReturn(Optional.of(MemberTestData.DEFAULT_NICKNAME));

            when(redisRepository.getProfile(memberNumber))
                    .thenReturn(Optional.empty());

            //when
            SideBarUser result = target.get(memberNumber);

            //then
            verify(memberReadService).getSideBarUser(memberNumber);
        }

    }

}