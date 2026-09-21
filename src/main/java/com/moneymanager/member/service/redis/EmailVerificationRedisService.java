package com.moneymanager.member.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.email<br>
 * 파일이름       : EmailVerificationRedisService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 20<br>
 * 설명              : 이메일 인증 관련 데이터를 Redis에서 조작하는 클래스
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
 * 		 	  <td>26. 9. 20</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
@RequiredArgsConstructor
public class EmailVerificationRedisService {

    private final Duration CODE_TTL = Duration.ofMinutes(5);
    private final Duration TOKEN_TTL = Duration.ofMinutes(10);

    private final RedisService redisService;
    private final MemberRedisKey redisKey;

    public void saveCode(String email, String code) {
        redisService.set(
                redisKey.emailVerificationCode(email),
                code,
                CODE_TTL
        );
    }

    public void saveToken(String email, String token) {
        redisService.set(
                redisKey.emailVerificationToken(email),
                token,
                TOKEN_TTL
        );
    }

    public String getCode(String email) {
        return redisService.get(redisKey.emailVerificationCode(email));
    }

    public String getToken(String email) {
        return redisService.get(redisKey.emailVerificationToken(email));
    }

    public void deleteCode(String email) {
        redisService.delete(redisKey.emailVerificationCode(email));
    }

    public void deleteToken(String email) {
        redisService.delete(redisKey.emailVerificationToken(email));
    }

}