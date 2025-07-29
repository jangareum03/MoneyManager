package com.areum.moneymanager.dto.member.request;

import lombok.Builder;
import lombok.Getter;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.member.request<br>
 * * 파일이름       : MemberRecoveryRequest<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              : 계정 찾기 요청을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 25.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class MemberRecoveryRequest {

	/**
	 * 아이디를 찾기 위한 정보를 담은 DTO
	 */
	@Builder
	@Getter
	public static class Id {
		//이름
		private String name;
		//생년월일
		private String birth;
	}

	/**
	 * 비밀번호 찾기 위한 정보를 담은 DTO
	 */
	@Builder
	@Getter
	public static class Password {
		//아이디
		private String id;
		//이름
		private String name;
	}
}
