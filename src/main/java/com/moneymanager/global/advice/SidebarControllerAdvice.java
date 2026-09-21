package com.moneymanager.global.advice;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.domain.dto.response.SideBarUser;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.read.MemberReader;
import com.moneymanager.member.service.redis.SideBarMemberRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import static com.moneymanager.global.exception.code.ErrorCode.UNAUTHORIZED;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.advice<br>
 * 파일이름       : SidebarControllerAdvice<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 9<br>
 * 설명              : 사이드바에 필요한 정보를 처리하는 공통 클래스
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
 * 		 	  <td>26. 9. 9.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ControllerAdvice
@RequiredArgsConstructor
public class SidebarControllerAdvice {

    private final SideBarMemberRedisService redisService;
    private final MemberReader memberReader;
    private final MemberRepository memberRepository;

    @ModelAttribute("sidebarUser")
    public SideBarUser currentUser(@AuthenticationPrincipal CustomUserDetails user) {
        //인증 완료된 사용자 여부 확인
        if(user == null) {
            return null;
        }

        String memberId = user.getId();

        //2. Redis 조회
        String nickname = redisService.getNickname(memberId);
        String profile = redisService.getProfile(memberId);

        //3. Redis에 없으면 DB 조회
        if(nickname == null || profile == null) {
            SideBarUser member = memberRepository.findByMemberNumberForSideBar(memberId)
                    .orElseThrow(() ->
                            new ApplicationException(
                                    UNAUTHORIZED,
                                    LogContent.of(
                                            "인증회원 조회",
                                            Member.class,
                                            "id", StringUtil.masking(memberId, 2, memberId.length() - 2)
                                    )
                            ).withMessageKey("member.sidebar.failed")
                            );

            if(nickname == null) {
                nickname = member.getNickname();
            }

            if(profile == null) {
                profile = member.getProfile();
            }
        }

        return new SideBarUser(nickname, memberReader.getProfilePath(memberId, profile));
    }

}