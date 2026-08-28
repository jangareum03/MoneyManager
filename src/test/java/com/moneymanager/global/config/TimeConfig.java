package com.moneymanager.global.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.LocalDate;

@TestConfiguration
public class TimeConfig {

	@Bean
	@Primary
	public MutableClock clock() {
		MutableClock clock = new MutableClock();
		clock.set(LocalDate.of(2026, 1, 1));

		return clock;
	}

}