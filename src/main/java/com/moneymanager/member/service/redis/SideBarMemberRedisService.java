package com.moneymanager.member.service.redis;

import com.moneymanager.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.redis.sidebar<br>
 * 파일이름       : SideBarMemberRedisService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 9<br>
 * 설명              : 사이드바 회원 조회 흐름을 관리하는 클래스
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
@Service
@RequiredArgsConstructor
public class SideBarMemberRedisService {

    private static final Duration TTL = Duration.ofSeconds(JwtTokenProvider.ACCESS_TOKEN_EXPIRATION_SECONDS);

    private final RedisService redisService;
    private final MemberRedisKey redisKey;

    public void saveNickname(String memberId, String nickname) {
        redisService.set(
                redisKey.sidebarNickname(memberId),
                nickname,
                TTL
        );
    }

    public void saveProfile(String memberId, String profile) {
        redisService.set(
                redisKey.sidebarProfile(memberId),
                profile,
                TTL
        );
    }

    public String getNickname(String memberId) {
        return redisService.get(redisKey.sidebarNickname(memberId));
    }

    public String getProfile(String memberId) {
        return redisService.get(redisKey.sidebarProfile(memberId));
    }

    public void deleteNickname(String memberId) {
        redisService.delete(redisKey.sidebarNickname(memberId));
    }

    public void deleteProfile(String memberId) {
        redisService.delete(redisKey.sidebarProfile(memberId));
    }

}