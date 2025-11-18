package com.moneymanager.domain.member.dto;

import com.moneymanager.domain.member.enums.MemberGender;
import lombok.Builder;
import lombok.Getter;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.member.response<br>
 * * 파일이름       : MemberInfoResponse<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 28.<br>
 * * 설명              : 내 정보 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 28.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class MemberInfoResponse {
	//간단한 정보
	private MemberMyPageResponse.MemberInfo info;
	//이름
	private String name;
	//성별
	private MemberGender gender;
	//이메일
	private String email;
	//가입일
	private String joinDate;
	//연속 출석일
	private Long attendCount;
}
