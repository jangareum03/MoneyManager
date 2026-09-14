package com.moneymanager.redis.service;

import com.moneymanager.member.service.generator.HashGenerator;
import com.moneymanager.redis.MemberRedisKey;
import com.moneymanager.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.read<br>
 * 파일이름       : PasswordResetService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 13<br>
 * 설명              : 비밀번호 초기화 기능 Redis 관련 기능을 제공하는 클래스
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
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final Duration TTL = Duration.ofMinutes(10);

    private final RedisService redisService;
    private final MemberRedisKey redisKey;

    private final HashGenerator hashGenerator;

    public void saveToken(String token) {
        redisService.set(
                redisKey.passwordResetToken(hashGenerator.sha256(token)),
                "1",
                TTL
        );
    }

    public boolean exists(String token) {
        String hashToken = hashGenerator.sha256(token);

        return "1".equals(redisService.get(redisKey.passwordResetToken(hashToken)));
    }

}