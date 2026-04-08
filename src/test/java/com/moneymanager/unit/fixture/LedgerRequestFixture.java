package com.moneymanager.unit.fixture;

import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.unit.service.validation.LedgerValidatorTest;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class LedgerRequestFixture {
	public static Stream<Arguments> successWriteRequest() throws IOException {
		return Stream.of(
				Arguments.of(
						"필수정보만 있는 기본 가계부",
						defaultLedgerWriteRequest().build()
				),
				Arguments.of(
						"고정주기가 있는 가계부",
						defaultLedgerWriteRequest()
								.fixed(true)
								.fixCycle("y")
								.build()
				),
				Arguments.of(
						"메모가 작성된 가계부",
						defaultLedgerWriteRequest()
								.memo("넷플릭스 구독료")
								.build()
				),
				Arguments.of(
						"장소가 작성된 가계부",
						withPlace()
								.build()
				),
				Arguments.of(
						"이미지가 첨부된 가계부",
						withImage()
								.build()
				)
		);
	}

	public static LedgerWriteRequest.LedgerWriteRequestBuilder defaultLedgerWriteRequest() {
		return LedgerWriteRequest.builder()
				.date("20260101")
				.categoryCode("010101")
				.fixed(false)
				.amount(10000L)
				.amountType("none");
	}

	public static LedgerWriteRequest.LedgerWriteRequestBuilder withPlace() {
		return defaultLedgerWriteRequest()
				.placeName("CGV 강남점")
				.roadAddress("서울특별시 강남구 강남대로 438 스타플렉스")
				.detailAddress("4층");
	}

	public static LedgerWriteRequest.LedgerWriteRequestBuilder withImage() throws IOException {
		return defaultLedgerWriteRequest()
				.image(
						List.of(
								new MockMultipartFile(
										"files",
										"test.jpg",
										"image/jpg",
										LedgerValidatorTest.class.getResourceAsStream("/files/test.jpg")
								),
								new MockMultipartFile(
										"files",
										"test.png",
										"image/png",
										LedgerValidatorTest.class.getResourceAsStream("/files/test.png")
								)
						)
				);
	}
}
