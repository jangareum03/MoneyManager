package com.moneymanager.support.data;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;

public final class DateTestData {

	public static Stream<Arguments> unsupportedFormats() {
		return Stream.of(
				Arguments.of(named("날짜 형식이 yyyy-MM-dd 인 경우", "2026-01-01")),
				Arguments.of(named("날짜 형식이 yyyy.MM.dd 인 경우", "2026.01.01")),
				Arguments.of(named("날짜 형식이 MM/dd/yyyy 인 경우", "10/22/2026")),
				Arguments.of(named("날짜 형식이 dd/MM/yyyy 인 경우", "15/06/2026"))
		);
	}

}
