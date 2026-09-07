package com.moneymanager.member.service.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.generator<br>
 * 파일이름       : MemberNumberGeneratorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 5<br>
 * 설명              : MemberNumberGenerator 클래스 로직을 검증하는 단위 테스트 클래스
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
class MemberNumberGeneratorTest {

    MemberNumberGenerator target = new MemberNumberGenerator();

    @Nested
    @DisplayName("회원번호를 생성할 때")
    class Generate {

        @Test
        @DisplayName("영어와 숫자 조합으로 12자리 문자로 생성한다.")
        void generates12DigitAlphanumericUserNumber_whenRequested() {
        	//when
            String result = target.generate();
        	
        	//then
        	assertThat(result.length()).isEqualTo(12);
            assertThat(result.matches("M[0-9A-Za-z]{11}")).isTrue();
        }
        
        @Test
        @DisplayName("생성할때마다 다른 회원번호를 생성한다.")
        void generatesUniqueUserNumber_whenCalledMultipleTimes() {
            //when
            String number1 = target.generate();
            String number2 = target.generate();

            //then
            assertThat(number1).isNotEqualTo(number2);
        }

    }

}