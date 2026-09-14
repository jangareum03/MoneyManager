package com.moneymanager.redis.service;

import com.moneymanager.member.service.generator.HashGenerator;
import com.moneymanager.redis.MemberRedisKey;
import com.moneymanager.redis.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : PasswordResetServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 13<br>
 * 설명              : PasswordResetService 클래스 로직을 검증하는 단위 테스트 클래스
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
@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @InjectMocks
    private PasswordResetService target;

    @Mock
    private MemberRedisKey redisKey;

    @Mock
    private RedisService redisService;

    @Mock
    private HashGenerator hashGenerator;

    @Nested
    @DisplayName("토큰이 존재 여부 확인할 때")
    class Exists {

        @Test
        @DisplayName("토큰이 존재하면 true를 반환한다.")
        void returnsTrue_whenTokenExists() {
        	//given
            String token = "token";

            when(hashGenerator.sha256(token))
                    .thenReturn("hash-token");

            when(redisKey.passwordResetToken("hash-token"))
                    .thenReturn(anyString());

            when(redisService.get(token))
                    .thenReturn("1");
        	
        	//when
            boolean result = target.exists(token);
        	
        	//then
        	assertThat(result).isTrue();
        }

        @Test
        @DisplayName("토큰이 존재하지 않으면 false를 반환한다.")
        void returnsFalse_whenTokenDoesNotExist() {
        	//given
            String token = "token";

            when(hashGenerator.sha256(token))
                    .thenReturn("hash-token");

            when(redisKey.passwordResetToken("hash-token"))
                    .thenReturn(anyString());

            when(redisService.get(token))
                    .thenReturn(null);
        	
        	//when
            boolean result = target.exists(token);

            //then
            assertThat(result).isFalse();
        }

    }

}