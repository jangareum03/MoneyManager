package com.moneymanager.member.domain.dto.request;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.dto.request<br>
 * 파일이름       : FindIdRequest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 11<br>
 * 설명              : 아이디 찾기를 위한 요청 클래스
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
 * 		 	  <td>26. 9. 11</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class FindIdRequest {

    private final String name;
    private final String email;

    public FindIdRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

}