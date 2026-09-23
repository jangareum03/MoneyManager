package com.moneymanager.member.service.validation;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.member.domain.dto.request.*;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.enums.WithdrawalReason;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

import static com.moneymanager.global.exception.code.ErrorCode.INVALID_VALUE;

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
@Component
@RequiredArgsConstructor
public class MemberValidator {

    private final MemberFieldValidator fieldValidator;
    private final MemberProfileValidator profileValidator;

    public void validateSignup(MemberSignUpRequest request) {
        String work = "회원가입 검증";

        fieldValidator.validateUsername(request.getUsername(), work);
        fieldValidator.validatePassword(request.getPassword(), work);
        fieldValidator.validateName(request.getName(), work);
        fieldValidator.validateBirthdate(request.getBirthdate(), work);
        fieldValidator.validateNickname(request.getNickname(), work);
        fieldValidator.validateEmail(request.getEmail(), work);
        fieldValidator.validateGender(request.getGender(), work);
    }

    public void validateLogin(String username, String password) {
        String work = "로그인 요청 검증";

        fieldValidator.validateUsername(username, work);
        fieldValidator.validatePassword(password, work);
    }

    public void validateFindId(FindIdRequest request) {
        String work = "아이디 찾기 검증";

        fieldValidator.validateName(request.getName(), work);
        fieldValidator.validateEmail(request.getEmail(), work);
    }

    public void validateFindPassword(FindPwdRequest request) {
        String work = "비밀번호 찾기 검증";

        fieldValidator.validateName(request.getName(), work);
        fieldValidator.validateUsername(request.getUsername(), work);
    }

    public void validateMemberUpdate(MemberUpdateRequest request) {
        String work = "회원정보 검증";

        switch (request.determineUpdateType()) {
            case NAME -> fieldValidator.validateName(request.getName(), work);
            case GENDER -> fieldValidator.validateGender(request.getGender(), work);
            case  PASSWORD -> fieldValidator.validatePassword(request.getPassword(), work);
        }
    }

    public void validateUsername(String username) {
        String work = "아이디 검증";

        fieldValidator.validateUsername(username, work);
    }

    public void validateProfile(MultipartFile file) {
        profileValidator.validate(file);
    }

    public void validateEmail(String email) {
        String work = "이메일 검증";

        fieldValidator.validateEmail(email, work);
    }

    public void validateWithdrawal(MemberWithdrawalRequest request) {
        String work = "회원탈퇴 검증";

        fieldValidator.validatePassword(request.password(), work);

        try{
            WithdrawalReason.from(request.reason());
        }catch (NoSuchElementException e) {
            throw new ApplicationException(
                    INVALID_VALUE,
                    LogContent.of(
                            "WithdrawalReason 변환",
                            Member.class,
                            "reason", request.reason()
                    )
            ).withMessageKey("member.reason.invalid");
        }
    }

}