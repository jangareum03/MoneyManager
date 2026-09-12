package com.moneymanager.member.redis.repository;

import com.moneymanager.member.redis.PasswordResetRedisKey;
import com.moneymanager.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.redis.repository<br>
 * 파일이름       : PasswordResetRedisRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 13<br>
 * 설명              : PasswordResetRedisRepository 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 9. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class PasswordResetRedisRepositoryIT extends IntegrationTest {

    @Autowired
    PasswordResetRedisRepository target;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PasswordResetRedisKey redisKey;


    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> "6379");
    }


    @Nested
    @DisplayName("토큰을 저장할 때")
    class SaveToken {
        
        @Test
        @DisplayName("토큰이 Redis에 정상적으로 저장된다.")
        void savesToken_whenTokenIsValid() {
        	//given
            String token = "123456";
        	
        	//when
            target.saveToken(token);
        	
        	//then
        	String saved = redisTemplate.opsForValue().get(redisKey.token(token));

            assertThat(redisTemplate.hasKey(redisKey.token(token))).isTrue();
            assertThat(saved).isEqualTo("1");
        }

        @Test
        @DisplayName("토큰에 TTL이 정상적으로 적용된다.")
        void appliesTtl_whenTokenIsSaved() {
            //given
            String token = "123456";

            //when
            target.saveToken(token);

        	//then
            Long ttl = redisTemplate.getExpire(redisKey.token(token));
            assertThat(ttl).isBetween(599L, 600L);
        }
    }

}