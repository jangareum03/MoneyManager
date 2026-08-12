package com.moneymanager.support;

import com.moneymanager.global.fillter.JwtAuthenticationFilter;
import com.moneymanager.global.operation.logger.OperationLogger;
import com.moneymanager.global.security.jwt.JwtTokenProvider;
import org.springframework.boot.test.mock.mockito.MockBean;

public abstract class ControllerTestSupport {

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockBean
	protected JwtTokenProvider tokenProvider;

	@MockBean
	private OperationLogger logger;

}
