package com.areum.moneymanager.service.member.auth;

import com.areum.moneymanager.dao.member.MemberInfoDaoImpl;
import com.areum.moneymanager.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.service.member.auth<br>
 *  * 파일이름       : CustomUserDetailService<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 스프링 시큐리티에서 사용자 인증 처리 위한 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

	@Autowired
	private MemberInfoDaoImpl memberInfoDao;

	@Override
	public UserDetails loadUserByUsername( String id ) throws UsernameNotFoundException {
		List<GrantedAuthority> grantedAuthorities = List.of( new SimpleGrantedAuthority("USER"));

		Member member =  memberInfoDao.findAuthMemberByUsername(id);

		if( member == null ) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없음: " + id);
		}

		return new User( member.getUserName(), member.getPassword(), grantedAuthorities );
	}
}
