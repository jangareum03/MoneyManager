package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.member.domain.query.MemberProfileQuery;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.read.ImagePathResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberProfileImageService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 15<br>
 * 설명              : 회원 프로필 로직 흐름을 관리하는 클래스
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
 * 		 	  <td>26. 9. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class MemberProfileImageService {

    private static final String DEFAULT_PROFILE = "/image/default/profile.png";

    private final ImagePathResolver pathResolver;
    private final MemberRepository memberRepository;

    public String get(String memberNumber) {
        //1. 프로필 경로 조회
        Optional<MemberProfileQuery> query = memberRepository.findProfileByMemberNumber(memberNumber);

        //2. 회원 미존재
        if(query.isEmpty()) {
            throw new ApplicationException(
                    DATA_NOT_FOUND,
                    LogContent.of(
                            "회원 프로필 조회",
                            Member.class,
                            "memberNumber", memberNumber
                    )
            );
        }

        //3. 프로필 경로 반환
        String profile =  query.get().getProfile();

        if(profile == null) {
            return DEFAULT_PROFILE;
        }

        return pathResolver.getRootPath().resolve(profile).toString();
    }

}