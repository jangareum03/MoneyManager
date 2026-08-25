package com.moneymanager.support.fixture.file;

import com.moneymanager.global.domain.FileMetadata;

import java.nio.file.Path;

public final class FileMetadataFixture {

    private final Path absolutePath;
    private final Path relativePath;
    private final String originalFileName;
    private final String storedFileName;
    private final String contentType;

    private FileMetadataFixture(Path absolutePath, Path relativePath, String originalFileName, String storedFileName, String contentType) {
        this.absolutePath = absolutePath;
        this.relativePath = relativePath;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.contentType = contentType;;
    }

    public static FileMetadata jpg(String relativePath, String fileName) {
        return FileMetadataFixture.of(relativePath, fileName + ".jpg", "image/jpeg").build();
    }

    public static FileMetadata png(String relativePath, String fileName) {
        return FileMetadataFixture.of(relativePath, fileName + ".png", "image/png").build();
    }

    private static FileMetadataFixture of(String path, String fileName, String contentType) {
        return new FileMetadataFixture(
                Path.of("root").resolve(path).resolve(fileName),
                Path.of(path).resolve(fileName),
                fileName,
                "stored" + fileName,
                contentType
        );
    }

    private FileMetadata build() {
        return FileMetadata.of(absolutePath, relativePath, originalFileName, storedFileName, contentType);
    }

}