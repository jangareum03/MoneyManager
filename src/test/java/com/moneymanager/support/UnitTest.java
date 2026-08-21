package com.moneymanager.support;

import com.moneymanager.global.fillter.JwtAuthenticationFilter;
import org.springframework.boot.test.mock.mockito.MockBean;

public abstract class UnitTest {

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

}