package com.moneymanager.security.jwt;

import com.moneymanager.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

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

	private final Key key;
	private final long accessTokenLimit = 1000 * 60 * 60;	//60분

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
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}


	/**
	 * Access Token 생성합니다.
	 * <p>
	 *     클라이언트가 가지고 있는 정보가 담긴 토큰입니다.
	 * </p>
	 *
	 * @param authentication	스프링 시큐리티가 관리하는 사용자 정보
	 * @return	사용자 정보를 담은 토큰
	 */
	public String generateAccessToken(Authentication authentication) {
		Claims claims = createClaim(authentication);
		Date now = new Date();

		return Jwts.builder()
				.setSubject(claims.getSubject())															//토큰 제목
				.setClaims(claims)																					//클레임 설정
				.setIssuedAt(now)																					//토큰 생성시간(=현재)
				.setExpiration( new Date( now.getTime() + accessTokenLimit) )		//토큰 만료시간(=한시간)
				.signWith(key, SignatureAlgorithm.HS256)											//서버키를 HS256 알고리즘으로 암호화 진행
				.compact();
	}


	/**
	 * 사용자의 아이디(<code>username</code>)로 Access Token을 생성합니다.
	 * <p>
	 *     주로, 기존의 Access Token을 재생성할 때 사용됩니다.
	 * </p>
	 *
	 * @param username		로그인 시도한 아이디
	 * @return	사용자 정보를 담은 토큰
	 */
	public String reissueAccessToken(String username) {
		Date now = new Date();

		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(now)
				.setExpiration(new Date( now.getTime() + accessTokenLimit ))
				.signWith( key, SignatureAlgorithm.HS256 )
				.compact();
	}


	/**
	 * 클레임을 생성 후 반환합니다. <br>
	 * JWT의 구성요소 중 Payload에 사용될 정보에 대한 내용을 담고 있는 객체입니다.
	 *
	 * @param authentication	스프링 시큐리티가 관리하는 사용자 정보
	 * @return	생성된 Claims
	 */
	private Claims createClaim(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
		claims.put("nickName", userDetails.getNickname());
		claims.put("profile", userDetails.getProfile());
		claims.put("roles", authentication.getAuthorities());

		return claims;
	}


	/**
	 * Refresh Token 생성합니다.
	 * <p>
	 *     Access Token 만료시간이 되면 새로운 토큰을 발급해주기 위한 토큰입니다.
	 * </p>
	 *
	 * @param authentication	스프링 시큐리티가 관리하는 사용자 정보
	 * @return	토큰
	 */
	public String generateRefreshToken(Authentication authentication) {
		Date now = new Date();

		long refreshTokenLimit = 1000 * 60 * 60 * 24;	// 1일(=24시간)

		return Jwts.builder()
				.setIssuedAt(now)																								//토큰 생성시간(=현재)
				.setExpiration( new Date(now.getTime() + refreshTokenLimit) )					//토큰 만료시간
				.signWith(key, SignatureAlgorithm.HS256)														//서버키를 HS256 알고리즘으로 암호화 진행
				.compact();
	}


	/**
	 * 토큰의 유효성을 검증합니다.
	 *
	 * @param token		검증할 토큰
	 * @return	토큰이 유효하다면 true, 아니면 false
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(key).build()
					.parseClaimsJws(token);

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


	/**
	 * 토큰에서 id 정보를 반환합니다.
	 *
	 * @param token	토큰
	 * @return	사용자 id
	 */
	public String getUserName(String token) {
		Claims claims = getClaims(token);

		return claims.getSubject();
	}


	/**
	 * 토큰에서 닉네임을 반환합니다.
	 *
	 * @param token	토큰
	 * @return	클레임에 저장된 닉네임
	 */
	public String getNickName(String token) {
		Claims claims = getClaims(token);

		return (String) claims.get("nickName");
	}


	/**
	 * 토큰에서 프로필 이미지를 반환합니다.
	 *
	 * @param token	토큰
	 * @return	클레임에 저장된 프로필
	 */
	public String getProfile(String token) {
		Claims claims = getClaims(token);

		return (String) claims.get("profile");
	}


	/**
	 * 토큰에서 토큰 만료일자를 반환합니다.
	 *
	 * @param token	토큰
	 * @return	만료일자
	 */
	public Date getExpiration(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getExpiration();
	}


	/**
	 * 토큰에서 클레임을 반환합니다.
	 *
	 * @param token	클레임을 얻을 토큰
	 * @return 클레임
	 */
	private Claims getClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key).build()
				.parseClaimsJws(token)
				.getBody();
	}
}
