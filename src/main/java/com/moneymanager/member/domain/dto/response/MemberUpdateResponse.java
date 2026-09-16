package com.moneymanager.member.domain.dto.response;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.dto.response<br>
 * 파일이름       : MemberUpdateResponse<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 17<br>
 * 설명              : 마이 페이지에서 정보 수정 결과를 위한 응답 클래스
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
 * 		 	  <td>26. 9. 17</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class MemberUpdateResponse {

    private final String type;
    private final String value;

    private MemberUpdateResponse(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public static MemberUpdateResponse of(String type, String value) {
        return new MemberUpdateResponse(type, value);
    }
}