package com.moneymanager.member.service.validation;

import com.moneymanager.global.domain.enums.RegexPattern;
import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.domain.enums.MemberGender;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.moneymanager.global.exception.code.ErrorCode.INVALID_VALUE;
import static com.moneymanager.global.exception.code.ErrorCode.REQUIRED_VALUE;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.validation<br>
 * 파일이름       : MemberFieldValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 4<br>
 * 설명              : 회원 필드 검증을 처리하는 클래스
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
public class MemberFieldValidator {

    void validateUsername(String username, String work) {
        if (StringUtil.isNullOrBlank(username)) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            work,
                            "username",
                            username
                    )
            ).withUserMessage("member.username.required");
        }

        if (!username.matches(RegexPattern.MEMBER_USERNAME.getPattern())) {
            throw new ApplicationException(
                    INVALID_VALUE,
                    LogContent.of(
                                    work,
                                    "username",
                                    username
                            )
                            .withOption("format", "영어, 숫자")
                            .withOption("min", 4)
                            .withOption("max", 15)
            ).withUserMessage("member.username.invalid");
        }
    }

    void validatePassword(String password, String work) {
        if (StringUtil.isNullOrBlank(password)) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            work,
                            "password",
                            password
                    )
            ).withUserMessage("member.password.required");
        }

        if (!password.matches(RegexPattern.MEMBER_PWD.getPattern())) {
            throw new ApplicationException(
                    INVALID_VALUE,
                    LogContent.of(
                                    work,
                                    "password",
                                    password
                            ).withOption("format", "영어, 숫자, !, %, #, ^, *")
                            .withOption("min", 8)
                            .withOption("max", 20)
            ).withUserMessage("member.password.invalid");
        }
    }

    void validateName(String name, String work) {
        if (StringUtil.isNullOrBlank(name)) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            work,
                            "name",
                            name
                    )
            ).withUserMessage("member.name.required");
        }

        if (!name.matches(RegexPattern.MEMBER_NAME.getPattern())) {
            throw new ApplicationException(
                    INVALID_VALUE,
                    LogContent.of(
                                    work,
                                    "name",
                                    name
                            )
                            .withOption("format", "한글")
                            .withOption("min", 2)
                            .withOption("max", 5)
            ).withUserMessage("member.name.invalid");
        }
    }

    void validateBirthdate(String birthDate, String work) {
        if (StringUtil.isNullOrBlank(birthDate)) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            work,
                            "birthdate",
                            birthDate
                    )
            ).withUserMessage("member.birthdate.required");
        }

        if (!birthDate.matches(RegexPattern.MEMBER_BIRTH.getPattern())) {
            throw new ApplicationException(
                    INVALID_VALUE,
                    LogContent.of(
                                    work,
                                    "birthdate",
                                    birthDate
                            )
                            .withOption("format", "숫자")
                            .withOption("size", 8)
            ).withUserMessage("member.birthdate.invalid");
        }
    }

    void validateNickname(String nickName, String work) {
        if (StringUtil.isNullOrBlank(nickName)) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            work,
                            "nickname",
                            nickName
                    )
            ).withUserMessage("member.nickname.required");
        }

        if (!nickName.matches(RegexPattern.MEMBER_NICKNAME.getPattern())) {
            throw new ApplicationException(
                    INVALID_VALUE,
                    LogContent.of(
                                    work,
                                    "nickname",
                                    nickName
                            )
                            .withOption("format", "한글, 숫자, 영어")
                            .withOption("min", 2)
                            .withOption("max", 10)
            ).withUserMessage("member.nickname.invalid");
        }
    }

    void validateEmail(String email, String work) {
        if (StringUtil.isNullOrBlank(email)) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            work,
                            "email",
                            email
                    )
            ).withUserMessage("member.email.required");
        }

        if (!email.matches(RegexPattern.MEMBER_EMAIL.getPattern())) {
            throw new ApplicationException(
                    INVALID_VALUE,
                    LogContent.of(
                                    work,
                                    "email",
                                    email
                            )
                            .withOption("format", "영어, 숫자, !, #, $, %, &, `, *, +, -, /, =, ?, ^, _, ', {, |, }, ~ (예: test@naver.com)")
            ).withUserMessage("member.email.invalid");
        }
    }

    void validateGender(String gender, String work) {
        if (StringUtil.isNullOrBlank(gender)) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            work,
                            "gender",
                            gender
                    )
            ).withUserMessage("member.gender.required");
        }

        List<String> genders = Arrays.stream(MemberGender.values())
                .map(g -> g.getValue().toLowerCase())
                .toList();

        if (!genders.contains(gender)) {
            throw new ApplicationException(
                    INVALID_VALUE,
                    LogContent.of(
                            work,
                            "gender",
                            gender
                    ).withOption("allowed", genders)
            ).withUserMessage("member.gender.invalid");
        }
    }

}