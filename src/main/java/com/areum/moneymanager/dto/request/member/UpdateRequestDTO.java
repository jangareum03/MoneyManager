package com.areum.moneymanager.dto.request.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;



/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.request.member<br>
 *  * 파일이름       : UpdateRequestDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원정보를 수정하기 위한 데이터를 전달하기 위한 클래스
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
public class UpdateRequestDTO {


	@Builder
	@Getter
	public static class Profile {
		private String beforeImage;
		private String afterImage;
		private MultipartFile file;
	}

	/**
	 * 회원 상태를 변경하기 위해 필요한 객체입니다.<br>
	 * <b color='white'>memberId</b>는 회원번호, <b color='white'>change</b>는 변경할 상태입니다.
	 */
	@Builder
	@Getter
	public static class Status {
		private String memberId;
		private String change;
	}


	/**
	 * 비밀번호를 변경하기 위해 필요한 객체입니다.<p>
	 * <b color='white'>password</b>는 변경할 비밀번호입니다.
	 */
	@Getter
	@NoArgsConstructor
	public static class Password {
		private String password;
	}


	/**
	 * 회원정보를 수정하기 위해 필요한 객체입니다.<br>
	 * <b color='white'>name</b>은 이름, <b color='white'>gender</b>는 성별, <b color='white'>email</b>은 이메일입니다.
	 */
	@Getter
	@NoArgsConstructor
	@ToString
	public static class MemberInfo {
		private String name;
		private String gender;
		private String email;
	}



	/**
	 * 회원탈퇴를 위해 필요한 객체입니다.<br>
	 * <b color='white'>id</b>은 아이디, <b color='white'>password</b>는 비밀번호, <b color='white'>code</b>는 탈퇴코드, <b color='white'>cause</b>은 탈퇴사유입니다.
	 */
	@Getter
	@NoArgsConstructor
	public static class Delete {
		private String id;
		private String password;
		private String code;
		private String cause;
	}
}
