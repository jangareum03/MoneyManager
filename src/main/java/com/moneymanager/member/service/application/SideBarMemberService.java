package com.moneymanager.member.service.application;

import com.moneymanager.member.domain.dto.response.SideBarUser;
import com.moneymanager.member.repository.SideBarRedisRepository;
import com.moneymanager.member.service.read.MemberReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private static final String DEFAULT_PROFILE = "/image/default/profile.png";

    private final SideBarRedisRepository redisRepository;
    private final MemberReadService memberReadService;

    public void saveSideBarInfo(String memberNumber) {
        SideBarUser  sideBarUser = memberReadService.getSideBarUser(memberNumber);

        String profile = sideBarUser.getProfile();

        redisRepository.saveNickname(memberNumber, sideBarUser.getNickname());
        redisRepository.saveProfile(
                memberNumber,
                profile == null ? DEFAULT_PROFILE: profile
        );
    }

    public SideBarUser get(String memberNumber) {
        String nickname = redisRepository.getNickname(memberNumber).orElse(null);
        String profile = redisRepository.getProfile(memberNumber).orElse(null);

        if(nickname == null || profile == null) {
            return memberReadService.getSideBarUser(memberNumber);
        }

        return new SideBarUser(nickname, profile);
    }

    public void delete(String memberNumber) {
        redisRepository.deleteNickname(memberNumber);
        redisRepository.deleteProfile(memberNumber);
    }

}