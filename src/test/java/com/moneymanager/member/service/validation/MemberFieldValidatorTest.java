package com.moneymanager.member.service.validation;

import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static com.moneymanager.global.exception.code.ErrorCode.INVALID_FORMAT;
import static com.moneymanager.global.exception.code.ErrorCode.REQUIRED_VALUE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.validation<br>
 * 파일이름       : MemberFieldValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 23<br>
 * 설명              : MemberFieldValidator 단위 테스트
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
 * 		 	  <td>26. 9. 23</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class MemberFieldValidatorTest {

    MemberFieldValidator target = new MemberFieldValidator();

    @Nested
    @DisplayName("비밀번호 검증할 때")
    class Password {

        private final String work = "작업";
        
        @Test
        @DisplayName("정상적인 값이면 검증에 통과한다.")
        void validatesPassword_whenPasswordIsValid() {
        	//given
            String password = MemberTestData.DEFAULT_PASSWORD;
        	
        	//when
            assertThatCode(() -> target.validatePassword(password, work))
                    .doesNotThrowAnyException();
        }
        
        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("null이거나 빈 문자열이면 검증에 실패한다.")
        void throwsException_whenNullOrEmpty(String password) {
        	//when
            Throwable throwable = catchThrowable(() -> target.validatePassword(password, work));
        	
        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasWork(work)
                    .hasField("password")
                    .hasValue(password)
                    .hasMessageKey("member.password.required");
        }
        
        @Test
        @DisplayName("유효하지 않으면 검증에 실패한다.")
        void throwsException_whenMismatch() {
            //given
            String password = "비밀번호123";

            //when
            Throwable throwable = catchThrowable(() -> target.validatePassword(password, work));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_FORMAT)
                    .hasWork(work)
                    .hasField("password")
                    .hasValue(password)
                    .hasOption("format", "영어, 숫자, !, %, #, ^, *")
                    .hasOption("min", 8)
                    .hasOption("max", 20)
                    .hasMessageKey("member.password.invalid");
        }

    }

}