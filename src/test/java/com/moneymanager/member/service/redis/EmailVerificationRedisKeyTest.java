package com.moneymanager.member.service.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.redis<br>
 * 파일이름       : EmailVerificationRedisKeyTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 6<br>
 * 설명              : EmailVerificationRedisKey 클래스 로직을 검증하는 단위 테스트 클래스
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
class EmailVerificationRedisKeyTest {

    EmailVerificationRedisKey target =  new EmailVerificationRedisKey();

    @Nested
    @DisplayName("이메일 코드 반환할 때")
    class GetCode {
        
        @Test
        @DisplayName("'email-verification:code:' 로 시작한다.")
        void returnsEmail_whenVerificationCodeKeyMatches() {
        	//given
            String email = "test@naver.com";
        	
        	//when
            String result =  target.code(email);
        	
        	//then
            String prefix = result.substring(0, result.lastIndexOf(':') + 1);

        	assertThat(prefix).isEqualTo("email-verification:code:");
            assertThat(result).isEqualTo(prefix + email);
        }

    }

}