package com.moneymanager.member.service.generator;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.generator<br>
 * 파일이름       : UuidTokenGenerator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 13<br>
 * 설명              : UUID 토큰 기능을 제공하는 클래스
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
@Component
public class UuidTokenGenerator {

    public String generate() {
        return UUID.randomUUID().toString();
    }

    public String hash(String token) {
        byte[] bytes =  DigestUtils.sha256(token);

        return Base64.encodeBase64String(bytes);
    }

}