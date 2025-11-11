package com.moneymanager.service.member;

import com.moneymanager.dao.member.MemberDaoImpl;
import com.moneymanager.dao.member.MemberTokenDao;
import com.moneymanager.entity.Member;
import com.moneymanager.entity.MemberToken;
import com.moneymanager.security.jwt.JwtTokenProvider;
import com.moneymanager.utils.DateTimeUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.member<br>
 * 파일이름       : TokenServiceImpl<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 12.<br>
 * 설명              : 회원 토큰 관련 로직을 처리하는 클래스
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
 * 		 	  <td>25. 11. 12.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
public class TokenServiceImpl {

	private final JwtTokenProvider tokenProvider;
	private final MemberTokenDao tokenDao;
	private final MemberDaoImpl memberDao;


	public TokenServiceImpl(JwtTokenProvider tokenProvider, MemberTokenDao tokenDao, MemberDaoImpl memberDao) {
		this.tokenDao = tokenDao;
		this.tokenProvider = tokenProvider;
		this.memberDao = memberDao;
	}


	/**
	 * 로그인 성공으로 생성된 토큰을 서버에 저장합니다.
	 *
	 * @param username		로그인 성공한 아이디
	 * @param accessToken	접근 시 필요한 토큰
	 * @param refreshToken	재발급시 필요한 토큰
	 */
	public void createMemberToken(String username, String accessToken, String refreshToken) {
		//만료일시
		Date accessExp = tokenProvider.getExpiration(accessToken);
		Date refreshExp = tokenProvider.getExpiration(refreshToken);

		Member member = memberDao.findAuthMemberByUsername(username);

		MemberToken token =
				MemberToken.builder()
						.member(member)
						.accessToken(accessToken)
						.accessExpireAt(DateTimeUtils.getLocalDateTime(accessExp))
						.refreshToken(refreshToken)
						.refreshExpireAt(DateTimeUtils.getLocalDateTime(refreshExp))
						.build();


		tokenDao.saveToken(token);
	}
}
