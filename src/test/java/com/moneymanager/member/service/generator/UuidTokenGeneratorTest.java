package com.moneymanager.member.service.generator;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.generator<br>
 * 파일이름       : UuidTokenGeneratorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 13<br>
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
 * 		 	  <td>26. 9. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class UuidTokenGeneratorTest {

    UuidTokenGenerator target = new UuidTokenGenerator();

    @Test
    @DisplayName("토큰을 생성한다.")
    void returnToken() {
    	//when
        String result = target.generate();
    	
    	//then
    	assertThat(result).isNotBlank();
    }
    
    @Test
    @DisplayName("토큰을 SHA-256으로 해싱한다.")
    void returnHashingToken_whenTokenGiven() {
    	//given
       String token = "token";
    	
    	//when
        String result = target.hash(token);
    	
    	//then
    	assertThat(result).isNotEqualTo(token);
        assertThat(result).isEqualTo(Base64.encodeBase64String(DigestUtils.sha256(token)));
    }

}