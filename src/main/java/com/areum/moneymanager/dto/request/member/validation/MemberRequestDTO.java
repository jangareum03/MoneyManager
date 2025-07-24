package com.areum.moneymanager.dto.request.member.validation;

import lombok.*;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.request.member.validation<br>
 *  * 파일이름       : MemberRequestDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 데이터 전달하기 위한 클래스
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
public class MemberRequestDTO {


	/**
	 * 회원 아이디 값을 검증할 때 필요한 객체입니다.<br>
	 * <b color='white'>id</b>는 아이디 입니다.
	 */
	@Getter
	@NoArgsConstructor
	public static class Id {
		private String id;
	}


	/**
	 * 회원 비밀번호 값을 검증할 때 필요한 객체입니다.<br>
	 * <b color='white'>password</b>는 비밀번호 입니다.
	 */
	@Getter
	@NoArgsConstructor
	public static class Password {
		private String password;
	}



	/**
	 * 회원 이름 값을 검증할 때 필요한 객체입니다.<br>
	 * <b color='white'>name</b>은 이름 입니다.
	 */
	@Getter
	@NoArgsConstructor
	public static class Name {
		private String name;
	}


	@Getter
	@NoArgsConstructor
	public static class Birth {
		private String birthDate;
	}



	/**
	 * 회원 닉네임 값을 검증할 때 필요한 객체입니다.<br>
	 * <b color='white'>nickName</b>은 닉네임 입니다.
	 */
	@Getter
	@NoArgsConstructor
	//닉네임 검증할 때
	public static class Nickname {
		private String nickName;
	}



	/**
	 * 회원 이메일 값을 검증할 때 필요한 객체입니다.<br>
	 * <b color='white'>email</b>는 이메일 입니다.
	 */
	@Getter
	@NoArgsConstructor
	public static class Email {
		private String email;
	}




	/**
	 * 이메일 인증코드를 검증할 때 필요한 객체입니다.<br>
	 * <b color='white'>email</b>은 이메일, <b color='white'>code</b>는 인증코드, <b color='white'>time</b> 인증코드 입력 시간입니다.
	 */
	@Data
	@NoArgsConstructor
	public static class EmailCodeCheck {
		private String email;
		private String code;
		private Time time;
	}


	/**
	 * 회원 아이디 값을 검증할 때 필요한 객체입니다.<br>
	 * <b color='white'>id</b>는 아이디 입니다.
	 */
	@Data
	@NoArgsConstructor
	//이메일 인증시간 검증할 때
	public static class Time{
		private int minute;
		private int second;
	}
}
