package com.moneymanager.member.service.validation;

import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.stream.StringTestStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.ErrorCode.INVALID_VALUE;
import static com.moneymanager.global.exception.code.ErrorCode.REQUIRED_VALUE;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.validation<br>
 * 파일이름       : MemberFieldValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 5<br>
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
 * 		 	  <td>26. 9. 5</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class MemberFieldValidatorTest {

    private final MemberValidator target = new MemberValidator();

    @Nested
    @DisplayName("아이디 검증할 때")
    class ValidateUsername {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenUsernameIsNullAndBlank(String username) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(username)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasField("username")
                    .hasValue(username);
        }

        @ParameterizedTest
        @MethodSource("invalidUsernames")
        @DisplayName("형식이 유효하지 않으면 예외를 발생시킨다.")
        void throwsException_whenUsernameIsInvalid(String username) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(username)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_VALUE)
                    .hasField("username")
                    .hasValue(username)
                    .hasOption("format", "영어, 숫자")
                    .hasOption("min", 4)
                    .hasOption("max", 15);
        }

        static Stream<Arguments> invalidUsernames() {
            return Stream.concat(
                    StringTestStream.invalidLengths("a", 4, 15),
                    Stream.of(
                            Arguments.of(
                                    named("한글이 포함된 경우", "아이디123")
                            ),
                            Arguments.of(
                                    named("특수문자가 포함된 경우", "abc#123")
                            ),
                            Arguments.of(
                                    named("한문이 포함된 경우", "test123家")
                            )
                    )
            );
        }

    }


    @Nested
    @DisplayName("비밀번호 검증할 때")
    class ValidatePassword {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenPasswordIsNullAndBlank(String password) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(password)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasField("password")
                    .hasValue(password);
        }

        @ParameterizedTest
        @MethodSource("invalidPasswords")
        @DisplayName("형식이 유효하지 않으면 예외를 발생시킨다.")
        void throwsException_whenPasswordIsInvalid(String password) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(password)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_VALUE)
                    .hasField("password")
                    .hasValue(password)
                    .hasOption("format", "영어, 숫자, !, %, #, ^, *")
                    .hasOption("min", 8)
                    .hasOption("max", 20);
        }

        static Stream<Arguments> invalidPasswords() {
            return Stream.concat(
                    StringTestStream.invalidLengths("a", 8, 20),
                    Stream.of(
                            Arguments.of(
                                    named("한글이 포함된 경우", "비밀번호1234")
                            ),
                            Arguments.of(
                                    named("허용하지 않은 특수문자 포함된 경우", "password@@")
                            ),
                            Arguments.of(
                                    named("한문이 포함된 경우", "test123家")
                            )
                    )
            );
        }

    }


    @Nested
    @DisplayName("이름 검증할 때")
    class ValidateName {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenNameIsNullAndBlank(String name) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(MemberTestData.DEFAULT_PASSWORD)
                    .name(name)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasField("name")
                    .hasValue(name);
        }

        @ParameterizedTest
        @MethodSource("invalidNames")
        @DisplayName("형식이 유효하지 않으면 예외를 발생시킨다.")
        void throwsException_whenNameIsInvalid(String name) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(MemberTestData.DEFAULT_PASSWORD)
                    .name(name)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_VALUE)
                    .hasField("name")
                    .hasValue(name)
                    .hasOption("format", "한글")
                    .hasOption("min", 2)
                    .hasOption("max", 5);
        }

        static Stream<Arguments> invalidNames() {
            return Stream.concat(
                    StringTestStream.invalidLengths("김", 2, 5),
                    Stream.of(
                            Arguments.of(
                                    named("영어 포함된 경우", "홍길동a")
                            ),
                            Arguments.of(
                                    named("숫자 포함된 경우", "홍길동5")
                            ),
                            Arguments.of(
                                    named("한문이 포함된 경우", "紅길동")
                            )
                    )
            );
        }

    }


    @Nested
    @DisplayName("생년월일 검증할 때")
    class ValidateBirthdate {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenBirthdateIsNullAndBlank(String birthdate) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(MemberTestData.DEFAULT_PASSWORD)
                    .name(MemberTestData.DEFAULT_NAME)
                    .birthdate(birthdate)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasField("birthdate")
                    .hasValue(birthdate);
        }

        @ParameterizedTest
        @MethodSource("invalidBirthdays")
        @DisplayName("형식이 유효하지 않으면 예외를 발생시킨다.")
        void throwsException_whenBirthdateIsInvalid(String birthdate) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(MemberTestData.DEFAULT_PASSWORD)
                    .name(MemberTestData.DEFAULT_NAME)
                    .birthdate(birthdate)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_VALUE)
                    .hasField("birthdate")
                    .hasValue(birthdate)
                    .hasOption("format", "숫자")
                    .hasOption("size", 8);
        }

        static Stream<Arguments> invalidBirthdays() {
            return Stream.concat(
                    StringTestStream.invalidLengths("1", 1, 8),
                    Stream.of(
                            Arguments.of(
                                    named("한글 포함된 경우", "1234567가")
                            ),
                            Arguments.of(
                                    named("영어 포함된 경우", "1234567a")
                            ),
                            Arguments.of(
                                    named("한문이 포함된 경우", "1234567金")
                            )
                    )
            );
        }

    }


    @Nested
    @DisplayName("닉네임 검증할 때")
    class ValidateNickname {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenNicknameIsNullAndBlank(String nickname) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(MemberTestData.DEFAULT_PASSWORD)
                    .name(MemberTestData.DEFAULT_NAME)
                    .birthdate(MemberTestData.DEFAULT_BIRTHDATE)
                    .nickname(nickname)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasField("nickname")
                    .hasValue(nickname);
        }

        @ParameterizedTest
        @MethodSource("invalidNicknames")
        @DisplayName("형식이 유효하지 않으면 예외를 발생시킨다.")
        void throwsException_whenNicknameIsInvalid(String nickname) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(MemberTestData.DEFAULT_PASSWORD)
                    .name(MemberTestData.DEFAULT_NAME)
                    .birthdate(MemberTestData.DEFAULT_BIRTHDATE)
                    .nickname(nickname)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_VALUE)
                    .hasField("nickname")
                    .hasValue(nickname)
                    .hasOption("format", "한글, 숫자, 영어")
                    .hasOption("min", 2)
                    .hasOption("max", 10);
        }

        static Stream<Arguments> invalidNicknames() {
            return Stream.concat(
                    StringTestStream.invalidLengths("가", 2, 10),
                    Stream.of(
                            Arguments.of(
                                    named("한문이 포함된 경우", "홍길동전水")
                            ),
                            Arguments.of(
                                    named("특수문자가 포함된 경우", "홍길동전★")
                            )
                    )
            );
        }

    }


    @Nested
    @DisplayName("이메일 검증할 때")
    class ValidateEmail {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenEmailIsNullAndBlank(String email) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(MemberTestData.DEFAULT_PASSWORD)
                    .name(MemberTestData.DEFAULT_NAME)
                    .birthdate(MemberTestData.DEFAULT_BIRTHDATE)
                    .nickname(MemberTestData.DEFAULT_NICKNAME)
                    .email(email)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasField("email")
                    .hasValue(email);
        }

        @ParameterizedTest
        @MethodSource("invalidEmails")
        @DisplayName("형식이 유효하지 않으면 예외를 발생시킨다.")
        void throwsException_whenEmailIsInvalid(String email) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(MemberTestData.DEFAULT_PASSWORD)
                    .name(MemberTestData.DEFAULT_NAME)
                    .birthdate(MemberTestData.DEFAULT_BIRTHDATE)
                    .nickname(MemberTestData.DEFAULT_NICKNAME)
                    .email(email)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_VALUE)
                    .hasField("email")
                    .hasValue(email)
                    .hasOption("format", "영어, 숫자, !, #, $, %, &, `, *, +, -, /, =, ?, ^, _, ', {, |, }, ~ (예: test@naver.com)");
        }

        static Stream<Arguments> invalidEmails() {
            return  Stream.of(
                    Arguments.of(
                            named("로컬파트만 있는 경우", "test134")
                    ),
                    Arguments.of(
                            named("로컬파트와 @만 있는 경우", "test134@")
                    ),
                    Arguments.of(
                            named("도메인파트에 점이 없는 경우", "test134@naver")
                    ),
                    Arguments.of(
                            named("도메인파트만 있는 경우", "naver.com")
                    ),
                    Arguments.of(
                            named("@로 시작하는 경우", "@naver.com")
                    )
            );
        }

    }


    @Nested
    @DisplayName("성별 검증할 때")
    class ValidateGender {

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenGenderIsNullAndBlank(String gender) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(MemberTestData.DEFAULT_PASSWORD)
                    .name(MemberTestData.DEFAULT_NAME)
                    .birthdate(MemberTestData.DEFAULT_BIRTHDATE)
                    .nickname(MemberTestData.DEFAULT_NICKNAME)
                    .email(MemberTestData.DEFAULT_EMAIL)
                    .gender(gender)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasField("gender")
                    .hasValue(gender);
        }

        @ParameterizedTest
        @ValueSource(strings = {"y", "male"})
        @DisplayName("유효하지 않은 성별이면 예외를 발생시킨다.")
        void throwsException_whenGenderIsInvalid(String gender) {
            //given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(MemberTestData.DEFAULT_PASSWORD)
                    .name(MemberTestData.DEFAULT_NAME)
                    .birthdate(MemberTestData.DEFAULT_BIRTHDATE)
                    .nickname(MemberTestData.DEFAULT_NICKNAME)
                    .email(MemberTestData.DEFAULT_EMAIL)
                    .gender(gender)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.signUp(request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_VALUE)
                    .hasField("gender")
                    .hasValue(gender)
                    .hasOption("allowed", "n, m, f");
        }

    }

}