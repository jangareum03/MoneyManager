package com.moneymanager.member.service.validation;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.domain.dto.request.FindIdRequest;
import org.springframework.stereotype.Component;

import static com.moneymanager.global.exception.code.ErrorCode.INVALID_FORMAT;
import static com.moneymanager.global.exception.code.ErrorCode.REQUIRED_VALUE;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.validation<br>
 * 파일이름       : AccountValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 4<br>
 * 설명              : 회원 인증 관련 검증 로직을 처리하는 클래스
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
 * 		 	  <td>26. 9. 4</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class AccountValidator {

    private final MemberFieldValidator fieldValidator = new MemberFieldValidator();

    public void validateLogin(String username, String password) {
        String work = "로그인 요청 검증";

        fieldValidator.validateUsername(username, work);
        fieldValidator.validatePassword(password, work);
    }

    public void validateFindId(FindIdRequest request) {
        String work = "계정 찾기 검증";

        fieldValidator.validateName(request.getName(), work);
        fieldValidator.validateEmail(request.getEmail(), work);
    }

    public void validateEmail(String email) {
        String work = "이메일 검증";

        fieldValidator.validateEmail(email, work);
    }

    public void validateEmailCode(String emailCode) {
        if(StringUtil.isNullOrBlank(emailCode)) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            "이메일 인증코드 검증",
                            "emailCode",
                            emailCode
                    )
            ).withUserMessage("member.email.code.required");
        }

        if(!emailCode.matches("[0-9]{6}")) {
            throw new ApplicationException(
                    INVALID_FORMAT,
                    LogContent.of(
                            "이메일 인증코드 검증",
                            "emailCode",
                            emailCode
                    )
            ).withUserMessage("member.email.code.invalid");
        }
    }

}