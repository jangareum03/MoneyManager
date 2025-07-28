package com.areum.moneymanager.dto.member.request;

import com.areum.moneymanager.dto.common.ImageDTO;
import lombok.*;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.member.request<br>
 * * 파일이름       : UpdateReqDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              : 회원정보 수정 요청을 위한 데이터 클래스
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
public class UpdateReqDTO {

	/**
	 * 프로필 수정을 위한 DTO
	 */
	@Builder
	@Getter
	public static class Profile {
		//현재 이미지
		private String beforeImage;
		//변경할 이미지
		private ImageDTO changeImage;
	}

	/**
	 * 비밀번호 수정을 위한 DTO
	 */
	@Builder
	@Getter
	public static class Password {
		//현재 비밀번호
		private String beforePassword;
		//변경할 비밀번호
		private String afterPassword;
	}

	/**
	 * 회원정보 수정을 위한 DTO
	 */
	@Builder
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class MemberInfo {
		//이름
		private String name;
		//성별
		private String gender;
		//이메일
		private String email;
	}

}
