package com.areum.moneymanager.dto.member.response;

import lombok.Builder;
import lombok.Getter;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.member.response<br>
 * * 파일이름       : LoginResDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 27.<br>
 * * 설명              : 로그인 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 27.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class LoginResDTO {

	/**
	 * 로그인 성공 시 정보를 담은 DTO
	 */
	@Builder
	@Getter
	public static class Success {
		//회원 식별 번호
		private String memberId;
		//회원 닉네임
		private String nickName;
	}

	/**
	 * 로그인 실패 시 정보를 담은 DTO
	 */
	@Builder
	@Getter
	public static class Failure {
		//회원 이름
		private String name;
		//회원 이메일
		private String email;
	}

}
