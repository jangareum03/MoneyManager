package com.moneymanager.member.service.generator;

import com.moneymanager.member.service.email.EmailCodeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.generator<br>
 * 파일이름       : EmailCodeGeneratorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 6<br>
 * 설명              : EmailCodeGenerator 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 9. 6</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class EmailCodeGeneratorTest {

    EmailCodeGenerator target = new EmailCodeGenerator();

    @Nested
    @DisplayName("인증 코드 생성할 때")
    class Create {
        
        @Test
        @DisplayName("숫자 6자리 문자로 생성한다.")
        void generatesSixDigitVerificationCode_whenCodeIsGenerated() {
            //when
            String result = target.generate();
        	
        	//then
        	assertThat(result.length()).isEqualTo(6);
            assertThat(result.matches("[0-9]{6}")).isTrue();
        }
        
        @Test
        @DisplayName("생성할때마다 다른 인증코드를 생성한다.")
        void generatesUniqueVerificationCode_whenCalledMultipleTimes() {
        	//when
            String code1 = target.generate();
            String code2 = target.generate();

        	//then
        	assertThat(code1).isNotEqualTo(code2);
        }

    }

}