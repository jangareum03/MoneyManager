package com.moneymanager.member.service.validation;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.moneymanager.global.exception.code.ErrorCode.REQUIRED_NOT_EXIST;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.validation<br>
 * 파일이름       : MemberValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 4<br>
 * 설명              : MemberValidator 클래스 로직을 검증하는 단위 테스트 클래스
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
class MemberValidatorTest {

    MemberValidator target = new MemberValidator();

    @Nested
    @DisplayName("회원가입 요청 검증할 때")
    class SignUp {

        @Test
        @DisplayName("정상적인 회원 정보면 검증을 성공한다.")
        void validatesMember_whenMemberIsValid() {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(MemberTestData.DEFAULT_PASSWORD)
                    .name(MemberTestData.DEFAULT_NAME)
                    .birthdate(MemberTestData.DEFAULT_BIRTHDATE)
                    .nickname(MemberTestData.DEFAULT_NICKNAME)
                    .email(MemberTestData.DEFAULT_EMAIL)
                    .gender(MemberTestData.DEFAULT_GENDER.getValue())
                    .build();

            //when
            assertDoesNotThrow(() -> target.signUp(request));
        }

        @Test
        @DisplayName("요청이 null이면 예외를 발생시킨다.")
        void throwsException_whenRequestIsNull() {
            //when
            Throwable throwable = catchThrowable(() -> target.signUp(null));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_NOT_EXIST)
                    .hasWork("회원가입 요청 검증")
                    .hasTarget(MemberSignUpRequest.class)
                    .hasValue(null);
        }

        @Test
        @DisplayName("회원 정보가 유효하지 않으면 예외를 전파한다.")
        void throwsException_whenUserIsInvalid() {
        	//given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(MemberTestData.DEFAULT_PASSWORD)
                    .name(MemberTestData.DEFAULT_NAME)
                    .birthdate(MemberTestData.DEFAULT_BIRTHDATE)
                    .nickname(MemberTestData.DEFAULT_NICKNAME)
                    .email(MemberTestData.DEFAULT_EMAIL)
                    .gender(null)
                    .build();
        	
        	//when
            assertThatThrownBy(() -> target.signUp(request))
                    .isInstanceOf(ApplicationException.class);
        }

    }

}