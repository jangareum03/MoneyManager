package com.moneymanager.global.security;

import com.moneymanager.member.domain.dto.MemberAuth;
import com.moneymanager.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.service.member.auth<br>
 * * 파일이름       : CustomUserDetailService<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 15<br>
 * * 설명              : 데이터베이스에서 사용자 정보를 조회하는 클래스
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
@Service
public class CustomUserDetailService implements UserDetailsService {

	private final MemberRepository memberRepository;

	public CustomUserDetailService( MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		MemberAuth memberAuth = memberRepository.findAuthByUsername(username);

		if(memberAuth == null) {
			throw new UsernameNotFoundException(username + " 아이디는 찾을 수 없습니다.");
		}

		return new CustomUserDetails(memberAuth);
	}

}