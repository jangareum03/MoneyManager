package com.moneymanager.security.utils;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.domain.global.enums.SystemMessage;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.moneymanager.exception.ErrorUtil.createClientException;

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
		return getCurrentUser().getId();
	}

	private CustomUserDetails getCurrentUser(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if( auth == null || !(auth.getPrincipal() instanceof CustomUserDetails) ) {
			ErrorDTO errorDTO = ErrorDTO.builder()
					.errorCode(ErrorCode.COMMON_AUTHENTICATION_NONE)
					.serviceName(this.getClass().getSimpleName())
					.message("인증 정보가 존재하지 않습니다.")
					.build();

			throw createClientException(errorDTO, SystemMessage.SECURITY.getMessage());
		}

		return (CustomUserDetails) auth.getPrincipal();
	}

}
