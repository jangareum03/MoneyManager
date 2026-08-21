package com.moneymanager.global.security.jwt;

import com.moneymanager.global.domain.dto.response.AccessToken;
import com.moneymanager.global.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.utils<br>
 * 파일이름       : JwtTokenProvider<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 6.<br>
 * 설명              : JWT 토큰 생성 및 유효성 검증하는 클래스
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
 * 		 	  <td>25. 11. 6.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Component
public class JwtTokenProvider {

	private final SecretKey key;

	public JwtTokenProvider(@Value("${secret.key}") String secretKey) {
		this.key = initKey(secretKey);
	}

	/**
	 * 서버 비밀키를 생성합니다.
	 *
	 * @param secretKey	서버키
	 * @return  HMAC-SHA 암호화된 서버키
	 */
	private SecretKey initKey(String secretKey) {
		return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}


	public AccessToken generateAccessToken(CustomUserDetails userDetails) {
		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

		Date now = new Date();

		//60분
		long accessTokenLimit = 1000 * 60 * 60;
		Date expiration = new Date(now.getTime() + accessTokenLimit);

		String token = Jwts.builder()
				.subject(userDetails.getUsername())													//토큰 제목
				.claim("nickname", userDetails.getNickname())					//클레임 설정
				.claim("profile", userDetails.getProfile())
				.claim("role", roles)
				.issuedAt(now)																					//토큰 발급시간
				.expiration(expiration)																		//토큰 만료시간
				.signWith(key)																					//서버키를 암호화
				.compact();

		return new AccessToken(token, expiration);
	}

	public String createAccessToken(String subject) {
		return Jwts.builder()
				.subject(subject)
				.claim("role", "ROLE_USER")
				.signWith(key)
				.compact();
	}

	public AccessToken generateRefreshToken(CustomUserDetails userDetails) {
		Date now = new Date();

		// 1일(=24시간)
		long refreshTokenLimit = 1000 * 60 * 60 * 24;
		Date expiration = new Date(now.getTime() + refreshTokenLimit);

		String token =  Jwts.builder()
				.issuedAt(now)																					//토큰 발급시간
				.expiration(expiration)																		//토큰 만료시간
				.signWith(key)																					//서버키를 암호화
				.compact();

		return new AccessToken(token, expiration);
	}

	public String getUserName(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();

		return claims.getSubject();
	}


	/**
	 * 토큰의 유효성을 검증합니다.
	 *
	 * @param token		검증할 토큰
	 * @return	토큰이 유효하다면 true, 아니면 false
	 */
	public boolean validateToken(String token) {
		try {
			Jwts	.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token);

			return  true;
		}catch ( SecurityException | MalformedJwtException e ) {
			log.warn("잘못된 JWT 서명으로 유효하지 못 합니다.");
		}catch (ExpiredJwtException e) {
			log.warn("JWT 토큰이 만료되었습니다.");
		}catch (UnsupportedJwtException e) {
			log.warn("지원되지 않은 JWT 토큰입니다.");
		}catch (IllegalArgumentException e) {
			log.warn("클레임 정보가 비어있습니다.");
		}

		return false;
	}

}