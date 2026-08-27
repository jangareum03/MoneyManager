package com.moneymanager.support.security;


import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.member.domain.dto.MemberAuth;
import com.moneymanager.member.domain.enums.MemberStatus;
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

		MemberAuth memberAuth = MemberAuth.builder()
															.memberId(withMockCustomUser.memberId())
															.username(withMockCustomUser.username())
															.password(withMockCustomUser.password())
															.role(withMockCustomUser.role())
															.status(MemberStatus.ACTIVE)
															.loginFailCount(1)
															.deletedDate(null)
															.build();

		CustomUserDetails principal = new CustomUserDetails(memberAuth);

		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(
						principal,
						principal.getPassword(),
						List.of(new SimpleGrantedAuthority(memberAuth.getRole()))
				);

		context.setAuthentication(authenticationToken);

		return context;
	}

}
