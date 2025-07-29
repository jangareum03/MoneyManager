package com.areum.moneymanager.service.validation;

import com.areum.moneymanager.dto.common.ErrorDTO;
import com.areum.moneymanager.enums.RegexPattern;
import com.areum.moneymanager.exception.code.ErrorCode;
import com.areum.moneymanager.exception.custom.ClientException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

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
@Component
public class MemberValidator {

	private final PasswordEncoder passwordEncoder;

	public MemberValidator(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}


	/**
	 * 비밀번호의 기본 형식을 확인합니다.
	 *
	 * @param errorDTO 에러정보를 담은 객체
	 * @throws ClientException 비밀번호 형식이 유효하지 않을 경우 발생
	 */
	public <T> void validatePasswordFormat(ErrorDTO<T> errorDTO) {
		List<ErrorCode> errorCodes = ErrorCode.getGroupByPrefix(errorDTO.getCode());
		String pwd = (String) errorDTO.getRequestData();

		//비밀번호가 미입력된 경우
		if (Objects.isNull(pwd) || pwd.isBlank()) {
			throw new ClientException(ErrorCode.getByName(errorCodes, "MISSING"), errorDTO);
		}

		//비밀번호 정규식 불일치
		if (!pwd.matches(RegexPattern.MEMBER_PWD.getPattern())) {
			throw new ClientException(ErrorCode.getByName(errorCodes, "FORMAT"), errorDTO);
		}
	}


	/**
	 * 비밀번호 형식과 기존 비밀번호와 동일한지 확인합니다.
	 *
	 * @param errorDTO 에러정보를 담은 객체
	 * @throws ClientException
	 */
	public <T> void validatePasswordFormatAndMatch(ErrorDTO<T> errorDTO) {
		validatePasswordFormat(errorDTO);

		String changePwd = (String) errorDTO.getRequestData();
	}

}
