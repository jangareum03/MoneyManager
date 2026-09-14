package com.moneymanager.redis.service;

import com.moneymanager.global.security.jwt.JwtTokenProvider;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.redis.MemberRedisKey;
import com.moneymanager.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.redis.sidebar<br>
 * 파일이름       : SideBarMemberService<br>
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
public class SideBarMemberService {

    private static final Duration TTL = Duration.ofSeconds(JwtTokenProvider.ACCESS_TOKEN_EXPIRATION_SECONDS);
    private static final String DEFAULT_PROFILE = "/image/default/profile.png";

    private final RedisService redisService;
    private final MemberRedisKey redisKey;

    public void saveNickname(String memberNumber, String nickname) {
        redisService.set(
                redisKey.sidebarNickname(memberNumber),
                nickname,
                TTL
        );
    }

    public void saveProfile(String memberNumber, String profile) {
        if(StringUtil.isNullOrBlank(profile)) {
            profile = DEFAULT_PROFILE;
        }

        redisService.set(
                redisKey.sidebarProfile(memberNumber),
                profile,
                TTL
        );
    }

    public String getNickname(String memberNumber) {
        return redisService.get(redisKey.sidebarNickname(memberNumber));
    }

    public String getProfile(String memberNumber) {
        return redisService.get(redisKey.sidebarProfile(memberNumber));
    }

    public void deleteNickname(String memberNumber) {
        redisService.delete(redisKey.sidebarNickname(memberNumber));
    }

    public void deleteProfile(String memberNumber) {
        redisService.delete(redisKey.sidebarProfile(memberNumber));
    }

}