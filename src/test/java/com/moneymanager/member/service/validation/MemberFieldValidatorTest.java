package com.moneymanager.member.service.validation;

import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.stream.StringTestStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static com.moneymanager.global.enums.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.validation<br>
 * 파일이름       : MemberFieldValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-09-23<br>
 * 역할             : MemberFieldValidator 단위 테스트
 * </p>
 */
@DisplayName("MemberFieldValidator 테스트")
class MemberFieldValidatorTest {

    MemberFieldValidator target = new MemberFieldValidator();

    @Nested
    @DisplayName("아이디 검증 메서드는")
    class Username {

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("validFormat")
        @DisplayName("정상적인 형식이면 검증에 통과한다.")
        void validates_whenFormatIsValid(String username) {
            //when
            assertThatCode(() -> target.validateUsername(username))
                    .doesNotThrowAnyException();
        }

        static Stream<Arguments> validFormat() {
            return Stream.of(
                    Arguments.of(
                            named("대문자만 포함된 아이디", "ABCED")
                    ),
                    Arguments.of(
                            named("소문자만 포함된 아이디", "abcde")
                    ),
                    Arguments.of(
                            named("영어 + 숫자 조합인 아이디", "aBc123")
                    )
            );
        }

        @ParameterizedTest(name = "[{index}] {0} 아이디")
        @MethodSource("validLength")
        @DisplayName("정상적인 길이면 검증에 통과한다.")
        void validates_whenLengthIsValid(String username) {
            //when
            assertThatCode(() -> target.validateUsername(username))
                    .doesNotThrowAnyException();
        }

        static Stream<Arguments> validLength() {
            return StringTestStream.validLengths("a", 4, 15);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("null이거나 비어있으면 예외가 발생한다.")
        void throwsException_whenUsernameIsNullOrEmpty(String username) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateUsername(username));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasMessageKey("error.member.username.required");
        }

        @ParameterizedTest
        @MethodSource("invalidLength")
        @DisplayName("길이가 범위에서 벗어나면 예외가 발생한다.")
        void throwsException_whenLengthIsInvalid(String username) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateUsername(username));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_LENGTH)
                    .hasMessageKey("error.member.username.length");
        }

        static Stream<Arguments> invalidLength() {
            return StringTestStream.invalidLengths("a", 4, 15);
        }

        @ParameterizedTest
        @MethodSource("invalidFormat")
        @DisplayName("유효하지 않은 형식이면 예외가 발생한다.")
        void throwsException_whenFormatIsInvalid(String username) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateUsername(username));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_FORMAT)
                    .hasMessageKey("error.member.username.invalidFormat");
        }

        static Stream<Arguments> invalidFormat() {
            return Stream.of(
                    Arguments.of(
                            named("특수문자가 포함된 아이디", "abc!!")
                    ),
                    Arguments.of(
                            named("한글이 포함된 아이디", "아이디12")
                    )
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"12345", "123abc"})
        @DisplayName("숫자로 시작하는 아이디면 예외가 발생한다.")
        void throwsException_whenStartsWithNumber(String username) {
            //when
            Throwable throwable = catchThrowable(() -> target.validateUsername(username));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_FORMAT)
                    .hasMessageKey("error.member.username.invalidStart");
        }

    }

}