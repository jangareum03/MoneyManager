package com.moneymanager.member.service.email;

import com.moneymanager.global.generator.HashGenerator;
import com.moneymanager.global.generator.UuidGenerator;
import com.moneymanager.member.service.redis.PasswordResetRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.email<br>
 * 파일이름       : PasswordResetTokenManager<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 21<br>
 * 설명              : 비밀번호 초기화와 관련된 제공하는 클래스
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
 * 		 	  <td>26. 9. 21</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
@RequiredArgsConstructor
public class PasswordResetTokenManager {

    private final PasswordResetRedisService redisService;
    private final UuidGenerator uuidGenerator;
    private final HashGenerator hashGenerator;

    public String createToken() {
        return uuidGenerator.generate();
    }

    public void saveToken(String token) {
        redisService.saveToken(getHashToken(token));
    }

    public boolean existsToken(String token) {
        return "1".equals(redisService.getToken(getHashToken(token)));
    }

    public void deleteToken(String  token) {
        redisService.deleteToken(getHashToken(token));
    }


    //===== 유틸 메서드 =====
    private String getHashToken(String token) {
        return hashGenerator.sha256(token);
    }

}