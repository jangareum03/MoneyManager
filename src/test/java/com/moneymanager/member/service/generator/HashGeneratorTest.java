package com.moneymanager.member.service.generator;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.generator<br>
 * 파일이름       : HashGeneratorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 14<br>
 * 설명              : HashGenerator 클래스 로직을 검증하는 단위 테스트 클래스
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
class HashGeneratorTest {

    HashGenerator target = new HashGenerator();

    @Nested
    @DisplayName("SHA-256 해시값 얻을 때")
    class GetSha256 {

        @Test
        @DisplayName("해시값을 반환한다.")
        void returnHashingValue_whenValueGiven() {
            //given
            String value = "value";

            //when
            String result = target.sha256(value);

            //then
            assertThat(result).isNotEqualTo(value);
            assertThat(result).isEqualTo(Base64.encodeBase64String(DigestUtils.sha256(value)));
        }
        
        @Test
        @DisplayName("원본 값이 다르면 다른 hash를 반환한다.")
        void returnsDifferentHash_whenValueIsDifferent() {
        	//given
            String value1 = "value1";
            String value2 = "value2";
        	
        	//when
            String result1 = target.sha256(value1);
            String result2 = target.sha256(value2);
        	
        	//then
        	assertThat(result1).isNotEqualTo(result2);
        }
        
        @Test
        @DisplayName("동일한값이면 같은 hash값을 반환한다.")
        void returnsSameHash_whenValueIsSame() {
            //given
            String value = "value1";

            //when
            String result1 = target.sha256(value);
            String result2 = target.sha256(value);

            //then
            assertThat(result1).isEqualTo(result2);
        }

    }

}