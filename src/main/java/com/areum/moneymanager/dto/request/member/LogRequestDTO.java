package com.areum.moneymanager.dto.request.member;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.request.member<br>
 *  * 파일이름       : LogRequestDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 로그 데이터를 전달하기 위한 클래스
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
