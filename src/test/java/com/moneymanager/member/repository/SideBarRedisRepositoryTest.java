package com.moneymanager.member.repository;

import com.moneymanager.member.service.redis.sidebar.SideBarMemberRedisKey;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : SideBarRedisRepositoryTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 9<br>
 * 설명              : SideBarRedisRepository 클래스 로직을 검증하는 단위 테스트 클래스
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
class SideBarRedisRepositoryTest {

    @InjectMocks
    SideBarRedisRepository target;

    @Mock
    StringRedisTemplate redisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    SideBarMemberRedisKey redisKey;

    @BeforeEach
    void setUp() {
        redisKey = new SideBarMemberRedisKey();

        target = new SideBarRedisRepository(redisTemplate, redisKey);
    }

    @Nested
    @DisplayName("닉네임을 저장할 때")
    class SaveNickname {

        @Test
        @DisplayName("1시간동안 저장한다.")
        void savesDataWithExpiration_whenGivenOneHours() {
            //given
            String prefix = "sidebar:nickname:";
            String memberNumber = MemberTestData.DEFAULT_NUMBER;
            String nickname = MemberTestData.DEFAULT_NICKNAME;

            when(redisTemplate.opsForValue())
                    .thenReturn(valueOperations);

            //when
            target.saveNickname(memberNumber, nickname);

            //then
            verify(valueOperations).set(
                    eq(prefix + memberNumber),
                    eq(nickname),
                    eq(Duration.ofHours(1))
            );
        }

    }

}