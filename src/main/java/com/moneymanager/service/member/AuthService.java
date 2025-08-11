package com.moneymanager.service.member;

import com.moneymanager.dao.member.MemberDaoImpl;
import com.moneymanager.dto.member.response.MemberLoginResponse;
import com.moneymanager.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.service.member<br>
 * * 파일이름       : AuthService<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7 15<br>
 * * 설명              : 회원 계정 비즈니스 로직을 처리하는 클래스
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
 * 		 	  <td>25. 7. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Service
public class AuthService {

	private final MemberDaoImpl memberDao;

	public AuthService(MemberDaoImpl memberDao) {
		this.memberDao = memberDao;
	}

	public MemberLoginResponse.Success getLoginMember(String username ) {
		Member member = memberDao.findLoginInfoByUsername(username);

		return MemberLoginResponse.Success.builder()
				.memberId(member.getId())
				.nickName(member.getNickName())
				.profile(ImageServiceImpl.getBasePath() + member.getInfo().getProfile())
				.build();
	}



}
