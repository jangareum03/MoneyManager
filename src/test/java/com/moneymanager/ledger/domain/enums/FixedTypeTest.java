package com.moneymanager.ledger.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.enums<br>
 * 파일이름       : FixedTypeTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 21<br>
 * 설명              : FixedType 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 8. 21</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class FixedTypeTest {

    @Nested
    @DisplayName("FixedType 변환할 때")
    class FromTest {

        @Nested
        @DisplayName("성공")
        class Success {

            @ParameterizedTest
            @MethodSource("validFixedTypes")
            @DisplayName("대문자 값이면 변환한다.")
            void createsFixedType_whenUpperCaseIsGiven(String value, FixedType expected) {
                //when
                FixedType result = FixedType.from(value.toUpperCase());

                //then
                assertThat(result).isSameAs(expected);
            }

            @ParameterizedTest
            @MethodSource("validFixedTypes")
            @DisplayName("소문자 값이면 변환한다.")
            void createsFixedType_whenLowerCaseIsValid(String value, FixedType expected) {
                //when
                FixedType result = FixedType.from(value.toLowerCase());

                //then
                assertThat(result).isSameAs(expected);
            }

            static Stream<Arguments> validFixedTypes() {
                return Stream.of(
                        Arguments.of(
                                named("REPEAT 경우", "Y"),
                                FixedType.REPEAT
                        ),
                        Arguments.of(
                                named("VARIABLE 경우", "n"),
                                FixedType.VARIABLE
                        )
                );
            }

        }


        @Nested
        @DisplayName("실패")
        class Failure {

            @ParameterizedTest
            @ValueSource(strings = {"TYPE", "b", "cash1"})
            @DisplayName("유효하지 않은 고정 여부면 예외를 발생시킨다.")
            void throwsNoSuchElementException_whenFixedTypeIsInvalid(String value) {
                assertThatThrownBy(() -> FixedType.from(value))
                        ;
            }

        }

    }

}