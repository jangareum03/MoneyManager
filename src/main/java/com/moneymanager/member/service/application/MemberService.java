package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.date.DateTimeUtil;
import com.moneymanager.member.domain.dto.response.MyPageResponse;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.query.MyPageQuery;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.read.MemberReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.moneymanager.global.domain.enums.DatePatterns.KOREAN_DATE;
import static com.moneymanager.global.domain.enums.DatePatterns.KOREAN_DATE_WITH_DAY;
import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 5<br>
 * 설명              : 회원 기능 로직 흐름을 관리하는 클래스
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
 * 		 	  <td>26. 9. 5</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberReader memberReader;
    private final MemberRepository memberRepository;

    public MyPageResponse getMyPageInfo(String memberId) {
        //회원정보 조회
        MyPageQuery myProfileInfo = memberRepository.findMyPageByMemberNumber(memberId)
                .orElseThrow(() -> new ApplicationException(
                                DATA_NOT_FOUND,
                                LogContent.of(
                                        "회원정보 조회",
                                        Member.class,
                                        "id", memberId
                                )
                        ).withMessageKey("member.info.failed")
                );

        //3. 프로필 경로 조회
        String profileImagePath = memberReader.getProfilePath(memberId);

        //4. 접속일 포맷 변경
        String loginDate = DateTimeUtil.formatDate(LocalDate.from(myProfileInfo.getLastLogin()), KOREAN_DATE_WITH_DAY.getPattern());
        String joinDate = DateTimeUtil.formatDate(LocalDate.from(myProfileInfo.getJoinDate()), KOREAN_DATE.getPattern());

        //5. 응답 반환
        return MyPageResponse.of(
                myProfileInfo.getType(),
                myProfileInfo.getName(),
                myProfileInfo.getNickname(),
                myProfileInfo.getGender(),
                myProfileInfo.getEmail(),
                profileImagePath,
                loginDate,
                joinDate,
                myProfileInfo.getAttendanceDays()
        );
    }

    public String getUsername(String memberId) {
        return memberRepository.findUsernameByMemberId(memberId);
    }

}