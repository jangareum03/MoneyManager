package com.moneymanager.member.domain.dto.query;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.dto.query<br>
 * 파일이름       : MemberProfileQuery<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 15<br>
 * 설명              : 데이터베이스에서 회원  프로필 조회 결과를 담기 위한 클래스
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
 * 		 	  <td>26. 9. 15.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class MemberProfileQuery {

    private final String memberId;
    private final String profile;

    private MemberProfileQuery(String memberId, String profile) {
        this.memberId = memberId;
        this.profile = profile;
    }

    public static MemberProfileQuery of(String memberId, String profile) {
        return new MemberProfileQuery(memberId, profile);
    }

}