package com.moneymanager.support.fixture.file;

import org.springframework.mock.web.MockMultipartFile;

public final class ImageFixture {

	private ImageFixture() {}

	public static MockMultipartFile jpg(String fileName) {
		byte[] content = new byte[1024];

		content[0] = (byte) 0xFF;
		content[1] = (byte) 0xD8;
		content[2] = (byte) 0xFF;
		content[3] = (byte) 0xE0;

		return new MockMultipartFile(
				"images",
				fileName + ".jpg",
				"image/jpeg",
				content
		);
	}

	public static MockMultipartFile png(String fileName) {
		byte[] content = new byte[1024];

		content[0] = (byte) 0x89;
		content[1] = (byte) 0x50;
		content[2] = (byte) 0x4E;
		content[3] = (byte) 0x47;

		return new MockMultipartFile(
				"images",
				fileName + ".png",
				"image/png",
				content
		);
	}

	public static MockMultipartFile emptyFile() {
		return new MockMultipartFile(
				"images",
				"test.jpg",
				"image/jpeg",
                (byte[]) null
        );
	}

}
