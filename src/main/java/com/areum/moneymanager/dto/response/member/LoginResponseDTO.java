package com.areum.moneymanager.dto.response.member;

import lombok.Builder;
import lombok.Getter;

/**
 * 서버에서 로그인 관련 작업을 사용자에게 전송할 데이터 객체
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
