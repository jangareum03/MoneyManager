package com.areum.moneymanager.dto.request.member;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


/**
 * 사용자의 서비스 이용 내역을 서버로 전송하는 데이터 객체
 */
public class LogRequestDTO {


	/**
	 * 로그인 내역을 저장할 때 필요한 객체입니다.<p>
	 * <b color='white'>id</b>는 회원번호, <b color='white'>username</b>는 아이디,
	 * <b color='white'>success</b>는 로그인 성공여부,
	 * <b color='white'>failureReason</b>는 실패원인, <b color='white'>environment</b>는 접속 환경입니다.
	 *
	 */
	@Builder(toBuilder = true)
	@Getter
	//로그인내역을 등록할 때
	public static class Login {
		private String id;
		private String username;
		private boolean success;
		private String failureReason;
		private LoginRequestDTO.Environment environment;
	}




	/**
	 * 회원정보 수정내역을 저장할 때 필요한 객체입니다. <br>
	 * <b color='white'>memberId</b>는 회원번호, <b color='white'>success</b>는 성공여부, <br>
	 * <b color='white'>type</b>는 유형, <b color='white'>item</b>는 항목, <br>
	 * <b color='white'>beforeInfo</b>는 기존정보, <b color='white'>afterInfo</b>는 변경된 정보, <b color='white'>failureReason</b>는 실패사유입니다.
	 */
	@Builder(toBuilder = true)
	@Getter
	public static class Member {
		private String memberId;
		private boolean success;
		private String type;
		private String item;
		private String beforeInfo;
		private String afterInfo;
		private String failureReason;
		private LocalDateTime today;
	}


}
