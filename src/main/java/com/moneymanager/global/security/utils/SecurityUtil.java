package com.moneymanager.global.security.utils;

import com.moneymanager.delete.domain.member.Member;
import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.moneymanager.global.exception.code.CommonErrorCode.UNAUTHORIZED;

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

	private final String work = "회원 인증";

	public String getMemberId() {
		String memberId = getCurrentUser().getId();

		if(memberId == null) {
			throw BusinessException.of(
					UNAUTHORIZED,
					LogContent.ofTarget(work, "회원번호 없음", Member.class, "id", null),
					"인증 실패했습니다. 다시 로그인해주세요."
			);
		}

		return memberId;
	}

	//현재 사용자 정보 조회
	private CustomUserDetails getCurrentUser(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if(auth == null) {
			throw BusinessException.of(
					UNAUTHORIZED,
					LogContent.ofTarget(work, "인증 객체 없음", Authentication.class, null),
					"인증 실패했습니다. 다시 로그인해주세요."
			);
		}

		Object principal = auth.getPrincipal();
		if(!(principal instanceof CustomUserDetails)) {
			throw BusinessException.of(
					UNAUTHORIZED,
					LogContent.ofTarget(work, "principal 타입 불일치", CustomUserDetails.class, "principal", principal == null ? null : principal.getClass().getSimpleName()),
					"인증 정보가 올바르지 않습니다. 다시 로그인해주세요."
			);
		}

		return (CustomUserDetails) principal;
	}

}