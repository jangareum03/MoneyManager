package com.moneymanager.config;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class MutableClock extends Clock {

	private Instant instant;
	private final ZoneId zone;

	public MutableClock() {
		this.zone = ZoneId.systemDefault();
		this.instant = Instant.now();
	}

	public void set(LocalDate date) {
		this.instant = date
				.atStartOfDay(zone)
				.toInstant();
	}

	@Override
	public ZoneId getZone() {
		return zone;
	}

	@Override
	public Clock withZone(ZoneId zone) {
		return Clock.fixed(instant, zone);
	}

	@Override
	public Instant instant() {
		return instant;
	}

}