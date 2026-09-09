package com.moneymanager.member.repository;

import com.moneymanager.member.service.redis.sidebar.SideBarMemberRedisKey;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : SideBarRedisRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 9<br>
 * 설명              : SideBarRedisRepository 클래스 로직을 검증하는 통합 테스트 클래스
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
class SideBarRedisRepositoryIT extends IntegrationTest {

    String profile = "member/profile/test.png";

    @Autowired
    SideBarRedisRepository target;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SideBarMemberRedisKey redisKey;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> "6379");
    }

    @Nested
    @DisplayName("닉네임을 저장할 때")
    class SaveNickname {

        @Test
        @DisplayName("닉네임을 Redis에 저장한다.")
        void savesNickname_whenRequestIsValid() {
            //given
            String number = MemberTestData.DEFAULT_NUMBER;
            String nickname = MemberTestData.DEFAULT_NICKNAME;

            //when
            target.saveNickname(number, nickname);

            //then
            String saved = redisTemplate.opsForValue().get(redisKey.nickname(number));

            assertThat(saved).isEqualTo(nickname);

            Long ttl = redisTemplate.getExpire(redisKey.nickname(number), TimeUnit.MINUTES);
            assertThat(ttl).isBetween(59L, 60L);
        }

    }


    @Nested
    @DisplayName("프로필을 저장할 때")
    class SaveProfile {

        @Test
        @DisplayName("프로필을 Redis에 저장한다.")
        void savesNickname_whenRequestIsValid() {
            //given
            String number = MemberTestData.DEFAULT_NUMBER;

            //when
            target.saveProfile(number, profile);

            //then
            String saved = redisTemplate.opsForValue().get(redisKey.profile(number));

            assertThat(saved).isEqualTo(profile);

            Long ttl = redisTemplate.getExpire(redisKey.profile(number), TimeUnit.MINUTES);
            assertThat(ttl).isBetween(59L, 60L);
        }

    }


    @Nested
    @DisplayName("닉네임를 조회할 때")
    class GetNickname {

        @Test
        @DisplayName("회원번호가 존재하면 해당하는 닉네임을 반환한다.")
        void returnsNickname_whenMemberNumberExists() {
            //given
            String number = MemberTestData.DEFAULT_NUMBER;
            String nickname = MemberTestData.DEFAULT_NICKNAME;

            target.saveNickname(number, nickname);

            //when
            Optional<String> result = target.getNickname(number);

            //then
            assertThat(result).isPresent()
                    .contains(nickname);
        }

        @Test
        @DisplayName("회원번호가 존재하지 않으면 null을 반환한다.")
        void returnsNull_whenMemberNumberDoesNotExist() {
            //given
            String nickname = MemberTestData.DEFAULT_NICKNAME;

            //when
            Optional<String> result = target.getNickname(nickname);

            //then
            assertThat(result).isEmpty();
        }

    }


    @Nested
    @DisplayName("프로필을 조회할 때")
    class GetProfile {

        @Test
        @DisplayName("회원번호가 존재하면 해당하는 프로필을 반환한다.")
        void returnsProfile_whenMemberNumberExists() {
            //given
            String number = MemberTestData.DEFAULT_NUMBER;

            target.saveProfile(number, profile);

            //when
            Optional<String> result = target.getProfile(number);

            //then
            assertThat(result).isPresent()
                    .contains(profile);
        }

        @Test
        @DisplayName("회원번호가 존재하지 않으면 null을 반환한다.")
        void returnsNull_whenMemberNumberDoesNotExist() {
            //given
            String number = "noExist";
            //when
            Optional<String> result = target.getNickname(number);

            //then
            assertThat(result).isEmpty();
        }

    }

}