package com.areum.moneymanager.dto.response.member;

import lombok.Builder;
import lombok.Getter;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.response.member<br>
 *  * 파일이름       : AttendanceResponseDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 사용자에게 로그인 및 계정찾기 데이터를 전달하기 위한 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
public class LoginResponseDTO {

	/**
	 * 서비스 이용 시 필요한 정보를 저장한 객체입니다. <p>
	 * <b color='white'>id</b>는 회원의 고유 식별번호, <b color='white'>nickName</b>은 닉네임입니다.
	 */
	@Builder
	@Getter
	public static class Login {
		private String memberId;
		private String nickName;
	}


	/**
	 * 로그인 불가능하거나 복구 가능한 탈퇴 회원이 로그인 시 이메일 전송을 위해 필요한 정보를 저장한 객체입니다.<p>
	 * <b color='white'>name</b>은 이름, <b color='white'>email</b>은 이메일입니다.
	 */
	@Builder
	@Getter
	public static class SendEmail {
		private String name;
		private String email;
	}


	/**
	 *	아이디 찾기의 결과를 저장한 객체입니다.<p>
	 *<b color='white'>id</b>는 아이디, <b color='white'>message</b>는 안내문구, <b color='white'>date</b>는 마지막 접속날짜입니다.
	 */
	@Builder
	@Getter
	public static class FindID {
		private String id;
		private String message;
		private String date;
	}


	/**
	 * 비밀번호 찾기의 결과를 저장하는 객체입니다.<p>
	 * <b color='white'>email</b>은 이메일, <b color='white'>message</b>는 안내문구입니다.
	 */
	@Builder
	@Getter
	public static class FindPwd {
		private String email;
		private String message;
	}
}
