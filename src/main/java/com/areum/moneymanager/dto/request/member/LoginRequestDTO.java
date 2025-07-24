package com.areum.moneymanager.dto.request.member;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;


/**
 * 사용자가 로그인 관한 작업을 서버로 전송할 데이터 객체
 */

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.request.member<br>
 *  * 파일이름       : LoginRequestDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 로그인 데이터를 전달하기 위한 클래스
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
public class LoginRequestDTO {


	/**
	 * 회원가입을 진행하기 위해 필요한 객체입니다. <br>
	 * <b color='white'>id</b>는 아이디, <b color='white'>password</b>는 비밀번호,
	 * <b color='white'>name</b>은 이름, <b color='white'>birth</b>는 생년월일,
	 * <b color='white'>nickName</b>은 닉네임, <b color='white'>email</b>은 이메일,
	 * <b color='white'>gender</b>는 성별입니다.
	 */
	@Builder
	@Getter
	@ToString
	public static class SignUp {
		private String id;
		private String password;
		private String name;
		private String birth;
		private String nickName;
		private String email;
		private String gender;
	}



	/**
	 * 로그인을 하기 위해 필요한 객체입니다.
	 * <p>
	 * <b color='white'>id</b>는 아이디, <b color='white'>password</b>는 비밀번호입니다.
	 */
	@Builder
	@Getter
	public static class Login {
		private String id;
		private String password;
	}


	/**
	 * 서비스에 접속한 환경을 저장하는 객체입니다.
	 * <p>
	 * <b color='white'>browser</b>는 브라우저 유형, <b color='white'>ip</b>는 ip주소, <b color='white'>loginDate</b>는 접속 날짜와시간입니다.
	 */
	@Builder
	@Getter
	public static class Environment {
		private String browser;
		private String ip;
		private LocalDateTime loginDate;
	}





	/**
	 * 아이디를 찾기 위해 필요한 객체입니다.<p>
	 *  <b color='white'>name</b>은 이름,
	 *  <b color='white'>birth</b>은 생년월일입니다.
	 */
	@Builder
	@Getter
	public static class FindID {
		private String name;
		private String birth;
	}


	/**
	 * 비밀번호 찾기 위해 필요한 객체입니다.<p>
	 * <b color='white'>id</b>은 아이디, <b color='white'>name</b>은 이름입니다.
	 */
	@Builder
	@Getter
	public static class FindPwd {
		private String id;
		private String name;
	}

}
