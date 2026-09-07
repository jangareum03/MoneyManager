package com.moneymanager.member.service.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.email<br>
 * 파일이름       : EmailMaskerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 7<br>
 * 설명              : EmailMasker 클래스 로직을 검증하는 단위 테스트 클래스
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
class EmailMaskerTest {

    @Nested
    @DisplayName("이메일 마스킹할 때")
    class Masking {
        
        @ParameterizedTest
        @MethodSource("validEmails")
        @DisplayName("정상적인 이메일과 시작위치라면 마스킹된 이메일을 반환한다.")
        void returnsMaskedEmail_whenEmailAndStartIndexAreValid(int startIndex, String email, String maskedEmail) {
        	//when
            String result = EmailMasker.mask(email, startIndex);
        	
        	//then
        	assertThat(result).isEqualTo(maskedEmail);
        }

        static Stream<Arguments> validEmails() {
            String email = "test@test.com";

            return Stream.of(
                    Arguments.of(
                            named("시작위치가 0인 경우", 0),
                            email,
                            "****@test.com"
                    ),
                    Arguments.of(
                            named("시작위치가 1인 경우", 1),
                            email,
                            "t***@test.com"
                    ),
                    Arguments.of(
                            named("시작위치가 2인 경우", 2),
                            email,
                            "te**@test.com"
                    ),
                    Arguments.of(
                            named("시작위치가 3인 경우", 3),
                            email,
                            "tes*@test.com"
                    )
            );
        }

        @ParameterizedTest
        @ValueSource(ints = {4, 5})
        @DisplayName("시작 위치가 @보다 크면 전체 마스킹되어 반환한다.")
        void returnsFullyMaskedEmail_whenStartIndexIsGreaterThanAtSign(int startIndex) {
        	//given
            String email = "test@test.com";
        	
        	//when
        	String result = EmailMasker.mask(email, startIndex);

        	//then
        	assertThat(result).isEqualTo("****@test.com");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -2})
        @DisplayName("시작 위치가 음수면 예외를 발생시킨다.")
        void throwsIllegalArgumentException_whenStartIndexIsNegative(int startIndex) {
        	//given
            String email = "test@test.com";
        	
        	//when
            assertThatThrownBy(() -> EmailMasker.mask(email, startIndex))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("시작 위치 음수");
        }

    }

}