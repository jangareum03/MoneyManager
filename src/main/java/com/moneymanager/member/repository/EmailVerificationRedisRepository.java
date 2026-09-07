package com.moneymanager.member.repository;

import com.moneymanager.member.service.redis.EmailVerificationRedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : EmailVerificationRedisRepository<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 6<br>
 * 설명              : Redis용 이메일 인증과 관련된 데이터를 조작하는 클래스
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
@Repository
@RequiredArgsConstructor
public class EmailVerificationRedisRepository {

    private final Duration CODE_TTL = Duration.ofMinutes(5);
    private final Duration TOKEN_TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;
    private final EmailVerificationRedisKey redisKey;

    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set(
                redisKey.code(email),
                code,
                CODE_TTL
        );
    }

    public void saveToken(String email, String token) {
        redisTemplate.opsForValue().set(
                redisKey.token(email),
                token,
                TOKEN_TTL
        );
    }

    public Optional<String> getCode(String email) {
        String value = redisTemplate.opsForValue()
                .get(redisKey.code(email));

        return Optional.ofNullable(value);
    }

    public Optional<String> getToken(String email) {
        String token = redisTemplate.opsForValue()
                .get(redisKey.token(email));

        return Optional.ofNullable(token);
    }

    public void deleteCode(String email) {
        redisTemplate.delete(redisKey.code(email));
    }

    public void deleteToken(String email) {
        redisTemplate.delete(redisKey.token(email));
    }

}