package com.moneymanager.member.repository;

import com.moneymanager.global.security.jwt.JwtTokenProvider;
import com.moneymanager.member.service.redis.sidebar.SideBarMemberRedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : SideBarRedisRepository<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 9<br>
 * 설명              : Redis용 회원 사이드바와 관련된 데이터를 조작하는 클래스
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
@Repository
@RequiredArgsConstructor
public class SideBarRedisRepository {

    private final Duration SIDEBAR_TTL = Duration.ofSeconds(JwtTokenProvider.ACCESS_TOKEN_EXPIRATION_SECONDS);

    private final StringRedisTemplate redisTemplate;
    private final SideBarMemberRedisKey redisKey;

    public void saveNickname(String memberNumber, String nickname) {
        redisTemplate.opsForValue().set(
                redisKey.nickname(memberNumber),
                nickname,
                SIDEBAR_TTL
        );
    }

    public void saveProfile(String memberNumber, String profile) {
        redisTemplate.opsForValue().set(
                redisKey.profile(memberNumber),
                profile,
                SIDEBAR_TTL
        );
    }

    public Optional<String> getNickname(String memberNumber) {
        String value = redisTemplate.opsForValue()
                .get(redisKey.nickname(memberNumber));

        return Optional.ofNullable(value);
    }

    public Optional<String> getProfile(String memberNumber) {
        String value = redisTemplate.opsForValue()
                .get(redisKey.profile(memberNumber));

        return Optional.ofNullable(value);
    }

}