package com.moneymanager.member.domain.query;

import com.moneymanager.member.domain.enums.MemberStatus;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.query<br>
 * 파일이름       : MemberFindIdQuery<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 11<br>
 * 설명              : 데이터베이스에서 회원의 아이디 찾기 조회 결과를 담기 위한 클래스
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
 * 		 	  <td>26. 9. 111</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class MemberFindIdQuery {

    private final String username;
    private final MemberStatus status;

    public  MemberFindIdQuery(String username, MemberStatus status) {
        this.username = username;
        this.status = status;
    }

}