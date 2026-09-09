package com.moneymanager.member.service.read;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.member.domain.dto.response.SideBarUser;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.moneymanager.global.exception.code.ErrorCode.DUPLICATE_DATA;
import static com.moneymanager.global.exception.code.ErrorCode.UNAUTHORIZED;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.read<br>
 * 파일이름       : MemberReadService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 13<br>
 * 설명              : 회원 정보를 조회하는 클래스
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
 * 		 	  <td>26. 8. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class MemberReadService {

    private final MemberRepository memberRepository;

    public int getAvailableImageCount(String memberId) {
        return memberRepository.findImageUploadLimitByMemberId(memberId);
    }

    public SideBarUser getSideBarUser(String memberNumber) {
        return memberRepository.findByMemberNumberForSideBar(memberNumber)
                .orElseThrow(() ->
                        new ApplicationException(
                                UNAUTHORIZED,
                                LogContent.of(
                                        "사이드바 정보 조회",
                                        Member.class,
                                        "memberNumber", memberNumber
                                )
                        ).withUserMessage("member.sidebar.failed")
                );
    }

    public boolean checkUsernameExists(String username) {
        return memberRepository.existsByUsername(username);
    }

    public boolean checkNicknameExists(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public boolean checkEmailExists(String email) {
        return memberRepository.existsByEmail(email);
    }

    public void validateSignUpEligibility(String username, String nickname) {
        String work = "회원가입 검증";

        //1.아이디 중복 검증
        if (checkUsernameExists(username)) {
            throw new ApplicationException(
                    DUPLICATE_DATA,
                    LogContent.of(
                            work,
                            "username",
                            username
                    ).withCause("중복 아이디")
            ).withUserMessage("member.username.duplicate");
        }

        //1..닉네임 중복 검증
        if (checkNicknameExists(nickname)) {
            throw new ApplicationException(
                    DUPLICATE_DATA,
                    LogContent.of(
                            work,
                            "nickname",
                            nickname
                    ).withCause("중복 닉네임")
            ).withUserMessage("member.nickname.duplicate");
        }
    }

}