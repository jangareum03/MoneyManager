package com.moneymanager.global.advice;

import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.member.domain.dto.response.SideBarUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.advice<br>
 * 파일이름       : GlobalControllerAdvice<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 12<br>
 * 설명              : 각 컨트롤러에서 처리하는 로직을 보조하는 클래스
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
 * 		 	  <td>26. 8. 12</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ControllerAdvice
public class GlobalControllerAdvice {

	@ModelAttribute("sidebarUser")
	public SideBarUser currentUser(@AuthenticationPrincipal CustomUserDetails user) {
		if(user == null) {
			return null;
		}

		return new SideBarUser(
				user.getNickname(),
				user.getProfile()
		);
	}

}