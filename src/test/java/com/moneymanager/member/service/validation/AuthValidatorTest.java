package com.moneymanager.member.service.validation;

import com.moneymanager.support.ApplicationExceptionAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.ErrorCode.INVALID_FORMAT;
import static com.moneymanager.global.exception.code.ErrorCode.REQUIRED_VALUE;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.validation<br>
 * 파일이름       : AuthValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 7<br>
 * 설명              : AuthValidator 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 9. 7</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class AuthValidatorTest {

    AuthValidator target = new AuthValidator();

    @Nested
    @DisplayName("인증코드 검증할 때")
    class EmailCode {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("인증코드가 null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenVerificationCodeIsNullOrEmpty(String code) {
        	//when
            Throwable throwable = catchThrowable(() -> target.validateEmailCode(code));
        	
        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasWork("이메일 인증코드 검증")
                    .hasField("emailCode")
                    .hasValue(code);
        }
        
        @ParameterizedTest
        @MethodSource("invalidEmailCodes")
        @DisplayName("인증코드가 형식 불일치면 예외를 발생시킨다.")
        void throwsException_whenVerificationCodeIsInvalid(String code) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateEmailCode(code));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_FORMAT)
                    .hasWork("이메일 인증코드 검증")
                    .hasField("emailCode")
                    .hasValue(code);
        }

        static Stream<Arguments> invalidEmailCodes() {
            return Stream.of(
                    Arguments.of(
                            named("5자리 숫자인경우", "12345")
                    ),
                    Arguments.of(
                            named("7자리 숫자인경우", "1234567")
                    ),
                    Arguments.of(
                            named("6자리 한글인경우", "일이삼사오육")
                    ),
                    Arguments.of(
                            named("6자리 영어인경우", "abCDef")
                    ),
                    Arguments.of(
                            named("6자리 복합 문자인경우", "12삼사ef")
                    )
            );
        }

    }

}