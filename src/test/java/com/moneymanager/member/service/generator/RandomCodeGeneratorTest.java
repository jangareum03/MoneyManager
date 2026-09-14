package com.moneymanager.member.service.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.generator<br>
 * 파일이름       : RandomCodeGeneratorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 14<br>
 * 설명              : RandomCodeGenerator 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 9. 14</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class RandomCodeGeneratorTest {

    RandomCodeGenerator target = new RandomCodeGenerator();

    @Nested
    @DisplayName("숫자로만 코드 생성할 때")
    class NumericCode {

        @ParameterizedTest
        @MethodSource("validNumericLengths")
        @DisplayName("인자 길이만큼 코드를 생성한다.")
        void createsCode_whenLengthIsValid(int length) {
            //when
            String result = target.generateNumeric(length);

            //then
            assertThat(result.length()).isEqualTo(length);
            assertThat(result.matches("[0-9]{%d}".formatted(length))).isTrue();
        }

        static Stream<Arguments> validNumericLengths() {
            return Stream.of(
                    Arguments.of(named("1인 경우", 1)),
                    Arguments.of(named("2인 경우", 2)),
                    Arguments.of(named("5인 경우", 5)),
                    Arguments.of(named("10인 경우", 10)),
                    Arguments.of(named("11인 경우", 11))
            );
        }

        @Test
        @DisplayName("생성할때마다 다른 인증코드를 생성한다.")
        void generatesUniqueVerificationCode_whenCalledMultipleTimes() {
            //given
            int length = 3;

            //when
            String code1 = target.generateNumeric(length);
            String code2 = target.generateNumeric(length);

            //then
            assertThat(code1).isNotEqualTo(code2);
        }

    }


    @Nested
    @DisplayName("숫자와 문자 조합으로 코드 생성할 때")
    class AlphanumericCode {

        @ParameterizedTest
        @MethodSource("validNumericLengths")
        @DisplayName("인자 길이만큼 코드를 생성한다.")
        void createsCode_whenLengthIsValid(int length) {
            //when
            String result = target.generateAlphanumeric(length);

            //then
            assertThat(result.length()).isEqualTo(length);
            assertThat(result.matches("[0-9A-Za-z]{%d}".formatted(length))).isTrue();
        }

        static Stream<Arguments> validNumericLengths() {
            return Stream.of(
                    Arguments.of(named("1인 경우", 1)),
                    Arguments.of(named("2인 경우", 2)),
                    Arguments.of(named("5인 경우", 5)),
                    Arguments.of(named("10인 경우", 10)),
                    Arguments.of(named("11인 경우", 11))
            );
        }

        @Test
        @DisplayName("생성할때마다 다른 인증코드를 생성한다.")
        void generatesUniqueVerificationCode_whenCalledMultipleTimes() {
            //given
            int length = 5;

            //when
            String code1 = target.generateAlphanumeric(length);
            String code2 = target.generateAlphanumeric(length);

            //then
            assertThat(code1).isNotEqualTo(code2);
        }

    }

}