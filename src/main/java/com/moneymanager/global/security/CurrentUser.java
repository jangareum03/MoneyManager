package com.moneymanager.global.security;

import com.moneymanager.global.exception.exception.InternalException;
import com.moneymanager.global.log.LogContent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.moneymanager.global.exception.code.CommonErrorCode.UNAUTHORIZED;

/**
 * <p>
 * 패키지이름    : com.moneymanager.security.utils<br>
 * 파일이름       : CurrentUser<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 9<br>
 * 설명              : 인증 성공한 사용자 정보를 조회하는 클래스
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
 * 		 	  <td>26. 1. 9.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class CurrentUser {

	public String getMemberId() {
		return getCurrentUser().getId();
	}


	//===== 보조 메서드 =====
	private CustomUserDetails getCurrentUser(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if(auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
			throw InternalException.of(
					UNAUTHORIZED,
					LogContent.of("회원 인증", Authentication.class)
			);
		}

		return (CustomUserDetails) auth.getPrincipal();
	}

}