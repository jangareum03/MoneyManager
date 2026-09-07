package com.moneymanager.member.repository;

import com.moneymanager.member.service.redis.EmailVerificationRedisKey;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : EmailVerificationRedisRepositoryTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 6<br>
 * 설명              : EmailVerificationRedisRepository 클래스 로직을 검증하는 단위 테스트 클래스
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
@ExtendWith(MockitoExtension.class)
class EmailVerificationRedisRepositoryTest {

    @InjectMocks
    EmailVerificationRedisRepository target;

    @Mock
    StringRedisTemplate  redisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    EmailVerificationRedisKey redisKey;

    @BeforeEach
    void setUp() {
        redisKey = new EmailVerificationRedisKey();

        target =  new EmailVerificationRedisRepository(redisTemplate, redisKey);
    }

    @Nested
    @DisplayName("인증코드를 저장할 때")
    class SaveCode {
        
        @Test
        @DisplayName("5분동안 저장한다.")
        void savesDataWithExpiration_whenGivenFiveMinutes() {
        	//given
            String prefix = "email-verification:code:";
            String email = "test@test.com";
        	String code = "123456";

            when(redisTemplate.opsForValue())
                    .thenReturn(valueOperations);

        	//when
        	target.saveCode(email, code);

        	//then
        	verify(valueOperations).set(
                    eq(prefix + email),
                    eq(code),
                    eq(Duration.ofMinutes(5))
            );
        }
        
    }


    @Nested
    @DisplayName("토큰을 저장할 때")
    class SaveToken {

        @Test
        @DisplayName("10분동안 저장한다.")
        void savesDataWithExpiration_whenGivenTenMinutes() {
            //given
            String prefix = "email-verification:token:";
            String email = "test@test.com";
            String token = UUID.randomUUID().toString();

            when(redisTemplate.opsForValue())
                    .thenReturn(valueOperations);

            //when
            target.saveToken(email, token);

            //then
            verify(valueOperations).set(
                    eq(prefix + email),
                    eq(token),
                    eq(Duration.ofMinutes(10))
            );
        }

    }

}