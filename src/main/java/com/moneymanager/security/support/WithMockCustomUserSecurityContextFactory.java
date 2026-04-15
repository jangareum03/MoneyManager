package com.moneymanager.security.support;


import com.moneymanager.domain.member.Member;
import com.moneymanager.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

final class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser withMockCustomUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		Member member = Member.builder()
				.id(withMockCustomUser.memberId())
				.userName(withMockCustomUser.username())
				.password(withMockCustomUser.password())
				.role("ROLE_" + withMockCustomUser.role())
				.build();

		CustomUserDetails principal = new CustomUserDetails(member);

		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(
						principal,
						principal.getPassword(),
						List.of(new SimpleGrantedAuthority(member.getRole()))
				);

		context.setAuthentication(authenticationToken);

		return context;
	}

}
