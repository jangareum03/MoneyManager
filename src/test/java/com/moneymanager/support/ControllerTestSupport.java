package com.moneymanager.support;

import com.moneymanager.security.jwt.JwtAuthenticationFilter;
import com.moneymanager.security.jwt.JwtTokenProvider;
import org.springframework.boot.test.mock.mockito.MockBean;

public abstract class ControllerTestSupport {

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockBean
	protected JwtTokenProvider tokenProvider;

}
