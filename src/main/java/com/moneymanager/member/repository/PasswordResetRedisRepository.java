package com.moneymanager.member.redis.repository;

import com.moneymanager.member.redis.PasswordResetRedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.redis.repository<br>
 * 파일이름       : PasswordResetRedisRepository<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 13<br>
 * 설명              : Redis용 비밀번호 찾기와 관련된 데이터를 조작하는 클래스
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
@Repository
@RequiredArgsConstructor
public class PasswordResetRedisRepository {

    private final Duration TOKEN_TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;;
    private final PasswordResetRedisKey redisKey;

    public void saveToken(String token) {
        redisTemplate.opsForValue().set(
                redisKey.token(token),
                "1",
                TOKEN_TTL
        );
    }

}