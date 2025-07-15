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

		System.out.println("🍋회원 아이디: " + member.getUserName() + ", 비밀번호: " + member.getPassword() + ", 권한:" + grantedAuthorities.get(0));
		return new User( member.getUserName(), member.getPassword(), grantedAuthorities );
	}
}
