package com.moneymanager.support.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
public class TestTimeConfig {

	@Bean
	@Primary
	public Clock clock() {
		return Clock.fixed(
				Instant.parse("2026-01-15T00:00:00Z"),
				ZoneId.systemDefault()
		);
	}

}