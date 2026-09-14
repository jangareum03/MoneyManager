package com.moneymanager.redis;

import com.moneymanager.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.redis<br>
 * 파일이름       : RedisServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 14<br>
 * 설명              : RedisService 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 9. 14</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class RedisServiceIT extends IntegrationTest {

    @Autowired
    private RedisService target;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> "6379");
    }

    @Nested
    @DisplayName("저장할 때")
    class Save {
        
        @Test
        @DisplayName("존재하지 않은 데이터는 저장한다.")
        void savesData_whenDataDoesNotExist() {
        	//given
            String key = "key";
            String value = "value";
            Duration duration = Duration.ofMinutes(3);
        	
        	//when
            target.set(key, value, duration);
        	
        	//then
        	assertThat(redisTemplate.hasKey(key)).isTrue();
            assertThat(redisTemplate.opsForValue().get(key)).isEqualTo(value);
        }
        
        @Test
        @DisplayName("중복된 key는 값을 덮어쓰여서 저장한다.")
        void updatesData_whenKeyIsDuplicate() {
        	//given
            String key = "key";
            String value = "value";
            Duration duration = Duration.ofMinutes(3);

            redisTemplate.opsForValue().set(key, value, duration);
        	
        	//when
            target.set(key, "changeValue", duration);
        	
        	//then
        	assertThat(redisTemplate.hasKey(key)).isTrue();
        	assertThat(redisTemplate.opsForValue().get(key)).isEqualTo("changeValue");
        }
        
        @Test
        @DisplayName("설정된 Duration 값으로 저장한다.")
        void savesData_whenDurationIsGiven() {
            //given
            String key = "key";
            String value = "value";
            Duration duration = Duration.ofMinutes(1);

            //when
            target.set(key, value, duration);

            //then
            assertThat(redisTemplate.getExpire(key)).isBetween(59L, 60L);
        }
        
    }


    @Nested
    @DisplayName("조회할 때")
    class Get {
        
        @Test
        @DisplayName("key가 존재하면 해당하는 값을 반환한다.")
        void returnsValue_whenKeyExists() {
        	//given
            String key = "key";
            String value = "value";
            Duration duration = Duration.ofMinutes(1);

            redisTemplate.opsForValue().set(key, value, duration);
        	
        	//when
            String result  = target.get(key);
        	
        	//then
        	assertThat(result).isEqualTo(value);
        }
        
        @Test
        @DisplayName("key가 존재하지 않으면 null을 반환한다.")
        void returnsNull_whenKeyDoesNotExist() {
        	//given
            String key = "no-key";
        	
        	//when
            String result = target.get(key);
        	
        	//then
            assertThat(result).isNull();
        }
    }


    @Nested
    @DisplayName("삭제할 때")
    class Delete {
        
        @Test
        @DisplayName("key가 존재하면 삭제한다.")
        void deletesData_whenKeyExists() {
        	//given
            String key = "key";
            String value = "value";
            Duration duration = Duration.ofMinutes(1);

            redisTemplate.opsForValue().set(key, value, duration);
            
        	//when
            target.delete(key);
        	
        	//then
        	assertThat(redisTemplate.opsForValue().get(key)).isNull();
        }

    }

}