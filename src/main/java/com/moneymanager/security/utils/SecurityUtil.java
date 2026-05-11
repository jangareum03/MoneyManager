package com.moneymanager.security.utils;

import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ErrorCode;
import com.moneymanager.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 패키지이름    : com.moneymanager.security.utils<br>
 * 파일이름       : SecurityUtil<br>
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
public class SecurityUtil {

	/**
	 * 인증 성공한 현재 사용자의 회원번호를 반환합니다.
	 *
	 * @return	현재 사용자의 회원번호
	 */
	public String getMemberId() {
		String memberId = getCurrentUser().getId();
		if(memberId == null) {
			throw BusinessException.of(
					ErrorCode.MEMBER_AUTHORITY_UNAUTHORIZED,
					"회원 인증 실패   |   reason=사용자 ID 없음   |   object=CustomUserDetails   |   field=id   |   value=null"
			).withUserMessage("인증 실패했습니다. 다시 로그인해주세요.");
		}

		return memberId;
	}

	//현재 사용자 정보 조회
	private CustomUserDetails getCurrentUser(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if(auth == null) {
			throw BusinessException.of(
					ErrorCode.MEMBER_AUTHORITY_FAILED,
					"회원 인증 실패   |   reason=인증 객체 없음   |   object=SecurityContent   |   field=authentication   |   value=null"
			).withUserMessage("인증 실패했습니다. 다시 로그인해주세요.");
		}

		Object principal = auth.getPrincipal();
		if(!(principal instanceof CustomUserDetails)) {
			throw BusinessException.of(
					ErrorCode.MEMBER_AUTHORITY_FAILED,
					"회원 인증 실패   |   reason=principal 타입 불일치   |   object=authentication   |   field=principal   |   value=" + principal
			).withUserMessage("인증 정보가 올바르지 않습니다. 다시 로그인해주세요.");
		}

		return (CustomUserDetails) principal;
	}
}
