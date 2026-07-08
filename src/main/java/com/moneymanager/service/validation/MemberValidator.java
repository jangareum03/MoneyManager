package com.moneymanager.service.validation;


import com.moneymanager.domain.global.enums.RegexPattern;
import com.moneymanager.exception.exception.ValidationException;
import com.moneymanager.exception.log.DeveloperLogInfo;
import com.moneymanager.utils.string.StringUtil;

import static com.moneymanager.exception.code.CommonErrorCode.INVALID_FORMAT;
import static com.moneymanager.exception.code.CommonErrorCode.REQUIRED_VALUE;


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

	private static final String MASKED_PASSWORD = "********";

	public static void validateLogin(String id, String password) {
		validateId(id);
		validatePassword(password);
	}

	private static void validateId(String id) {
		if(StringUtil.isNullOrBlank(id)) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					DeveloperLogInfo.of("아이디 검증", "아이디 없음","userName", id),
					"아이디를 입력해주세요."
			);
		}

		if(!StringUtil.matchesPattern(id, RegexPattern.MEMBER_ID.getPattern())) {
			throw ValidationException.of(
					INVALID_FORMAT,
					DeveloperLogInfo.of("아이디 검증", "아이디 형식 불일치", "userName", id)
							.addOption("format", "영어, 숫자")
							.addOption("min", 4)
							.addOption("max", 15),
					"아이디는 4~15자 사이의 영어와 숫자만 입력 가능합니다."
			);
		}
	}

	private static void validatePassword(String password ) {
		if(StringUtil.isNullOrBlank(password)) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					DeveloperLogInfo.of("비밀번호 검증", "비밀번호 없음", "password", MASKED_PASSWORD),
					"비밀번호를 입력해주세요."
			);
		}

		if(!StringUtil.matchesPattern(password, RegexPattern.MEMBER_PWD.getPattern()) ) {
			throw ValidationException.of(
					INVALID_FORMAT,
					DeveloperLogInfo.of("비밀번호 검증", "비밀번호 형식 불일치", "password", MASKED_PASSWORD)
							.addOption("format", "영어, 숫자, 느낌표, 퍼센트, 별표, #, ^")
							.addOption("min", 8)
							.addOption("max", 20),
					"비밀번호는 8~20자 사이의 영어,숫자,특수문자(!%#^*)만 입력 가능합니다."
			);
		}
	}
}
