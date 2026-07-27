package com.moneymanager.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TimeConfig {

	@Bean
	@Primary
	public MutableClock clock() {
		return new MutableClock();
	}

}