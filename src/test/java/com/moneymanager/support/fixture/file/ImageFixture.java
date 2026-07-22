package com.moneymanager.support.fixture.file;

import org.springframework.mock.web.MockMultipartFile;

public final class ImageFixture {

	private ImageFixture() {}

	public static MockMultipartFile jpg(String fileName) {
		return new MockMultipartFile(
				"images",
				fileName + ".jpg",
				"image/jpeg",
				"image-content".getBytes()
		);
	}

	public static MockMultipartFile png(String fileName) {
		return new MockMultipartFile(
				"images",
				fileName + ".png",
				"image/png",
				"image-content".getBytes()
		);
	}

}
