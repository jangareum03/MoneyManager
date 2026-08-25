package com.moneymanager.support.stream;

import com.moneymanager.global.domain.enums.DatePatterns;
import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;

public final class DateTestStream {

	private  DateTestStream() {}

	public static Stream<Arguments> validDates() {
		return Stream.of(DatePatterns.values())
				.filter(patterns ->
						patterns != DatePatterns.KOREAN_YEAR
								&& patterns != DatePatterns.KOREAN_YEAR_MONTH
								&& patterns != DatePatterns.KOREAN_YEAR_MONTH_WEEK
				)
				.map(date ->  Arguments.of(
						named(
								"날짜 형식이 " + date.getPattern() + " 인 경우",
								LocalDate.of(2026, 1, 15).format(DateTimeFormatter.ofPattern(date.getPattern()))
						)
				));
	}

	public static Stream<Arguments> invalidDates() {
		return Stream.of(
				Arguments.of(named("날짜 연도가 잘못된 경우(date: 200000101)", "200000101")),
				Arguments.of(named("날짜 월이 잘못된 경우(date: 20261301)", "20261301")),
				Arguments.of(named("날짜 일이 잘못된 경우(date: 20260140)", "20260140")),
				Arguments.of(named("숫자말고 다른 문자가 있는 경우(date: 한abc)", "한abc"))
		);
	}

	public static Stream<Arguments> unsupportedFormats() {
		return Stream.of(
				Arguments.of(named("날짜 형식이 yyyy-MM-dd 인 경우", "2026-01-01")),
				Arguments.of(named("날짜 형식이 yyyy.MM.dd 인 경우", "2026.01.01")),
				Arguments.of(named("날짜 형식이 MM/dd/yyyy 인 경우", "01/01/2026")),
				Arguments.of(named("날짜 형식이 dd/MM/yyyy 인 경우", "01/01/2026"))
		);
	}

}