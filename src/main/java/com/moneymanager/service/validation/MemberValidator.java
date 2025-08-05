package com.moneymanager.service.validation;

import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.dto.member.request.MemberLoginRequest;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.utils.ValidationUtil;


/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.service.validation<br>
 * * 파일이름       : MemberValidator<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 24<br>
 * * 설명              : 회원 관련 검증 로직을 처리하는 클래스
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
 * 		 	  <td>25. 7. 24</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성(버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class MemberValidator {

	/**
	 * 사용자 로그인 시 입력한 값들의 기본 형식을 검사합니다.
	 *
	 * @param request 로그인 요청정보를 담은 객체
	 * @throws ClientException 입력값이 로그인 불가능할 시
	 */
	public static void validateLogin(MemberLoginRequest request) {
		//아이디 확인
		if (ValidationUtil.isEmptyInput(request.getId())) {
			throw new ClientException(ErrorDTO.builder().errorCode(ErrorCode.LOGIN_ID_MISSING).requestData(request.getId()).build());
		}

		if (!ValidationUtil.isMatchPattern(request.getId(), "MEMBER_ID")) {
			throw new ClientException(ErrorDTO.builder().errorCode(ErrorCode.LOGIN_ID_FORMAT).requestData(request.getId()).build());
		}

		//비밀번호 확인
		if (ValidationUtil.isEmptyInput(request.getPassword())) {
			throw new ClientException(ErrorDTO.builder().errorCode(ErrorCode.LOGIN_PASSWORD_MISSING).requestData(request.getPassword()).build());
		}

		if (!ValidationUtil.isMatchPattern(request.getPassword(), "MEMBER_PWD")) {
			throw new ClientException(ErrorDTO.builder().errorCode(ErrorCode.LOGIN_PASSWORD_FORMAT).requestData(request.getPassword()).build());
		}
	}
}
