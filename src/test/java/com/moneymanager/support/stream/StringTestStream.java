package com.moneymanager.support.stream;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;

public final class StringTestStream {

	private StringTestStream() {}

	public static Stream<Arguments> blankStrings() {
		return Stream.of(
				Arguments.of(named("1개 공백인 경우", " ")),
				Arguments.of(named("5개 공백인 경우", " ".repeat(5))),
				Arguments.of(named("탭인 경우", "\t")),
				Arguments.of(named("줄바꿈인 경우", "\n"))
		);
	}

	public static Stream<Arguments> validLengths(String text, int min, int max) {
		int middle = (min + max) / 2;

		return Stream.of(
				min,
				min + 1,
				middle,
				max - 1,
				max
		)
			.distinct()
			.map(length -> Arguments.of(
					named(length + "글자", text.repeat(length))
			));
	}

	public static Stream<Arguments> invalidLengths(String text, int min, int max) {
		return Stream.of(
				min - 1, max+1
				)
				.filter(length -> length > 0)
				.distinct()
				.map(length -> Arguments.of(
						named(length + "글자", text.repeat(length))
				));
	}

}
