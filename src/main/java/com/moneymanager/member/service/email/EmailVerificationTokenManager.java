package com.moneymanager.member.service.email;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.service.generator.EmailTokenGenerator;
import com.moneymanager.member.service.redis.EmailVerificationRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;
import static com.moneymanager.global.exception.code.ErrorCode.MISMATCH;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.email<br>
 * 파일이름       : EmailVerificationTokenManager<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 21<br>
 * 설명              : 이메일 인증토큰과 관련된 제공하는 클래스
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
public class EmailVerificationTokenManager {

    private final EmailVerificationRedisService redisService;
    private final EmailTokenGenerator tokenGenerator;

    public String createToken() {
        return tokenGenerator.generate();
    }

    public void saveToken(String email, String token) {
        redisService.saveToken(email, token);
    }

    public Optional<String> getToken(String email) {
        if(existsToken(email)) {
            return Optional.of(redisService.getToken(email));
        }

        return Optional.empty();
    }

    public boolean existsToken(String email) {
        return redisService.getToken(email) != null;
    }

    public void verifyToken(String email, String token) {
        Optional<String> redisToken = getToken(email);

        if(redisToken.isEmpty()) {
            throw new ApplicationException(
                    DATA_NOT_FOUND,
                    LogContent.of(
                            "이메일 토큰 검증",
                            "email",
                            EmailMasker.mask(email)
                    ).withCause("미인증된 이메일")
            ).withMessageKey("member.signup.failed");
        }

        if(!redisToken.get().equals(token)) {
            throw new ApplicationException(
                    MISMATCH,
                    LogContent.of(
                            "이메일 토큰 검증",
                            "token",
                            StringUtil.masking(token, 3, token.length() - 3)
                    )
            ).withMessageKey("member.signup.failed");
        }
    }

    public void deleteToken(String email) {
        redisService.deleteToken(email);
    }

}