package com.moneymanager.member.repository;

import com.moneymanager.member.service.redis.EmailVerificationRedisKey;
import com.moneymanager.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : EmailVerificationRedisRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 6<br>
 * 설명              : EmailVerificationRedisRepository 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 9. 6</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class EmailVerificationRedisRepositoryIT extends IntegrationTest {

    @Autowired
    private EmailVerificationRedisRepository target;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EmailVerificationRedisKey redisKey;


    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> "6379");
    }


    @Nested
    @DisplayName("인증코드를 저장할 때")
    class SaveCode {

        @Test
        @DisplayName("인증코드를 Redis에 저장한다.")
        void savesCode_whenRequestIsValid() {
        	//given
            String email = "test@test.com";
            String code = "123456";
        	
        	//when
            target.saveCode(email, code);
        	
        	//then
            String saved = redisTemplate.opsForValue().get(redisKey.code(email));

            assertThat(saved).isEqualTo(code);

            Long ttl = redisTemplate.getExpire(redisKey.code(email), TimeUnit.SECONDS);
            assertThat(ttl).isBetween(299L, 300L);
        }
        
    }


    @Nested
    @DisplayName("토큰을 저장할 때")
    class SaveToken {

        @Test
        @DisplayName("토큰을 Redis에 저장한다.")
        void savesToken_whenRequestIsValid() {
            //given
            String email = "test@test.com";
            String token = UUID.randomUUID().toString();

            //when
            target.saveToken(email, token);

            //then
            String saved = redisTemplate.opsForValue().get(redisKey.token(email));

            assertThat(saved).isEqualTo(token);

            Long ttl = redisTemplate.getExpire(redisKey.token(email), TimeUnit.SECONDS);
            assertThat(ttl).isBetween(599L, 600L);
        }

    }


    @Nested
    @DisplayName("인증코드를 조회할 때")
    class GetCode {
        
        @Test
        @DisplayName("이메일이 존재하면 해당하는 인증코드를 반환한다.")
        void returnsAuthCode_whenEmailExists() {
        	//given
            String email = "test@test.com";
            String code = "123456";

            target.saveCode(email, code);
        	
        	//when
            Optional<String> result = target.getCode(email);
        	
        	//then
        	assertThat(result).isPresent()
                    .contains(code);
        }

        @Test
        @DisplayName("이메일이 존재하지 않으면 null을 반환한다.")
        void returnsNull_whenEmailDoesNotExist() {
        	//given
            String email = "no@test.com";
        	
        	//when
            Optional<String> result = target.getCode(email);
        	
        	//then
            assertThat(result).isEmpty();
        }
        
    }


    @Nested
    @DisplayName("토큰을 조회할 때")
    class GetToken {

        @Test
        @DisplayName("이메일이 존재하면 해당하는 토큰을 반환한다.")
        void returnsToken_whenEmailExists() {
            //given
            String email = "test@test.com";
            String token = "123456";

            target.saveToken(email, token);

            //when
            Optional<String> result = target.getToken(email);

            //then
            assertThat(result).isPresent()
                    .contains(token);
        }

        @Test
        @DisplayName("이메일이 존재하지 않으면 Empty를 반환한다.")
        void returnsEmpty_whenEmailDoesNotExist() {
            //given
            String email = "no@test.com";

            //when
            Optional<String> result = target.getToken(email);

            //then
            assertThat(result).isEmpty();
        }

    }


    @Nested
    @DisplayName("인증코드를 삭제할 때")
    class DeleteCode {
        
        @Test
        @DisplayName("이메일이 존재하면 해당하는 인증코드를 삭제한다.")
        void deletesVerificationCode_whenEmailExists() {
        	//given
            String email = "test@test.com";
            String code = "123456";

            target.saveCode(email, code);
        	
        	//when
            target.deleteCode(email);
        	
        	//then
        	assertThat(redisTemplate.hasKey(redisKey.code(email))).isFalse();
        }

    }


    @Nested
    @DisplayName("토큰을 삭제할 때")
    class DeleteToken {

        @Test
        @DisplayName("이메일이 존재하면 해당하는 토큰을 삭제한다.")
        void deletesToken_whenEmailExists() {
            //given
            String email = "test@test.com";
            String token = "123456";

            target.saveToken(email, token);

            //when
            target.deleteToken(email);

            //then
            assertThat(redisTemplate.hasKey(redisKey.token(email))).isFalse();
        }

        @Test
        @DisplayName("이메일이 존재하지 않으면 토큰을 삭제하지 못 한다.")
        void doesNothing_whenUserDoesNotExist() {
            //given
            String email = "test@test.com";
            String token = "123456";

            target.saveToken(email, token);

            //when
            target.deleteToken("noexist@test.com");

            //then
            assertThat(redisTemplate.hasKey(redisKey.token(email))).isTrue();
        }

    }

}