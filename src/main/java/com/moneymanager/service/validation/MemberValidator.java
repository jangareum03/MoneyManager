package com.moneymanager.service.validation;


import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.enums.RegexPattern;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.LoginException;


/**
 * <p>
 * 패키지이름    : com.moneymanager.service.validation<br>
 * 파일이름       : MemberValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 7.<br>
 * 설명              :	회원 관련 검증 로직을 처리하는 클래스
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
 * 		 	  <td>25. 8. 7.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */

public class MemberValidator {

	/**
	 * 로그인 시 전달받은 <code>id</code>와 <code>password</code>가 유효한지  검증합니다.
	 * <p>
	 *     아이디(id) 또는 비밀번호(password) 둘 중 하나라도 유효하지 않으면 {@link LoginException}을 발생시킵니다.
	 * </p>
	 *
	 * @param id					로그인 한 아이디
	 * @param password		로그인 한 비밀번호
	 * @throws LoginException	유효하지 않은 아이디/비밀번호 입력한 경우 발생
	 */
	public static void validateLogin( String id, String password ) {
		ErrorDTO<String> errorDTO = validateId(id);

		//아이디 검증 실패한 상태
		if( errorDTO != null ) {
			throw new LoginException(errorDTO);
		}

		errorDTO = validatePassword(password);
		if( errorDTO != null ) {
			throw new LoginException(errorDTO);
		}
	}


	/**
	 * 회원의 아이디를 검증합니다.
	 * <p>
	 *     로그인 시 전달받은 <code>id</code>의 입력 여부와 형식을 확인합니다.
	 * </p>
	 *
	 * @param id		검증할 회원의 아이디
	 * @return 검증에 통과하면 <code>null</code>, 실패하면  {@link ErrorDTO}
	 */
	private static ErrorDTO<String> validateId( String id ) {
		ErrorDTO<String> errorDTO = null;

		if( id == null || id.isBlank() ) {	//아이디 미입력한 경우
			errorDTO = ErrorDTO.<String>builder()
					.errorCode(ErrorCode.MEMBER_ID_MISSING)
					.message("아이디를 입력해주세요.")
					.requestData(id).build();
		}else if( !id.matches(RegexPattern.MEMBER_ID.getPattern()) ) {	//아이디 형식 불일치한 경우
			errorDTO = ErrorDTO.<String>builder()
					.errorCode(ErrorCode.MEMBER_ID_FORMAT)
					.message("아이디는 4~15자 사이의 영어와 숫자만 입력 가능합니다.")
					.requestData(id).build();
		}

		return errorDTO;
	}


	/**
	 * 회원의 비밀번호를 검증합니다.
	 * <p>
	 *     로그인 시 전달받은 <code>password</code>의 입력 여부와 형식을 확인합니다.
	 * </p>
	 *
	 * @param password		검증할 회원의 비밀번호
	 * @return 검증에 통과하면 <code>null</code>, 실패하면  {@link ErrorDTO}
	 */
	private static ErrorDTO<String> validatePassword( String password ) {
		ErrorDTO<String> errorDTO = null;

		if( password == null || password.isBlank() ) {	//비밀번호 미입력한 경우
			errorDTO = ErrorDTO.<String>builder()
					.errorCode(ErrorCode.MEMBER_PASSWORD_MISSING)
					.message("비밀번호를 입력해주세요.")
					.requestData(password).build();
		}else if( !password.matches(RegexPattern.MEMBER_PWD.getPattern()) ) {	//비밀번호 형식 불일치한 경우
			errorDTO = ErrorDTO.<String>builder()
					.errorCode(ErrorCode.MEMBER_PASSWORD_FORMAT)
					.message("비밀번호는 8~20자 사이의 영어,숫자,특수문자(!%#^*)만 입력 가능합니다.")
					.requestData(password).build();
		}

		return errorDTO;
	}
}
