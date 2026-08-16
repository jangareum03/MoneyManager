package com.moneymanager.member.service.validation;

import com.moneymanager.global.domain.enums.RegexPattern;
import com.moneymanager.global.exception.code.CommonErrorCode;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.validation<br>
 * 파일이름       : MemberValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 11<br>
 * 설명              : 회원 요청 관련 검증 로직을 처리하는 클래스
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
 * 		 	  <td>26. 8. 11</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class MemberValidator {

	public static void login(String username, String password) {
		checkUsername(username);
		checkPassword(password);
	}

	private static void checkUsername(String username) {
		if (StringUtil.isNullOrBlank(username)) {
			throw ValidationException.of(
					CommonErrorCode.REQUIRED_VALUE,
					LogContent.of(
							"로그인 검증",
							"아이디 누락",
							"username",
							username
					),
					"아이디를 입력해주세요."
			);
		}

		if(!StringUtil.matchesPattern(username, RegexPattern.MEMBER_USERNAME.getPattern())) {
			throw ValidationException.of(
					CommonErrorCode.INVALID_FORMAT,
					LogContent.of(
							"로그인 검증",
							"아이디 형식 불일치",
							"username",
							username
					),
					"아이디는 4~15자 사이의 영어와 숫자만 입력 가능합니다."
			);
		}
	}
	private static void checkPassword(String password) {
		if (StringUtil.isNullOrBlank(password)) {
			throw ValidationException.of(
					CommonErrorCode.REQUIRED_VALUE,
					LogContent.of(
							"로그인 검증",
							"비밀번호 누락",
							"password",
							password
					),
					"비밀번호를 입력해주세요."
			);
		}

		if(!StringUtil.matchesPattern(password, RegexPattern.MEMBER_PWD.getPattern())) {
			throw ValidationException.of(
					CommonErrorCode.INVALID_FORMAT,
					LogContent.of(
							"로그인 검증",
							"비밀번호 형식 불일치",
							"password",
							password
					),
					"8~20자 사이의 영어,숫자,특수문자(!%#^*)만 입력 가능합니다."
			);
		}
	}

}