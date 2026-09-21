package com.moneymanager.member.service.read;

import com.moneymanager.global.file.ImagePathResolver;
import com.moneymanager.member.domain.query.MemberProfileQuery;
import com.moneymanager.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.read<br>
 * 파일이름       : MemberReader<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 21<br>
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
 * 		 	  <td>26. 9. 21</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
@RequiredArgsConstructor
public class MemberReader {

    private final MemberRepository memberRepository;
    private final ImagePathResolver pathResolver;

    public Integer getAvailableImageCount(String memberId) {
        return memberRepository.findImageUploadLimitByMemberId(memberId);
    }

    public String getProfileName(String memberId) {
        return findProfileName(memberId)
                .orElse("profile.png");
    }

    public String getProfilePath(String memberId) {
        return memberRepository.findProfileByMemberId(memberId)
                .map(m -> pathResolver.profilePath(memberId)
                        .resolve(m.getProfile())
                        .toString())
                .orElse("/image/default/profile.png");
    }


    //===== 유틸 메ㅐ서드 =====
    private Optional<String> findProfileName(String memberId) {
        return memberRepository.findProfileByMemberId(memberId)
                .map(MemberProfileQuery::getProfile);
    }
}