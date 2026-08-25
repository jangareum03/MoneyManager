package com.moneymanager.support.fixture.file;

import org.springframework.mock.web.MockMultipartFile;

public final class ImageFixture {

	private final String name;
	private final String contentType;
	private final byte[] content;

	private ImageFixture(String name, String contentType, byte[] content) {
		this.name = name;
		this.contentType = contentType;
		this.content = content;
	}

	public static MockMultipartFile jpg(String fileName) {
		return new MockMultipartFile(
				"images",
				fileName + ".jpg",
				"image/jpeg",
				jpgContent(1024)
		);
	}

	public static MockMultipartFile png(String fileName) {
		return new MockMultipartFile(
				"images",
				fileName + ".png",
				"image/png",
				pngContent(1024)
		);
	}

	public static MockMultipartFile empty(String fileName) {
		return new MockMultipartFile(
				"images",
				fileName + ".jpg",
				"image/jpeg",
				(byte[]) null
		);
	}

	private static byte[]jpgContent(int size) {
		byte[] content = new byte[size];

		content[0] = (byte) 0xFF;
		content[1] = (byte) 0xD8;
		content[2] = (byte) 0xFF;
		content[3] = (byte) 0xE0;

		return content;
	}

	private static byte[] pngContent(int size) {
		byte[] content = new byte[size];

		content[0] = (byte) 0x89;
		content[1] = (byte) 0x50;
		content[2] = (byte) 0x4E;
		content[3] = (byte) 0x47;

		return content;
	}

}